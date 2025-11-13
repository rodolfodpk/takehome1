package com.rdpk.metering.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.rdpk.metering.config.EventMetrics
import com.rdpk.metering.config.ResilienceService
import com.rdpk.metering.domain.LateEvent
import com.rdpk.metering.domain.UsageEvent
import com.rdpk.metering.repository.LateEventRepository
import org.slf4j.LoggerFactory
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
    private val clock: Clock
) {
    
    private val log = LoggerFactory.getLogger(javaClass)
    
    companion object {
        private val WINDOW_DURATION_SECONDS = 30L
        private val LATE_THRESHOLD_MINUTES = 1L // Events < 1 minute late are processed immediately
    }
    
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
            if (lateByMinutes < LATE_THRESHOLD_MINUTES) {
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
            lateEventRepository.save(lateEvent)
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
     */
    private fun serializeEvent(event: UsageEvent): String {
        return try {
            objectMapper.writeValueAsString(event)
        } catch (e: Exception) {
            log.error("Error serializing event for late event storage: ${event.eventId}", e)
            "{}"
        }
    }
    
    /**
     * Truncate timestamp to window boundary
     */
    private fun truncateToWindow(timestamp: Instant): Instant {
        val epochSeconds = timestamp.epochSecond
        val windowStartSeconds = (epochSeconds / WINDOW_DURATION_SECONDS) * WINDOW_DURATION_SECONDS
        return Instant.ofEpochSecond(windowStartSeconds)
    }
    
    /**
     * Deserialize LateEvent data back to UsageEvent
     */
    fun deserializeEvent(lateEvent: LateEvent): UsageEvent? {
        return try {
            objectMapper.readValue(lateEvent.data, UsageEvent::class.java)
        } catch (e: Exception) {
            log.error("Error deserializing late event: ${lateEvent.eventId}", e)
            null
        }
    }
}

