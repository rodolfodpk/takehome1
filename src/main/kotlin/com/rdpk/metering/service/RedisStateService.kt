package com.rdpk.metering.service

import com.rdpk.metering.config.EventMetrics
import com.rdpk.metering.config.ResilienceService
import com.rdpk.metering.domain.UsageEvent
import io.micrometer.core.instrument.Timer
import org.redisson.api.RedissonReactiveClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Duration

/**
 * Service for managing Redis state (counters, window coordination)
 * Optimized for 10k+ updates/second with batching
 */
@Service
class RedisStateService(
    private val redissonReactive: RedissonReactiveClient,
    private val resilienceService: ResilienceService,
    private val eventMetrics: EventMetrics
) {
    
    private val log = LoggerFactory.getLogger(javaClass)
    
    companion object {
        private const val COUNTER_TTL_SECONDS = 3600L // 1 hour TTL for counters
    }
    
    /**
     * Update Redis counters for an event
     * Uses atomic operations for thread-safety
     */
    fun updateCounters(event: UsageEvent): Mono<Void> {
        val tenantId = event.tenantId
        val customerId = event.customerId
        
        // Counter keys
        val tokensKey = "metering:tenant:$tenantId:customer:$customerId:tokens"
        val callsKey = "metering:tenant:$tenantId:customer:$customerId:calls"
        val inputTokensKey = "metering:tenant:$tenantId:customer:$customerId:inputTokens"
        val outputTokensKey = "metering:tenant:$tenantId:customer:$customerId:outputTokens"
        
        // Extract token values from data JSONB
        // inputTokens and outputTokens are required (validated at DTO level)
        val tokens = (event.data["tokens"] as? Number)?.toInt() ?: 0
        val inputTokens = (event.data["inputTokens"] as? Number)?.toInt() ?: 0
        val outputTokens = (event.data["outputTokens"] as? Number)?.toInt() ?: 0
        
        // Use batch for atomic updates
        val batch = redissonReactive.createBatch()
        
        // Update counters atomically
        batch.getAtomicLong(tokensKey).addAndGet(tokens.toLong())
        batch.getAtomicLong(callsKey).addAndGet(1L)
        batch.getAtomicLong(inputTokensKey).addAndGet(inputTokens.toLong())
        batch.getAtomicLong(outputTokensKey).addAndGet(outputTokens.toLong())
        
        val sample = Timer.start()
        
        return resilienceService.applyRedisResilience(
            batch.execute()
                .then()
        )
            .doOnSuccess {
                sample.stop(eventMetrics.redisCounterUpdateLatency)
            }
            .doOnError { error ->
                sample.stop(eventMetrics.redisCounterUpdateLatency)
                log.error("Error updating Redis counters for event: ${event.eventId}", error)
            }
    }
    
    /**
     * Get current counters for a customer
     */
    fun getCounters(tenantId: Long, customerId: Long): Mono<CustomerCounters> {
        val tokensKey = "metering:tenant:$tenantId:customer:$customerId:tokens"
        val callsKey = "metering:tenant:$tenantId:customer:$customerId:calls"
        val inputTokensKey = "metering:tenant:$tenantId:customer:$customerId:inputTokens"
        val outputTokensKey = "metering:tenant:$tenantId:customer:$customerId:outputTokens"
        
        return resilienceService.applyRedisResilience(
            Mono.zip(
                redissonReactive.getAtomicLong(tokensKey).get(),
                redissonReactive.getAtomicLong(callsKey).get(),
                redissonReactive.getAtomicLong(inputTokensKey).get(),
                redissonReactive.getAtomicLong(outputTokensKey).get()
            ).map { tuple ->
                CustomerCounters(
                    tokens = tuple.t1,
                    calls = tuple.t2,
                    inputTokens = tuple.t3,
                    outputTokens = tuple.t4
                )
            }
        )
    }
    
    /**
     * Clear counters for a customer (after aggregation)
     */
    fun clearCounters(tenantId: Long, customerId: Long): Mono<Void> {
        val tokensKey = "metering:tenant:$tenantId:customer:$customerId:tokens"
        val callsKey = "metering:tenant:$tenantId:customer:$customerId:calls"
        val inputTokensKey = "metering:tenant:$tenantId:customer:$customerId:inputTokens"
        val outputTokensKey = "metering:tenant:$tenantId:customer:$customerId:outputTokens"
        
        val batch = redissonReactive.createBatch()
        batch.getAtomicLong(tokensKey).delete()
        batch.getAtomicLong(callsKey).delete()
        batch.getAtomicLong(inputTokensKey).delete()
        batch.getAtomicLong(outputTokensKey).delete()
        
        return resilienceService.applyRedisResilience(
            batch.execute().then()
        )
    }
    
    
    data class CustomerCounters(
        val tokens: Long,
        val calls: Long,
        val inputTokens: Long,
        val outputTokens: Long
    )
}

