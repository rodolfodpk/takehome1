package com.rdpk.metering.integration.service

import com.rdpk.metering.dto.EventMetadata
import com.rdpk.metering.dto.UsageEventRequest
import com.rdpk.metering.integration.AbstractKotestIntegrationTest
import com.rdpk.metering.repository.CustomerRepository
import com.rdpk.metering.repository.TenantRepository
import com.rdpk.metering.service.EventProcessingService
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier
import java.time.Instant
import java.time.LocalDateTime

/**
 * Integration tests for EventProcessingService
 * Uses real PostgreSQL and Redis via Testcontainers - NO MOCKS
 * Using Kotest BDD style with Spring Extension
 * 
 * Tests run sequentially (in order) to support multi-step scenarios
 * Database and Redis are cleaned before the test class runs
 */
class EventProcessingServiceIntegrationTest : AbstractKotestIntegrationTest() {

    @Autowired
    lateinit var eventProcessingService: EventProcessingService

    @Autowired
    lateinit var tenantRepository: TenantRepository

    @Autowired
    lateinit var customerRepository: CustomerRepository

    init {
        describe("EventProcessingService") {
        
        it("should process valid event successfully") {
            // Setup: Create test tenant and customer
            val now = LocalDateTime.now(clock)
            val tenant = com.rdpk.metering.domain.Tenant(
                name = "Test Tenant",
                active = true,
                created = now,
                updated = now
            )
            val savedTenant = tenantRepository.save(tenant).block()!!
            val testTenantId = savedTenant.id ?: throw IllegalStateException("Tenant ID is null")

            val customer = com.rdpk.metering.domain.Customer(
                tenantId = savedTenant.id!!,
                externalId = "customer-1",
                name = "Test Customer",
                created = now,
                updated = now
            )
customerRepository.save(customer).block()
            val testCustomerExternalId = "customer-1"
            
            // Test
            val request = UsageEventRequest(
                eventId = "test-event-1",
                timestamp = clock.instant(),
                tenantId = testTenantId.toString(),
                customerId = testCustomerExternalId,
                apiEndpoint = "/api/completion",
                metadata = EventMetadata(
                    tokens = 100,
                    model = "gpt-4",
                    latencyMs = 250,
                    inputTokens = 50,
                    outputTokens = 50
                )
            )

            StepVerifier.create(eventProcessingService.processEvent(request))
                .assertNext { response ->
                    response.eventId shouldBe "test-event-1"
                    response.status shouldBe "PROCESSED"
                    response.processedAt shouldNotBe null
                }
                .verifyComplete()
        }

        it("should reject event with invalid tenant") {
            // Setup: Create test customer (but use invalid tenant)
            val now = LocalDateTime.now(clock)
            val tenant = com.rdpk.metering.domain.Tenant(
                name = "Test Tenant 2",
                active = true,
                created = now,
                updated = now
            )
            val savedTenant = this@EventProcessingServiceIntegrationTest.tenantRepository.save(tenant).block()!!
            val customer = com.rdpk.metering.domain.Customer(
                tenantId = savedTenant.id!!,
                externalId = "customer-2",
                name = "Test Customer 2",
                created = now,
                updated = now
            )
customerRepository.save(customer).block()
            
            val request = UsageEventRequest(
                eventId = "test-event-2",
                timestamp = clock.instant(),
                tenantId = "999", // Non-existent tenant
                customerId = "customer-2",
                apiEndpoint = "/api/completion",
                metadata = EventMetadata()
            )

            StepVerifier.create(eventProcessingService.processEvent(request))
                .expectErrorMatches { error ->
                    error.shouldBeInstanceOf<IllegalArgumentException>()
                    error.message?.contains("Tenant not found") == true
                }
                .verify()
        }

        it("should reject event with invalid customer") {
            // Setup: Create test tenant
            val now = LocalDateTime.now(clock)
            val tenant = com.rdpk.metering.domain.Tenant(
                name = "Test Tenant 3",
                active = true,
                created = now,
                updated = now
            )
            val savedTenant = tenantRepository.save(tenant).block()!!
            val testTenantId = savedTenant.id ?: throw IllegalStateException("Tenant ID is null")
            
            val request = UsageEventRequest(
                eventId = "test-event-3",
                timestamp = clock.instant(),
                tenantId = testTenantId.toString(),
                customerId = "non-existent-customer",
                apiEndpoint = "/api/completion",
                metadata = EventMetadata()
            )

            StepVerifier.create(eventProcessingService.processEvent(request))
                .expectErrorMatches { error ->
                    error.shouldBeInstanceOf<IllegalArgumentException>()
                    error.message?.contains("Customer not found") == true
                }
                .verify()
        }
        }
    }
}
