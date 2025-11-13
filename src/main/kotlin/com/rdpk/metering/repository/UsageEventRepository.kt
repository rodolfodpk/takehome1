package com.rdpk.metering.repository

import com.rdpk.metering.domain.UsageEvent
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

@Repository
interface UsageEventRepository : ReactiveCrudRepository<UsageEvent, Long> {
    
    /**
     * Find usage events by event ID (unique)
     * Uses explicit JSONB casting to handle Map<String, Any> conversion
     */
    @Query("""
        SELECT id, event_id, tenant_id, customer_id, timestamp, endpoint, tokens, model, latency_ms, 
               metadata::text as metadata, created, updated
        FROM usage_events 
        WHERE event_id = :eventId
    """)
    fun findByEventId(eventId: String): Mono<UsageEvent>
    
    /**
     * Find usage events by tenant, customer, and time range
     * Uses explicit JSONB casting to handle Map<String, Any> conversion
     */
    @Query("""
        SELECT id, event_id, tenant_id, customer_id, timestamp, endpoint, tokens, model, latency_ms, 
               metadata::text as metadata, created, updated
        FROM usage_events 
        WHERE tenant_id = :tenantId 
        AND customer_id = :customerId 
        AND timestamp >= :start 
        AND timestamp <= :end
        ORDER BY timestamp DESC
    """)
    fun findByTenantIdAndCustomerIdAndTimestampBetween(
        tenantId: Long,
        customerId: Long,
        start: Instant,
        end: Instant
    ): Flux<UsageEvent>
    
    /**
     * Find usage events by tenant and time range
     * Uses explicit JSONB casting to handle Map<String, Any> conversion
     */
    @Query("""
        SELECT id, event_id, tenant_id, customer_id, timestamp, endpoint, tokens, model, latency_ms, 
               metadata::text as metadata, created, updated
        FROM usage_events 
        WHERE tenant_id = :tenantId 
        AND timestamp >= :start 
        AND timestamp <= :end
        ORDER BY timestamp DESC
    """)
    fun findByTenantIdAndTimestampBetween(
        tenantId: Long,
        start: Instant,
        end: Instant
    ): Flux<UsageEvent>
    
    /**
     * Find usage events by tenant and customer
     * Uses explicit JSONB casting to handle Map<String, Any> conversion
     */
    @Query("""
        SELECT id, event_id, tenant_id, customer_id, timestamp, endpoint, tokens, model, latency_ms, 
               metadata::text as metadata, created, updated
        FROM usage_events 
        WHERE tenant_id = :tenantId 
        AND customer_id = :customerId
        ORDER BY timestamp DESC
    """)
    fun findByTenantIdAndCustomerId(tenantId: Long, customerId: Long): Flux<UsageEvent>
    
    /**
     * Find usage events by customer
     * Uses explicit JSONB casting to handle Map<String, Any> conversion
     */
    @Query("""
        SELECT id, event_id, tenant_id, customer_id, timestamp, endpoint, tokens, model, latency_ms, 
               metadata::text as metadata, created, updated
        FROM usage_events 
        WHERE customer_id = :customerId
        ORDER BY timestamp DESC
    """)
    fun findByCustomerId(customerId: Long): Flux<UsageEvent>
}

