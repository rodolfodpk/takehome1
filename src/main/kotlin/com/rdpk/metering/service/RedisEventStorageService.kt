package com.rdpk.metering.service

import com.rdpk.metering.config.EventMetrics
import com.rdpk.metering.config.ResilienceService
import com.rdpk.metering.domain.UsageEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.micrometer.core.instrument.Timer
import org.redisson.api.RedissonReactiveClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Service for storing events in Redis (hot path)
 * Events are stored immediately with TTL for durability
 * Background job batches these to Postgres (cold path)
 */
@Service
class RedisEventStorageService(
    private val redissonReactive: RedissonReactiveClient,
    private val objectMapper: ObjectMapper,
    private val resilienceService: ResilienceService,
    private val eventMetrics: EventMetrics,
    @Value("\${metering.redis.event-ttl-hours:1}")
    private val eventTtlHours: Long
) {
    
    private val log = LoggerFactory.getLogger(javaClass)
    
    companion object {
        private const val EVENTS_LIST_KEY = "events:pending:list"
    }
    
    /**
     * Store event in Redis immediately (hot path)
     * Uses Redis List for efficient batching
     */
    fun storeEvent(event: UsageEvent): Mono<Void> {
        val sample = Timer.start()
        
        return try {
            val eventJson = objectMapper.writeValueAsString(event)
            resilienceService.applyRedisResilience(
                redissonReactive.getList<String>(EVENTS_LIST_KEY)
                    .add(eventJson)
                    .then()
            )
                .doOnSuccess {
                    sample.stop(eventMetrics.redisStorageLatency)
                    log.debug("Stored event in Redis: ${event.eventId}")
                }
                .doOnError { error ->
                    sample.stop(eventMetrics.redisStorageLatency)
                    log.error("Failed to store event in Redis: ${event.eventId}", error)
                }
        } catch (e: Exception) {
            sample.stop(eventMetrics.redisStorageLatency)
            log.error("Error serializing event: ${event.eventId}", e)
            Mono.error(e)
        }
    }
    
    /**
     * Get batch of pending events from Redis (cold path)
     * Used by background job to batch persist to Postgres
     */
    fun getPendingEvents(batchSize: Int): Mono<List<UsageEvent>> {
        val list = redissonReactive.getList<String>(EVENTS_LIST_KEY)
        val sample = Timer.start()
        
        return resilienceService.applyRedisResilience(
            Flux.range(0, batchSize)
                .flatMap { index ->
                    list.get(index)
                        .onErrorResume { Mono.empty() }
                }
                .take(batchSize.toLong())
                .map { json: String ->
                    objectMapper.readValue(json, UsageEvent::class.java)
                }
                .collectList()
        )
            .doOnSuccess {
                sample.stop(eventMetrics.redisReadLatency)
            }
            .doOnError { error: Throwable ->
                sample.stop(eventMetrics.redisReadLatency)
                log.error("Error reading events from Redis", error)
            }
            .onErrorResume { error: Throwable ->
                log.error("Error reading events from Redis", error)
                Mono.just(emptyList())
            }
    }
    
    /**
     * Remove processed events from Redis
     * Used after successful batch persistence to Postgres
     * Note: Uses LPOP to remove from left (FIFO) - events are processed in order
     * In production, consider using Redis Sorted Set with scores for better performance
     */
    fun removeEvents(events: List<UsageEvent>): Mono<Void> {
        return if (events.isEmpty()) {
            Mono.empty()
        } else {
            val list = redissonReactive.getList<String>(EVENTS_LIST_KEY)
            val eventIds = events.map { it.eventId }.toSet()
            
            // Serialize events to JSON for comparison
            val eventJsonSet = events.mapNotNull { event ->
                try {
                    objectMapper.writeValueAsString(event)
                } catch (e: Exception) {
                    log.warn("Error serializing event for removal: ${event.eventId}", e)
                    null
                }
            }.toSet()
            
            // Remove matching JSON strings from list
            resilienceService.applyRedisResilience(
                Flux.fromIterable(eventJsonSet)
                    .flatMap { json ->
                        list.remove(json)
                    }
                    .then()
            )
                .doOnError { error: Throwable ->
                    log.error("Error removing events from Redis", error)
                }
        }
    }
}

