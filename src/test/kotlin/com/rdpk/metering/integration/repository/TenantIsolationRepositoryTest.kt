package com.rdpk.metering.integration.repository

import com.rdpk.metering.domain.UsageEvent
import com.rdpk.metering.integration.AbstractKotestIntegrationTest
import com.rdpk.metering.repository.UsageEventRepository
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
    lateinit var tenantRepository: com.rdpk.metering.repository.TenantRepository

    @Autowired
    lateinit var customerRepository: com.rdpk.metering.repository.CustomerRepository

    init {
        describe("Tenant Isolation in Repository Queries") {

            it("should find events by tenant and customer - respects tenant isolation") {
                // Setup: Create two tenants with customers
                // Note: cleanupDatabase() is called in beforeSpec, so no need to call it here
                val now = LocalDateTime.now(clock)
                val uniqueSuffix = System.currentTimeMillis()
                
                val tenantA = com.rdpk.metering.domain.Tenant(
                    name = "Tenant A Isolation $uniqueSuffix",
                    active = true,
                    created = now,
                    updated = now
                )
                val savedTenantA = tenantRepository.save(tenantA).block()!!
                val tenantAId = savedTenantA.id!!

                val tenantB = com.rdpk.metering.domain.Tenant(
                    name = "Tenant B Isolation $uniqueSuffix",
                    active = true,
                    created = now,
                    updated = now
                )
                val savedTenantB = tenantRepository.save(tenantB).block()!!
                val tenantBId = savedTenantB.id!!

                val customerA1 = com.rdpk.metering.domain.Customer(
                    tenantId = tenantAId,
                    externalId = "customer-a1-isolation-$uniqueSuffix",
                    name = "Customer A1",
                    created = now,
                    updated = now
                )
                val savedCustomerA1 = customerRepository.save(customerA1).block()!!
                val customerA1Id = savedCustomerA1.id!!

                val customerB1 = com.rdpk.metering.domain.Customer(
                    tenantId = tenantBId,
                    externalId = "customer-b1-isolation-$uniqueSuffix",
                    name = "Customer B1",
                    created = now,
                    updated = now
                )
                val savedCustomerB1 = customerRepository.save(customerB1).block()!!
                val customerB1Id = savedCustomerB1.id!!

                // Store events for both tenants
                val eventA1 = UsageEvent(
                    eventId = "event-a1-isolation-$uniqueSuffix",
                    tenantId = tenantAId,
                    customerId = customerA1Id,
                    timestamp = clock.instant(),
                    data = mapOf(
                        "endpoint" to "/api/completion",
                        "tokens" to 100,
                        "tenant" to "A"
                    )
                )

                val eventB1 = UsageEvent(
                    eventId = "event-b1-isolation-$uniqueSuffix",
                    tenantId = tenantBId,
                    customerId = customerB1Id,
                    timestamp = clock.instant(),
                    data = mapOf(
                        "endpoint" to "/api/completion",
                        "tokens" to 200,
                        "tenant" to "B"
                    )
                )

                val nowInstant = clock.instant()
                val start = nowInstant.minusSeconds(3600) // 1 hour ago
                val end = nowInstant.plusSeconds(3600) // 1 hour from now

                // Save events
                repository.save(eventA1).block()
                repository.save(eventB1).block()

                // Query Tenant A's events using required method with time range
                val tenantAEvents = repository.findByTenantIdAndCustomerIdAndTimestampBetween(tenantAId, customerA1Id, start, end)
                    .collectList()
                    .block()
                
                tenantAEvents shouldNotBe null
                tenantAEvents!! shouldHaveSize 1
                tenantAEvents[0].tenantId shouldBe tenantAId
                tenantAEvents[0].eventId shouldBe eventA1.eventId
                tenantAEvents[0].data["tenant"] shouldBe "A"
                // Verify Tenant B's events are not included
                tenantAEvents.none { it.tenantId == tenantBId } shouldBe true

                // Query Tenant B's events using required method with time range
                val tenantBEvents = repository.findByTenantIdAndCustomerIdAndTimestampBetween(tenantBId, customerB1Id, start, end)
                    .collectList()
                    .block()
                
                tenantBEvents shouldNotBe null
                tenantBEvents!! shouldHaveSize 1
                tenantBEvents[0].tenantId shouldBe tenantBId
                tenantBEvents[0].eventId shouldBe eventB1.eventId
                tenantBEvents[0].data["tenant"] shouldBe "B"
                // Verify Tenant A's events are not included
                tenantBEvents.none { it.tenantId == tenantAId } shouldBe true
            }

            it("should prevent cross-tenant queries - customer belongs to different tenant") {
                // Setup: Create two tenants with customers (use unique names to avoid conflicts)
                // Note: cleanupDatabase() is called in beforeSpec, so no need to call it here
                val now = LocalDateTime.now(clock)
                val uniqueSuffix = System.currentTimeMillis()
                val tenantA = createTenant("Tenant A Cross $uniqueSuffix", now)
                val tenantB = createTenant("Tenant B Cross $uniqueSuffix", now)
                val customerA1 = createCustomer(tenantA.id!!, "customer-a1-cross-$uniqueSuffix", now)
                val customerB1 = createCustomer(tenantB.id!!, "customer-b1-cross-$uniqueSuffix", now)

                // Store events for both
                val eventA = createEvent(tenantA.id!!, customerA1.id!!, "event-a-cross")
                val eventB = createEvent(tenantB.id!!, customerB1.id!!, "event-b-cross")

                // Save events
                repository.save(eventA).block()
                repository.save(eventB).block()

                // Verify events were saved correctly
                val savedEventA = repository.findByTenantIdAndCustomerIdAndTimestampBetween(
                    tenantA.id!!, customerA1.id!!, 
                    clock.instant().minusSeconds(3600), 
                    clock.instant().plusSeconds(3600)
                ).collectList().block()
                
                savedEventA shouldNotBe null
                savedEventA!! shouldHaveSize 1

                val savedEventB = repository.findByTenantIdAndCustomerIdAndTimestampBetween(
                    tenantB.id!!, customerB1.id!!, 
                    clock.instant().minusSeconds(3600), 
                    clock.instant().plusSeconds(3600)
                ).collectList().block()
                
                savedEventB shouldNotBe null
                savedEventB!! shouldHaveSize 1

                // Try to query Tenant A's ID with Tenant B's customer ID
                // This should return empty (customer belongs to different tenant)
                // The query filters by BOTH tenantId AND customerId, so it should return empty
                // IMPORTANT: customerB1 belongs to tenantB, so querying with tenantA.id should return nothing
                val nowInstant = clock.instant()
                val start = nowInstant.minusSeconds(3600) // 1 hour ago
                val end = nowInstant.plusSeconds(3600) // 1 hour from now
                
                // Verify customerB1 actually belongs to tenantB (data integrity check)
                val customerB1FromDb = customerRepository.findById(customerB1.id!!).block()
                if (customerB1FromDb?.tenantId != tenantB.id) {
                    throw IllegalStateException("Data integrity issue: customerB1.tenantId=${customerB1FromDb?.tenantId} but tenantB.id=${tenantB.id}")
                }
                
                // Query should return empty - customerB1 belongs to tenantB, not tenantA
                // The query filters by BOTH tenantId AND customerId, so mismatched tenant/customer should return empty
                // Verify the query parameters are correct before querying
                if (tenantA.id == tenantB.id) {
                    throw IllegalStateException("Test setup error: tenantA.id (${tenantA.id}) == tenantB.id (${tenantB.id})")
                }
                if (customerB1FromDb?.tenantId == tenantA.id) {
                    throw IllegalStateException("Test setup error: customerB1.tenantId (${customerB1FromDb.tenantId}) == tenantA.id (${tenantA.id})")
                }
                
                // Query should return empty - customerB1 belongs to tenantB, not tenantA
                // The query filters by BOTH tenantId AND customerId, so mismatched tenant/customer should return empty
                // Since customerB1.tenantId = tenantB.id != tenantA.id, no event can match both conditions
                val queryTenantId = tenantA.id!!
                val queryCustomerId = customerB1.id!!
                
                // Double-check our assumptions
                val customerB1Verified = customerRepository.findById(queryCustomerId).block()
                if (customerB1Verified?.tenantId != tenantB.id) {
                    throw IllegalStateException("Test data corrupted: customerB1.tenantId=${customerB1Verified?.tenantId} but expected ${tenantB.id}")
                }
                
                val result = repository.findByTenantIdAndCustomerIdAndTimestampBetween(queryTenantId, queryCustomerId, start, end)
                    .collectList()
                    .block()
                
                // Verify result is empty - if not, there's a bug in the query
                if (result != null && result.isNotEmpty()) {
                    val firstEvent = result.first()
                    throw AssertionError(
                        "Query returned ${result.size} event(s) when it should return 0. " +
                        "Query params: tenantId=$queryTenantId, customerId=$queryCustomerId. " +
                        "customerB1.tenantId=${customerB1Verified?.tenantId}, tenantB.id=${tenantB.id}, tenantA.id=$queryTenantId. " +
                        "First returned event: tenantId=${firstEvent.tenantId}, customerId=${firstEvent.customerId}, eventId=${firstEvent.eventId}"
                    )
                }
                
                result shouldNotBe null
                result!! shouldHaveSize 0
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
            data = mapOf(
                "endpoint" to "/api/completion",
                "tokens" to 100,
                "test" to "data"
            )
        )
    }
}

