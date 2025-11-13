package com.rdpk.metering.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Clock
import java.time.LocalDateTime

@Table("customers")
data class Customer(
    @Id
    @Column("id")
    val id: Long? = null,
    
    @Column("tenant_id")
    val tenantId: Long,
    
    @Column("external_id")
    val externalId: String,
    
    @Column("name")
    val name: String,
    
    @Column("created")
    val created: LocalDateTime,
    
    @Column("updated")
    val updated: LocalDateTime
) {
    fun withId(id: Long): Customer = copy(id = id)
    fun withUpdated(updated: LocalDateTime): Customer = copy(updated = updated)
    fun withName(name: String, clock: Clock = Clock.systemUTC()): Customer = copy(name = name, updated = LocalDateTime.now(clock))
}

