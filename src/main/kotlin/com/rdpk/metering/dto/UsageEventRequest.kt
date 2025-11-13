package com.rdpk.metering.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.Instant

/**
 * Request DTO for event ingestion endpoint
 * Matches the exact format specified in requirements
 */
@Schema(description = "Request for ingesting a metering event")
data class UsageEventRequest(
    @field:NotNull(message = "eventId is required")
    @field:NotBlank(message = "eventId cannot be blank")
    @Schema(description = "Unique event identifier", required = true, example = "event-123")
    @JsonProperty("eventId")
    val eventId: String,
    
    @field:NotNull(message = "timestamp is required")
    @Schema(description = "Event timestamp in ISO-8601 format", required = true, example = "2024-01-01T00:00:00Z")
    @JsonProperty("timestamp")
    val timestamp: Instant,
    
    @field:NotNull(message = "tenantId is required")
    @field:NotBlank(message = "tenantId cannot be blank")
    @Schema(description = "Tenant identifier", required = true, example = "1")
    @JsonProperty("tenantId")
    val tenantId: String,
    
    @field:NotNull(message = "customerId is required")
    @field:NotBlank(message = "customerId cannot be blank")
    @Schema(description = "Customer identifier (external ID)", required = true, example = "customer-1")
    @JsonProperty("customerId")
    val customerId: String,
    
    @field:NotNull(message = "apiEndpoint is required")
    @field:NotBlank(message = "apiEndpoint cannot be blank")
    @Schema(description = "API endpoint that was called", required = true, example = "/api/completion")
    @JsonProperty("apiEndpoint")
    val apiEndpoint: String,
    
    @field:Valid
    @field:NotNull(message = "metadata is required")
    @Schema(description = "Event metadata containing token and performance information", required = true)
    @JsonProperty("metadata")
    val metadata: EventMetadata
)

/**
 * Metadata for the usage event
 * Matches the exact format specified in requirements
 * 
 * Required fields: inputTokens, outputTokens (for accurate token tracking and aggregation)
 * Optional fields: tokens (fallback), model, latencyMs
 */
@Schema(description = "Event metadata containing token usage and performance metrics")
data class EventMetadata(
    @field:NotNull(message = "inputTokens is required for token tracking")
    @Schema(description = "Number of input tokens consumed", required = true, example = "500")
    @JsonProperty("inputTokens")
    val inputTokens: Int,
    
    @field:NotNull(message = "outputTokens is required for token tracking")
    @Schema(description = "Number of output tokens generated", required = true, example = "1000")
    @JsonProperty("outputTokens")
    val outputTokens: Int,
    
    @Schema(description = "Total tokens (fallback if inputTokens/outputTokens not available)", required = false, example = "1500")
    @JsonProperty("tokens")
    val tokens: Int? = null,
    
    @Schema(description = "Model name used for the request", required = false, example = "gpt-4")
    @JsonProperty("model")
    val model: String? = null,
    
    @Schema(description = "Request latency in milliseconds", required = false, example = "234")
    @JsonProperty("latencyMs")
    val latencyMs: Int? = null
)

