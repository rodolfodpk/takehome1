package com.rdpk.metering.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Clock
import java.time.LocalDateTime

@Table("tenants")
data class Tenant(
    @Id
    @Column("id")
    val id: Long? = null,
    
    @Column("name")
    val name: String,
    
    @Column("active")
    val active: Boolean = true,
    
    @Column("created")
    val created: LocalDateTime,
    
    @Column("updated")
    val updated: LocalDateTime
) {
    fun withId(id: Long): Tenant = copy(id = id)
    fun withUpdated(updated: LocalDateTime): Tenant = copy(updated = updated)
    fun deactivate(clock: Clock = Clock.systemUTC()): Tenant = copy(active = false, updated = LocalDateTime.now(clock))
    fun activate(clock: Clock = Clock.systemUTC()): Tenant = copy(active = true, updated = LocalDateTime.now(clock))
}

