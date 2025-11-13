package com.rdpk.metering.integration.service

import com.rdpk.metering.domain.AggregationWindow
import com.rdpk.metering.domain.UsageEvent
import com.fasterxml.jackson.databind.ObjectMapper
import com.rdpk.metering.integration.AbstractKotestIntegrationTest
import com.rdpk.metering.repository.AggregationWindowRepository
import com.rdpk.metering.repository.CustomerRepository
import com.rdpk.metering.repository.TenantRepository
import com.rdpk.metering.repository.UsageEventRepository
import com.rdpk.metering.service.AggregationService
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier
import java.time.Instant
import java.time.LocalDateTime

/**
 * Integration tests for multi-tenant aggregation isolation
 * Verifies that aggregations are correctly scoped per tenant and tenant data is isolated
 * Uses real PostgreSQL and Redis via Testcontainers - NO MOCKS
 */
class MultiTenantAggregationTest : AbstractKotestIntegrationTest() {

    @Autowired
    lateinit var tenantRepository: TenantRepository

    @Autowired
    lateinit var customerRepository: CustomerRepository

    @Autowired
    lateinit var usageEventRepository: UsageEventRepository

    @Autowired
    lateinit var aggregationWindowRepository: AggregationWindowRepository

    @Autowired
    lateinit var aggregationService: AggregationService
    
    @Autowired
    lateinit var objectMapper: ObjectMapper
    
    @Autowired
    lateinit var redisStateService: com.rdpk.metering.service.RedisStateService

    init {
        describe("Multi-Tenant Aggregation Isolation") {

            it("should create tenant-scoped aggregations") {
                // Setup: Create two tenants with customers
                val now = LocalDateTime.now(clock)
                val tenantA = createTenant("Tenant A", now)
                val tenantB = createTenant("Tenant B", now)
                val customerA1 = createCustomer(tenantA.id!!, "customer-a1", now)
                val customerB1 = createCustomer(tenantB.id!!, "customer-b1", now)

                // Create events for both tenants in same time window
                val windowStart = clock.instant().minusSeconds(30)
                val windowEnd = clock.instant()

                val eventA1 = createEvent(tenantA.id!!, customerA1.id!!, "event-a1", windowStart.plusSeconds(10))
                val eventA2 = createEvent(tenantA.id!!, customerA1.id!!, "event-a2", windowStart.plusSeconds(20))
                val eventB1 = createEvent(tenantB.id!!, customerB1.id!!, "event-b1", windowStart.plusSeconds(15))
                val eventB2 = createEvent(tenantB.id!!, customerB1.id!!, "event-b2", windowStart.plusSeconds(25))

                // Store events
                StepVerifier.create(
                    usageEventRepository.saveAll(listOf(eventA1, eventA2, eventB1, eventB2))
                        .then()
                ).verifyComplete()

                // Update Redis counters for Tenant A (aggregation reads from Redis)
                StepVerifier.create(
                    redisStateService.updateCounters(eventA1)
                        .then(redisStateService.updateCounters(eventA2))
                ).verifyComplete()

                // Update Redis counters for Tenant B
                StepVerifier.create(
                    redisStateService.updateCounters(eventB1)
                        .then(redisStateService.updateCounters(eventB2))
                ).verifyComplete()

                // Aggregate Tenant A's events
                StepVerifier.create(
                    aggregationService.aggregateWindow(
                        tenantA.id!!, customerA1.id!!, windowStart, windowEnd,
                        listOf(eventA1, eventA2)
                    )
                )
                    .assertNext { result ->
                        result.totalCalls shouldBe 2L
                        result.totalTokens shouldBe 200L // 100 + 100
                        // Verify Tenant B's events are not included
                    }
                    .verifyComplete()

                // Aggregate Tenant B's events
                StepVerifier.create(
                    aggregationService.aggregateWindow(
                        tenantB.id!!, customerB1.id!!, windowStart, windowEnd,
                        listOf(eventB1, eventB2)
                    )
                )
                    .assertNext { result ->
                        result.totalCalls shouldBe 2L
                        result.totalTokens shouldBe 200L // 100 + 100
                        // Verify Tenant A's events are not included
                    }
                    .verifyComplete()
            }

            it("should create separate aggregation windows for different tenants") {
                // Setup: Create two tenants with same customer external ID
                val now = LocalDateTime.now(clock)
                val tenantA = createTenant("Tenant A", now)
                val tenantB = createTenant("Tenant B", now)
                val customerA1 = createCustomer(tenantA.id!!, "customer-1", now) // Same external ID
                val customerB1 = createCustomer(tenantB.id!!, "customer-1", now) // Same external ID

                val windowStart = clock.instant().minusSeconds(30)
                val windowEnd = clock.instant()

                // Create events for both tenants
                val eventA = createEvent(tenantA.id!!, customerA1.id!!, "event-a", windowStart.plusSeconds(10))
                val eventB = createEvent(tenantB.id!!, customerB1.id!!, "event-b", windowStart.plusSeconds(15))

                // Store events
                StepVerifier.create(
                    usageEventRepository.saveAll(listOf(eventA, eventB))
                        .then()
                ).verifyComplete()

                // Update Redis counters for both tenants (aggregation reads from Redis)
                StepVerifier.create(
                    redisStateService.updateCounters(eventA)
                        .then(redisStateService.updateCounters(eventB))
                ).verifyComplete()

                // Create aggregation windows for both tenants
                val aggregationA = aggregationService.aggregateWindow(
                    tenantA.id!!, customerA1.id!!, windowStart, windowEnd, listOf(eventA)
                ).block()!!

                val aggregationB = aggregationService.aggregateWindow(
                    tenantB.id!!, customerB1.id!!, windowStart, windowEnd, listOf(eventB)
                ).block()!!

                val aggregationDataA = aggregationService.serializeAggregationData(aggregationA)
                val aggregationDataB = aggregationService.serializeAggregationData(aggregationB)

                val windowA = AggregationWindow(
                    tenantId = tenantA.id!!,
                    customerId = customerA1.id!!,
                    windowStart = windowStart,
                    windowEnd = windowEnd,
                    aggregationData = aggregationDataA,
                    created = now,
                    updated = now
                )

                val windowB = AggregationWindow(
                    tenantId = tenantB.id!!,
                    customerId = customerB1.id!!,
                    windowStart = windowStart,
                    windowEnd = windowEnd,
                    aggregationData = aggregationDataB,
                    created = now,
                    updated = now
                )

                // Save both aggregation windows using explicit JSONB casting
                StepVerifier.create(
                    aggregationWindowRepository.saveWithJsonb(
                        windowA.tenantId,
                        windowA.customerId,
                        windowA.windowStart,
                        windowA.windowEnd,
                        windowA.aggregationData,
                        windowA.created ?: now,
                        windowA.updated ?: now
                    )
                        .then(
                            aggregationWindowRepository.saveWithJsonb(
                                windowB.tenantId,
                                windowB.customerId,
                                windowB.windowStart,
                                windowB.windowEnd,
                                windowB.aggregationData,
                                windowB.created ?: now,
                                windowB.updated ?: now
                            )
                        )
                        .then() // Convert to Mono<Void> for verifyComplete
                ).verifyComplete()

                // Verify aggregation windows are created separately
                StepVerifier.create(
                    aggregationWindowRepository.findByTenantIdAndCustomerIdAndWindowStart(
                        tenantA.id!!, customerA1.id!!, windowStart
                    )
                )
                    .assertNext { window ->
                        window.tenantId shouldBe tenantA.id
                        window.customerId shouldBe customerA1.id
                        val data = objectMapper.readValue(window.aggregationData, Map::class.java) as Map<*, *>
                        // Handle Int to Long conversion (Jackson may deserialize small numbers as Int)
                        val totalCalls = (data["totalCalls"] as? Number)?.toLong() ?: 0L
                        val totalTokens = (data["totalTokens"] as? Number)?.toLong() ?: 0L
                        totalCalls shouldBe 1L
                        totalTokens shouldBe 100L
                    }
                    .verifyComplete()

                StepVerifier.create(
                    aggregationWindowRepository.findByTenantIdAndCustomerIdAndWindowStart(
                        tenantB.id!!, customerB1.id!!, windowStart
                    )
                )
                    .assertNext { window ->
                        window.tenantId shouldBe tenantB.id
                        window.customerId shouldBe customerB1.id
                        val data = objectMapper.readValue(window.aggregationData, Map::class.java) as Map<*, *>
                        // Handle Int to Long conversion (Jackson may deserialize small numbers as Int)
                        val totalCalls = (data["totalCalls"] as? Number)?.toLong() ?: 0L
                        val totalTokens = (data["totalTokens"] as? Number)?.toLong() ?: 0L
                        totalCalls shouldBe 1L
                        totalTokens shouldBe 100L
                    }
                    .verifyComplete()

                // Verify unique constraint works (same windowStart, different tenants)
                // Both windows should exist independently - verify using required method
                StepVerifier.create(
                    aggregationWindowRepository.findByTenantIdAndCustomerIdAndWindowStart(
                        tenantA.id!!, customerA1.id!!, windowStart
                    )
                )
                    .assertNext { window ->
                        window.tenantId shouldBe tenantA.id
                    }
                    .verifyComplete()

                StepVerifier.create(
                    aggregationWindowRepository.findByTenantIdAndCustomerIdAndWindowStart(
                        tenantB.id!!, customerB1.id!!, windowStart
                    )
                )
                    .assertNext { window ->
                        window.tenantId shouldBe tenantB.id
                    }
                    .verifyComplete()
            }

            it("should verify aggregations only include events from correct tenant") {
                // Setup: Create two tenants
                val now = LocalDateTime.now(clock)
                val tenantA = createTenant("Tenant A", now)
                val tenantB = createTenant("Tenant B", now)
                val customerA1 = createCustomer(tenantA.id!!, "customer-a1", now)
                val customerB1 = createCustomer(tenantB.id!!, "customer-b1", now)

                val windowStart = clock.instant().minusSeconds(30)
                val windowEnd = clock.instant()

                // Create events with different token counts to verify isolation
                val eventA1 = createEvent(tenantA.id!!, customerA1.id!!, "event-a1", windowStart.plusSeconds(10), tokens = 50)
                val eventA2 = createEvent(tenantA.id!!, customerA1.id!!, "event-a2", windowStart.plusSeconds(20), tokens = 150)
                val eventB1 = createEvent(tenantB.id!!, customerB1.id!!, "event-b1", windowStart.plusSeconds(15), tokens = 300)

                // Store events
                StepVerifier.create(
                    usageEventRepository.saveAll(listOf(eventA1, eventA2, eventB1))
                        .then()
                ).verifyComplete()

                // Update Redis counters for Tenant A (aggregation reads from Redis)
                StepVerifier.create(
                    redisStateService.updateCounters(eventA1)
                        .then(redisStateService.updateCounters(eventA2))
                ).verifyComplete()

                // Get Tenant A's events for aggregation
                StepVerifier.create(
                    usageEventRepository.findByTenantIdAndCustomerIdAndTimestampBetween(
                        tenantA.id!!, customerA1.id!!, windowStart, windowEnd
                    )
                        .collectList()
                )
                    .assertNext { events ->
                        // Verify only Tenant A's events are included
                        events shouldHaveSize 2
                        events.all { it.tenantId == tenantA.id } shouldBe true
                        events.none { it.tenantId == tenantB.id } shouldBe true
                        events.map { it.eventId } shouldContain eventA1.eventId
                        events.map { it.eventId } shouldContain eventA2.eventId
                        events.map { it.eventId }.none { it == eventB1.eventId } shouldBe true

                        // Verify events are correct (aggregation is tested separately to avoid blocking)
                    }
                    .verifyComplete()
                
                // Verify aggregation separately using reactive chain (no blocking)
                StepVerifier.create(
                    usageEventRepository.findByTenantIdAndCustomerIdAndTimestampBetween(
                        tenantA.id!!, customerA1.id!!, windowStart, windowEnd
                    )
                        .collectList()
                        .flatMap { events ->
                            aggregationService.aggregateWindow(
                                tenantA.id!!, customerA1.id!!, windowStart, windowEnd, events
                            )
                        }
                )
                    .assertNext { aggregation ->
                        // Verify aggregation totals are correct (only Tenant A's events)
                        aggregation.totalCalls shouldBe 2L
                        aggregation.totalTokens shouldBe 200L // 50 + 150
                        // Verify Tenant B's event (300 tokens) is NOT included
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
        timestamp: Instant,
        tokens: Int = 100
    ): UsageEvent {
        // Split tokens into input/output for realistic data
        val inputTokens = tokens / 2
        val outputTokens = tokens - inputTokens
        return UsageEvent(
            eventId = "$eventIdPrefix-${System.currentTimeMillis()}",
            tenantId = tenantId,
            customerId = customerId,
            timestamp = timestamp,
            data = mapOf(
                "endpoint" to "/api/completion",
                "tokens" to tokens,
                "inputTokens" to inputTokens,
                "outputTokens" to outputTokens,
                "test" to "data"
            )
        )
    }
}

