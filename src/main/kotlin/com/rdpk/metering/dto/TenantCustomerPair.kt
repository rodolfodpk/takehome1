package com.rdpk.metering.dto

import com.rdpk.metering.domain.Customer
import com.rdpk.metering.domain.Tenant
import org.springframework.data.relational.core.mapping.Column
import java.time.LocalDateTime

/**
 * DTO for JOIN query result containing both Tenant and Customer
 * Used to optimize AggregationScheduler: eliminates N+1 queries
 */
data class TenantCustomerPair(
    // Tenant fields (aliased with t_ prefix in query)
    @Column("t_id")
    val tenantId: Long,
    
    @Column("t_name")
    val tenantName: String,
    
    @Column("t_active")
    val tenantActive: Boolean,
    
    @Column("t_created")
    val tenantCreated: LocalDateTime,
    
    @Column("t_updated")
    val tenantUpdated: LocalDateTime,
    
    // Customer fields (aliased with c_ prefix in query)
    @Column("c_id")
    val customerId: Long,
    
    @Column("c_tenant_id")
    val customerTenantId: Long,
    
    @Column("c_external_id")
    val customerExternalId: String,
    
    @Column("c_name")
    val customerName: String,
    
    @Column("c_created")
    val customerCreated: LocalDateTime,
    
    @Column("c_updated")
    val customerUpdated: LocalDateTime
) {
    /**
     * Convert to Tenant entity
     */
    fun toTenant(): Tenant {
        return Tenant(
            id = tenantId,
            name = tenantName,
            active = tenantActive,
            created = tenantCreated,
            updated = tenantUpdated
        )
    }
    
    /**
     * Convert to Customer entity
     */
    fun toCustomer(): Customer {
        return Customer(
            id = customerId,
            tenantId = customerTenantId,
            externalId = customerExternalId,
            name = customerName,
            created = customerCreated,
            updated = customerUpdated
        )
    }
}

