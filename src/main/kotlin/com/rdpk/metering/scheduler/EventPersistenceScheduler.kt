package com.rdpk.metering.scheduler

import com.rdpk.metering.config.EventMetrics
import com.rdpk.metering.config.ResilienceService
import com.rdpk.metering.repository.UsageEventRepository
import com.rdpk.metering.repository.UsageEventRepositoryExtensions
import com.rdpk.metering.service.RedisEventStorageService
import io.micrometer.core.instrument.Timer
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

/**
 * Background scheduler for batch persisting events from Redis to Postgres
 * Runs every 2 seconds to batch events efficiently
 * Cold path: Redis → Postgres (batched)
 */
@Component
class EventPersistenceScheduler(
    private val redisEventStorageService: RedisEventStorageService,
    private val usageEventRepository: UsageEventRepository,
    private val usageEventRepositoryExtensions: UsageEventRepositoryExtensions,
    private val resilienceService: ResilienceService,
    private val eventMetrics: EventMetrics
) {
    
    private val log = LoggerFactory.getLogger(javaClass)
    
    companion object {
        private const val BATCH_SIZE = 1000
    }
    
    /**
     * Batch persist events from Redis to Postgres
     * Runs every 2 seconds
     */
    @Scheduled(fixedRate = 2000) // 2 seconds
    fun batchPersistEvents() {
        redisEventStorageService.getPendingEvents(BATCH_SIZE)
            .flatMap { batch ->
                if (batch.isEmpty()) {
                    Mono.empty<Void>()
                } else {
                    log.debug("Persisting batch of ${batch.size} events to Postgres")
                    val sample = Timer.start()
                    
                    // Batch insert to Postgres with resilience using JSONB casting
                    resilienceService.applyPostgresResilience(
                        usageEventRepositoryExtensions.saveAllWithJsonb(batch)
                            .then()
                    )
                        .flatMap {
                            // Remove from Redis after successful persistence
                            redisEventStorageService.removeEvents(batch)
                        }
                        .doOnSuccess {
                            sample.stop(eventMetrics.dbPersistenceLatency)
                            eventMetrics.recordBatchSize(batch.size)
                            log.debug("Successfully persisted and removed ${batch.size} events")
                        }
                        .doOnError { error ->
                            sample.stop(eventMetrics.dbPersistenceLatency)
                            log.error("Error persisting batch of ${batch.size} events", error)
                            // Events remain in Redis for retry
                        }
                }
            }
            .subscribe(
                {},
                { error ->
                    log.error("Error in batch persistence scheduler", error)
                }
            )
    }
}

