package com.rdpk.metering.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

/**
 * Response DTO for event ingestion endpoint
 */
data class UsageEventResponse(
    @JsonProperty("eventId")
    val eventId: String,
    
    @JsonProperty("status")
    val status: String,
    
    @JsonProperty("processedAt")
    val processedAt: Instant
)

