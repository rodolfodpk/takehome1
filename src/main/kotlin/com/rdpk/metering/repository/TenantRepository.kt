package com.rdpk.metering.repository

import com.rdpk.metering.domain.Tenant
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface TenantRepository : ReactiveCrudRepository<Tenant, Long> {
    fun findByName(name: String): Mono<Tenant>
    fun findByActive(active: Boolean): reactor.core.publisher.Flux<Tenant>
}

