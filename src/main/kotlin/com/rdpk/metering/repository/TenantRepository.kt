package com.rdpk.metering.repository

import com.rdpk.metering.domain.Tenant
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TenantRepository : ReactiveCrudRepository<Tenant, Long> {
    // No custom methods needed - only used in tests via ReactiveCrudRepository methods (save, findById, etc.)
}

