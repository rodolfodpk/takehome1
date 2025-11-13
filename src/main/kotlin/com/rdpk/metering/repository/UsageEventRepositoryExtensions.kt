package com.rdpk.metering.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.rdpk.metering.domain.UsageEvent
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Extension methods for UsageEventRepository to handle JSONB properly
 * 
 * Strategy: Use explicit SQL casting (::jsonb for writes, ::text for reads)
 * This avoids the Spring converter type matching issue with Json types
 */
interface UsageEventRepositoryExtensions {
    /**
     * Save UsageEvent with explicit JSONB casting
     * Use this instead of standard save() for entities with metadata
     */
    fun saveWithJsonb(event: UsageEvent): Mono<UsageEvent>
    
    /**
     * Batch save UsageEvents with explicit JSONB casting
     * Use this instead of standard saveAll() for entities with metadata
     */
    fun saveAllWithJsonb(events: List<UsageEvent>): Flux<UsageEvent>
}

@Repository
class UsageEventRepositoryExtensionsImpl(
    private val databaseClient: DatabaseClient,
    private val objectMapper: ObjectMapper
) : UsageEventRepositoryExtensions {
    
    override fun saveWithJsonb(event: UsageEvent): Mono<UsageEvent> {
        val metadataJson = if (event.metadata != null) {
            objectMapper.writeValueAsString(event.metadata)
        } else {
            "{}"
        }
        
        val sql = """
            INSERT INTO usage_events (event_id, tenant_id, customer_id, timestamp, endpoint, tokens, model, latency_ms, metadata)
            VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9::jsonb)
            RETURNING id, event_id, tenant_id, customer_id, timestamp, endpoint, tokens, model, latency_ms, 
                      metadata::text as metadata, created, updated
        """
        
        return databaseClient.sql(sql)
            .bind("$1", event.eventId)
            .bind("$2", event.tenantId)
            .bind("$3", event.customerId)
            .bind("$4", event.timestamp)
            .bind("$5", event.endpoint)
            .bind("$6", event.tokens)
            .bind("$7", event.model)
            .bind("$8", event.latencyMs)
            .bind("$9", metadataJson)
            .map { row, _ ->
                UsageEvent(
                    id = row.get("id", Long::class.java),
                    eventId = row.get("event_id", String::class.java)!!,
                    tenantId = row.get("tenant_id", Long::class.java)!!,
                    customerId = row.get("customer_id", Long::class.java)!!,
                    timestamp = row.get("timestamp", java.time.Instant::class.java)!!,
                    endpoint = row.get("endpoint", String::class.java)!!,
                    tokens = row.get("tokens", Int::class.java),
                    model = row.get("model", String::class.java),
                    latencyMs = row.get("latency_ms", Int::class.java),
                    metadata = parseMetadata(row.get("metadata", String::class.java)),
                    created = row.get("created", java.time.LocalDateTime::class.java),
                    updated = row.get("updated", java.time.LocalDateTime::class.java)
                )
            }
            .one()
    }
    
    override fun saveAllWithJsonb(events: List<UsageEvent>): Flux<UsageEvent> {
        if (events.isEmpty()) return Flux.empty()
        
        // Use batch insert for better performance
        val sql = """
            INSERT INTO usage_events (event_id, tenant_id, customer_id, timestamp, endpoint, tokens, model, latency_ms, metadata)
            VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9::jsonb)
            RETURNING id, event_id, tenant_id, customer_id, timestamp, endpoint, tokens, model, latency_ms, 
                      metadata::text as metadata, created, updated
        """
        
        return Flux.fromIterable(events)
            .flatMap { event ->
                val metadataJson = if (event.metadata != null) {
                    objectMapper.writeValueAsString(event.metadata)
                } else {
                    "{}"
                }
                
                databaseClient.sql(sql)
                    .bind("$1", event.eventId)
                    .bind("$2", event.tenantId)
                    .bind("$3", event.customerId)
                    .bind("$4", event.timestamp)
                    .bind("$5", event.endpoint)
                    .bind("$6", event.tokens)
                    .bind("$7", event.model)
                    .bind("$8", event.latencyMs)
                    .bind("$9", metadataJson)
                    .map { row, _ ->
                        UsageEvent(
                            id = row.get("id", Long::class.java),
                            eventId = row.get("event_id", String::class.java)!!,
                            tenantId = row.get("tenant_id", Long::class.java)!!,
                            customerId = row.get("customer_id", Long::class.java)!!,
                            timestamp = row.get("timestamp", java.time.Instant::class.java)!!,
                            endpoint = row.get("endpoint", String::class.java)!!,
                            tokens = row.get("tokens", Int::class.java),
                            model = row.get("model", String::class.java),
                            latencyMs = row.get("latency_ms", Int::class.java),
                            metadata = parseMetadata(row.get("metadata", String::class.java)),
                            created = row.get("created", java.time.LocalDateTime::class.java),
                            updated = row.get("updated", java.time.LocalDateTime::class.java)
                        )
                    }
                    .one()
            }
    }
    
    @Suppress("UNCHECKED_CAST")
    private fun parseMetadata(jsonString: String?): Map<String, Any>? {
        if (jsonString == null || jsonString.isBlank()) return null
        return try {
            objectMapper.readValue(jsonString, Map::class.java) as Map<String, Any>
        } catch (e: Exception) {
            null
        }
    }
}

