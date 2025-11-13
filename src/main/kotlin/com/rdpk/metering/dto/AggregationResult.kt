package com.rdpk.metering.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Aggregation result matching the exact format from requirements
 */
data class AggregationResult(
    @JsonProperty("totalCalls")
    val totalCalls: Long,
    
    @JsonProperty("totalTokens")
    val totalTokens: Long,
    
    @JsonProperty("totalInputTokens")
    val totalInputTokens: Long,
    
    @JsonProperty("totalOutputTokens")
    val totalOutputTokens: Long,
    
    @JsonProperty("avgLatencyMs")
    val avgLatencyMs: Double?,
    
    @JsonProperty("byEndpoint")
    val byEndpoint: Map<String, EndpointStats>,
    
    @JsonProperty("byModel")
    val byModel: Map<String, ModelStats>
)

data class EndpointStats(
    @JsonProperty("calls")
    val calls: Long,
    
    @JsonProperty("tokens")
    val tokens: Long
)

data class ModelStats(
    @JsonProperty("calls")
    val calls: Long,
    
    @JsonProperty("tokens")
    val tokens: Long
)

