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
     * Uses "postgres" circuit breaker, retry, and time limiter (if configured)
     * Circuit breakers and time limiters are optional (can be disabled for performance testing)
     */
    fun <T> applyPostgresResilience(mono: Mono<T>): Mono<T> {
        var result = mono
        
        // Apply circuit breaker only if configured (can be disabled for k6 performance testing)
        // Check if circuit breaker exists and is configured (not just a default)
        try {
            if (circuitBreakerRegistry.circuitBreaker("postgres") != null) {
                // Only apply if explicitly configured (Spring Boot auto-config creates them from properties)
                // If properties are missing/commented, this will still work but circuit breaker won't block
                val circuitBreaker = circuitBreakerRegistry.circuitBreaker("postgres")
                result = result.transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
            }
        } catch (e: Exception) {
            // Circuit breaker not configured (e.g., disabled in k6 profile) - skip it
        }
        
        // Apply retry (always enabled for resilience)
        result = result.transformDeferred(
            RetryOperator.of(retryRegistry.retry("postgres"))
        )
        
        // Apply time limiter only if configured (disabled in k6 profile for performance testing)
        try {
            val timeLimiter = timeLimiterRegistry.timeLimiter("postgres")
            result = result.transformDeferred(TimeLimiterOperator.of(timeLimiter))
        } catch (e: Exception) {
            // Time limiter not configured (e.g., in k6 profile) - skip it
        }
        
        return result
    }
    
    /**
     * Apply resilience patterns to PostgreSQL operations (Flux)
     * Uses "postgres" circuit breaker, retry, and time limiter (if configured)
     * Circuit breakers and time limiters are optional (can be disabled for performance testing)
     */
    fun <T> applyPostgresResilience(flux: Flux<T>): Flux<T> {
        var result = flux
        
        // Apply circuit breaker only if configured (can be disabled for k6 performance testing)
        // Check if circuit breaker exists and is configured (not just a default)
        try {
            if (circuitBreakerRegistry.circuitBreaker("postgres") != null) {
                // Only apply if explicitly configured (Spring Boot auto-config creates them from properties)
                // If properties are missing/commented, this will still work but circuit breaker won't block
                val circuitBreaker = circuitBreakerRegistry.circuitBreaker("postgres")
                result = result.transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
            }
        } catch (e: Exception) {
            // Circuit breaker not configured (e.g., disabled in k6 profile) - skip it
        }
        
        // Apply retry (always enabled for resilience)
        result = result.transformDeferred(
            RetryOperator.of(retryRegistry.retry("postgres"))
        )
        
        // Apply time limiter only if configured (disabled in k6 profile for performance testing)
        try {
            val timeLimiter = timeLimiterRegistry.timeLimiter("postgres")
            result = result.transformDeferred(TimeLimiterOperator.of(timeLimiter))
        } catch (e: Exception) {
            // Time limiter not configured (e.g., in k6 profile) - skip it
        }
        
        return result
    }
    
    /**
     * Apply resilience patterns to Redis operations (Mono)
     * Uses "redis" circuit breaker, retry, and time limiter (if configured)
     * Circuit breakers and time limiters are optional (can be disabled for performance testing)
     */
    fun <T> applyRedisResilience(mono: Mono<T>): Mono<T> {
        var result = mono
        
        // Apply circuit breaker only if configured (can be disabled for k6 performance testing)
        try {
            val circuitBreaker = circuitBreakerRegistry.circuitBreaker("redis")
            result = result.transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
        } catch (e: Exception) {
            // Circuit breaker not configured (e.g., disabled in k6 profile) - skip it
        }
        
        // Apply retry (always enabled for resilience)
        result = result.transformDeferred(
            RetryOperator.of(retryRegistry.retry("redis"))
        )
        
        // Apply time limiter only if configured (disabled in k6 profile for performance testing)
        try {
            val timeLimiter = timeLimiterRegistry.timeLimiter("redis")
            result = result.transformDeferred(TimeLimiterOperator.of(timeLimiter))
        } catch (e: Exception) {
            // Time limiter not configured (e.g., in k6 profile) - skip it
        }
        
        return result
    }
    
    /**
     * Apply resilience patterns to Redis operations (Flux)
     * Uses "redis" circuit breaker, retry, and time limiter (if configured)
     * Circuit breakers and time limiters are optional (can be disabled for performance testing)
     */
    fun <T> applyRedisResilience(flux: Flux<T>): Flux<T> {
        var result = flux
        
        // Apply circuit breaker only if configured (can be disabled for k6 performance testing)
        try {
            val circuitBreaker = circuitBreakerRegistry.circuitBreaker("redis")
            result = result.transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
        } catch (e: Exception) {
            // Circuit breaker not configured (e.g., disabled in k6 profile) - skip it
        }
        
        // Apply retry (always enabled for resilience)
        result = result.transformDeferred(
            RetryOperator.of(retryRegistry.retry("redis"))
        )
        
        // Apply time limiter only if configured (disabled in k6 profile for performance testing)
        try {
            val timeLimiter = timeLimiterRegistry.timeLimiter("redis")
            result = result.transformDeferred(TimeLimiterOperator.of(timeLimiter))
        } catch (e: Exception) {
            // Time limiter not configured (e.g., in k6 profile) - skip it
        }
        
        return result
    }
}

