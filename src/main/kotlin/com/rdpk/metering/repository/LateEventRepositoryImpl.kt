package com.rdpk.metering.repository

import com.rdpk.metering.domain.LateEvent
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.Instant

/**
 * Custom implementation for LateEventRepository to handle JSONB properly
 * Uses DatabaseClient directly with PostgreSQL's to_jsonb() function for proper JSONB conversion
 * Spring Data R2DBC will automatically wire this as the implementation (postfix "Impl")
 */
@Repository
class LateEventRepositoryImpl(
    private val databaseClient: DatabaseClient
) {
    
    /**
     * Save late event with explicit JSONB casting for data field
     * Uses PostgreSQL's to_jsonb() function to convert String to JSONB
     * This is more reliable than trying to bind Json codec directly via R2DBC
     */
    fun saveWithJsonb(
        eventId: String,
        originalTimestamp: Instant,
        receivedTimestamp: Instant,
        tenantId: Long,
        customerId: Long,
        data: String
    ): Mono<LateEvent> {
        // Use to_jsonb() PostgreSQL function to convert String to JSONB
        // This is more reliable than trying to bind Json codec directly
        // The JSON string must be valid JSON (validated in LateEventService.serializeEvent)
        return databaseClient.sql("""
            INSERT INTO late_events (event_id, original_timestamp, received_timestamp, tenant_id, customer_id, data)
            VALUES (:eventId, :originalTimestamp, :receivedTimestamp, :tenantId, :customerId, to_jsonb(:data::text))
            ON CONFLICT (event_id) 
            DO UPDATE SET 
                original_timestamp = EXCLUDED.original_timestamp,
                received_timestamp = EXCLUDED.received_timestamp,
                tenant_id = EXCLUDED.tenant_id,
                customer_id = EXCLUDED.customer_id,
                data = EXCLUDED.data
            RETURNING id, event_id, original_timestamp, received_timestamp, tenant_id, customer_id, data::text as data
        """)
            .bind("eventId", eventId)
            .bind("originalTimestamp", originalTimestamp)
            .bind("receivedTimestamp", receivedTimestamp)
            .bind("tenantId", tenantId)
            .bind("customerId", customerId)
            .bind("data", data) // Bind as String, PostgreSQL converts to JSONB via to_jsonb()
            .map { row, metadata ->
                LateEvent(
                    id = row.get("id", Long::class.java),
                    eventId = row.get("event_id", String::class.java)!!,
                    originalTimestamp = row.get("original_timestamp", Instant::class.java)!!,
                    receivedTimestamp = row.get("received_timestamp", Instant::class.java)!!,
                    tenantId = row.get("tenant_id", Long::class.java)!!,
                    customerId = row.get("customer_id", Long::class.java)!!,
                    data = row.get("data", String::class.java)!!
                )
            }
            .one()
    }
}

