package com.rdpk.metering.config

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator
import io.github.resilience4j.reactor.retry.RetryOperator
import io.github.resilience4j.reactor.timelimiter.TimeLimiterOperator
import io.github.resilience4j.retry.RetryRegistry
import io.github.resilience4j.timelimiter.TimeLimiterRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Configuration for Resilience4j circuit breakers, retry, and timeouts
 * Provides resilience patterns for both PostgreSQL and Redis operations
 */
@Configuration
class ResilienceConfig {

    /**
     * Service for applying resilience patterns to reactive operations
     */
    @Bean
    fun resilienceService(
        circuitBreakerRegistry: CircuitBreakerRegistry,
        retryRegistry: RetryRegistry,
        timeLimiterRegistry: TimeLimiterRegistry
    ): ResilienceService {
        return ResilienceService(circuitBreakerRegistry, retryRegistry, timeLimiterRegistry)
    }
}

/**
 * Service for applying circuit breakers, retry, and timeouts to reactive operations
 * Uses Reactor transformers for proper integration with Mono/Flux
 */
class ResilienceService(
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
    private val retryRegistry: RetryRegistry,
    private val timeLimiterRegistry: TimeLimiterRegistry
) {
    
    /**
     * Apply resilience patterns to PostgreSQL operations (Mono)
     * Uses "postgres" circuit breaker, retry, and time limiter
     */
    fun <T> applyPostgresResilience(mono: Mono<T>): Mono<T> {
        return mono
            .transformDeferred(
                CircuitBreakerOperator.of(circuitBreakerRegistry.circuitBreaker("postgres"))
            )
            .transformDeferred(
                RetryOperator.of(retryRegistry.retry("postgres"))
            )
            .transformDeferred(
                TimeLimiterOperator.of(timeLimiterRegistry.timeLimiter("postgres"))
            )
    }
    
    /**
     * Apply resilience patterns to PostgreSQL operations (Flux)
     * Uses "postgres" circuit breaker, retry, and time limiter
     */
    fun <T> applyPostgresResilience(flux: Flux<T>): Flux<T> {
        return flux
            .transformDeferred(
                CircuitBreakerOperator.of(circuitBreakerRegistry.circuitBreaker("postgres"))
            )
            .transformDeferred(
                RetryOperator.of(retryRegistry.retry("postgres"))
            )
            .transformDeferred(
                TimeLimiterOperator.of(timeLimiterRegistry.timeLimiter("postgres"))
            )
    }
    
    /**
     * Apply resilience patterns to Redis operations (Mono)
     * Uses "redis" circuit breaker, retry, and time limiter
     */
    fun <T> applyRedisResilience(mono: Mono<T>): Mono<T> {
        return mono
            .transformDeferred(
                CircuitBreakerOperator.of(circuitBreakerRegistry.circuitBreaker("redis"))
            )
            .transformDeferred(
                RetryOperator.of(retryRegistry.retry("redis"))
            )
            .transformDeferred(
                TimeLimiterOperator.of(timeLimiterRegistry.timeLimiter("redis"))
            )
    }
    
    /**
     * Apply resilience patterns to Redis operations (Flux)
     * Uses "redis" circuit breaker, retry, and time limiter
     */
    fun <T> applyRedisResilience(flux: Flux<T>): Flux<T> {
        return flux
            .transformDeferred(
                CircuitBreakerOperator.of(circuitBreakerRegistry.circuitBreaker("redis"))
            )
            .transformDeferred(
                RetryOperator.of(retryRegistry.retry("redis"))
            )
            .transformDeferred(
                TimeLimiterOperator.of(timeLimiterRegistry.timeLimiter("redis"))
            )
    }
}

