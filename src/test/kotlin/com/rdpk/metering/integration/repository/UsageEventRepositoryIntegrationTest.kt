package com.rdpk.metering.integration.repository

import com.rdpk.metering.domain.UsageEvent
import com.rdpk.metering.integration.AbstractKotestIntegrationTest
import com.rdpk.metering.repository.UsageEventRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier
import java.time.Instant
import java.time.LocalDateTime
import java.time.Clock

/**
 * Integration tests for UsageEventRepository
 * Uses real PostgreSQL via Testcontainers - NO MOCKS
 * Using Kotest BDD style with Spring Extension
 * 
 * Tests run sequentially (in order) to support multi-step scenarios
 * Database and Redis are cleaned before the test class runs
 */
class UsageEventRepositoryIntegrationTest : AbstractKotestIntegrationTest() {

    @Autowired
    lateinit var repository: UsageEventRepository

    @Autowired
    lateinit var tenantRepository: com.rdpk.metering.repository.TenantRepository

    @Autowired
    lateinit var customerRepository: com.rdpk.metering.repository.CustomerRepository

    init {
        describe("UsageEventRepository") {
        
        it("should find events by tenant and customer with time range") {
            // Setup: Create test tenant and customers
            val nowLocal = LocalDateTime.now(clock)
            val tenant = com.rdpk.metering.domain.Tenant(
                name = "Test Tenant 2 ${System.currentTimeMillis()}",
                active = true,
                created = nowLocal,
                updated = nowLocal
            )
            val savedTenant = tenantRepository.save(tenant).block()!!
            val testTenantId = savedTenant.id ?: throw IllegalStateException("Tenant ID is null")

            val customer1 = com.rdpk.metering.domain.Customer(
                tenantId = testTenantId,
                externalId = "customer-1",
                name = "Test Customer 1",
                created = nowLocal,
                updated = nowLocal
            )
            val savedCustomer1 = customerRepository.save(customer1).block()!!
            val testCustomerId1 = savedCustomer1.id ?: throw IllegalStateException("Customer ID is null")

            val customer2 = com.rdpk.metering.domain.Customer(
                tenantId = testTenantId,
                externalId = "customer-2",
                name = "Test Customer 2",
                created = nowLocal,
                updated = nowLocal
            )
            val savedCustomer2 = customerRepository.save(customer2).block()!!
            val testCustomerId2 = savedCustomer2.id ?: throw IllegalStateException("Customer ID is null")
            
            val now = clock.instant()
            val start = now.minusSeconds(3600) // 1 hour ago
            val end = now.plusSeconds(3600) // 1 hour from now
            
            val uniqueId = System.currentTimeMillis()
            val event1 = UsageEvent(
                eventId = "event-1-$uniqueId",
                tenantId = testTenantId,
                customerId = testCustomerId1,
                timestamp = now,
                data = mapOf("endpoint" to "/api/completion")
                // created and updated are @ReadOnlyProperty - managed by database defaults
            )
            val event2 = UsageEvent(
                eventId = "event-2-$uniqueId",
                tenantId = testTenantId,
                customerId = testCustomerId1,
                timestamp = now,
                data = mapOf("endpoint" to "/api/embedding")
                // created and updated are @ReadOnlyProperty - managed by database defaults
            )
            val event3 = UsageEvent(
                eventId = "event-3-$uniqueId",
                tenantId = testTenantId,
                customerId = testCustomerId2, // Different customer
                timestamp = now,
                data = mapOf("endpoint" to "/api/completion")
                // created and updated are @ReadOnlyProperty - managed by database defaults
            )

            StepVerifier.create(
                repository.saveAll(listOf(event1, event2, event3))
                    .thenMany(
                        repository.findByTenantIdAndCustomerIdAndTimestampBetween(testTenantId, testCustomerId1, start, end)
                    )
            )
                .expectNextCount(2)
                .verifyComplete()
        }

        it("should find events by timestamp range") {
            // Setup: Create test tenant and customer
            val nowLocal = LocalDateTime.now(clock)
            val tenant = com.rdpk.metering.domain.Tenant(
                name = "Test Tenant 3 ${System.currentTimeMillis()}",
                active = true,
                created = nowLocal,
                updated = nowLocal
            )
            val savedTenant = tenantRepository.save(tenant).block()!!
            val testTenantId = savedTenant.id ?: throw IllegalStateException("Tenant ID is null")

            val customer = com.rdpk.metering.domain.Customer(
                tenantId = testTenantId,
                externalId = "customer-1",
                name = "Test Customer",
                created = nowLocal,
                updated = nowLocal
            )
            val savedCustomer = customerRepository.save(customer).block()!!
            val testCustomerId = savedCustomer.id ?: throw IllegalStateException("Customer ID is null")
            
            val now = clock.instant()
            val uniqueId = System.currentTimeMillis()
            val event1 = UsageEvent(
                eventId = "event-1-$uniqueId",
                tenantId = testTenantId,
                customerId = testCustomerId,
                timestamp = now.minusSeconds(60),
                data = mapOf("endpoint" to "/api/completion")
                // created and updated are @ReadOnlyProperty - managed by database defaults
            )
            val event2 = UsageEvent(
                eventId = "event-2-$uniqueId",
                tenantId = testTenantId,
                customerId = testCustomerId,
                timestamp = now.minusSeconds(30),
                data = mapOf("endpoint" to "/api/completion")
                // created and updated are @ReadOnlyProperty - managed by database defaults
            )
            val event3 = UsageEvent(
                eventId = "event-3-$uniqueId",
                tenantId = testTenantId,
                customerId = testCustomerId,
                timestamp = now.plusSeconds(30), // Outside range
                data = mapOf("endpoint" to "/api/completion")
                // created and updated are @ReadOnlyProperty - managed by database defaults
            )

            val start = now.minusSeconds(90)
            val end = now

            StepVerifier.create(
                repository.saveAll(listOf(event1, event2, event3))
                    .thenMany(
                        repository.findByTenantIdAndCustomerIdAndTimestampBetween(testTenantId, testCustomerId, start, end)
                    )
            )
                .expectNextCount(2)
                .verifyComplete()
        }
        }
    }
}
