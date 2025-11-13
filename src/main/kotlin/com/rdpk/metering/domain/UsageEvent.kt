package com.rdpk.metering.domain

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.ReadOnlyProperty
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.time.LocalDateTime

@Table("usage_events")
data class UsageEvent(
    @Id
    @Column("id")
    val id: Long? = null,
    
    @Column("event_id")
    val eventId: String,
    
    @Column("tenant_id")
    val tenantId: Long,
    
    @Column("customer_id")
    val customerId: Long,
    
    @Column("timestamp")
    val timestamp: Instant,
    
    @Column("endpoint")
    val endpoint: String,
    
    @Column("tokens")
    val tokens: Int? = null,
    
    @Column("model")
    val model: String? = null,
    
    @Column("latency_ms")
    val latencyMs: Int? = null,
    
    @Column("metadata")
    val metadata: Map<String, Any>? = null, // JSONB - handled via custom repository methods and @Query with ::text casting
    
    @ReadOnlyProperty
    @Column("created")
    val created: LocalDateTime? = null,
    
    @ReadOnlyProperty
    @Column("updated")
    val updated: LocalDateTime? = null
) {
    fun withId(id: Long): UsageEvent = copy(id = id)
}

