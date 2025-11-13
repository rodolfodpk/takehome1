package com.rdpk.metering.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("late_events")
data class LateEvent(
    @Id
    @Column("id")
    val id: Long? = null,
    
    @Column("event_id")
    val eventId: String,
    
    @Column("original_timestamp")
    val originalTimestamp: Instant,
    
    @Column("received_timestamp")
    val receivedTimestamp: Instant,
    
    @Column("tenant_id")
    val tenantId: Long,
    
    @Column("customer_id")
    val customerId: Long,
    
    @Column("data")
    val data: String // JSONB as String
) {
    fun withId(id: Long): LateEvent = copy(id = id)
}

