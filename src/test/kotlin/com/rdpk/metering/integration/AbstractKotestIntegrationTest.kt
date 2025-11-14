package com.rdpk.metering.integration

import com.rdpk.metering.domain.AggregationWindow
import com.rdpk.metering.domain.Customer
import com.rdpk.metering.domain.LateEvent
import com.rdpk.metering.domain.Tenant
import com.rdpk.metering.domain.UsageEvent
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import org.redisson.api.RedissonReactiveClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

/**
 * Base class for Kotest integration tests with cleanup hooks
 * 
 * Features:
 * - Automatic cleanup of DB and Redis before each test class (beforeSpec)
 * - Configurable cleanup before each test method (beforeTest) - disabled by default
 * - Sequential test execution (tests run in order) for multi-step scenarios
 * - Shared Testcontainers for PostgreSQL and Redis
 * 
 * Usage:
 * ```kotlin
 * @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
 * class MyIntegrationTest : AbstractKotestIntegrationTest() {
 *     
 *     init {
 *         describe("My Feature") {
 *             it("step 1") { ... }
 *             it("step 2") { ... } // Runs after step 1
 *         }
 *     }
 * }
 * ```
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractKotestIntegrationTest : DescribeSpec() {

    override fun extensions() = listOf(SpringExtension)
    
    /**
     * Isolation mode: SingleInstance ensures tests run sequentially within a spec
     * This allows multi-step test scenarios where each test builds on the previous one
     */
    override fun isolationMode(): IsolationMode = IsolationMode.SingleInstance

    companion object {
        private val postgresContainer = SharedTestContainers.postgresContainer
        private val redisContainer = SharedTestContainers.redisContainer

        init {
            // Ensure containers are started before Spring context loads
            if (!postgresContainer.isRunning) {
                postgresContainer.start()
            }
            if (!redisContainer.isRunning) {
                redisContainer.start()
            }
        }

        @DynamicPropertySource
        @JvmStatic
        fun configureProperties(registry: DynamicPropertyRegistry) {
            // Ensure containers are started and ready
            if (!postgresContainer.isRunning) {
                postgresContainer.start()
            }
            if (!redisContainer.isRunning) {
                redisContainer.start()
            }
            
            // Wait for containers to be fully ready
            Thread.sleep(1000) // Give containers time to be ready
            
            // R2DBC Configuration
            registry.add("spring.r2dbc.url") {
                "r2dbc:postgresql://${postgresContainer.host}:${postgresContainer.firstMappedPort}/${postgresContainer.databaseName}"
            }
            registry.add("spring.r2dbc.username") { postgresContainer.username }
            registry.add("spring.r2dbc.password") { postgresContainer.password }

            // Flyway Configuration (JDBC for migrations)
            registry.add("spring.flyway.url") {
                "jdbc:postgresql://${postgresContainer.host}:${postgresContainer.firstMappedPort}/${postgresContainer.databaseName}"
            }
            registry.add("spring.flyway.user") { postgresContainer.username }
            registry.add("spring.flyway.password") { postgresContainer.password }

            // Redis Configuration - ensure container is ready
            val redisPort = redisContainer.getMappedPort(6379)
            registry.add("spring.data.redis.host") { redisContainer.host }
            registry.add("spring.data.redis.port") { redisPort.toString() }
            registry.add("spring.data.redis.password") { "" }
            
            // Disable schedulers in tests to prevent background processing interference
            registry.add("metering.schedulers.enabled") { "false" }
        }
    }

    @Autowired
    lateinit var template: R2dbcEntityTemplate

    @Autowired(required = false)
    var redissonReactiveClient: RedissonReactiveClient? = null
    
    @Autowired
    lateinit var applicationContext: ApplicationContext

    /**
     * Clock bean for consistent time operations in tests
     * Use this instead of LocalDateTime.now() or Instant.now() for consistency with production code
     * 
     * For deterministic tests, you can override this with Clock.fixed():
     * ```kotlin
     * @TestConfiguration
     * class TestConfig {
     *     @Bean
     *     @Primary
     *     fun clock(): Clock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC)
     * }
     * ```
     */
    @Autowired
    lateinit var clock: java.time.Clock

    /**
     * Cleanup before each test class (beforeSpec)
     * This is the default behavior - clean once per test class
     */
    override suspend fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        cleanupDatabase()
        cleanupRedis()
        // Reset circuit breakers to ensure clean state for each test class
        resetCircuitBreakers()
    }
    
    /**
     * Reset circuit breakers to CLOSED state
     * This ensures tests start with a clean circuit breaker state
     */
    protected fun resetCircuitBreakers() {
        try {
            val circuitBreakerRegistry = applicationContext.getBean(
                io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry::class.java
            )
            circuitBreakerRegistry.circuitBreaker("postgres")?.transitionToClosedState()
            circuitBreakerRegistry.circuitBreaker("redis")?.transitionToClosedState()
        } catch (e: Exception) {
            // Circuit breaker reset is best-effort - don't fail tests if it's unavailable
        }
    }

    /**
     * Cleanup before each test method (beforeTest)
     * Ensures each test starts with a clean database, Redis, and circuit breaker state
     * This prevents data leakage between tests in the same test class
     */
    override suspend fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
        cleanupDatabase()      // Clean Postgres before each test
        cleanupRedis()         // Clean Redis before each test
        resetCircuitBreakers() // Reset circuit breakers before each test
    }

    /**
     * Cleanup all database tables
     * Uses TRUNCATE with CASCADE to handle foreign key constraints
     */
    protected fun cleanupDatabase() {
        // Delete in order to respect foreign key constraints
        template.delete(LateEvent::class.java).all().block()
        template.delete(AggregationWindow::class.java).all().block()
        template.delete(UsageEvent::class.java).all().block()
        template.delete(Customer::class.java).all().block()
        template.delete(Tenant::class.java).all().block()
        
        // Alternative: Use TRUNCATE for faster cleanup (requires raw SQL)
        // Note: This requires DatabaseClient, not R2dbcEntityTemplate
        // For now, we use delete which is safer and works with R2DBC
    }

    /**
     * Cleanup all Redis keys used by the application
     * Cleans:
     * - Event storage lists (events:pending:list)
     * - Counter keys (metering:tenant:*:customer:*:*)
     * - Lock keys (metering:lock:*)
     */
    protected fun cleanupRedis() {
        val client = redissonReactiveClient ?: return
        
        try {
            // Get all keys matching our patterns
            val eventKeys = client.getKeys().getKeysByPattern("events:*")
            val counterKeys = client.getKeys().getKeysByPattern("metering:tenant:*")
            val lockKeys = client.getKeys().getKeysByPattern("metering:lock:*")
            
            // Delete all matching keys
            Mono.zip(
                eventKeys.collectList(),
                counterKeys.collectList(),
                lockKeys.collectList()
            )
                .flatMap { tuple ->
                    val events = tuple.t1
                    val counters = tuple.t2
                    val locks = tuple.t3
                    val allKeys = events + counters + locks
                    if (allKeys.isNotEmpty()) {
                        client.getKeys().delete(*allKeys.toTypedArray())
                    } else {
                        Mono.just(0)
                    }
                }
                .block()
        } catch (e: Exception) {
            // Redis cleanup is best-effort - don't fail tests if Redis is unavailable
            // This can happen if Redis isn't needed for a particular test
        }
    }

}

