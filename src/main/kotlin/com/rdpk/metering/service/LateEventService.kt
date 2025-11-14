package com.rdpk.metering.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.rdpk.metering.config.EventMetrics
import com.rdpk.metering.config.ResilienceService
import com.rdpk.metering.domain.LateEvent
import com.rdpk.metering.domain.UsageEvent
import com.rdpk.metering.repository.LateEventRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime

/**
 * Service for handling late events
 * Implements hybrid approach: immediate processing if < 1 minute late, otherwise batch processing
 */
@Service
class LateEventService(
    private val lateEventRepository: LateEventRepository,
    private val objectMapper: ObjectMapper,
    private val resilienceService: ResilienceService,
    private val eventMetrics: EventMetrics,
    private val clock: Clock,
    @Value("\${metering.window.duration-seconds:30}")
    private val windowDurationSeconds: Long,
    @Value("\${metering.late-event.threshold-minutes:1}")
    private val lateThresholdMinutes: Long
) {
    
    private val log = LoggerFactory.getLogger(javaClass)
    
    /**
     * Check if event is late and handle accordingly
     * Returns true if event was handled as late event, false otherwise
     */
    fun checkAndHandleLateEvent(event: UsageEvent, currentTime: Instant? = null): Mono<Boolean> {
        val now = currentTime ?: clock.instant()
        val eventTimestamp = event.timestamp
        val currentWindowStart = truncateToWindow(now)
        val eventWindowStart = truncateToWindow(eventTimestamp)
        
        // Check if event is for a previous window
        if (eventWindowStart.isBefore(currentWindowStart)) {
            val lateByMinutes = Duration.between(eventWindowStart, now).toMinutes()
            
            // If < 1 minute late, try to process immediately (likely network delay)
            if (lateByMinutes < lateThresholdMinutes) {
                log.debug("Event ${event.eventId} is < 1 minute late, will be processed normally")
                return Mono.just(false) // Process normally
            } else {
                // Store as late event for batch processing
                eventMetrics.lateEventsDetected.increment()
                log.info("Event ${event.eventId} is late by $lateByMinutes minutes, storing for batch processing")
                return storeLateEvent(event, now)
                    .map { true }
            }
        }
        
        return Mono.just(false) // Not late, process normally
    }
    
    /**
     * Store event as late event
     */
    private fun storeLateEvent(event: UsageEvent, receivedTime: Instant): Mono<LateEvent> {
        val eventData = serializeEvent(event)
        val now = LocalDateTime.now(clock)
        
        val lateEvent = LateEvent(
            eventId = event.eventId,
            originalTimestamp = event.timestamp,
            receivedTimestamp = receivedTime,
            tenantId = event.tenantId,
            customerId = event.customerId,
            data = eventData
        )
        
        return resilienceService.applyPostgresResilience(
            lateEventRepository.saveWithJsonb(
                eventId = lateEvent.eventId,
                originalTimestamp = lateEvent.originalTimestamp,
                receivedTimestamp = lateEvent.receivedTimestamp,
                tenantId = lateEvent.tenantId,
                customerId = lateEvent.customerId,
                data = lateEvent.data
            )
        )
            .doOnSuccess {
                log.debug("Stored late event: ${event.eventId}")
            }
            .doOnError { error ->
                log.error("Error storing late event: ${event.eventId}", error)
            }
    }
    
    /**
     * Serialize UsageEvent to JSON for storage
     * Only serializes the essential fields needed to reconstruct the event
     * Excludes id, created, updated to avoid null issues and keep JSON clean
     * Ensures valid JSON that PostgreSQL can parse
     */
    private fun serializeEvent(event: UsageEvent): String {
        return try {
            // Create a clean map with only the fields we need to reconstruct the event
            // Same pattern as AggregationService.serializeAggregationData which works
            val eventMap = mapOf(
                "eventId" to event.eventId,
                "tenantId" to event.tenantId,
                "customerId" to event.customerId,
                "timestamp" to event.timestamp.toString(),
                "data" to event.data
            )
            val json = objectMapper.writeValueAsString(eventMap)
            // Validate it's parseable JSON before storing
            objectMapper.readTree(json)
            json
        } catch (e: Exception) {
            log.error("Error serializing event for late event storage: ${event.eventId}", e)
            throw e // Don't return "{}" - fail fast so we can debug
        }
    }
    
    /**
     * Truncate timestamp to window boundary
     */
    private fun truncateToWindow(timestamp: Instant): Instant {
        val epochSeconds = timestamp.epochSecond
        val windowStartSeconds = (epochSeconds / windowDurationSeconds) * windowDurationSeconds
        return Instant.ofEpochSecond(windowStartSeconds)
    }
    
    /**
     * Deserialize LateEvent data back to UsageEvent
     * Reconstructs UsageEvent from the stored JSON
     */
    fun deserializeEvent(lateEvent: LateEvent): UsageEvent? {
        return try {
            // lateEvent.data is a JSON string read from database (via data::text)
            // If using to_jsonb(), it might be double-encoded (JSON string containing JSON)
            // If using ::jsonb, it should be the JSON object directly
            var jsonString = lateEvent.data.trim()
            
            // Check if it's a JSON-encoded string (starts and ends with quotes)
            if (jsonString.startsWith("\"") && jsonString.endsWith("\"")) {
                // It's a JSON string containing JSON - decode it first
                jsonString = objectMapper.readValue(jsonString, String::class.java)
            }
            
            // Now parse the actual JSON
            @Suppress("UNCHECKED_CAST")
            val eventMap = objectMapper.readValue(jsonString, Map::class.java) as Map<String, Any>
            
            // Extract the nested data field
            @Suppress("UNCHECKED_CAST")
            val dataMap = (eventMap["data"] as? Map<*, *>)?.let { 
                it as Map<String, Any> 
            } ?: emptyMap<String, Any>()
            
            UsageEvent(
                id = null,
                eventId = eventMap["eventId"] as String,
                tenantId = (eventMap["tenantId"] as Number).toLong(),
                customerId = (eventMap["customerId"] as Number).toLong(),
                timestamp = Instant.parse(eventMap["timestamp"] as String),
                data = dataMap,
                created = null,
                updated = null
            )
        } catch (e: Exception) {
            log.error("Error deserializing late event: ${lateEvent.eventId}, data: ${lateEvent.data.take(200)}", e)
            null
        }
    }
}

