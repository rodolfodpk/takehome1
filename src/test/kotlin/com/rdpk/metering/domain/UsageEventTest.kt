package com.rdpk.metering.domain

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.Instant
import java.time.LocalDateTime

/**
 * Unit tests for UsageEvent domain entity
 * Pure domain logic - no database or external dependencies
 * Using Kotest BDD style
 */
class UsageEventTest : DescribeSpec({

    describe("UsageEvent domain entity") {
        
        it("should create UsageEvent with all fields") {
            val now = LocalDateTime.now()
            val timestamp = Instant.now()
            
            val event = UsageEvent(
                eventId = "event-123",
                tenantId = 1L,
                customerId = 100L,
                timestamp = timestamp,
                endpoint = "/api/completion",
                tokens = 100,
                model = "gpt-4",
                latencyMs = 250,
                metadata = mapOf("tokens" to 100, "model" to "gpt-4", "latencyMs" to 250),
                created = now,
                updated = now
            )
            
            event.eventId shouldBe "event-123"
            event.tenantId shouldBe 1L
            event.customerId shouldBe 100L
            event.timestamp shouldBe timestamp
            event.endpoint shouldBe "/api/completion"
            event.tokens shouldBe 100
            event.model shouldBe "gpt-4"
            event.latencyMs shouldBe 250
            event.created shouldNotBe null
            event.updated shouldNotBe null
        }

        it("should create UsageEvent with withId helper") {
            val event = UsageEvent(
                eventId = "event-123",
                tenantId = 1L,
                customerId = 100L,
                timestamp = Instant.now(),
                endpoint = "/api/completion"
            )
            
            val eventWithId = event.withId(999L)
            
            eventWithId.id shouldBe 999L
            eventWithId.eventId shouldBe "event-123"
            eventWithId.tenantId shouldBe event.tenantId
        }

        it("should allow nullable optional fields") {
            val event = UsageEvent(
                eventId = "event-123",
                tenantId = 1L,
                customerId = 100L,
                timestamp = Instant.now(),
                endpoint = "/api/completion",
                tokens = null,
                model = null,
                latencyMs = null,
                metadata = null // Map<String, Any>? can be null
            )
            
            event.tokens shouldBe null
            event.model shouldBe null
            event.latencyMs shouldBe null
            event.metadata shouldBe null
        }
    }
})
