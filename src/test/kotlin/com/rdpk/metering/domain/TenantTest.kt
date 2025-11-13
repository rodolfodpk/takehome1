package com.rdpk.metering.domain

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Unit tests for Tenant domain entity
 * Pure domain logic - no database or external dependencies
 * Using Kotest BDD style
 */
class TenantTest : DescribeSpec({

    describe("Tenant domain entity") {
        
        it("should create Tenant with all fields") {
            val now = LocalDateTime.now()
            
            val tenant = Tenant(
                id = 1L,
                name = "Test Tenant",
                active = true,
                created = now,
                updated = now
            )
            
            tenant.id shouldBe 1L
            tenant.name shouldBe "Test Tenant"
            tenant.active shouldBe true
            tenant.created shouldBe now
            tenant.updated shouldBe now
        }

        it("should use withId helper method") {
            val now = LocalDateTime.now()
            val tenant = Tenant(
                name = "Test Tenant",
                active = true,
                created = now,
                updated = now
            )
            
            val tenantWithId = tenant.withId(999L)
            
            tenantWithId.id shouldBe 999L
            tenantWithId.name shouldBe tenant.name
        }

        it("should use withUpdated helper method") {
            val now = LocalDateTime.now()
            val later = now.plusHours(1)
            val tenant = Tenant(
                id = 1L,
                name = "Test Tenant",
                active = true,
                created = now,
                updated = now
            )
            
            val updated = tenant.withUpdated(later)
            
            updated.updated shouldBe later
            updated.id shouldBe tenant.id
            updated.name shouldBe tenant.name
        }

        it("should deactivate tenant") {
            val now = LocalDateTime.now()
            val fixedClock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC)
            val tenant = Tenant(
                id = 1L,
                name = "Test Tenant",
                active = true,
                created = now,
                updated = now
            )
            
            val deactivated = tenant.deactivate(fixedClock)
            
            deactivated.active shouldBe false
            deactivated.updated shouldBe LocalDateTime.now(fixedClock)
            deactivated.id shouldBe tenant.id
            deactivated.name shouldBe tenant.name
        }

        it("should activate tenant") {
            val now = LocalDateTime.now()
            val fixedClock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC)
            val tenant = Tenant(
                id = 1L,
                name = "Test Tenant",
                active = false,
                created = now,
                updated = now
            )
            
            val activated = tenant.activate(fixedClock)
            
            activated.active shouldBe true
            activated.updated shouldBe LocalDateTime.now(fixedClock)
            activated.id shouldBe tenant.id
            activated.name shouldBe tenant.name
        }
    }
})

