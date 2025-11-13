package com.rdpk.metering.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

/**
 * Configuration for time management
 * Provides a singleton Clock bean for consistent time access across the application
 * 
 * Benefits:
 * - Enables time control in tests (use Clock.fixed() or Clock.offset())
 * - Consistent time source across all services
 * - Better testability for time-dependent logic (windows, late events, scheduling)
 */
@Configuration
class TimeConfig {

    /**
     * Provides a Clock bean for time operations
     * In production: uses system UTC clock
     * In tests: can be overridden with Clock.fixed() for deterministic testing
     */
    @Bean
    fun clock(): Clock {
        return Clock.systemUTC()
    }
}

