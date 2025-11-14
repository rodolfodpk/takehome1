package com.rdpk.metering.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.rdpk.metering.domain.UsageEvent
import com.rdpk.metering.dto.EventMetadata
import com.rdpk.metering.dto.UsageEventRequest
import com.rdpk.metering.dto.UsageEventResponse
import com.rdpk.metering.config.EventMetrics
import com.rdpk.metering.config.ResilienceService
import com.rdpk.metering.dto.ValidationResult
import com.rdpk.metering.repository.CustomerRepository
import io.micrometer.core.instrument.Timer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime

/**
 * Service for processing usage events
 * Handles validation, Redis state updates, and event persistence
 * Optimized for 10,000+ events/second throughput
 */
@Service
class EventProcessingService(
    private val customerRepository: CustomerRepository,
    private val redisEventStorageService: RedisEventStorageService,
    private val redisStateService: RedisStateService,
    private val lateEventService: LateEventService,
    private val objectMapper: ObjectMapper,
    private val resilienceService: ResilienceService,
    private val eventMetrics: EventMetrics,
    private val clock: Clock
) {
    
    private val log = LoggerFactory.getLogger(javaClass)
    
    /**
     * Process a usage event
     * Hot path: Validates, updates Redis, stores in Redis buffer
     * Returns immediately - DB persistence happens in background
     */
    fun processEvent(request: UsageEventRequest): Mono<UsageEventResponse> {
        val sample = Timer.start()
        
        return validateTenantAndCustomer(request.tenantId, request.customerId)
            .flatMap { validationResult ->
                val tenant = validationResult.toTenant()
                val customer = validationResult.toCustomer()
                // Convert request to domain entity
                val event = toUsageEvent(request, customer.id!!)
                
                // Record metrics
                eventMetrics.eventsIngested.increment()
                eventMetrics.eventsIngestedByTenant(event.tenantId).increment()
                eventMetrics.eventsIngestedByCustomer(event.tenantId, event.customerId).increment()
                
                // Check if event is late
                lateEventService.checkAndHandleLateEvent(event)
                    .flatMap { isLate ->
                        if (isLate) {
                            // Event stored as late event, return success
                            Mono.just(toResponse(request))
                        } else {
                            // Normal processing: Hot path - Store in Redis immediately
                            redisEventStorageService.storeEvent(event)
                                .then(redisStateService.updateCounters(event))
                                .then(Mono.just(toResponse(request)))
                        }
                    }
            }
            .doOnSuccess {
                sample.stop(eventMetrics.eventsProcessedLatency)
            }
            .doOnError { error ->
                sample.stop(eventMetrics.eventsProcessedLatency)
                eventMetrics.eventsIngestionErrors.increment()
                log.error("Error processing event: ${request.eventId}", error)
            }
            .onErrorResume { error ->
                // Store in DLQ for retry
                handleError(request, error)
            }
    }
    
    /**
     * Validate tenant and customer in a single JOIN query
     * Optimizes hot path: reduces from 2 DB queries to 1
     */
    private fun validateTenantAndCustomer(tenantId: String, customerId: String): Mono<ValidationResult> {
        val tenantIdLong = tenantId.toLongOrNull() 
            ?: return Mono.error(IllegalArgumentException("Invalid tenantId: $tenantId"))
        
        return resilienceService.applyPostgresResilience(
            customerRepository.findCustomerWithActiveTenant(tenantIdLong, customerId)
                .switchIfEmpty(
                    Mono.error(IllegalArgumentException("Customer not found: $customerId for tenant: $tenantId, or tenant is not active"))
                )
        )
    }
    
    private fun toUsageEvent(request: UsageEventRequest, customerId: Long): UsageEvent {
        val now = LocalDateTime.now(clock)
        // Build data JSONB containing all event fields
        // inputTokens and outputTokens are required (validated at DTO level)
        val data = buildMap<String, Any> {
            put("endpoint", request.apiEndpoint)
            put("inputTokens", request.metadata.inputTokens)
            put("outputTokens", request.metadata.outputTokens)
            // Optional fields
            if (request.metadata.tokens != null) put("tokens", request.metadata.tokens)
            if (request.metadata.model != null) put("model", request.metadata.model)
            if (request.metadata.latencyMs != null) put("latencyMs", request.metadata.latencyMs)
        }
        return UsageEvent(
            eventId = request.eventId,
            tenantId = request.tenantId.toLongOrNull() ?: throw IllegalArgumentException("Invalid tenantId"),
            customerId = customerId,
            timestamp = request.timestamp,
            data = data,
            created = now,
            updated = now
        )
    }
    
    private fun toResponse(request: UsageEventRequest): UsageEventResponse {
        return UsageEventResponse(
            eventId = request.eventId,
            status = "PROCESSED",
            processedAt = clock.instant()
        )
    }
    
    private fun handleError(request: UsageEventRequest, error: Throwable): Mono<UsageEventResponse> {
        // TODO: Store in DLQ for retry (Phase 5)
        log.error("Failed to process event: ${request.eventId}", error)
        return Mono.error(error)
    }
}

