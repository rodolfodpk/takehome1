package com.rdpk.metering.service

import com.rdpk.metering.config.EventMetrics
import org.redisson.api.RedissonReactiveClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.concurrent.TimeUnit

/**
 * Service for distributed locking using Redis
 * Prevents duplicate window processing across multiple instances
 */
@Service
class DistributedLockService(
    private val redissonReactive: RedissonReactiveClient,
    private val eventMetrics: EventMetrics,
    @Value("\${metering.lock.default-timeout-seconds:30}")
    private val defaultLockTimeoutSeconds: Long,
    @Value("\${metering.lock.default-lease-time-seconds:60}")
    private val defaultLeaseTimeSeconds: Long
) {
    
    private val log = LoggerFactory.getLogger(javaClass)
    
    /**
     * Execute operation with distributed lock
     * Returns empty Mono if lock cannot be acquired
     */
    fun <T> withLock(
        lockKey: String,
        timeout: Duration = Duration.ofSeconds(defaultLockTimeoutSeconds),
        leaseTime: Duration = Duration.ofSeconds(defaultLeaseTimeSeconds),
        operation: Mono<T>
    ): Mono<T> {
        val lock = redissonReactive.getLock(lockKey)
        
        return lock.tryLock(
            timeout.toMillis(),
            leaseTime.toMillis(),
            TimeUnit.MILLISECONDS
        )
            .flatMap { acquired ->
                if (acquired) {
                    log.debug("Acquired lock: $lockKey")
                    operation
                        .flatMap { result ->
                            // Chain unlock in reactive stream to maintain context
                            lock.unlock()
                                .doOnSuccess { log.debug("Released lock: $lockKey") }
                                .doOnError { error ->
                                    // If unlock fails due to thread context, log and continue
                                    // Lock will expire via lease time (60s) automatically
                                    if (error is IllegalMonitorStateException) {
                                        log.debug("Lock unlock failed due to thread context (will expire): $lockKey")
                                    } else {
                                        log.warn("Error releasing lock: $lockKey", error)
                                    }
                                }
                                .onErrorResume { Mono.empty<Void>() } // Swallow unlock errors
                                .then(Mono.just(result)) // Return original result
                        }
                        .onErrorResume { error ->
                            log.error("Error in locked operation: $lockKey", error)
                            // Try to unlock on error, but don't fail if unlock fails
                            lock.unlock()
                                .doOnError { unlockError ->
                                    if (unlockError is IllegalMonitorStateException) {
                                        log.debug("Lock unlock failed due to thread context (will expire): $lockKey")
                                    } else {
                                        log.warn("Error releasing lock after operation error: $lockKey", unlockError)
                                    }
                                }
                                .onErrorResume { Mono.empty<Void>() }
                                .then(Mono.error(error)) // Propagate original error
                        }
                } else {
                    log.debug("Failed to acquire lock: $lockKey")
                    eventMetrics.lockContention.increment()
                    Mono.empty()
                }
            }
    }
}

