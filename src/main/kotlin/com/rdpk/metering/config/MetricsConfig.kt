package com.rdpk.metering.config

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration for custom metrics
 * 
 * ## What Micrometer Automatically Covers:
 * 
 * ### 1. JVM Metrics (automatic)
 * - `jvm.memory.used` - Memory usage by area (heap, non-heap)
 * - `jvm.memory.max` - Maximum memory
 * - `jvm.gc.pause` - GC pause times
 * - `jvm.threads.live` - Active threads
 * - `jvm.threads.daemon` - Daemon threads
 * 
 * ### 2. HTTP Metrics (automatic from Spring WebFlux)
 * - `http.server.requests` - Request count, latency, status codes
 * - Tags: `method`, `uri`, `status`, `exception`
 * - Includes P50, P95, P99 percentiles
 * 
 * ### 3. R2DBC Connection Pool Metrics (automatic)
 * - `r2dbc.pool.acquired` - Acquired connections
 * - `r2dbc.pool.pending` - Pending connection requests
 * - `r2dbc.pool.idle` - Idle connections
 * - `r2dbc.pool.timeout` - Connection timeout events
 * 
 * ### 4. Resilience4j Metrics (automatic via resilience4j-micrometer)
 * - `resilience4j.circuitbreaker.calls` - Circuit breaker calls by state
 * - `resilience4j.circuitbreaker.state` - Current state (0=closed, 1=open, 2=half-open)
 * - `resilience4j.circuitbreaker.failure.rate` - Failure rate
 * - `resilience4j.retry.calls` - Retry attempts
 * - `resilience4j.timelimiter.calls` - Timeout occurrences
 * 
 * ## Custom Metrics (this config):
 * Business-specific metrics for the metering system
 */
@Configuration
class MetricsConfig {

    /**
     * Metrics for event processing
     */
    @Bean
    fun eventMetrics(meterRegistry: MeterRegistry): EventMetrics {
        return EventMetrics(meterRegistry)
    }
}

/**
 * Custom metrics for event processing and aggregation
 * These complement the automatic metrics from Micrometer
 */
class EventMetrics(private val meterRegistry: MeterRegistry) {
    
    // Cache for tenant counters to avoid recreating them
    private val tenantCounters = mutableMapOf<Long, Counter>()
    
    // Cache for customer counters to avoid recreating them
    private val customerCounters = mutableMapOf<Pair<Long, Long>, Counter>()
    
    // Event ingestion metrics
    val eventsIngested: Counter = Counter.builder("metering.events.ingested")
        .description("Total number of events ingested")
        .tag("type", "total")
        .register(meterRegistry)
    
    fun eventsIngestedByTenant(tenantId: Long): Counter {
        return tenantCounters.getOrPut(tenantId) {
            Counter.builder("metering.events.ingested")
                .description("Events ingested per tenant")
                .tag("type", "by_tenant")
                .tag("tenant_id", tenantId.toString())
                .register(meterRegistry)
        }
    }
    
    fun eventsIngestedByCustomer(tenantId: Long, customerId: Long): Counter {
        val key = Pair(tenantId, customerId)
        return customerCounters.getOrPut(key) {
            Counter.builder("metering.events.ingested")
                .description("Events ingested per customer")
                .tag("type", "by_customer")
                .tag("tenant_id", tenantId.toString())
                .tag("customer_id", customerId.toString())
                .register(meterRegistry)
        }
    }
    
    val eventsProcessedLatency: Timer = Timer.builder("metering.events.processing.latency")
        .description("Event processing latency (hot path)")
        .publishPercentiles(0.5, 0.95, 0.99) // P50, P95, P99
        .register(meterRegistry)
    
    val eventsIngestionErrors: Counter = Counter.builder("metering.events.ingestion.errors")
        .description("Total number of event ingestion errors")
        .register(meterRegistry)
    
    // Late event metrics
    val lateEventsDetected: Counter = Counter.builder("metering.events.late.detected")
        .description("Total number of late events detected")
        .register(meterRegistry)
    
    val lateEventsProcessed: Counter = Counter.builder("metering.events.late.processed")
        .description("Total number of late events processed")
        .register(meterRegistry)
    
    // Redis operation metrics
    val redisStorageLatency: Timer = Timer.builder("metering.redis.storage.latency")
        .description("Redis event storage latency")
        .publishPercentiles(0.5, 0.95, 0.99)
        .register(meterRegistry)
    
    val redisCounterUpdateLatency: Timer = Timer.builder("metering.redis.counter.update.latency")
        .description("Redis counter update latency")
        .publishPercentiles(0.5, 0.95, 0.99)
        .register(meterRegistry)
    
    val redisReadLatency: Timer = Timer.builder("metering.redis.read.latency")
        .description("Redis read operations latency")
        .publishPercentiles(0.5, 0.95, 0.99)
        .register(meterRegistry)
    
    // Database operation metrics
    val dbPersistenceLatency: Timer = Timer.builder("metering.db.persistence.latency")
        .description("Database persistence latency (batch writes)")
        .publishPercentiles(0.5, 0.95, 0.99)
        .register(meterRegistry)
    
    // Cache for batch size counter to avoid recreating it
    private val batchSizeCounter: Counter by lazy {
        Counter.builder("metering.db.persistence.batch.size")
            .description("Number of events persisted per batch")
            .register(meterRegistry)
    }
    
    fun recordBatchSize(batchSize: Int) {
        batchSizeCounter.increment(batchSize.toDouble())
    }
    
    // Aggregation metrics
    val aggregationProcessingLatency: Timer = Timer.builder("metering.aggregation.processing.latency")
        .description("Aggregation window processing latency")
        .publishPercentiles(0.5, 0.95, 0.99)
        .register(meterRegistry)
    
    val aggregationWindowsProcessed: Counter = Counter.builder("metering.aggregation.windows.processed")
        .description("Total number of aggregation windows processed")
        .register(meterRegistry)
    
    val aggregationWindowsErrors: Counter = Counter.builder("metering.aggregation.windows.errors")
        .description("Total number of aggregation window processing errors")
        .register(meterRegistry)
    
    // Lock metrics
    val lockAcquisitionLatency: Timer = Timer.builder("metering.lock.acquisition.latency")
        .description("Distributed lock acquisition latency")
        .publishPercentiles(0.5, 0.95, 0.99)
        .register(meterRegistry)
    
    val lockContention: Counter = Counter.builder("metering.lock.contention")
        .description("Number of lock contention events (timeout/failure)")
        .register(meterRegistry)
}

