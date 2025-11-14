package com.rdpk.metering.scheduler

import com.rdpk.metering.service.LateEventProcessingService
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

/**
 * Scheduler for processing late events
 * Delegates to LateEventProcessingService for the actual business logic
 * Can be disabled by setting metering.schedulers.enabled=false
 */
@Component
@ConditionalOnProperty(name = ["metering.schedulers.enabled"], havingValue = "true", matchIfMissing = true)
class LateEventProcessor(
    private val lateEventProcessingService: LateEventProcessingService,
    @Value("\${metering.late-event.batch-size:100}")
    private val batchSize: Int
) {
    
    private val log = LoggerFactory.getLogger(javaClass)
    
    /**
     * Process late events periodically
     * Default: 5 minutes (300000ms)
     * Configure via: metering.late-event.processing-interval-ms
     * Can be disabled by setting metering.schedulers.enabled=false
     */
    @Scheduled(fixedRateString = "\${metering.late-event.processing-interval-ms:300000}")
    fun processLateEvents() {
        lateEventProcessingService.processLateEvents(batchSize)
            .subscribe(
                {},
                { error ->
                    log.error("Error in late event processor", error)
                }
            )
    }
}

