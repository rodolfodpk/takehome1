package com.rdpk.metering.util

import reactor.core.publisher.Mono
import reactor.util.context.Context

/**
 * Utility object for accessing tenant context in reactive chains
 */
object TenantContext {
    
    const val TENANT_ID_KEY = "tenantId"
    
    /**
     * Get tenant ID from Reactor Context
     * Returns empty Mono if tenant ID is not in context
     */
    fun getTenantId(): Mono<String> {
        return Mono.deferContextual { ctx ->
            val tenantId = ctx.getOrEmpty<String>(TENANT_ID_KEY)
            if (tenantId.isPresent) {
                Mono.just(tenantId.get())
            } else {
                Mono.empty()
            }
        }
    }
    
    /**
     * Get tenant ID from Reactor Context or throw exception
     */
    fun requireTenantId(): Mono<String> {
        return getTenantId()
            .switchIfEmpty(
                Mono.error(IllegalStateException("Tenant ID not found in context. Ensure X-Tenant-Id header is set."))
            )
    }
    
    /**
     * Execute operation with tenant ID in context
     */
    fun <T> withTenant(tenantId: String, operation: Mono<T>): Mono<T> {
        return operation.contextWrite(Context.of(TENANT_ID_KEY, tenantId))
    }
}

