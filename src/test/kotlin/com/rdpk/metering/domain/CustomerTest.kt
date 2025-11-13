package com.rdpk.metering.domain

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Unit tests for Customer domain entity
 * Pure domain logic - no database or external dependencies
 * Using Kotest BDD style
 */
class CustomerTest : DescribeSpec({

    describe("Customer domain entity") {
        
        it("should create Customer with all fields") {
            val now = LocalDateTime.now()
            
            val customer = Customer(
                id = 1L,
                tenantId = 10L,
                externalId = "customer-001",
                name = "Test Customer",
                created = now,
                updated = now
            )
            
            customer.id shouldBe 1L
            customer.tenantId shouldBe 10L
            customer.externalId shouldBe "customer-001"
            customer.name shouldBe "Test Customer"
            customer.created shouldBe now
            customer.updated shouldBe now
        }

        it("should use withId helper method") {
            val now = LocalDateTime.now()
            val customer = Customer(
                tenantId = 10L,
                externalId = "customer-001",
                name = "Test Customer",
                created = now,
                updated = now
            )
            
            val customerWithId = customer.withId(999L)
            
            customerWithId.id shouldBe 999L
            customerWithId.tenantId shouldBe customer.tenantId
            customerWithId.externalId shouldBe customer.externalId
        }

        it("should use withUpdated helper method") {
            val now = LocalDateTime.now()
            val later = now.plusHours(1)
            val customer = Customer(
                id = 1L,
                tenantId = 10L,
                externalId = "customer-001",
                name = "Test Customer",
                created = now,
                updated = now
            )
            
            val updated = customer.withUpdated(later)
            
            updated.updated shouldBe later
            updated.id shouldBe customer.id
            updated.name shouldBe customer.name
        }

        it("should use withName helper method") {
            val now = LocalDateTime.now()
            val fixedClock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC)
            val customer = Customer(
                id = 1L,
                tenantId = 10L,
                externalId = "customer-001",
                name = "Old Name",
                created = now,
                updated = now
            )
            
            val renamed = customer.withName("New Name", fixedClock)
            
            renamed.name shouldBe "New Name"
            renamed.updated shouldBe LocalDateTime.now(fixedClock)
            renamed.id shouldBe customer.id
            renamed.tenantId shouldBe customer.tenantId
        }
    }
})

