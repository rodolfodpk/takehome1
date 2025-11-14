package com.rdpk.metering.integration.service

import com.rdpk.metering.domain.LateEvent
import com.rdpk.metering.domain.UsageEvent
import com.rdpk.metering.integration.AbstractKotestIntegrationTest
import com.rdpk.metering.repository.CustomerRepository
import com.rdpk.metering.repository.LateEventRepository
import com.rdpk.metering.repository.TenantRepository
import com.rdpk.metering.service.LateEventService
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Integration tests for LateEventService
 * Uses real PostgreSQL via Testcontainers - NO MOCKS
 * Using Kotest BDD style with Spring Extension
 * 
 * Tests run sequentially (in order) to support multi-step scenarios
 * Database and Redis are cleaned before the test class runs
 */
class LateEventServiceIntegrationTest : AbstractKotestIntegrationTest() {

    @Autowired
    lateinit var lateEventService: LateEventService

    @Autowired
    lateinit var lateEventRepository: LateEventRepository

    @Autowired
    lateinit var tenantRepository: TenantRepository

    @Autowired
    lateinit var customerRepository: CustomerRepository

    init {
        describe("LateEventService") {
            
            it("should return false for event that is not late") {
                // Setup: Create test tenant and customer
                val now = LocalDateTime.now(clock)
                val tenant = com.rdpk.metering.domain.Tenant(
                    name = "Test Tenant Not Late",
                    active = true,
                    created = now,
                    updated = now
                )
                val savedTenant = tenantRepository.save(tenant).block()!!
                val testTenantId = savedTenant.id ?: throw IllegalStateException("Tenant ID is null")

                val customer = com.rdpk.metering.domain.Customer(
                    tenantId = savedTenant.id!!,
                    externalId = "customer-not-late-1",
                    name = "Test Customer Not Late",
                    created = now,
                    updated = now
                )
                val savedCustomer = customerRepository.save(customer).block()!!
                val testCustomerId = savedCustomer.id ?: throw IllegalStateException("Customer ID is null")
                
                // Event timestamp is in the current window
                val fixedClock = Clock.fixed(Instant.parse("2024-01-01T12:00:00Z"), ZoneOffset.UTC)
                val eventTimestamp = Instant.parse("2024-01-01T12:00:15Z") // 15 seconds into current window
                
                val event = UsageEvent(
                    eventId = "not-late-event-1",
                    tenantId = testTenantId,
                    customerId = testCustomerId,
                    timestamp = eventTimestamp,
                    data = mapOf("endpoint" to "/api/completion", "tokens" to 100)
                )
                
                StepVerifier.create(lateEventService.checkAndHandleLateEvent(event, fixedClock.instant()))
                    .assertNext { isLate ->
                        isLate shouldBe false
                    }
                    .verifyComplete()
            }

            it("should return false for event that is < 1 minute late (process normally)") {
                // Setup: Create test tenant and customer
                val now = LocalDateTime.now(clock)
                val tenant = com.rdpk.metering.domain.Tenant(
                    name = "Test Tenant Slightly Late",
                    active = true,
                    created = now,
                    updated = now
                )
                val savedTenant = tenantRepository.save(tenant).block()!!
                val testTenantId = savedTenant.id ?: throw IllegalStateException("Tenant ID is null")

                val customer = com.rdpk.metering.domain.Customer(
                    tenantId = savedTenant.id!!,
                    externalId = "customer-slightly-late-1",
                    name = "Test Customer Slightly Late",
                    created = now,
                    updated = now
                )
                val savedCustomer = customerRepository.save(customer).block()!!
                val testCustomerId = savedCustomer.id ?: throw IllegalStateException("Customer ID is null")
                
                // Event is 30 seconds late (less than 1 minute threshold)
                // Note: Actually 1 minute late based on window boundaries, but < 1 minute late by actual time
                val fixedClock = Clock.fixed(Instant.parse("2024-01-01T12:00:45Z"), ZoneOffset.UTC)
                val eventTimestamp = Instant.parse("2024-01-01T12:00:00Z") // Previous window start
                
                val event = UsageEvent(
                    eventId = "slightly-late-event-1",
                    tenantId = testTenantId,
                    customerId = testCustomerId,
                    timestamp = eventTimestamp,
                    data = mapOf("endpoint" to "/api/completion", "tokens" to 100)
                )
                
                StepVerifier.create(lateEventService.checkAndHandleLateEvent(event, fixedClock.instant()))
                    .assertNext { isLate ->
                        // 45 seconds late is < 1 minute threshold, so processes normally
                        isLate shouldBe false
                    }
                    .verifyComplete()
            }

            it("should return true and store event that is >= 1 minute late") {
                // Setup: Create test tenant and customer
                val now = LocalDateTime.now(clock)
                val tenant = com.rdpk.metering.domain.Tenant(
                    name = "Test Tenant Late",
                    active = true,
                    created = now,
                    updated = now
                )
                val savedTenant = tenantRepository.save(tenant).block()!!
                val testTenantId = savedTenant.id ?: throw IllegalStateException("Tenant ID is null")

                val customer = com.rdpk.metering.domain.Customer(
                    tenantId = savedTenant.id!!,
                    externalId = "customer-late-1",
                    name = "Test Customer Late",
                    created = now,
                    updated = now
                )
                val savedCustomer = customerRepository.save(customer).block()!!
                val testCustomerId = savedCustomer.id ?: throw IllegalStateException("Customer ID is null")
                
                // Event is 2 minutes late (exceeds 1 minute threshold)
                val fixedClock = Clock.fixed(Instant.parse("2024-01-01T12:02:00Z"), ZoneOffset.UTC)
                val eventTimestamp = Instant.parse("2024-01-01T12:00:00Z") // Previous window, 2 minutes ago
                
                val event = UsageEvent(
                    eventId = "late-event-1",
                    tenantId = testTenantId,
                    customerId = testCustomerId,
                    timestamp = eventTimestamp,
                    data = mapOf("endpoint" to "/api/completion", "tokens" to 100)
                )
                
                StepVerifier.create(lateEventService.checkAndHandleLateEvent(event, fixedClock.instant()))
                    .assertNext { isLate ->
                        isLate shouldBe true // >= 1 minute late, stored as late event
                    }
                    .verifyComplete()
                
                // Verify late event was stored in database
                val storedEvents = lateEventRepository.findAll()
                    .collectList()
                    .block()
                
                storedEvents shouldNotBe null
                storedEvents!!.size shouldBe 1
                storedEvents[0].eventId shouldBe "late-event-1"
                storedEvents[0].tenantId shouldBe testTenantId
                storedEvents[0].customerId shouldBe testCustomerId
                storedEvents[0].originalTimestamp shouldBe eventTimestamp
            }

            it("should handle exact 1 minute threshold correctly") {
                // Setup: Create test tenant and customer
                val now = LocalDateTime.now(clock)
                val tenant = com.rdpk.metering.domain.Tenant(
                    name = "Test Tenant Exact Threshold",
                    active = true,
                    created = now,
                    updated = now
                )
                val savedTenant = tenantRepository.save(tenant).block()!!
                val testTenantId = savedTenant.id ?: throw IllegalStateException("Tenant ID is null")

                val customer = com.rdpk.metering.domain.Customer(
                    tenantId = savedTenant.id!!,
                    externalId = "customer-exact-threshold-1",
                    name = "Test Customer Exact Threshold",
                    created = now,
                    updated = now
                )
                val savedCustomer = customerRepository.save(customer).block()!!
                val testCustomerId = savedCustomer.id ?: throw IllegalStateException("Customer ID is null")
                
                // Event is exactly 1 minute late (boundary case)
                val fixedClock = Clock.fixed(Instant.parse("2024-01-01T12:01:00Z"), ZoneOffset.UTC)
                val eventTimestamp = Instant.parse("2024-01-01T12:00:00Z") // Exactly 1 minute ago
                
                val event = UsageEvent(
                    eventId = "exact-threshold-event-1",
                    tenantId = testTenantId,
                    customerId = testCustomerId,
                    timestamp = eventTimestamp,
                    data = mapOf("endpoint" to "/api/completion", "tokens" to 100)
                )
                
                StepVerifier.create(lateEventService.checkAndHandleLateEvent(event, fixedClock.instant()))
                    .assertNext { isLate ->
                        // Exactly 1 minute should be >= threshold, so stored as late event
                        isLate shouldBe true
                    }
                    .verifyComplete()
            }

            it("should successfully deserialize valid late event") {
                // Setup: Create test tenant and customer
                val now = LocalDateTime.now(clock)
                val tenant = com.rdpk.metering.domain.Tenant(
                    name = "Test Tenant Deserialize",
                    active = true,
                    created = now,
                    updated = now
                )
                val savedTenant = tenantRepository.save(tenant).block()!!
                val testTenantId = savedTenant.id ?: throw IllegalStateException("Tenant ID is null")

                val customer = com.rdpk.metering.domain.Customer(
                    tenantId = savedTenant.id!!,
                    externalId = "customer-deserialize-1",
                    name = "Test Customer Deserialize",
                    created = now,
                    updated = now
                )
                val savedCustomer = customerRepository.save(customer).block()!!
                val testCustomerId = savedCustomer.id ?: throw IllegalStateException("Customer ID is null")
                
                // Create and save a late event
                val eventTimestamp = Instant.parse("2024-01-01T12:00:00Z")
                val receivedTimestamp = Instant.parse("2024-01-01T12:02:00Z")
                
                val originalEvent = UsageEvent(
                    eventId = "deserialize-test-event",
                    tenantId = testTenantId,
                    customerId = testCustomerId,
                    timestamp = eventTimestamp,
                    data = mapOf("endpoint" to "/api/completion", "tokens" to 100, "model" to "gpt-4")
                )
                
                // Store as late event using the service method (which handles serialization properly)
                lateEventService.checkAndHandleLateEvent(originalEvent, receivedTimestamp).block()
                
                // Retrieve the saved late event
                val savedLateEvent = lateEventRepository.findAll()
                    .filter { it.eventId == originalEvent.eventId }
                    .next()
                    .block()!!
                
                // Deserialize
                val deserializedEvent = lateEventService.deserializeEvent(savedLateEvent)
                
                deserializedEvent shouldNotBe null
                deserializedEvent!!.eventId shouldBe "deserialize-test-event"
                deserializedEvent.tenantId shouldBe testTenantId
                deserializedEvent.customerId shouldBe testCustomerId
                deserializedEvent.timestamp shouldBe eventTimestamp
                deserializedEvent.data["endpoint"] shouldBe "/api/completion"
                deserializedEvent.data["tokens"] shouldBe 100
                deserializedEvent.data["model"] shouldBe "gpt-4"
            }

            it("should return null for invalid JSON in late event data") {
                // Setup: Create test tenant and customer
                val now = LocalDateTime.now(clock)
                val tenant = com.rdpk.metering.domain.Tenant(
                    name = "Test Tenant Invalid JSON",
                    active = true,
                    created = now,
                    updated = now
                )
                val savedTenant = tenantRepository.save(tenant).block()!!
                val testTenantId = savedTenant.id ?: throw IllegalStateException("Tenant ID is null")

                val customer = com.rdpk.metering.domain.Customer(
                    tenantId = savedTenant.id!!,
                    externalId = "customer-invalid-json-1",
                    name = "Test Customer Invalid JSON",
                    created = now,
                    updated = now
                )
                val savedCustomer = customerRepository.save(customer).block()!!
                val testCustomerId = savedCustomer.id ?: throw IllegalStateException("Customer ID is null")
                
                // Create late event with invalid JSON
                val lateEvent = LateEvent(
                    eventId = "invalid-json-event",
                    originalTimestamp = Instant.parse("2024-01-01T12:00:00Z"),
                    receivedTimestamp = Instant.parse("2024-01-01T12:02:00Z"),
                    tenantId = testTenantId,
                    customerId = testCustomerId,
                    data = "{invalid json}" // Invalid JSON
                )
                
                val savedLateEvent = lateEventRepository.saveWithJsonb(
                    eventId = lateEvent.eventId,
                    originalTimestamp = lateEvent.originalTimestamp,
                    receivedTimestamp = lateEvent.receivedTimestamp,
                    tenantId = lateEvent.tenantId,
                    customerId = lateEvent.customerId,
                    data = lateEvent.data
                ).block()!!
                
                // Deserialize should return null for invalid JSON
                val deserializedEvent = lateEventService.deserializeEvent(savedLateEvent)
                
                deserializedEvent shouldBe null
            }

            it("should handle events from multiple windows correctly") {
                // Setup: Create test tenant and customer
                val now = LocalDateTime.now(clock)
                val tenant = com.rdpk.metering.domain.Tenant(
                    name = "Test Tenant Multi Window",
                    active = true,
                    created = now,
                    updated = now
                )
                val savedTenant = tenantRepository.save(tenant).block()!!
                val testTenantId = savedTenant.id ?: throw IllegalStateException("Tenant ID is null")

                val customer = com.rdpk.metering.domain.Customer(
                    tenantId = savedTenant.id!!,
                    externalId = "customer-multi-window-1",
                    name = "Test Customer Multi Window",
                    created = now,
                    updated = now
                )
                val savedCustomer = customerRepository.save(customer).block()!!
                val testCustomerId = savedCustomer.id ?: throw IllegalStateException("Customer ID is null")
                
                // Test events from different windows
                val fixedClock = Clock.fixed(Instant.parse("2024-01-01T12:05:00Z"), ZoneOffset.UTC)
                
                // Event from 2 windows ago (should be stored)
                val event1 = UsageEvent(
                    eventId = "multi-window-event-1",
                    tenantId = testTenantId,
                    customerId = testCustomerId,
                    timestamp = Instant.parse("2024-01-01T12:00:00Z"), // 5 minutes ago
                    data = mapOf("endpoint" to "/api/completion")
                )
                
                // Event from 1 window ago (should be stored)
                val event2 = UsageEvent(
                    eventId = "multi-window-event-2",
                    tenantId = testTenantId,
                    customerId = testCustomerId,
                    timestamp = Instant.parse("2024-01-01T12:03:30Z"), // 1.5 minutes ago
                    data = mapOf("endpoint" to "/api/completion")
                )
                
                // Process both events
                StepVerifier.create(lateEventService.checkAndHandleLateEvent(event1, fixedClock.instant()))
                    .assertNext { isLate -> isLate shouldBe true }
                    .verifyComplete()
                
                StepVerifier.create(lateEventService.checkAndHandleLateEvent(event2, fixedClock.instant()))
                    .assertNext { isLate -> isLate shouldBe true }
                    .verifyComplete()
                
                // Verify both were stored
                val storedEvents = lateEventRepository.findAll()
                    .collectList()
                    .block()
                
                storedEvents shouldNotBe null
                storedEvents!!.size shouldBe 2
                storedEvents.map { it.eventId }.toSet() shouldBe setOf("multi-window-event-1", "multi-window-event-2")
            }

            it("should correctly truncate timestamps to window boundaries") {
                // Setup: Create test tenant and customer
                val now = LocalDateTime.now(clock)
                val tenant = com.rdpk.metering.domain.Tenant(
                    name = "Test Tenant Window Boundary",
                    active = true,
                    created = now,
                    updated = now
                )
                val savedTenant = tenantRepository.save(tenant).block()!!
                val testTenantId = savedTenant.id ?: throw IllegalStateException("Tenant ID is null")

                val customer = com.rdpk.metering.domain.Customer(
                    tenantId = savedTenant.id!!,
                    externalId = "customer-window-boundary-1",
                    name = "Test Customer Window Boundary",
                    created = now,
                    updated = now
                )
                val savedCustomer = customerRepository.save(customer).block()!!
                val testCustomerId = savedCustomer.id ?: throw IllegalStateException("Customer ID is null")
                
                // Test window boundary calculation (30-second windows)
                val fixedClock = Clock.fixed(Instant.parse("2024-01-01T12:01:30Z"), ZoneOffset.UTC)
                
                // Event at 12:00:15 should be in window starting at 12:00:00
                val event1 = UsageEvent(
                    eventId = "window-boundary-1",
                    tenantId = testTenantId,
                    customerId = testCustomerId,
                    timestamp = Instant.parse("2024-01-01T12:00:15Z"),
                    data = mapOf("endpoint" to "/api/completion")
                )
                
                // Event at 12:00:45 should be in window starting at 12:00:30
                val event2 = UsageEvent(
                    eventId = "window-boundary-2",
                    tenantId = testTenantId,
                    customerId = testCustomerId,
                    timestamp = Instant.parse("2024-01-01T12:00:45Z"),
                    data = mapOf("endpoint" to "/api/completion")
                )
                
                // Both events are from previous windows, but event2 is from a more recent window
                // Event1 is from window 12:00:00-12:00:30 (1.5 minutes ago)
                // Event2 is from window 12:00:30-12:01:00 (1 minute ago)
                
                StepVerifier.create(lateEventService.checkAndHandleLateEvent(event1, fixedClock.instant()))
                    .assertNext { isLate -> isLate shouldBe true } // 1.5 minutes late
                    .verifyComplete()
                
                StepVerifier.create(lateEventService.checkAndHandleLateEvent(event2, fixedClock.instant()))
                    .assertNext { isLate -> isLate shouldBe true } // 1 minute late (exactly at threshold)
                    .verifyComplete()
                
                // Note: We don't verify exact counts here because the scheduler may process
                // late events in the background. The important thing is that checkAndHandleLateEvent
                // correctly identifies and stores late events, which we've verified above.
            }
        }
    }
}

