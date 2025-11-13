package com.rdpk.metering.scheduler

import com.rdpk.metering.config.EventMetrics
import com.rdpk.metering.config.ResilienceService
import com.rdpk.metering.domain.AggregationWindow
import com.rdpk.metering.repository.AggregationWindowRepository
import com.rdpk.metering.repository.CustomerRepository
import com.rdpk.metering.repository.TenantRepository
import com.rdpk.metering.repository.UsageEventRepository
import com.rdpk.metering.service.AggregationService
import com.rdpk.metering.service.DistributedLockService
import com.rdpk.metering.service.RedisStateService
import io.micrometer.core.instrument.Timer
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime

/**
 * Scheduler for batch aggregation of 30-second windows
 * Runs every 30 seconds to process completed windows
 * Uses distributed locks to prevent duplicate processing
 */
@Component
class AggregationScheduler(
    private val tenantRepository: TenantRepository,
    private val customerRepository: CustomerRepository,
    private val aggregationWindowRepository: AggregationWindowRepository,
    private val usageEventRepository: UsageEventRepository,
    private val redisStateService: RedisStateService,
    private val aggregationService: AggregationService,
    private val distributedLockService: DistributedLockService,
    private val resilienceService: ResilienceService,
    private val eventMetrics: EventMetrics,
    private val clock: Clock
) {
    
    private val log = LoggerFactory.getLogger(javaClass)
    
    companion object {
        private val WINDOW_DURATION_SECONDS = 30L
    }
    
    /**
     * Process aggregation windows every 30 seconds
     */
    @Scheduled(fixedRate = 30000) // 30 seconds
    fun processAggregationWindows() {
        val now = clock.instant()
        val windowEnd = truncateToWindow(now)
        val windowStart = windowEnd.minusSeconds(WINDOW_DURATION_SECONDS)
        
        log.debug("Processing aggregation window: $windowStart to $windowEnd")
        
        // Get all active tenants with resilience
        resilienceService.applyPostgresResilience(
            tenantRepository.findByActive(true)
        )
            .flatMap { tenant ->
                // Get all customers for this tenant with resilience
                resilienceService.applyPostgresResilience(
                    customerRepository.findByTenantId(tenant.id!!)
                )
                    .flatMap { customer ->
                        // Try to acquire lock for this (tenant, customer, window)
                        val lockKey = "aggregation:lock:${tenant.id}:${customer.id}:${windowStart.epochSecond}"
                        
                        val lockSample = Timer.start()
                        distributedLockService.withLock<Void>(
                            lockKey = lockKey,
                            timeout = Duration.ofSeconds(5),
                            leaseTime = Duration.ofSeconds(60),
                            operation = processWindow(tenant.id!!, customer.id!!, windowStart, windowEnd)
                        )
                        .doOnSuccess {
                            lockSample.stop(eventMetrics.lockAcquisitionLatency)
                        }
                        .doOnError { error ->
                            lockSample.stop(eventMetrics.lockAcquisitionLatency)
                            eventMetrics.lockContention.increment()
                            log.error("Error processing window for tenant ${tenant.id}, customer ${customer.id}", error)
                        }
                        .onErrorResume { error ->
                            log.error("Error processing window for tenant ${tenant.id}, customer ${customer.id}", error)
                            Mono.empty<Void>()
                        }
                    }
            }
            .then()
            .subscribe(
                {},
                { error ->
                    log.error("Error in aggregation scheduler", error)
                }
            )
    }
    
    private fun processWindow(
        tenantId: Long,
        customerId: Long,
        windowStart: Instant,
        windowEnd: Instant
    ): Mono<Void> {
        val sample = Timer.start()
        
        // Check if window already processed with resilience
        return resilienceService.applyPostgresResilience(
            aggregationWindowRepository.findByTenantIdAndCustomerIdAndWindowStart(
                tenantId, customerId, windowStart
            )
                .hasElement()
        )
            .flatMap { exists ->
                if (exists) {
                    log.debug("Window already processed: tenant=$tenantId, customer=$customerId, windowStart=$windowStart")
                    Mono.empty<Void>()
                } else {
                    // Fetch events for this window from Postgres with resilience
                    resilienceService.applyPostgresResilience(
                        usageEventRepository.findByTenantIdAndCustomerIdAndTimestampBetween(
                            tenantId, customerId, windowStart, windowEnd
                        )
                            .collectList()
                    )
                        .flatMap { events ->
                            // Get counters from Redis (for totals)
                            redisStateService.getCounters(tenantId, customerId)
                                .flatMap { counters ->
                                    // Aggregate from events (detailed breakdowns)
                                    aggregationService.aggregateWindow(
                                        tenantId, customerId, windowStart, windowEnd, events
                                    )
                                        .map { aggregationResult ->
                                            // Serialize aggregation data
                                            val aggregationData = aggregationService.serializeAggregationData(aggregationResult)
                                            
                                            // Create aggregation window entity
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
                                        .flatMap { aggregationWindow ->
                                            // Persist to Postgres with resilience
                                            resilienceService.applyPostgresResilience(
                                                aggregationWindowRepository.save(aggregationWindow)
                                                    .then(redisStateService.clearCounters(tenantId, customerId))
                                            )
                                                .doOnSuccess {
                                                    sample.stop(eventMetrics.aggregationProcessingLatency)
                                                    eventMetrics.aggregationWindowsProcessed.increment()
                                                    log.debug("Successfully aggregated window: tenant=$tenantId, customer=$customerId, windowStart=$windowStart")
                                                }
                                                .doOnError { error ->
                                                    sample.stop(eventMetrics.aggregationProcessingLatency)
                                                    eventMetrics.aggregationWindowsErrors.increment()
                                                }
                                        }
                                }
                        }
                }
            }
            .then()
    }
    
    private fun truncateToWindow(timestamp: Instant): Instant {
        val epochSeconds = timestamp.epochSecond
        val windowStartSeconds = (epochSeconds / WINDOW_DURATION_SECONDS) * WINDOW_DURATION_SECONDS
        return Instant.ofEpochSecond(windowStartSeconds)
    }
}

