package com.rdpk.metering.service

import com.rdpk.metering.config.EventMetrics
import com.rdpk.metering.config.ResilienceService
import com.rdpk.metering.domain.AggregationWindow
import com.rdpk.metering.domain.LateEvent
import com.rdpk.metering.domain.UsageEvent
import com.rdpk.metering.repository.AggregationWindowRepository
import com.rdpk.metering.repository.LateEventRepository
import com.rdpk.metering.repository.UsageEventRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime

/**
 * Service for processing late events
 * Contains the business logic for processing events that arrived after their window closed
 * Called by LateEventProcessor scheduler
 */
@Service
class LateEventProcessingService(
    private val lateEventRepository: LateEventRepository,
    private val usageEventRepository: UsageEventRepository,
    private val aggregationWindowRepository: AggregationWindowRepository,
    private val lateEventService: LateEventService,
    private val aggregationService: AggregationService,
    private val resilienceService: ResilienceService,
    private val eventMetrics: EventMetrics,
    private val clock: Clock,
    @Value("\${metering.window.duration-seconds:30}")
    private val windowDurationSeconds: Long
) {
    
    private val log = LoggerFactory.getLogger(javaClass)
    
    /**
     * Process all late events (in batches)
     * Returns a Mono that completes when all events in the batch are processed
     */
    fun processLateEvents(batchSize: Int): Mono<Void> {
        log.debug("Starting late event processing")
        
        // Get all late events (in batches) with resilience
        return resilienceService.applyPostgresResilience(
            lateEventRepository.findAll()
                .take(batchSize.toLong())
        )
            .flatMap { lateEvent ->
                processLateEvent(lateEvent)
                    .doOnSuccess {
                        eventMetrics.lateEventsProcessed.increment()
                    }
                    .onErrorResume { error ->
                        log.error("Error processing late event: ${lateEvent.eventId}", error)
                        Mono.empty<Void>()
                    }
            }
            .then()
    }
    
    /**
     * Process a single late event
     * Deserializes the event, persists it, updates or creates aggregation window, then deletes the late event
     */
    fun processLateEvent(lateEvent: LateEvent): Mono<Void> {
        // Deserialize event
        val event = lateEventService.deserializeEvent(lateEvent)
            ?: return Mono.empty()
        
        // First, persist the event to usage_events table with resilience
        return resilienceService.applyPostgresResilience(
            usageEventRepository.save(event)
        )
            .flatMap { savedEvent ->
                // Determine which window this event belongs to
                val windowStart = truncateToWindow(lateEvent.originalTimestamp)
                val windowEnd = windowStart.plusSeconds(windowDurationSeconds)
                
                // Check if aggregation window exists with resilience
                resilienceService.applyPostgresResilience(
                    aggregationWindowRepository.findByTenantIdAndCustomerIdAndWindowStart(
                        lateEvent.tenantId, lateEvent.customerId, windowStart
                    )
                        .hasElement()
                )
                    .flatMap { windowExists ->
                        if (windowExists) {
                            // Window exists - update aggregation
                            updateExistingAggregation(lateEvent, savedEvent, windowStart, windowEnd)
                        } else {
                            // Window doesn't exist - create new aggregation
                            createNewAggregation(lateEvent, savedEvent, windowStart, windowEnd)
                        }
                    }
            }
            .then(deleteLateEvent(lateEvent))
    }
    
    private fun updateExistingAggregation(
        lateEvent: LateEvent,
        event: UsageEvent,
        windowStart: Instant,
        windowEnd: Instant
    ): Mono<Void> {
        return resilienceService.applyPostgresResilience(
            aggregationWindowRepository.findByTenantIdAndCustomerIdAndWindowStart(
                lateEvent.tenantId, lateEvent.customerId, windowStart
            )
        )
            .flatMap { existingWindow ->
                // Get all events for this window (including the late event) with resilience
                resilienceService.applyPostgresResilience(
                    usageEventRepository.findByTenantIdAndCustomerIdAndTimestampBetween(
                        lateEvent.tenantId, lateEvent.customerId, windowStart, windowEnd
                    )
                        .collectList()
                )
                    .flatMap { events ->
                        // Add the late event to the list
                        val allEvents = events + event
                        
                        // Re-aggregate
                        aggregationService.aggregateWindow(
                            lateEvent.tenantId, lateEvent.customerId, windowStart, windowEnd, allEvents
                        )
                            .map { aggregationResult ->
                                val aggregationData = aggregationService.serializeAggregationData(aggregationResult)
                                
                                // Update existing window
                                existingWindow.copy(
                                    aggregationData = aggregationData,
                                    updated = LocalDateTime.now(clock)
                                )
                            }
                            .flatMap { updatedWindow ->
                                resilienceService.applyPostgresResilience(
                                    aggregationWindowRepository.saveWithJsonb(
                                        updatedWindow.tenantId,
                                        updatedWindow.customerId,
                                        updatedWindow.windowStart,
                                        updatedWindow.windowEnd,
                                        updatedWindow.aggregationData,
                                        updatedWindow.created ?: LocalDateTime.now(clock),
                                        updatedWindow.updated ?: LocalDateTime.now(clock)
                                    )
                                )
                                    .doOnSuccess {
                                        log.info("Updated aggregation window for late event: ${lateEvent.eventId}")
                                    }
                            }
                    }
            }
            .then()
    }
    
    private fun createNewAggregation(
        lateEvent: LateEvent,
        event: UsageEvent,
        windowStart: Instant,
        windowEnd: Instant
    ): Mono<Void> {
        // Get all events for this window with resilience
        return resilienceService.applyPostgresResilience(
            usageEventRepository.findByTenantIdAndCustomerIdAndTimestampBetween(
                lateEvent.tenantId, lateEvent.customerId, windowStart, windowEnd
            )
                .collectList()
        )
            .flatMap { events ->
                val allEvents = events + event
                
                // Aggregate
                aggregationService.aggregateWindow(
                    lateEvent.tenantId, lateEvent.customerId, windowStart, windowEnd, allEvents
                )
                    .map { aggregationResult ->
                        val aggregationData = aggregationService.serializeAggregationData(aggregationResult)
                        val now = LocalDateTime.now(clock)
                        
                        AggregationWindow(
                            tenantId = lateEvent.tenantId,
                            customerId = lateEvent.customerId,
                            windowStart = windowStart,
                            windowEnd = windowEnd,
                            aggregationData = aggregationData,
                            created = now,
                            updated = now
                        )
                    }
                    .flatMap { newWindow ->
                        resilienceService.applyPostgresResilience(
                            aggregationWindowRepository.saveWithJsonb(
                                newWindow.tenantId,
                                newWindow.customerId,
                                newWindow.windowStart,
                                newWindow.windowEnd,
                                newWindow.aggregationData,
                                newWindow.created ?: LocalDateTime.now(clock),
                                newWindow.updated ?: LocalDateTime.now(clock)
                            )
                        )
                            .doOnSuccess {
                                log.info("Created aggregation window for late event: ${lateEvent.eventId}")
                            }
                    }
            }
            .then()
    }
    
    private fun deleteLateEvent(lateEvent: LateEvent): Mono<Void> {
        return resilienceService.applyPostgresResilience(
            lateEventRepository.delete(lateEvent)
                .then()
        )
            .doOnSuccess {
                log.debug("Deleted processed late event: ${lateEvent.eventId}")
            }
    }
    
    private fun truncateToWindow(timestamp: Instant): Instant {
        val epochSeconds = timestamp.epochSecond
        val windowStartSeconds = (epochSeconds / windowDurationSeconds) * windowDurationSeconds
        return Instant.ofEpochSecond(windowStartSeconds)
    }
}

