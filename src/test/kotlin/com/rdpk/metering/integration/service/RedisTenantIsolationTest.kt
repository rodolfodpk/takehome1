package com.rdpk.metering.integration.service

import com.rdpk.metering.domain.UsageEvent
import com.rdpk.metering.integration.AbstractKotestIntegrationTest
import com.rdpk.metering.repository.CustomerRepository
import com.rdpk.metering.repository.TenantRepository
import com.rdpk.metering.service.RedisEventStorageService
import com.rdpk.metering.service.RedisStateService
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier
import java.time.Instant
import java.time.LocalDateTime

/**
 * Integration tests for Redis tenant isolation
 * Verifies that Redis state (counters, event buffer) is properly isolated per tenant
 * Uses real Redis via Testcontainers - NO MOCKS
 */
class RedisTenantIsolationTest : AbstractKotestIntegrationTest() {

    @Autowired
    lateinit var tenantRepository: TenantRepository

    @Autowired
    lateinit var customerRepository: CustomerRepository

    @Autowired
    lateinit var redisStateService: RedisStateService

    @Autowired
    lateinit var redisEventStorageService: RedisEventStorageService

    init {
        describe("Redis Tenant Isolation") {

            it("should isolate Redis counters per tenant") {
                // Setup: Create two tenants with customers
                val now = LocalDateTime.now(clock)
                val tenantA = createTenant("Tenant A", now)
                val tenantB = createTenant("Tenant B", now)
                val customerA1 = createCustomer(tenantA.id!!, "customer-a1", now)
                val customerB1 = createCustomer(tenantB.id!!, "customer-b1", now)

                // Create events for both tenants
                val eventA1 = createEvent(tenantA.id!!, customerA1.id!!, "event-a1", tokens = 100)
                val eventA2 = createEvent(tenantA.id!!, customerA1.id!!, "event-a2", tokens = 200)
                val eventB1 = createEvent(tenantB.id!!, customerB1.id!!, "event-b1", tokens = 300)

                // Update counters for both tenants
                StepVerifier.create(
                    redisStateService.updateCounters(eventA1)
                        .then(redisStateService.updateCounters(eventA2))
                        .then(redisStateService.updateCounters(eventB1))
                ).verifyComplete()

                // Get Tenant A's counters
                StepVerifier.create(
                    redisStateService.getCounters(tenantA.id!!, customerA1.id!!)
                )
                    .assertNext { counters ->
                        // Verify Tenant A's counters (100 + 200 = 300 tokens, 2 calls)
                        counters.tokens shouldBe 300L
                        counters.calls shouldBe 2L
                    }
                    .verifyComplete()

                // Get Tenant B's counters
                StepVerifier.create(
                    redisStateService.getCounters(tenantB.id!!, customerB1.id!!)
                )
                    .assertNext { counters ->
                        // Verify Tenant B's counters (300 tokens, 1 call)
                        counters.tokens shouldBe 300L
                        counters.calls shouldBe 1L
                        // Verify Tenant A's events don't affect Tenant B's counters
                    }
                    .verifyComplete()
            }

            it("should verify Redis counter keys include tenant_id") {
                // Setup: Create two tenants with same customer external ID
                val now = LocalDateTime.now(clock)
                val tenantA = createTenant("Tenant A", now)
                val tenantB = createTenant("Tenant B", now)
                val customerA1 = createCustomer(tenantA.id!!, "customer-1", now) // Same external ID
                val customerB1 = createCustomer(tenantB.id!!, "customer-1", now) // Same external ID

                // Create events
                val eventA = createEvent(tenantA.id!!, customerA1.id!!, "event-a", tokens = 100)
                val eventB = createEvent(tenantB.id!!, customerB1.id!!, "event-b", tokens = 200)

                // Update counters
                StepVerifier.create(
                    redisStateService.updateCounters(eventA)
                        .then(redisStateService.updateCounters(eventB))
                ).verifyComplete()

                // Verify counters are isolated (keys include tenant_id)
                // Tenant A's counters
                StepVerifier.create(
                    redisStateService.getCounters(tenantA.id!!, customerA1.id!!)
                )
                    .assertNext { counters ->
                        counters.tokens shouldBe 100L
                        counters.calls shouldBe 1L
                    }
                    .verifyComplete()

                // Tenant B's counters (different tenant, same customer external ID)
                StepVerifier.create(
                    redisStateService.getCounters(tenantB.id!!, customerB1.id!!)
                )
                    .assertNext { counters ->
                        counters.tokens shouldBe 200L
                        counters.calls shouldBe 1L
                        // Verify Tenant A's counters don't affect Tenant B
                    }
                    .verifyComplete()
            }

            it("should isolate Redis event buffer per tenant") {
                // Setup: Create two tenants
                val now = LocalDateTime.now(clock)
                val tenantA = createTenant("Tenant A", now)
                val tenantB = createTenant("Tenant B", now)
                val customerA1 = createCustomer(tenantA.id!!, "customer-a1", now)
                val customerB1 = createCustomer(tenantB.id!!, "customer-b1", now)

                // Create events for both tenants
                val eventA = createEvent(tenantA.id!!, customerA1.id!!, "event-a")
                val eventB = createEvent(tenantB.id!!, customerB1.id!!, "event-b")

                // Store events in Redis buffer
                StepVerifier.create(
                    redisEventStorageService.storeEvent(eventA)
                        .then(redisEventStorageService.storeEvent(eventB))
                ).verifyComplete()

                // Get pending events (should include both, but verify tenant_id is correct)
                StepVerifier.create(
                    redisEventStorageService.getPendingEvents(100)
                )
                    .assertNext { events ->
                        // Verify both events are in buffer
                        events shouldHaveSize 2
                        // Verify events have correct tenant_id
                        val eventAFromRedis = events.find { it.eventId == eventA.eventId }
                        val eventBFromRedis = events.find { it.eventId == eventB.eventId }
                        
                        eventAFromRedis shouldNotBe null
                        eventAFromRedis!!.tenantId shouldBe tenantA.id
                        eventAFromRedis.customerId shouldBe customerA1.id
                        
                        eventBFromRedis shouldNotBe null
                        eventBFromRedis!!.tenantId shouldBe tenantB.id
                        eventBFromRedis.customerId shouldBe customerB1.id
                    }
                    .verifyComplete()
            }

            it("should clear counters independently per tenant") {
                // Setup: Create two tenants
                val now = LocalDateTime.now(clock)
                val tenantA = createTenant("Tenant A", now)
                val tenantB = createTenant("Tenant B", now)
                val customerA1 = createCustomer(tenantA.id!!, "customer-a1", now)
                val customerB1 = createCustomer(tenantB.id!!, "customer-b1", now)

                // Update counters for both tenants
                val eventA = createEvent(tenantA.id!!, customerA1.id!!, "event-a", tokens = 100)
                val eventB = createEvent(tenantB.id!!, customerB1.id!!, "event-b", tokens = 200)

                StepVerifier.create(
                    redisStateService.updateCounters(eventA)
                        .then(redisStateService.updateCounters(eventB))
                ).verifyComplete()

                // Clear Tenant A's counters
                StepVerifier.create(
                    redisStateService.clearCounters(tenantA.id!!, customerA1.id!!)
                ).verifyComplete()

                // Verify Tenant A's counters are cleared
                StepVerifier.create(
                    redisStateService.getCounters(tenantA.id!!, customerA1.id!!)
                )
                    .assertNext { counters ->
                        counters.tokens shouldBe 0L
                        counters.calls shouldBe 0L
                    }
                    .verifyComplete()

                // Verify Tenant B's counters are still intact
                StepVerifier.create(
                    redisStateService.getCounters(tenantB.id!!, customerB1.id!!)
                )
                    .assertNext { counters ->
                        counters.tokens shouldBe 200L
                        counters.calls shouldBe 1L
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

    private fun createEvent(
        tenantId: Long,
        customerId: Long,
        eventIdPrefix: String,
        timestamp: Instant = clock.instant(),
        tokens: Int = 100
    ): UsageEvent {
        return UsageEvent(
            eventId = "$eventIdPrefix-${System.currentTimeMillis()}",
            tenantId = tenantId,
            customerId = customerId,
            timestamp = timestamp,
            endpoint = "/api/completion",
            tokens = tokens,
            metadata = mapOf(
                "tokens" to tokens,
                "inputTokens" to (tokens / 2),
                "outputTokens" to (tokens / 2)
            )
        )
    }
}

