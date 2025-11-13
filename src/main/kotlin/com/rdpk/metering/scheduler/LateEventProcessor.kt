package com.rdpk.metering.scheduler

import com.rdpk.metering.config.EventMetrics
import com.rdpk.metering.config.ResilienceService
import com.rdpk.metering.domain.AggregationWindow
import com.rdpk.metering.domain.LateEvent
import com.rdpk.metering.domain.UsageEvent
import com.rdpk.metering.repository.AggregationWindowRepository
import com.rdpk.metering.repository.LateEventRepository
import com.rdpk.metering.repository.UsageEventRepository
import com.rdpk.metering.service.AggregationService
import com.rdpk.metering.service.LateEventService
import com.rdpk.metering.service.RedisStateService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime

/**
 * Scheduler for processing late events
 * Runs periodically to process events that arrived after their window closed
 */
@Component
class LateEventProcessor(
    private val lateEventRepository: LateEventRepository,
    private val usageEventRepository: UsageEventRepository,
    private val aggregationWindowRepository: AggregationWindowRepository,
    private val lateEventService: LateEventService,
    private val aggregationService: AggregationService,
    private val redisStateService: RedisStateService,
    private val resilienceService: ResilienceService,
    private val eventMetrics: EventMetrics,
    private val clock: Clock
) {
    
    private val log = LoggerFactory.getLogger(javaClass)
    
    companion object {
        private val WINDOW_DURATION_SECONDS = 30L
        private const val BATCH_SIZE = 100
    }
    
    /**
     * Process late events periodically
     * Runs every 5 minutes to process accumulated late events
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    fun processLateEvents() {
        log.debug("Starting late event processing")
        
        // Get all late events (in batches) with resilience
        resilienceService.applyPostgresResilience(
            lateEventRepository.findAll()
                .take(BATCH_SIZE.toLong())
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
            .subscribe(
                {},
                { error ->
                    log.error("Error in late event processor", error)
                }
            )
    }
    
    private fun processLateEvent(lateEvent: LateEvent): Mono<Void> {
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
                val windowEnd = windowStart.plusSeconds(WINDOW_DURATION_SECONDS)
                
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
                                    aggregationWindowRepository.save(updatedWindow)
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
                            aggregationWindowRepository.save(newWindow)
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
        val windowStartSeconds = (epochSeconds / WINDOW_DURATION_SECONDS) * WINDOW_DURATION_SECONDS
        return Instant.ofEpochSecond(windowStartSeconds)
    }
}

