package com.rdpk.metering.repository

import com.rdpk.metering.domain.Customer
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface CustomerRepository : ReactiveCrudRepository<Customer, Long> {
    fun findByTenantId(tenantId: Long): Flux<Customer>
    fun findByTenantIdAndExternalId(tenantId: Long, externalId: String): Mono<Customer>
}

