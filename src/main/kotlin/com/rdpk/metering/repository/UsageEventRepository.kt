package com.rdpk.metering.repository

import com.rdpk.metering.domain.UsageEvent
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

@Repository
interface UsageEventRepository : ReactiveCrudRepository<UsageEvent, Long> {
    
    /**
     * Find usage events by tenant, customer, and time range
     * Required by requirements: "Querying usage events by time range and customer"
     * Uses explicit JSONB casting to handle Map<String, Any> conversion
     */
    @Query("""
        SELECT id, event_id, tenant_id, customer_id, timestamp, 
               data::text as data, created, updated
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
}

