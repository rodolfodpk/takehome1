package com.rdpk.metering.integration.service

import com.rdpk.metering.domain.AggregationWindow
import com.rdpk.metering.domain.LateEvent
import com.rdpk.metering.domain.Tenant
import com.rdpk.metering.domain.Customer
import com.rdpk.metering.domain.UsageEvent
import com.rdpk.metering.integration.AbstractKotestIntegrationTest
import com.rdpk.metering.repository.AggregationWindowRepository
import com.rdpk.metering.repository.CustomerRepository
import com.rdpk.metering.repository.LateEventRepository
import com.rdpk.metering.repository.TenantRepository
import com.rdpk.metering.repository.UsageEventRepository
import com.rdpk.metering.service.LateEventProcessingService
import com.rdpk.metering.service.LateEventService
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier
import java.time.Instant
import java.time.LocalDateTime

/**
 * Integration tests for LateEventProcessingService
 * Tests the business logic for processing late events that arrived after their aggregation window closed
 * Uses real PostgreSQL and Redis via Testcontainers - NO MOCKS
 */
class LateEventProcessingServiceIntegrationTest : AbstractKotestIntegrationTest() {

    @Autowired
    lateinit var lateEventProcessingService: LateEventProcessingService

    @Autowired
    lateinit var lateEventRepository: LateEventRepository

    @Autowired
    lateinit var usageEventRepository: UsageEventRepository

    @Autowired
    lateinit var aggregationWindowRepository: AggregationWindowRepository

    @Autowired
    lateinit var tenantRepository: TenantRepository

    @Autowired
    lateinit var customerRepository: CustomerRepository

    @Autowired
    lateinit var lateEventService: LateEventService

    init {
        describe("LateEventProcessingService") {

            it("should process late event and create new aggregation window") {
                // Setup: Create tenant and customer
                val tenant = createTenant("Test Tenant")
                val customer = createCustomer(tenant.id!!, "customer-1")

                // Create a late event (event that arrived after its window closed)
                // Window is 30 seconds, so we need event to be > 1 minute late to be stored
                // Calculate window start using same truncation logic as LateEventProcessingService
                val eventTimestamp = clock.instant().minusSeconds(120).plusSeconds(10) // Event timestamp
                val windowStart = truncateToWindow(eventTimestamp) // Truncate to 30-second boundary
                val receivedTimestamp = clock.instant() // Received now (2 minutes late)

                // Create event and use LateEventService to properly serialize it
                val event = UsageEvent(
                    eventId = "late-1",
                    tenantId = tenant.id!!,
                    customerId = customer.id!!,
                    timestamp = eventTimestamp,
                    data = mapOf("tokens" to 100)
                )

                // Use LateEventService to store it (this properly serializes it)
                // This should return true (event is stored as late event)
                val isLate = lateEventService.checkAndHandleLateEvent(event, receivedTimestamp).block()!!
                isLate shouldBe true // Event is > 1 minute late, should be stored

                // Process late events
                StepVerifier.create(
                    lateEventProcessingService.processLateEvents(100)
                )
                    .verifyComplete()

                // Verify event was persisted to usage_events
                val persistedEvents = usageEventRepository.findByTenantIdAndCustomerIdAndTimestampBetween(
                    tenant.id!!, customer.id!!, windowStart, windowStart.plusSeconds(30)
                )
                    .collectList()
                    .block()!!

                persistedEvents shouldHaveSize 1
                persistedEvents[0].eventId shouldBe "late-1"

                // Verify aggregation window was created
                val window = aggregationWindowRepository.findByTenantIdAndCustomerIdAndWindowStart(
                    tenant.id!!, customer.id!!, windowStart
                ).block()

                window shouldNotBe null
                if (window != null) {
                    window.tenantId shouldBe tenant.id
                    window.customerId shouldBe customer.id
                }

                // Verify late event was deleted
                val remainingLateEvents = lateEventRepository.findAll().collectList().block()!!
                remainingLateEvents shouldHaveSize 0
            }

            it("should process late event and update existing aggregation window") {
                // Setup: Create tenant and customer
                val tenant = createTenant("Test Tenant 2")
                val customer = createCustomer(tenant.id!!, "customer-2")

                // Calculate window start using same truncation logic
                val eventTimestampForWindow = clock.instant().minusSeconds(120).plusSeconds(5)
                val windowStart = truncateToWindow(eventTimestampForWindow)
                val windowEnd = windowStart.plusSeconds(30)

                // Create an existing aggregation window
                val existingWindow = AggregationWindow(
                    tenantId = tenant.id!!,
                    customerId = customer.id!!,
                    windowStart = windowStart,
                    windowEnd = windowEnd,
                    aggregationData = """{"totalCalls":1,"totalTokens":50}""",
                    created = LocalDateTime.now(clock),
                    updated = LocalDateTime.now(clock)
                )
                aggregationWindowRepository.saveWithJsonb(
                    existingWindow.tenantId,
                    existingWindow.customerId,
                    existingWindow.windowStart,
                    existingWindow.windowEnd,
                    existingWindow.aggregationData,
                    existingWindow.created!!,
                    existingWindow.updated!!
                ).block()!!

                // Create an existing event in the window
                val existingEvent = UsageEvent(
                    eventId = "existing-1",
                    tenantId = tenant.id!!,
                    customerId = customer.id!!,
                    timestamp = windowStart.plusSeconds(5),
                    data = mapOf("tokens" to 50)
                )
                usageEventRepository.save(existingEvent).block()!!

                // Create a late event for the same window
                val eventTimestamp = windowStart.plusSeconds(15)
                val receivedTimestamp = clock.instant() // 2 minutes late
                
                val event = UsageEvent(
                    eventId = "late-2",
                    tenantId = tenant.id!!,
                    customerId = customer.id!!,
                    timestamp = eventTimestamp,
                    data = mapOf("tokens" to 100)
                )

                // Use LateEventService to store it (this properly serializes it)
                // This should return true (event is stored as late event)
                val isLate = lateEventService.checkAndHandleLateEvent(event, receivedTimestamp).block()!!
                isLate shouldBe true // Event is > 1 minute late, should be stored

                // Process late events
                StepVerifier.create(
                    lateEventProcessingService.processLateEvents(100)
                )
                    .verifyComplete()

                // Verify both events are in usage_events
                val persistedEvents = usageEventRepository.findByTenantIdAndCustomerIdAndTimestampBetween(
                    tenant.id!!, customer.id!!, windowStart, windowEnd
                )
                    .collectList()
                    .block()!!

                persistedEvents shouldHaveSize 2

                // Verify aggregation window was updated
                val updatedWindow = aggregationWindowRepository.findByTenantIdAndCustomerIdAndWindowStart(
                    tenant.id!!, customer.id!!, windowStart
                ).block()

                updatedWindow shouldNotBe null
                if (updatedWindow != null) {
                    updatedWindow.aggregationData shouldNotBe existingWindow.aggregationData
                    // Window should have been updated (re-aggregated with both events)
                    updatedWindow.updated!!.isAfter(existingWindow.updated!!) shouldBe true
                }

                // Verify late event was deleted
                val remainingLateEvents = lateEventRepository.findAll().collectList().block()!!
                remainingLateEvents shouldHaveSize 0
            }

            it("should handle multiple late events in batch") {
                // Setup
                val tenant = createTenant("Test Tenant 3")
                val customer = createCustomer(tenant.id!!, "customer-3")

                // Create two late events for different windows
                val eventTimestamp1 = clock.instant().minusSeconds(180).plusSeconds(10)
                val eventTimestamp2 = clock.instant().minusSeconds(150).plusSeconds(10)
                val windowStart1 = truncateToWindow(eventTimestamp1)
                val windowStart2 = truncateToWindow(eventTimestamp2)

                val event1 = UsageEvent(
                    eventId = "late-batch-1",
                    tenantId = tenant.id!!,
                    customerId = customer.id!!,
                    timestamp = eventTimestamp1,
                    data = mapOf("tokens" to 100)
                )

                val event2 = UsageEvent(
                    eventId = "late-batch-2",
                    tenantId = tenant.id!!,
                    customerId = customer.id!!,
                    timestamp = eventTimestamp2,
                    data = mapOf("tokens" to 200)
                )

                // Use LateEventService to store them (this properly serializes them)
                // Both events are > 1 minute late, should be stored
                val isLate1 = lateEventService.checkAndHandleLateEvent(event1, clock.instant()).block()!!
                val isLate2 = lateEventService.checkAndHandleLateEvent(event2, clock.instant()).block()!!
                isLate1 shouldBe true
                isLate2 shouldBe true

                // Process late events
                StepVerifier.create(
                    lateEventProcessingService.processLateEvents(100)
                )
                    .verifyComplete()

                // Verify both events were processed
                val persistedEvents1 = usageEventRepository.findByTenantIdAndCustomerIdAndTimestampBetween(
                    tenant.id!!, customer.id!!, windowStart1, windowStart1.plusSeconds(30)
                )
                    .collectList()
                    .block()!!

                val persistedEvents2 = usageEventRepository.findByTenantIdAndCustomerIdAndTimestampBetween(
                    tenant.id!!, customer.id!!, windowStart2, windowStart2.plusSeconds(30)
                )
                    .collectList()
                    .block()!!

                persistedEvents1 shouldHaveSize 1
                persistedEvents2 shouldHaveSize 1

                // Verify both aggregation windows were created
                val window1 = aggregationWindowRepository.findByTenantIdAndCustomerIdAndWindowStart(
                    tenant.id!!, customer.id!!, windowStart1
                ).block()

                val window2 = aggregationWindowRepository.findByTenantIdAndCustomerIdAndWindowStart(
                    tenant.id!!, customer.id!!, windowStart2
                ).block()

                window1 shouldNotBe null
                window2 shouldNotBe null
                if (window1 != null && window2 != null) {
                    window1.tenantId shouldBe tenant.id
                    window2.tenantId shouldBe tenant.id
                }

                // Verify late events were deleted
                val remainingLateEvents = lateEventRepository.findAll().collectList().block()!!
                remainingLateEvents shouldHaveSize 0
            }

            it("should handle invalid late event data gracefully") {
                // Setup
                val tenant = createTenant("Test Tenant 4")
                val customer = createCustomer(tenant.id!!, "customer-4")

                // Create a late event with invalid JSON data
                // Use saveWithJsonb directly to bypass validation
                lateEventRepository.saveWithJsonb(
                    eventId = "late-invalid",
                    originalTimestamp = clock.instant().minusSeconds(120),
                    receivedTimestamp = clock.instant(),
                    tenantId = tenant.id!!,
                    customerId = customer.id!!,
                    data = "invalid json"
                ).block()!!

                // Process late events - should handle gracefully
                StepVerifier.create(
                    lateEventProcessingService.processLateEvents(100)
                )
                    .verifyComplete()

                // Invalid event should remain in late_events (not processed)
                val remainingLateEvents = lateEventRepository.findAll().collectList().block()!!
                remainingLateEvents shouldHaveSize 1
                remainingLateEvents[0].eventId shouldBe "late-invalid"
            }

            it("should respect batch size limit") {
                // Setup
                val tenant = createTenant("Test Tenant 5")
                val customer = createCustomer(tenant.id!!, "customer-5")

                // Create 5 late events
                for (i in 1..5) {
                    val eventTimestamp = clock.instant().minusSeconds(120).plusSeconds(i.toLong()) // Different timestamps
                    val event = UsageEvent(
                        eventId = "late-batch-$i",
                        tenantId = tenant.id!!,
                        customerId = customer.id!!,
                        timestamp = eventTimestamp,
                        data = mapOf("tokens" to i.toLong())
                    )
                    // Use LateEventService to store them (this properly serializes them)
                    lateEventService.checkAndHandleLateEvent(event, clock.instant()).block()!!
                }

                // Process with batch size of 2
                StepVerifier.create(
                    lateEventProcessingService.processLateEvents(2)
                )
                    .verifyComplete()

                // Only 2 events should be processed
                val remainingLateEvents = lateEventRepository.findAll().collectList().block()!!
                remainingLateEvents shouldHaveSize 3 // 5 - 2 = 3 remaining
            }
        }
    }

    private fun createTenant(name: String): Tenant {
        val tenant = Tenant(
            name = "$name ${System.currentTimeMillis()}",
            active = true,
            created = LocalDateTime.now(clock),
            updated = LocalDateTime.now(clock)
        )
        return tenantRepository.save(tenant).block()!!
    }

    private fun createCustomer(tenantId: Long, externalId: String): Customer {
        val customer = Customer(
            tenantId = tenantId,
            externalId = externalId,
            name = "Customer $externalId",
            created = LocalDateTime.now(clock),
            updated = LocalDateTime.now(clock)
        )
        return customerRepository.save(customer).block()!!
    }
    
    /**
     * Truncate timestamp to window boundary (same logic as LateEventProcessingService)
     * Window duration is 30 seconds by default
     */
    private fun truncateToWindow(timestamp: Instant): Instant {
        val windowDurationSeconds = 30L
        val epochSeconds = timestamp.epochSecond
        val windowStartSeconds = (epochSeconds / windowDurationSeconds) * windowDurationSeconds
        return Instant.ofEpochSecond(windowStartSeconds)
    }
}

