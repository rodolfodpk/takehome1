package com.rdpk.metering.repository

import com.rdpk.metering.domain.AggregationWindow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

@Repository
interface AggregationWindowRepository : ReactiveCrudRepository<AggregationWindow, Long> {
    
    /**
     * Find aggregation windows by tenant and customer
     */
    fun findByTenantIdAndCustomerId(tenantId: Long, customerId: Long): Flux<AggregationWindow>
    
    /**
     * Find aggregation window by tenant, customer, and window start (unique constraint)
     */
    fun findByTenantIdAndCustomerIdAndWindowStart(
        tenantId: Long,
        customerId: Long,
        windowStart: Instant
    ): Mono<AggregationWindow>
    
    /**
     * Find aggregation windows by tenant and time range
     */
    @Query("""
        SELECT * FROM aggregation_windows 
        WHERE tenant_id = :tenantId 
        AND window_start >= :start 
        AND window_end <= :end
        ORDER BY window_start DESC
    """)
    fun findByTenantIdAndWindowStartBetween(
        tenantId: Long,
        start: Instant,
        end: Instant
    ): Flux<AggregationWindow>
    
    /**
     * Find aggregation windows by customer and time range
     */
    @Query("""
        SELECT * FROM aggregation_windows 
        WHERE customer_id = :customerId 
        AND window_start >= :start 
        AND window_end <= :end
        ORDER BY window_start DESC
    """)
    fun findByCustomerIdAndWindowStartBetween(
        customerId: Long,
        start: Instant,
        end: Instant
    ): Flux<AggregationWindow>
    
    /**
     * Find aggregation windows by tenant, customer, and time range
     */
    @Query("""
        SELECT * FROM aggregation_windows 
        WHERE tenant_id = :tenantId 
        AND customer_id = :customerId 
        AND window_start >= :start 
        AND window_end <= :end
        ORDER BY window_start DESC
    """)
    fun findByTenantIdAndCustomerIdAndWindowStartBetween(
        tenantId: Long,
        customerId: Long,
        start: Instant,
        end: Instant
    ): Flux<AggregationWindow>
}

