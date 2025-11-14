package com.rdpk.metering.domain

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.Instant

/**
 * Unit tests for LateEvent domain entity
 * Pure domain logic - no database or external dependencies
 * Using Kotest BDD style
 */
class LateEventTest : DescribeSpec({

    describe("LateEvent domain entity") {
        
        it("should create LateEvent with all fields") {
            val now = Instant.now()
            val originalTimestamp = now.minusSeconds(120) // 2 minutes ago
            val receivedTimestamp = now
            
            val lateEvent = LateEvent(
                eventId = "late-event-123",
                originalTimestamp = originalTimestamp,
                receivedTimestamp = receivedTimestamp,
                tenantId = 1L,
                customerId = 100L,
                data = """{"eventId":"late-event-123","timestamp":"${originalTimestamp}","tenantId":1,"customerId":100}"""
            )
            
            lateEvent.eventId shouldBe "late-event-123"
            lateEvent.originalTimestamp shouldBe originalTimestamp
            lateEvent.receivedTimestamp shouldBe receivedTimestamp
            lateEvent.tenantId shouldBe 1L
            lateEvent.customerId shouldBe 100L
            lateEvent.data shouldNotBe null
            lateEvent.id shouldBe null // ID is null until saved
        }

        it("should create LateEvent with withId helper") {
            val now = Instant.now()
            val originalTimestamp = now.minusSeconds(60)
            
            val lateEvent = LateEvent(
                eventId = "late-event-456",
                originalTimestamp = originalTimestamp,
                receivedTimestamp = now,
                tenantId = 2L,
                customerId = 200L,
                data = "{}"
            )
            
            val lateEventWithId = lateEvent.withId(999L)
            
            lateEventWithId.id shouldBe 999L
            lateEventWithId.eventId shouldBe "late-event-456"
            lateEventWithId.originalTimestamp shouldBe originalTimestamp
            lateEventWithId.tenantId shouldBe lateEvent.tenantId
            lateEventWithId.customerId shouldBe lateEvent.customerId
            lateEventWithId.data shouldBe lateEvent.data
        }

        it("should allow nullable id field") {
            val now = Instant.now()
            
            val lateEvent = LateEvent(
                eventId = "late-event-789",
                originalTimestamp = now.minusSeconds(30),
                receivedTimestamp = now,
                tenantId = 3L,
                customerId = 300L,
                data = "{}"
            )
            
            lateEvent.id shouldBe null // ID is optional until persisted
        }

        it("should preserve all fields when using withId") {
            val now = Instant.now()
            val originalTimestamp = now.minusSeconds(90)
            val receivedTimestamp = now
            
            val lateEvent = LateEvent(
                eventId = "late-event-preserve",
                originalTimestamp = originalTimestamp,
                receivedTimestamp = receivedTimestamp,
                tenantId = 4L,
                customerId = 400L,
                data = """{"test":"data"}"""
            )
            
            val lateEventWithId = lateEvent.withId(12345L)
            
            // Verify all fields are preserved
            lateEventWithId.id shouldBe 12345L
            lateEventWithId.eventId shouldBe lateEvent.eventId
            lateEventWithId.originalTimestamp shouldBe lateEvent.originalTimestamp
            lateEventWithId.receivedTimestamp shouldBe lateEvent.receivedTimestamp
            lateEventWithId.tenantId shouldBe lateEvent.tenantId
            lateEventWithId.customerId shouldBe lateEvent.customerId
            lateEventWithId.data shouldBe lateEvent.data
        }
    }
})

