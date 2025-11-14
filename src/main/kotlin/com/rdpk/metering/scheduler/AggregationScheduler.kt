package com.rdpk.metering.scheduler

import com.rdpk.metering.config.EventMetrics
import com.rdpk.metering.config.ResilienceService
import com.rdpk.metering.domain.AggregationWindow
import com.rdpk.metering.dto.TenantCustomerPair
import com.rdpk.metering.repository.AggregationWindowRepository
import com.rdpk.metering.repository.CustomerRepository
import com.rdpk.metering.repository.UsageEventRepository
import com.rdpk.metering.service.AggregationService
import com.rdpk.metering.service.DistributedLockService
import com.rdpk.metering.service.RedisStateService
import io.micrometer.core.instrument.Timer
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.beans.factory.annotation.Value
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
 * Runs at configurable interval (default: 30 seconds) to process completed windows
 * Uses distributed locks to prevent duplicate processing
 * Configure interval via: metering.aggregation.processing-interval-ms
 * Can be disabled by setting metering.schedulers.enabled=false
 */
@Component
@ConditionalOnProperty(name = ["metering.schedulers.enabled"], havingValue = "true", matchIfMissing = true)
class AggregationScheduler(
    private val customerRepository: CustomerRepository,
    private val aggregationWindowRepository: AggregationWindowRepository,
    private val usageEventRepository: UsageEventRepository,
    private val redisStateService: RedisStateService,
    private val aggregationService: AggregationService,
    private val distributedLockService: DistributedLockService,
    private val resilienceService: ResilienceService,
    private val eventMetrics: EventMetrics,
    private val clock: Clock,
    @Value("\${metering.window.duration-seconds:30}")
    private val windowDurationSeconds: Long,
    @Value("\${metering.lock.timeout-seconds:5}")
    private val lockTimeoutSeconds: Long,
    @Value("\${metering.lock.lease-time-seconds:60}")
    private val lockLeaseTimeSeconds: Long
) {
    
    private val log = LoggerFactory.getLogger(javaClass)
    
    /**
     * Process aggregation windows at configurable interval
     * Default: 30 seconds (30000ms)
     * Configure via: metering.aggregation.processing-interval-ms
     */
    @Scheduled(fixedRateString = "\${metering.aggregation.processing-interval-ms:30000}")
    fun processAggregationWindows() {
        val now = clock.instant()
        val windowEnd = truncateToWindow(now)
        val windowStart = windowEnd.minusSeconds(windowDurationSeconds)
        
        log.debug("Processing aggregation window: $windowStart to $windowEnd")
        
        // Get all active tenants with their customers in a single JOIN query
        // Optimizes from N+1 queries to 1 query
        resilienceService.applyPostgresResilience(
            customerRepository.findAllActiveTenantsWithCustomers()
        )
            .flatMap { pair ->
                val tenant = pair.toTenant()
                val customer = pair.toCustomer()
                
                // Try to acquire lock for this (tenant, customer, window)
                val lockKey = "aggregation:lock:${tenant.id}:${customer.id}:${windowStart.epochSecond}"
                
                val lockSample = Timer.start()
                distributedLockService.withLock<Void>(
                    lockKey = lockKey,
                    timeout = Duration.ofSeconds(lockTimeoutSeconds),
                    leaseTime = Duration.ofSeconds(lockLeaseTimeSeconds),
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
                                            // Persist to Postgres with resilience using explicit JSONB casting
                                            resilienceService.applyPostgresResilience(
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
        val windowStartSeconds = (epochSeconds / windowDurationSeconds) * windowDurationSeconds
        return Instant.ofEpochSecond(windowStartSeconds)
    }
}

