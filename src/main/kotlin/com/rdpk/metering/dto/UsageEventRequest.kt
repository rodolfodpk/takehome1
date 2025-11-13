package com.rdpk.metering.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.Instant

/**
 * Request DTO for event ingestion endpoint
 * Matches the exact format specified in requirements
 */
data class UsageEventRequest(
    @field:NotNull(message = "eventId is required")
    @field:NotBlank(message = "eventId cannot be blank")
    @JsonProperty("eventId")
    val eventId: String,
    
    @field:NotNull(message = "timestamp is required")
    @JsonProperty("timestamp")
    val timestamp: Instant,
    
    @field:NotNull(message = "tenantId is required")
    @field:NotBlank(message = "tenantId cannot be blank")
    @JsonProperty("tenantId")
    val tenantId: String,
    
    @field:NotNull(message = "customerId is required")
    @field:NotBlank(message = "customerId cannot be blank")
    @JsonProperty("customerId")
    val customerId: String,
    
    @field:NotNull(message = "apiEndpoint is required")
    @field:NotBlank(message = "apiEndpoint cannot be blank")
    @JsonProperty("apiEndpoint")
    val apiEndpoint: String,
    
    @field:Valid
    @field:NotNull(message = "metadata is required")
    @JsonProperty("metadata")
    val metadata: EventMetadata
)

/**
 * Metadata for the usage event
 * Matches the exact format specified in requirements
 */
data class EventMetadata(
    @JsonProperty("tokens")
    val tokens: Int? = null,
    
    @JsonProperty("model")
    val model: String? = null,
    
    @JsonProperty("latencyMs")
    val latencyMs: Int? = null,
    
    @JsonProperty("inputTokens")
    val inputTokens: Int? = null,
    
    @JsonProperty("outputTokens")
    val outputTokens: Int? = null
)

