package com.rdpk.metering.repository

import com.rdpk.metering.domain.Customer
import com.rdpk.metering.dto.TenantCustomerPair
import com.rdpk.metering.dto.ValidationResult
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface CustomerRepository : ReactiveCrudRepository<Customer, Long> {
    fun findByTenantId(tenantId: Long): Flux<Customer>
    fun findByTenantIdAndExternalId(tenantId: Long, externalId: String): Mono<Customer>
    
    /**
     * Find customer with active tenant in a single JOIN query
     * Optimizes hot path: reduces from 2 queries to 1
     * Columns are aliased to avoid conflicts (t_ for tenant, c_ for customer)
     */
    @Query("""
        SELECT 
            t.id as t_id, t.name as t_name, t.active as t_active, 
            t.created as t_created, t.updated as t_updated,
            c.id as c_id, c.tenant_id as c_tenant_id, c.external_id as c_external_id,
            c.name as c_name, c.created as c_created, c.updated as c_updated
        FROM customers c
        INNER JOIN tenants t ON c.tenant_id = t.id
        WHERE t.id = :tenantId 
        AND c.external_id = :customerId
        AND t.active = true
    """)
    fun findCustomerWithActiveTenant(tenantId: Long, customerId: String): Mono<ValidationResult>
    
    /**
     * Find all active tenants with their customers in a single JOIN query
     * Optimizes background scheduler: reduces from N+1 queries to 1
     * Columns are aliased to avoid conflicts (t_ for tenant, c_ for customer)
     */
    @Query("""
        SELECT 
            t.id as t_id, t.name as t_name, t.active as t_active, 
            t.created as t_created, t.updated as t_updated,
            c.id as c_id, c.tenant_id as c_tenant_id, c.external_id as c_external_id,
            c.name as c_name, c.created as c_created, c.updated as c_updated
        FROM customers c
        INNER JOIN tenants t ON c.tenant_id = t.id
        WHERE t.active = true
        ORDER BY t.id, c.id
    """)
    fun findAllActiveTenantsWithCustomers(): Flux<TenantCustomerPair>
}

