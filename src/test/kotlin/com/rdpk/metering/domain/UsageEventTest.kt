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
                data = mapOf(
                    "endpoint" to "/api/completion",
                    "tokens" to 100,
                    "model" to "gpt-4",
                    "latencyMs" to 250
                ),
                created = now,
                updated = now
            )
            
            event.eventId shouldBe "event-123"
            event.tenantId shouldBe 1L
            event.customerId shouldBe 100L
            event.timestamp shouldBe timestamp
            event.data["endpoint"] shouldBe "/api/completion"
            event.data["tokens"] shouldBe 100
            event.data["model"] shouldBe "gpt-4"
            event.data["latencyMs"] shouldBe 250
            event.created shouldNotBe null
            event.updated shouldNotBe null
        }

        it("should create UsageEvent with withId helper") {
            val event = UsageEvent(
                eventId = "event-123",
                tenantId = 1L,
                customerId = 100L,
                timestamp = Instant.now(),
                data = mapOf("endpoint" to "/api/completion")
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
                data = mapOf("endpoint" to "/api/completion") // data is required, but can have minimal fields
            )
            
            event.data["tokens"] shouldBe null
            event.data["model"] shouldBe null
            event.data["latencyMs"] shouldBe null
        }
    }
})
