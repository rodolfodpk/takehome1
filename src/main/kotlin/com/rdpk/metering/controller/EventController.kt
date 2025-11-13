package com.rdpk.metering.controller

import com.rdpk.metering.dto.UsageEventRequest
import com.rdpk.metering.dto.UsageEventResponse
import com.rdpk.metering.service.EventProcessingService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

/**
 * REST controller for event ingestion
 * Handles 10,000+ events/second per instance
 */
@RestController
@RequestMapping("/api/v1/events")
@Tag(name = "Events", description = "Event ingestion API")
class EventController(
    private val eventProcessingService: EventProcessingService
) {
    
    @PostMapping
    @Operation(
        summary = "Ingest a metering event",
        description = "Accepts a metering event and processes it asynchronously. Returns immediately after validation."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Event processed successfully"),
            ApiResponse(responseCode = "400", description = "Invalid event data"),
            ApiResponse(responseCode = "404", description = "Tenant or customer not found"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    fun ingestEvent(
        @Valid @RequestBody request: Mono<UsageEventRequest>
    ): Mono<ResponseEntity<UsageEventResponse>> {
        return request
            .flatMap { eventProcessingService.processEvent(it) }
            .map { response ->
                ResponseEntity.status(HttpStatus.CREATED).body(response)
            }
    }
}

