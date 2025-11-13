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
                        .doFinally {
                            lock.unlock()
                                .doOnSuccess { log.debug("Released lock: $lockKey") }
                                .doOnError { error ->
                                    log.warn("Error releasing lock: $lockKey", error)
                                }
                                .subscribe()
                        }
                        .onErrorResume { error ->
                            log.error("Error in locked operation: $lockKey", error)
                            lock.unlock().then(Mono.error(error))
                        }
                } else {
                    log.debug("Failed to acquire lock: $lockKey")
                    eventMetrics.lockContention.increment()
                    Mono.empty()
                }
            }
    }
}

