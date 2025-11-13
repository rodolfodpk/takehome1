package com.rdpk.metering.repository

import com.rdpk.metering.domain.AggregationWindow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.Instant
import java.time.LocalDateTime

@Repository
interface AggregationWindowRepository : ReactiveCrudRepository<AggregationWindow, Long> {
    
    /**
     * Find aggregation window by tenant, customer, and window start (unique constraint)
     * Required by requirements: "Finding aggregation windows for reporting"
     */
    fun findByTenantIdAndCustomerIdAndWindowStart(
        tenantId: Long,
        customerId: Long,
        windowStart: Instant
    ): Mono<AggregationWindow>
    
    /**
     * Save aggregation window with explicit JSONB casting for aggregation_data
     * Uses explicit SQL casting to handle String -> JSONB conversion
     */
    @Query("""
        INSERT INTO aggregation_windows (tenant_id, customer_id, window_start, window_end, aggregation_data, created, updated)
        VALUES (:tenantId, :customerId, :windowStart, :windowEnd, :aggregationData::jsonb, :created, :updated)
        ON CONFLICT (tenant_id, customer_id, window_start) 
        DO UPDATE SET 
            aggregation_data = EXCLUDED.aggregation_data,
            window_end = EXCLUDED.window_end,
            updated = EXCLUDED.updated
        RETURNING id, tenant_id, customer_id, window_start, window_end, aggregation_data::text as aggregation_data, created, updated
    """)
    fun saveWithJsonb(
        tenantId: Long,
        customerId: Long,
        windowStart: Instant,
        windowEnd: Instant,
        aggregationData: String,
        created: LocalDateTime,
        updated: LocalDateTime
    ): Mono<AggregationWindow>
}

