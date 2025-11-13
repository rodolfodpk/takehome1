package com.rdpk.metering.integration.e2e

import com.rdpk.metering.dto.EventMetadata
import com.rdpk.metering.dto.UsageEventRequest
import com.rdpk.metering.integration.AbstractKotestIntegrationTest
import com.rdpk.metering.repository.CustomerRepository
import com.rdpk.metering.repository.TenantRepository
import com.rdpk.metering.repository.UsageEventRepository
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Instant
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

/**
 * E2E tests for tenant isolation
 * Verifies that tenant data is properly isolated end-to-end from HTTP request to database storage
 * Tests complete HTTP flow: POST /api/v1/events -> Redis -> Postgres
 * Uses real PostgreSQL and Redis via Testcontainers - NO MOCKS
 */
@AutoConfigureWebTestClient
class TenantIsolationE2ETest : AbstractKotestIntegrationTest() {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var tenantRepository: TenantRepository

    @Autowired
    lateinit var customerRepository: CustomerRepository

    @Autowired
    lateinit var usageEventRepository: UsageEventRepository
    
    @Autowired(required = false)
    var eventPersistenceScheduler: com.rdpk.metering.scheduler.EventPersistenceScheduler? = null

    init {
        describe("Tenant Isolation E2E") {

            it("should isolate tenant data - Tenant A cannot see Tenant B's events") {
                // Setup: Create two tenants with customers
                val now = LocalDateTime.now(clock)
                val tenantA = createTenant("Tenant A", now)
                val tenantB = createTenant("Tenant B", now)
                val customerA1 = createCustomer(tenantA.id!!, "customer-a1", now)
                val customerB1 = createCustomer(tenantB.id!!, "customer-b1", now)

                // Send events for both tenants via HTTP
                val eventAId = "e2e-tenant-a-${System.currentTimeMillis()}"
                val requestA = UsageEventRequest(
                    eventId = eventAId,
                    timestamp = clock.instant(),
                    tenantId = tenantA.id!!.toString(),
                    customerId = customerA1.externalId,
                    apiEndpoint = "/api/completion",
                    metadata = EventMetadata(inputTokens = 50, outputTokens = 50, tokens = 100, model = "gpt-4")
                )

                val eventBId = "e2e-tenant-b-${System.currentTimeMillis()}"
                val requestB = UsageEventRequest(
                    eventId = eventBId,
                    timestamp = clock.instant(),
                    tenantId = tenantB.id!!.toString(),
                    customerId = customerB1.externalId,
                    apiEndpoint = "/api/completion",
                    metadata = EventMetadata(inputTokens = 100, outputTokens = 100, tokens = 200, model = "gpt-3.5-turbo")
                )

                // Send both events
                webTestClient.post()
                    .uri("/api/v1/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestA)
                    .exchange()
                    .expectStatus().isCreated

                webTestClient.post()
                    .uri("/api/v1/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestB)
                    .exchange()
                    .expectStatus().isCreated

                // Manually trigger persistence from Redis to Postgres for tests
                eventPersistenceScheduler?.batchPersistEvents()
                
                // Wait for persistence (events are persisted asynchronously)
                // Use reactive retry pattern with simple delay and retry
                val nowInstant = clock.instant()
                val start = nowInstant.minusSeconds(3600) // 1 hour ago
                val end = nowInstant.plusSeconds(3600) // 1 hour from now
                
                // Simple retry: query with delay, retry up to 20 times using repeatWhenEmpty
                StepVerifier.create(
                    usageEventRepository.findByTenantIdAndCustomerIdAndTimestampBetween(tenantA.id!!, customerA1.id!!, start, end)
                        .collectList()
                        .filter { it.isNotEmpty() }
                        .repeatWhenEmpty { flux ->
                            flux
                                .take(20) // Max 20 retries
                                .delayElements(java.time.Duration.ofMillis(250))
                                .doOnNext { eventPersistenceScheduler?.batchPersistEvents() }
                        }
                        .timeout(java.time.Duration.ofSeconds(10))
                )
                    .assertNext { events ->
                        events shouldHaveSize 1
                        events[0].tenantId shouldBe tenantA.id
                        events[0].eventId shouldBe eventAId
                        // Verify Tenant B's events are not included
                        events.none { it.tenantId == tenantB.id } shouldBe true
                    }
                    .verifyComplete()

                // Query Tenant B's events using required method with time range
                StepVerifier.create(
                    usageEventRepository.findByTenantIdAndCustomerIdAndTimestampBetween(tenantB.id!!, customerB1.id!!, start, end)
                        .collectList()
                )
                    .assertNext { events ->
                        events shouldHaveSize 1
                        events[0].tenantId shouldBe tenantB.id
                        events[0].eventId shouldBe eventBId
                        // Verify Tenant A's events are not included
                        events.none { it.tenantId == tenantA.id } shouldBe true
                    }
                    .verifyComplete()
            }

            it("should handle multiple tenants with same customer external ID") {
                // Setup: Create two tenants with same customer external ID
                val now = LocalDateTime.now(clock)
                val tenantA = createTenant("Tenant A", now)
                val tenantB = createTenant("Tenant B", now)
                val customerA1 = createCustomer(tenantA.id!!, "customer-1", now) // Same external ID
                val customerB1 = createCustomer(tenantB.id!!, "customer-1", now) // Same external ID

                // Send events to both tenants with same customer external ID
                val eventAId = "e2e-same-customer-a-${System.currentTimeMillis()}"
                val requestA = UsageEventRequest(
                    eventId = eventAId,
                    timestamp = clock.instant(),
                    tenantId = tenantA.id!!.toString(),
                    customerId = "customer-1", // Same external ID
                    apiEndpoint = "/api/completion",
                    metadata = EventMetadata(inputTokens = 50, outputTokens = 50, tokens = 100)
                )

                val eventBId = "e2e-same-customer-b-${System.currentTimeMillis()}"
                val requestB = UsageEventRequest(
                    eventId = eventBId,
                    timestamp = clock.instant(),
                    tenantId = tenantB.id!!.toString(),
                    customerId = "customer-1", // Same external ID
                    apiEndpoint = "/api/completion",
                    metadata = EventMetadata(inputTokens = 100, outputTokens = 100, tokens = 200)
                )

                // Send both events
                webTestClient.post()
                    .uri("/api/v1/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestA)
                    .exchange()
                    .expectStatus().isCreated

                webTestClient.post()
                    .uri("/api/v1/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestB)
                    .exchange()
                    .expectStatus().isCreated

                // Manually trigger persistence from Redis to Postgres for tests
                eventPersistenceScheduler?.batchPersistEvents()
                
                // Wait for persistence (events are persisted asynchronously)
                // Use reactive retry pattern with simple delay and retry
                val nowInstant = clock.instant()
                val start = nowInstant.minusSeconds(3600) // 1 hour ago
                val end = nowInstant.plusSeconds(3600) // 1 hour from now
                
                // Simple retry: query with delay, retry up to 20 times using repeatWhenEmpty
                StepVerifier.create(
                    usageEventRepository.findByTenantIdAndCustomerIdAndTimestampBetween(tenantA.id!!, customerA1.id!!, start, end)
                        .collectList()
                        .filter { it.isNotEmpty() }
                        .repeatWhenEmpty { flux ->
                            flux
                                .take(20) // Max 20 retries
                                .delayElements(java.time.Duration.ofMillis(250))
                                .doOnNext { eventPersistenceScheduler?.batchPersistEvents() }
                        }
                        .timeout(java.time.Duration.ofSeconds(10))
                )
                    .assertNext { events ->
                        events shouldHaveSize 1
                        events[0].tenantId shouldBe tenantA.id
                        events[0].eventId shouldBe eventAId
                        events[0].data["tokens"] shouldBe 100
                    }
                    .verifyComplete()

                StepVerifier.create(
                    usageEventRepository.findByTenantIdAndCustomerIdAndTimestampBetween(tenantB.id!!, customerB1.id!!, start, end)
                        .collectList()
                )
                    .assertNext { events ->
                        events shouldHaveSize 1
                        events[0].tenantId shouldBe tenantB.id
                        events[0].eventId shouldBe eventBId
                        events[0].data["tokens"] shouldBe 200
                    }
                    .verifyComplete()
            }
        }
    }

    private fun createTenant(name: String, now: LocalDateTime): com.rdpk.metering.domain.Tenant {
        val tenant = com.rdpk.metering.domain.Tenant(
            name = "$name ${System.currentTimeMillis()}",
            active = true,
            created = now,
            updated = now
        )
        return tenantRepository.save(tenant).block()!!
    }

    private fun createCustomer(tenantId: Long, externalId: String, now: LocalDateTime): com.rdpk.metering.domain.Customer {
        val customer = com.rdpk.metering.domain.Customer(
            tenantId = tenantId,
            externalId = externalId,
            name = "Customer $externalId",
            created = now,
            updated = now
        )
        return customerRepository.save(customer).block()!!
    }
}

