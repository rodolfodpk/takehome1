package com.rdpk.metering.service

import com.rdpk.metering.config.EventMetrics
import com.rdpk.metering.config.ResilienceService
import com.rdpk.metering.domain.AggregationWindow
import com.rdpk.metering.domain.UsageEvent
import com.rdpk.metering.repository.AggregationWindowRepository
import com.rdpk.metering.repository.UsageEventRepository
import io.micrometer.core.instrument.Timer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime

/**
 * Service for processing aggregation windows
 * Contains the business logic for aggregating events into windows
 * Called by AggregationScheduler
 */
@Service
class AggregationProcessingService(
    private val aggregationWindowRepository: AggregationWindowRepository,
    private val usageEventRepository: UsageEventRepository,
    private val redisStateService: RedisStateService,
    private val aggregationService: AggregationService,
    private val resilienceService: ResilienceService,
    private val eventMetrics: EventMetrics,
    private val clock: Clock,
    @Value("\${metering.window.duration-seconds:30}")
    private val windowDurationSeconds: Long
) {
    
    private val log = LoggerFactory.getLogger(javaClass)
    
    /**
     * Process a single aggregation window
     * Checks if already processed, fetches events, aggregates, and persists
     */
    fun processWindow(
        tenantId: Long,
        customerId: Long,
        windowStart: Instant,
        windowEnd: Instant
    ): Mono<Void> {
        val sample = Timer.start()
        
        return checkWindowExists(tenantId, customerId, windowStart)
            .flatMap { exists ->
                if (exists) {
                    log.debug("Window already processed: tenant=$tenantId, customer=$customerId, windowStart=$windowStart")
                    Mono.empty<Void>()
                } else {
                    processNewWindow(tenantId, customerId, windowStart, windowEnd, sample)
                }
            }
            .then()
    }
    
    /**
     * Check if window already exists in database
     */
    private fun checkWindowExists(
        tenantId: Long,
        customerId: Long,
        windowStart: Instant
    ): Mono<Boolean> {
        return resilienceService.applyPostgresResilience(
            aggregationWindowRepository.findByTenantIdAndCustomerIdAndWindowStart(
                tenantId, customerId, windowStart
            )
                .hasElement()
        )
    }
    
    /**
     * Process a new window that hasn't been processed yet
     */
    private fun processNewWindow(
        tenantId: Long,
        customerId: Long,
        windowStart: Instant,
        windowEnd: Instant,
        sample: Timer.Sample
    ): Mono<Void> {
        return fetchEventsForWindow(tenantId, customerId, windowStart, windowEnd)
            .flatMap { events ->
                createAggregationWindow(tenantId, customerId, windowStart, windowEnd, events)
            }
            .flatMap { aggregationWindow ->
                persistWindow(aggregationWindow, tenantId, customerId, sample)
            }
    }
    
    /**
     * Fetch events for the window from Postgres
     */
    private fun fetchEventsForWindow(
        tenantId: Long,
        customerId: Long,
        windowStart: Instant,
        windowEnd: Instant
    ): Mono<List<UsageEvent>> {
        return resilienceService.applyPostgresResilience(
            usageEventRepository.findByTenantIdAndCustomerIdAndTimestampBetween(
                tenantId, customerId, windowStart, windowEnd
            )
                .collectList()
        )
    }
    
    /**
     * Create aggregation window entity from events
     * Gets Redis counters, aggregates events, and serializes data
     */
    private fun createAggregationWindow(
        tenantId: Long,
        customerId: Long,
        windowStart: Instant,
        windowEnd: Instant,
        events: List<UsageEvent>
    ): Mono<AggregationWindow> {
        return redisStateService.getCounters(tenantId, customerId)
            .flatMap { counters ->
                aggregationService.aggregateWindow(
                    tenantId, customerId, windowStart, windowEnd, events
                )
                    .map { aggregationResult ->
                        val aggregationData = aggregationService.serializeAggregationData(aggregationResult)
                        val now = LocalDateTime.now(clock)
                        
                        AggregationWindow(
                            tenantId = tenantId,
                            customerId = customerId,
                            windowStart = windowStart,
                            windowEnd = windowEnd,
                            aggregationData = aggregationData,
                            created = now,
                            updated = now
                        )
                    }
            }
    }
    
    /**
     * Persist aggregation window to Postgres and clear Redis counters
     */
    private fun persistWindow(
        aggregationWindow: AggregationWindow,
        tenantId: Long,
        customerId: Long,
        sample: Timer.Sample
    ): Mono<Void> {
        return resilienceService.applyPostgresResilience(
            aggregationWindowRepository.saveWithJsonb(
                aggregationWindow.tenantId,
                aggregationWindow.customerId,
                aggregationWindow.windowStart,
                aggregationWindow.windowEnd,
                aggregationWindow.aggregationData,
                aggregationWindow.created ?: LocalDateTime.now(clock),
                aggregationWindow.updated ?: LocalDateTime.now(clock)
            )
                .then(redisStateService.clearCounters(tenantId, customerId))
        )
            .doOnSuccess {
                sample.stop(eventMetrics.aggregationProcessingLatency)
                eventMetrics.aggregationWindowsProcessed.increment()
                log.debug("Successfully aggregated window: tenant=$tenantId, customer=$customerId, windowStart=${aggregationWindow.windowStart}")
            }
            .doOnError { error ->
                sample.stop(eventMetrics.aggregationProcessingLatency)
                eventMetrics.aggregationWindowsErrors.increment()
                log.error("Error persisting aggregation window: tenant=$tenantId, customer=$customerId", error)
            }
    }
}

