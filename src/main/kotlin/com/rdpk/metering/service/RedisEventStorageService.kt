package com.rdpk.metering.service

import com.rdpk.metering.config.EventMetrics
import com.rdpk.metering.config.ResilienceService
import com.rdpk.metering.domain.UsageEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.micrometer.core.instrument.Timer
import org.redisson.api.RedissonReactiveClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Service for storing events in Redis (hot path)
 * Events are stored immediately for durability
 * Background job batches these to Postgres (cold path)
 */
@Service
class RedisEventStorageService(
    private val redissonReactive: RedissonReactiveClient,
    private val objectMapper: ObjectMapper,
    private val resilienceService: ResilienceService,
    private val eventMetrics: EventMetrics
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
     * Optimized: Uses LRANGE to get all events in a single Redis call
     */
    fun getPendingEvents(batchSize: Int): Mono<List<UsageEvent>> {
        val list = redissonReactive.getList<String>(EVENTS_LIST_KEY)
        val sample = Timer.start()
        
        return resilienceService.applyRedisResilience(
            // Use range() to get all events in a single LRANGE call (optimized from N calls to 1)
            // range() returns Mono<List<String>>, so we flatMap to process each element
            list.range(0, batchSize - 1)
                .flatMapMany { jsonList: List<String> ->
                    Flux.fromIterable(jsonList)
                        .mapNotNull { json: String ->
                            try {
                                objectMapper.readValue(json, UsageEvent::class.java)
                            } catch (e: Exception) {
                                log.warn("Error deserializing event JSON: $json", e)
                                null
                            }
                        }
                }
                .collectList()
                .map { it.filterNotNull() }
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
                Mono.just<List<UsageEvent>>(emptyList())
            }
    }
    
    /**
     * Remove processed events from Redis
     * Used after successful batch persistence to Postgres
     * Since we read events with range(0, batchSize-1), we remove them by value (FIFO)
     * Note: This still makes N calls, but it's simpler and more reliable than trying to use removeAt
     * Future optimization: Use Lua script for atomic batch removal
     */
    fun removeEvents(events: List<UsageEvent>): Mono<Void> {
        return if (events.isEmpty()) {
            Mono.empty()
        } else {
            val list = redissonReactive.getList<String>(EVENTS_LIST_KEY)
            
            // Serialize events to JSON for removal
            val eventJsonList = events.mapNotNull { event ->
                try {
                    objectMapper.writeValueAsString(event)
                } catch (e: Exception) {
                    log.warn("Error serializing event for removal: ${event.eventId}", e)
                    null
                }
            }
            
            // Remove matching JSON strings from list (FIFO - first match is removed)
            resilienceService.applyRedisResilience(
                Flux.fromIterable(eventJsonList)
                    .flatMap { json ->
                        list.remove(json)
                            .onErrorResume { Mono.just(false) } // Ignore errors
                    }
                    .then()
            )
                .doOnError { error: Throwable ->
                    log.error("Error removing events from Redis", error)
                }
        }
    }
}

