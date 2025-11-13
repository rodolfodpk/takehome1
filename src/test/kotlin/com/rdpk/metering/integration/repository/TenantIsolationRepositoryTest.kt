package com.rdpk.metering.integration.repository

import com.rdpk.metering.domain.UsageEvent
import com.rdpk.metering.integration.AbstractKotestIntegrationTest
import com.rdpk.metering.repository.UsageEventRepository
import com.rdpk.metering.repository.UsageEventRepositoryExtensions
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier
import java.time.Instant
import java.time.LocalDateTime

/**
 * Integration tests for tenant isolation in repository queries
 * Verifies that tenant data is properly isolated and queries respect tenant boundaries
 * Uses real PostgreSQL via Testcontainers - NO MOCKS
 */
class TenantIsolationRepositoryTest : AbstractKotestIntegrationTest() {

    @Autowired
    lateinit var repository: UsageEventRepository

    @Autowired
    lateinit var repositoryExtensions: UsageEventRepositoryExtensions

    @Autowired
    lateinit var tenantRepository: com.rdpk.metering.repository.TenantRepository

    @Autowired
    lateinit var customerRepository: com.rdpk.metering.repository.CustomerRepository

    init {
        describe("Tenant Isolation in Repository Queries") {

            it("should find events by tenant and customer - respects tenant isolation") {
                // Setup: Create two tenants with customers
                val now = LocalDateTime.now(clock)
                
                val tenantA = com.rdpk.metering.domain.Tenant(
                    name = "Tenant A ${System.currentTimeMillis()}",
                    active = true,
                    created = now,
                    updated = now
                )
                val savedTenantA = tenantRepository.save(tenantA).block()!!
                val tenantAId = savedTenantA.id!!

                val tenantB = com.rdpk.metering.domain.Tenant(
                    name = "Tenant B ${System.currentTimeMillis()}",
                    active = true,
                    created = now,
                    updated = now
                )
                val savedTenantB = tenantRepository.save(tenantB).block()!!
                val tenantBId = savedTenantB.id!!

                val customerA1 = com.rdpk.metering.domain.Customer(
                    tenantId = tenantAId,
                    externalId = "customer-a1",
                    name = "Customer A1",
                    created = now,
                    updated = now
                )
                val savedCustomerA1 = customerRepository.save(customerA1).block()!!
                val customerA1Id = savedCustomerA1.id!!

                val customerB1 = com.rdpk.metering.domain.Customer(
                    tenantId = tenantBId,
                    externalId = "customer-b1",
                    name = "Customer B1",
                    created = now,
                    updated = now
                )
                val savedCustomerB1 = customerRepository.save(customerB1).block()!!
                val customerB1Id = savedCustomerB1.id!!

                // Store events for both tenants
                val eventA1 = UsageEvent(
                    eventId = "event-a1-${System.currentTimeMillis()}",
                    tenantId = tenantAId,
                    customerId = customerA1Id,
                    timestamp = clock.instant(),
                    endpoint = "/api/completion",
                    tokens = 100,
                    metadata = mapOf("tenant" to "A")
                )

                val eventB1 = UsageEvent(
                    eventId = "event-b1-${System.currentTimeMillis()}",
                    tenantId = tenantBId,
                    customerId = customerB1Id,
                    timestamp = clock.instant(),
                    endpoint = "/api/completion",
                    tokens = 200,
                    metadata = mapOf("tenant" to "B")
                )

                StepVerifier.create(
                    repositoryExtensions.saveWithJsonb(eventA1)
                        .then(repositoryExtensions.saveWithJsonb(eventB1))
                ).verifyComplete()

                // Query Tenant A's events
                StepVerifier.create(
                    repository.findByTenantIdAndCustomerId(tenantAId, customerA1Id)
                        .collectList()
                )
                    .assertNext { events ->
                        events shouldHaveSize 1
                        events[0].tenantId shouldBe tenantAId
                        events[0].eventId shouldBe eventA1.eventId
                        events[0].metadata?.get("tenant") shouldBe "A"
                        // Verify Tenant B's events are not included
                        events.none { it.tenantId == tenantBId } shouldBe true
                    }
                    .verifyComplete()

                // Query Tenant B's events
                StepVerifier.create(
                    repository.findByTenantIdAndCustomerId(tenantBId, customerB1Id)
                        .collectList()
                )
                    .assertNext { events ->
                        events shouldHaveSize 1
                        events[0].tenantId shouldBe tenantBId
                        events[0].eventId shouldBe eventB1.eventId
                        events[0].metadata?.get("tenant") shouldBe "B"
                        // Verify Tenant A's events are not included
                        events.none { it.tenantId == tenantAId } shouldBe true
                    }
                    .verifyComplete()
            }

            it("should find events by tenant and timestamp - respects tenant isolation") {
                // Setup: Create two tenants
                val now = LocalDateTime.now(clock)
                val tenantA = createTenant("Tenant A", now)
                val tenantB = createTenant("Tenant B", now)
                val customerA1 = createCustomer(tenantA.id!!, "customer-a1", now)
                val customerB1 = createCustomer(tenantB.id!!, "customer-b1", now)

                val startTime = clock.instant()
                val endTime = startTime.plusSeconds(60)

                // Store events for both tenants in same time range
                val eventA = createEvent(tenantA.id!!, customerA1.id!!, "event-a", startTime.plusSeconds(10))
                val eventB = createEvent(tenantB.id!!, customerB1.id!!, "event-b", startTime.plusSeconds(20))

                StepVerifier.create(
                    repositoryExtensions.saveWithJsonb(eventA)
                        .then(repositoryExtensions.saveWithJsonb(eventB))
                ).verifyComplete()

                // Query Tenant A's events by time range
                StepVerifier.create(
                    repository.findByTenantIdAndTimestampBetween(tenantA.id!!, startTime, endTime)
                        .collectList()
                )
                    .assertNext { events ->
                        events shouldHaveSize 1
                        events[0].tenantId shouldBe tenantA.id
                        events[0].eventId shouldBe eventA.eventId
                        // Verify Tenant B's events are not included
                        events.none { it.tenantId == tenantB.id } shouldBe true
                    }
                    .verifyComplete()
            }

            it("should prevent cross-tenant queries - customer belongs to different tenant") {
                // Setup: Create two tenants with customers
                val now = LocalDateTime.now(clock)
                val tenantA = createTenant("Tenant A", now)
                val tenantB = createTenant("Tenant B", now)
                val customerA1 = createCustomer(tenantA.id!!, "customer-a1", now)
                val customerB1 = createCustomer(tenantB.id!!, "customer-b1", now)

                // Store events for both
                val eventA = createEvent(tenantA.id!!, customerA1.id!!, "event-a")
                val eventB = createEvent(tenantB.id!!, customerB1.id!!, "event-b")

                StepVerifier.create(
                    repositoryExtensions.saveWithJsonb(eventA)
                        .then(repositoryExtensions.saveWithJsonb(eventB))
                ).verifyComplete()

                // Try to query Tenant A's ID with Tenant B's customer ID
                // This should return empty (customer belongs to different tenant)
                StepVerifier.create(
                    repository.findByTenantIdAndCustomerId(tenantA.id!!, customerB1.id!!)
                        .collectList()
                )
                    .assertNext { events ->
                        events.shouldBeEmpty()
                    }
                    .verifyComplete()
            }

            it("should store events with correct tenant_id from request body") {
                // Setup: Create tenant and customer
                val now = LocalDateTime.now(clock)
                val tenant = createTenant("Test Tenant", now)
                val customer = createCustomer(tenant.id!!, "customer-1", now)

                // Store event with tenantId in the event
                val event = UsageEvent(
                    eventId = "test-event-${System.currentTimeMillis()}",
                    tenantId = tenant.id!!, // tenantId from request body
                    customerId = customer.id!!,
                    timestamp = clock.instant(),
                    endpoint = "/api/completion",
                    tokens = 100,
                    metadata = mapOf("test" to "data")
                )

                StepVerifier.create(
                    repositoryExtensions.saveWithJsonb(event)
                        .then(repository.findByEventId(event.eventId))
                )
                    .assertNext { savedEvent ->
                        savedEvent.tenantId shouldBe tenant.id
                        savedEvent.customerId shouldBe customer.id
                        savedEvent.eventId shouldBe event.eventId
                    }
                    .verifyComplete()
            }

            it("should document that findByCustomerId is unsafe (no tenant filter)") {
                // Setup: Create two tenants with different customer IDs
                val now = LocalDateTime.now(clock)
                val tenantA = createTenant("Tenant A", now)
                val tenantB = createTenant("Tenant B", now)
                val customerA1 = createCustomer(tenantA.id!!, "customer-a1", now)
                val customerB1 = createCustomer(tenantB.id!!, "customer-b1", now)

                // Store events for both tenants
                val eventA = createEvent(tenantA.id!!, customerA1.id!!, "event-a")
                val eventB = createEvent(tenantB.id!!, customerB1.id!!, "event-b")

                StepVerifier.create(
                    repositoryExtensions.saveWithJsonb(eventA)
                        .then(repositoryExtensions.saveWithJsonb(eventB))
                ).verifyComplete()

                // Query using findByCustomerId (no tenant filter)
                // This method should NEVER be used in production without tenant filter
                // Customer IDs are unique per tenant, so this works, but it's unsafe
                StepVerifier.create(
                    repository.findByCustomerId(customerA1.id!!)
                        .collectList()
                )
                    .assertNext { events ->
                        // Customer IDs are unique per tenant, so this returns only Tenant A's events
                        events shouldHaveSize 1
                        events[0].tenantId shouldBe tenantA.id
                        events[0].eventId shouldBe eventA.eventId
                    }
                    .verifyComplete()

                // NOTE: This test documents that findByCustomerId() exists but should not be used
                // in production code. Always use tenant-scoped methods like findByTenantIdAndCustomerId()
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

    private fun createEvent(
        tenantId: Long,
        customerId: Long,
        eventIdPrefix: String,
        timestamp: Instant = clock.instant()
    ): UsageEvent {
        return UsageEvent(
            eventId = "$eventIdPrefix-${System.currentTimeMillis()}",
            tenantId = tenantId,
            customerId = customerId,
            timestamp = timestamp,
            endpoint = "/api/completion",
            tokens = 100,
            metadata = mapOf("test" to "data")
        )
    }
}

