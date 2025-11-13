package com.rdpk.metering.scheduler

import com.rdpk.metering.config.EventMetrics
import com.rdpk.metering.config.ResilienceService
import com.rdpk.metering.repository.UsageEventRepository
import com.rdpk.metering.service.RedisEventStorageService
import io.micrometer.core.instrument.Timer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
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
    private val resilienceService: ResilienceService,
    private val eventMetrics: EventMetrics,
    @Value("\${metering.event.persistence-batch-size:1000}")
    private val batchSize: Int
) {
    
    private val log = LoggerFactory.getLogger(javaClass)
    
    /**
     * Batch persist events from Redis to Postgres
     * Default: 2 seconds (2000ms)
     * Configure via: metering.event.persistence-interval-ms
     */
    @Scheduled(fixedRateString = "\${metering.event.persistence-interval-ms:2000}")
    fun batchPersistEvents() {
        redisEventStorageService.getPendingEvents(batchSize)
            .flatMap { batch ->
                if (batch.isEmpty()) {
                    Mono.empty<Void>()
                } else {
                    log.debug("Persisting batch of ${batch.size} events to Postgres")
                    val sample = Timer.start()
                    
                    // Batch insert to Postgres with resilience
                    resilienceService.applyPostgresResilience(
                        usageEventRepository.saveAll(batch)
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

