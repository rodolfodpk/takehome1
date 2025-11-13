package com.rdpk.metering.repository

import com.rdpk.metering.domain.LateEvent
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

@Repository
interface LateEventRepository : ReactiveCrudRepository<LateEvent, Long> {
    
    /**
     * Find late event by event ID (unique)
     */
    fun findByEventId(eventId: String): Mono<LateEvent>
    
    /**
     * Find late events by tenant and customer
     */
    fun findByTenantIdAndCustomerId(tenantId: Long, customerId: Long): Flux<LateEvent>
    
    /**
     * Find late events by tenant and original timestamp range
     */
    @Query("""
        SELECT * FROM late_events 
        WHERE tenant_id = :tenantId 
        AND original_timestamp >= :start 
        AND original_timestamp <= :end
        ORDER BY original_timestamp DESC
    """)
    fun findByTenantIdAndOriginalTimestampBetween(
        tenantId: Long,
        start: Instant,
        end: Instant
    ): Flux<LateEvent>
    
    /**
     * Find late events by tenant, customer, and original timestamp range
     */
    @Query("""
        SELECT * FROM late_events 
        WHERE tenant_id = :tenantId 
        AND customer_id = :customerId 
        AND original_timestamp >= :start 
        AND original_timestamp <= :end
        ORDER BY original_timestamp DESC
    """)
    fun findByTenantIdAndCustomerIdAndOriginalTimestampBetween(
        tenantId: Long,
        customerId: Long,
        start: Instant,
        end: Instant
    ): Flux<LateEvent>
    
}

