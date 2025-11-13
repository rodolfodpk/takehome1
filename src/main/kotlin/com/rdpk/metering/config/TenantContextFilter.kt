package com.rdpk.metering.config

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.util.context.Context

/**
 * WebFilter to extract tenant ID from X-Tenant-Id header
 * and store it in Reactor Context for downstream reactive operations
 */
@Component
class TenantContextFilter : WebFilter {
    
    private val log = LoggerFactory.getLogger(javaClass)
    
    companion object {
        const val TENANT_ID_HEADER = "X-Tenant-Id"
        const val TENANT_ID_CONTEXT_KEY = "tenantId"
    }
    
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val tenantId = exchange.request.headers.getFirst(TENANT_ID_HEADER)
        
        return if (tenantId != null) {
            // Store tenant ID in Reactor Context
            chain.filter(exchange)
                .contextWrite { ctx ->
                    ctx.put(TENANT_ID_CONTEXT_KEY, tenantId)
                }
        } else {
            // No tenant header - continue without context
            // Event processing will validate tenant from request body
            chain.filter(exchange)
        }
    }
}

