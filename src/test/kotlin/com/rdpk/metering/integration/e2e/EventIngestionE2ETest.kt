package com.rdpk.metering.integration.e2e

import com.rdpk.metering.dto.EventMetadata
import com.rdpk.metering.dto.UsageEventRequest
import com.rdpk.metering.integration.AbstractKotestIntegrationTest
import com.rdpk.metering.repository.CustomerRepository
import com.rdpk.metering.repository.TenantRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.test.StepVerifier
import java.time.Instant
import java.time.LocalDateTime

/**
 * E2E tests for event ingestion flow
 * Tests complete HTTP flow: POST /api/v1/events -> Redis -> Postgres
 * Uses WebTestClient for HTTP testing
 * Uses real PostgreSQL and Redis via Testcontainers - NO MOCKS
 */
@AutoConfigureWebTestClient
class EventIngestionE2ETest : AbstractKotestIntegrationTest() {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var tenantRepository: TenantRepository

    @Autowired
    lateinit var customerRepository: CustomerRepository

    init {
        describe("Event Ingestion E2E Flow") {
            
            it("should ingest event via HTTP and return success") {
                // Setup: Create test tenant and customer
                val now = LocalDateTime.now(clock)
                val tenant = com.rdpk.metering.domain.Tenant(
                    name = "E2E Test Tenant ${System.currentTimeMillis()}",
                    active = true,
                    created = now,
                    updated = now
                )
                val savedTenant = tenantRepository.save(tenant).block()!!
                val testTenantId = savedTenant.id!!.toString()

                val customer = com.rdpk.metering.domain.Customer(
                    tenantId = savedTenant.id!!,
                    externalId = "e2e-customer-1",
                    name = "E2E Test Customer",
                    created = now,
                    updated = now
                )
                val savedCustomer = customerRepository.save(customer).block()!!
                val testCustomerId = savedCustomer.externalId
                
                // Create event request
                val eventId = "e2e-event-${System.currentTimeMillis()}"
                val request = UsageEventRequest(
                    eventId = eventId,
                    timestamp = clock.instant(),
                    tenantId = testTenantId,
                    customerId = testCustomerId,
                    apiEndpoint = "/api/completion",
                    metadata = EventMetadata(
                        tokens = 100,
                        model = "gpt-4",
                        latencyMs = 250,
                        inputTokens = 50,
                        outputTokens = 50
                    )
                )

                // Send HTTP POST request (tenantId comes from request body)
                webTestClient.post()
                    .uri("/api/v1/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isCreated // Controller returns 201 CREATED
                    .expectBody()
                    .jsonPath("$.eventId").isEqualTo(eventId)
                    .jsonPath("$.status").isEqualTo("PROCESSED")
                    .jsonPath("$.processedAt").exists()
            }

            it("should reject event with invalid tenant") {
                val request = UsageEventRequest(
                    eventId = "invalid-tenant-event",
                    timestamp = clock.instant(),
                    tenantId = "999999",
                    customerId = "customer-1",
                    apiEndpoint = "/api/completion",
                    metadata = EventMetadata()
                )

                webTestClient.post()
                    .uri("/api/v1/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isBadRequest
            }

            it("should reject event with invalid customer") {
                // Setup: Create tenant but no customer
                val now = LocalDateTime.now(clock)
                val tenant = com.rdpk.metering.domain.Tenant(
                    name = "E2E Test Tenant 2 ${System.currentTimeMillis()}",
                    active = true,
                    created = now,
                    updated = now
                )
                val savedTenant = tenantRepository.save(tenant).block()!!
                val testTenantId = savedTenant.id!!.toString()

                val request = UsageEventRequest(
                    eventId = "invalid-customer-event",
                    timestamp = clock.instant(),
                    tenantId = testTenantId,
                    customerId = "non-existent-customer",
                    apiEndpoint = "/api/completion",
                    metadata = EventMetadata()
                )

                webTestClient.post()
                    .uri("/api/v1/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isBadRequest
            }
        }
    }
}

