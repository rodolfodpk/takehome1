package com.rdpk.metering.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.time.LocalDateTime

@Table("aggregation_windows")
data class AggregationWindow(
    @Id
    @Column("id")
    val id: Long? = null,
    
    @Column("tenant_id")
    val tenantId: Long,
    
    @Column("customer_id")
    val customerId: Long,
    
    @Column("window_start")
    val windowStart: Instant,
    
    @Column("window_end")
    val windowEnd: Instant,
    
    @Column("total_calls")
    val totalCalls: Long,
    
    @Column("total_tokens")
    val totalTokens: Long,
    
    @Column("avg_latency_ms")
    val avgLatencyMs: Double? = null,
    
    @Column("aggregation_data")
    val aggregationData: String, // JSONB as String
    
    @Column("created")
    val created: LocalDateTime? = null,
    
    @Column("updated")
    val updated: LocalDateTime? = null
) {
    fun withId(id: Long): AggregationWindow = copy(id = id)
}

