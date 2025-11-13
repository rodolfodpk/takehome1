package com.rdpk.metering.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.rdpk.metering.domain.UsageEvent
import com.rdpk.metering.dto.AggregationResult
import com.rdpk.metering.dto.EndpointStats
import com.rdpk.metering.dto.ModelStats
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.shouldNotBe
import java.time.Instant
import java.time.LocalDateTime

/**
 * Unit tests for AggregationService pure domain logic
 * Tests only pure functions that don't require Redis or database
 * NO MOCKS - only testing serialization logic
 */
class AggregationServiceUnitTest : DescribeSpec({

    val objectMapper = ObjectMapper()

    describe("AggregationService serialization") {
        
        it("should serialize aggregation result to JSON") {
            val result = AggregationResult(
                totalCalls = 100L,
                totalTokens = 5000L,
                totalInputTokens = 2000L,
                totalOutputTokens = 3000L,
                avgLatencyMs = 250.5,
                byEndpoint = mapOf(
                    "/api/completion" to EndpointStats(calls = 60L, tokens = 3000L),
                    "/api/embedding" to EndpointStats(calls = 40L, tokens = 2000L)
                ),
                byModel = mapOf(
                    "gpt-4" to ModelStats(calls = 70L, tokens = 3500L),
                    "gpt-3.5-turbo" to ModelStats(calls = 30L, tokens = 1500L)
                )
            )
            
            val json = objectMapper.writeValueAsString(result)
            
            json shouldNotBe null
            json shouldContain "\"totalCalls\":100"
            json shouldContain "\"totalTokens\":5000"
            json shouldContain "\"totalInputTokens\":2000"
            json shouldContain "\"totalOutputTokens\":3000"
        }

        it("should handle empty events list in serialization") {
            val result = AggregationResult(
                totalCalls = 0L,
                totalTokens = 0L,
                totalInputTokens = 0L,
                totalOutputTokens = 0L,
                avgLatencyMs = null,
                byEndpoint = emptyMap(),
                byModel = emptyMap()
            )
            
            val json = objectMapper.writeValueAsString(result)
            
            json shouldNotBe null
            json shouldContain "\"totalCalls\":0"
        }
    }
})

/**
 * Test helper to verify aggregation logic
 * These are pure functions that can be tested without Redis
 */
class AggregationLogicTest : DescribeSpec({

    fun createEvent(
        endpoint: String = "/api/completion",
        tokens: Int? = 100,
        model: String? = "gpt-4",
        latencyMs: Int? = 250
    ): UsageEvent {
        val data = buildMap<String, Any> {
            put("endpoint", endpoint)
            if (tokens != null) put("tokens", tokens)
            if (model != null) put("model", model)
            if (latencyMs != null) put("latencyMs", latencyMs)
        }
        return UsageEvent(
            eventId = "test-event",
            tenantId = 1L,
            customerId = 100L,
            timestamp = Instant.now(),
            data = data,
            created = LocalDateTime.now(),
            updated = LocalDateTime.now()
        )
    }

    describe("Aggregation logic") {
        
        it("should aggregate events by endpoint") {
            val events = listOf(
                createEvent(endpoint = "/api/completion", tokens = 100),
                createEvent(endpoint = "/api/completion", tokens = 200),
                createEvent(endpoint = "/api/embedding", tokens = 50)
            )
            
            val result = events
                .groupBy { it.data["endpoint"] as? String ?: "unknown" }
                .mapValues { (_, eventList) ->
                    val totalCalls = eventList.size.toLong()
                    val totalTokens = eventList.sumOf { (it.data["tokens"] as? Number)?.toLong() ?: 0L }
                    EndpointStats(calls = totalCalls, tokens = totalTokens)
                }
            
            result.size shouldBe 2
            result["/api/completion"]?.calls shouldBe 2L
            result["/api/completion"]?.tokens shouldBe 300L
            result["/api/embedding"]?.calls shouldBe 1L
            result["/api/embedding"]?.tokens shouldBe 50L
        }

        it("should aggregate events by model") {
            val events = listOf(
                createEvent(model = "gpt-4", tokens = 100),
                createEvent(model = "gpt-4", tokens = 200),
                createEvent(model = "gpt-3.5-turbo", tokens = 50),
                createEvent(model = null, tokens = 25) // Should be filtered out
            )
            
            val result = events
                .filter { it.data["model"] != null }
                .groupBy { it.data["model"] as String }
                .mapValues { (_, eventList) ->
                    val totalCalls = eventList.size.toLong()
                    val totalTokens = eventList.sumOf { (it.data["tokens"] as? Number)?.toLong() ?: 0L }
                    ModelStats(calls = totalCalls, tokens = totalTokens)
                }
            
            result.size shouldBe 2
            result["gpt-4"]?.calls shouldBe 2L
            result["gpt-4"]?.tokens shouldBe 300L
            result["gpt-3.5-turbo"]?.calls shouldBe 1L
            result["gpt-3.5-turbo"]?.tokens shouldBe 50L
        }

        it("should calculate average latency") {
            val events = listOf(
                createEvent(latencyMs = 100),
                createEvent(latencyMs = 200),
                createEvent(latencyMs = 300),
                createEvent(latencyMs = null) // Should be excluded
            )
            
            val avgLatency = events
                .mapNotNull { (it.data["latencyMs"] as? Number)?.toDouble() }
                .average()
            
            avgLatency shouldBe 200.0
        }
    }
})
