package com.rdpk.metering.integration

import com.rdpk.metering.domain.UsageEvent
import com.rdpk.metering.integration.AbstractKotestIntegrationTest
import com.rdpk.metering.repository.UsageEventRepository
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import reactor.test.StepVerifier
import java.time.Instant
import java.time.LocalDateTime

/**
 * Simple test to verify Map<String, Any> -> JSONB conversion
 */
class JsonbMapTest : AbstractKotestIntegrationTest() {

    @Autowired
    lateinit var repository: UsageEventRepository

    @Autowired
    lateinit var tenantRepository: com.rdpk.metering.repository.TenantRepository

    @Autowired
    lateinit var customerRepository: com.rdpk.metering.repository.CustomerRepository

    @Autowired
    lateinit var databaseClient: DatabaseClient

    @Autowired
    lateinit var objectMapper: ObjectMapper

    init {
        describe("JSONB Map conversion") {
            
            it("should save and retrieve Map<String, Any> as JSONB") {
                // Setup: Create test tenant and customer
                val now = LocalDateTime.now(clock)
                val tenant = com.rdpk.metering.domain.Tenant(
                    name = "Test Tenant ${System.currentTimeMillis()}",
                    active = true,
                    created = now,
                    updated = now
                )
                val savedTenant = tenantRepository.save(tenant).block()!!
                val testTenantId = savedTenant.id ?: throw IllegalStateException("Tenant ID is null")

                val customer = com.rdpk.metering.domain.Customer(
                    tenantId = testTenantId,
                    externalId = "customer-1",
                    name = "Test Customer",
                    created = now,
                    updated = now
                )
                val savedCustomer = customerRepository.save(customer).block()!!
                val testCustomerId = savedCustomer.id ?: throw IllegalStateException("Customer ID is null")
                
                // Create event with Map metadata
                val eventId = "test-jsonb-${System.currentTimeMillis()}"
                val metadata = mapOf(
                    "tokens" to 100,
                    "model" to "gpt-4",
                    "latencyMs" to 250,
                    "inputTokens" to 50,
                    "outputTokens" to 50
                )
                
                val event = UsageEvent(
                    eventId = eventId,
                    tenantId = testTenantId,
                    customerId = testCustomerId,
                    timestamp = clock.instant(),
                    endpoint = "/api/completion",
                    tokens = 100,
                    model = "gpt-4",
                    latencyMs = 250,
                    metadata = metadata
                )

                // Save using custom method with explicit JSONB casting, then retrieve
                val extensions = com.rdpk.metering.repository.UsageEventRepositoryExtensionsImpl(
                    databaseClient, objectMapper
                )
                
                StepVerifier.create(
                    extensions.saveWithJsonb(event)
                        .then(repository.findByEventId(eventId))
                )
                    .assertNext { found ->
                        found.eventId shouldBe eventId
                        found.metadata shouldNotBe null
                        found.metadata?.get("tokens") shouldBe 100
                        found.metadata?.get("model") shouldBe "gpt-4"
                        found.metadata?.get("latencyMs") shouldBe 250
                        found.metadata?.get("inputTokens") shouldBe 50
                        found.metadata?.get("outputTokens") shouldBe 50
                    }
                    .verifyComplete()
            }
        }
    }
}

