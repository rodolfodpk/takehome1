# Testing Guide

Comprehensive testing strategy for the Real-Time API Metering & Aggregation Engine using Kotest (BDD style) and Testcontainers.

## Test Strategy

The project uses **package-per-layer** architecture with comprehensive unit, integration, and E2E tests:

- ✅ **Domain Tests** (3 test files) - Pure domain model validation (no dependencies)
- ✅ **Service Unit Tests** (1 test file) - Pure business logic (AggregationService)
- ✅ **Repository Integration Tests** (1 test file) - Database operations with real PostgreSQL
- ✅ **Service Integration Tests** (1 test file) - Business logic with real repository and Redis
- ✅ **E2E Tests** (1 test file) - Complete HTTP flow with WebTestClient
- ✅ **JSONB Tests** (1 test file) - JSONB conversion verification
- **Total: 10 test files, using Kotest BDD style**

All integration tests use **Testcontainers** for real PostgreSQL and Redis - **NO MOCKS**.

## Running Tests

### Run All Tests

```bash
# Run all tests (uses Testcontainers, no Docker Compose needed)
make test

# Or directly with Maven
mvn test
```

**Note:** Tests use Testcontainers, so Docker must be running. PostgreSQL and Redis are automatically provided by Testcontainers - no Docker Compose needed.

### Run Specific Test Classes

```bash
# Domain tests
mvn test -Dtest=UsageEventTest
mvn test -Dtest=TenantTest
mvn test -Dtest=CustomerTest

# Service unit tests
mvn test -Dtest=AggregationServiceUnitTest

# Repository integration tests
mvn test -Dtest=UsageEventRepositoryIntegrationTest

# Service integration tests
mvn test -Dtest=EventProcessingServiceIntegrationTest

# E2E tests
mvn test -Dtest=EventIngestionE2ETest

# JSONB conversion tests
mvn test -Dtest=JsonbMapTest
```

### Run with Coverage Report

```bash
mvn clean test jacoco:report
```

Coverage reports are generated in `target/site/jacoco/index.html`.

## Test Performance

- **Execution Time:** ~10-15 seconds for all tests
- **Container Reuse:** Shared PostgreSQL and Redis containers across all tests
- **Isolation:** Database and Redis cleanup before each test class
- **Sequential Execution:** Tests run in order (`IsolationMode.SingleInstance`) for multi-step scenarios
- **Testcontainers:** PostgreSQL 17.2 and Redis 7-alpine containers with `withReuse(true)` for fast tests

## Test Structure

### Package-Per-Layer Architecture

```
src/test/kotlin/com/rdpk/metering/
├── domain/                    # Unit tests (pure domain logic)
│   ├── UsageEventTest.kt      # UsageEvent domain validation
│   ├── TenantTest.kt          # Tenant domain validation
│   └── CustomerTest.kt        # Customer domain validation
├── service/                   # Unit tests (pure business logic)
│   └── AggregationServiceUnitTest.kt  # Aggregation logic (no DB/Redis)
└── integration/              # Integration tests (real DB/Redis)
    ├── AbstractKotestIntegrationTest.kt  # Base class for integration tests
    ├── SharedTestContainers.kt            # Singleton containers
    ├── JsonbMapTest.kt                    # JSONB conversion test
    ├── repository/
    │   └── UsageEventRepositoryIntegrationTest.kt
    ├── service/
    │   └── EventProcessingServiceIntegrationTest.kt
    └── e2e/
        └── EventIngestionE2ETest.kt
```

### Base Test Class

All integration tests extend `AbstractKotestIntegrationTest`:

```kotlin
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractKotestIntegrationTest : DescribeSpec() {

    override fun extensions() = listOf(SpringExtension)
    
    @Autowired
    lateinit var clock: Clock
    
    // Automatic cleanup before test class
    override fun beforeSpec(spec: Spec) {
        // Clean database and Redis
    }
}
```

**Features:**
- Extends `DescribeSpec` (Kotest BDD style)
- Uses `SpringExtension` for Spring integration
- Automatic cleanup before each test class (DB + Redis)
- Sequential test execution (`IsolationMode.SingleInstance`)
- Injects `Clock` bean for time consistency

### Singleton Containers

`SharedTestContainers` ensures single PostgreSQL and Redis containers are shared across all tests:

```kotlin
object SharedTestContainers {
    val postgresContainer: PostgreSQLContainer<*> = PostgreSQLContainer(
        DockerImageName.parse("postgres:17.2")
    )
        .withDatabaseName("testdb")
        .withReuse(true)
    
    val redisContainer: GenericContainer<*> = GenericContainer(
        DockerImageName.parse("redis:7-alpine")
    )
        .withReuse(true)
}
```

**Benefits:**
- Fast test execution (containers start once)
- Resource efficient (shared containers)
- Automatic cleanup between test classes

## Test Categories

### 1. Domain Tests

Unit tests for domain model validation (pure domain logic, no dependencies):

**Files:**
- `UsageEventTest.kt` - UsageEvent entity validation
- `TenantTest.kt` - Tenant entity validation
- `CustomerTest.kt` - Customer entity validation

**Example:**
```kotlin
class UsageEventTest : DescribeSpec({
    describe("UsageEvent domain entity") {
        it("should create UsageEvent with all fields") {
            val event = UsageEvent(
                eventId = "event-123",
                tenantId = 1L,
                customerId = 100L,
                timestamp = Instant.now(),
                endpoint = "/api/completion",
                tokens = 100,
                model = "gpt-4",
                latencyMs = 250,
                metadata = mapOf("tokens" to 100)
            )
            
            event.eventId shouldBe "event-123"
            event.tenantId shouldBe 1L
        }
    }
})
```

### 2. Service Unit Tests

Unit tests for pure business logic (no database or Redis):

**Files:**
- `AggregationServiceUnitTest.kt` - Aggregation logic (totals, by endpoint, by model)

**Example:**
```kotlin
class AggregationServiceUnitTest : DescribeSpec({
    describe("AggregationService") {
        it("should aggregate events correctly") {
            val events = listOf(
                UsageEvent(..., tokens = 100, ...),
                UsageEvent(..., tokens = 200, ...)
            )
            
            val result = aggregationService.aggregateWindow(
                tenantId = 1L,
                customerId = 100L,
                windowStart = start,
                windowEnd = end,
                events = events
            )
            
            result.totalTokens shouldBe 300
        }
    }
})
```

### 3. Repository Integration Tests

Integration tests with real PostgreSQL (via Testcontainers):

**Files:**
- `UsageEventRepositoryIntegrationTest.kt` - Database operations

**Example:**
```kotlin
class UsageEventRepositoryIntegrationTest : AbstractKotestIntegrationTest() {
    
    @Autowired
    lateinit var repository: UsageEventRepository
    
    init {
        describe("UsageEventRepository") {
            it("should save and find usage event by eventId") {
                val event = UsageEvent(
                    eventId = "test-event-${System.currentTimeMillis()}",
                    tenantId = testTenantId,
                    customerId = testCustomerId,
                    timestamp = clock.instant(), // Use injected Clock
                    endpoint = "/api/completion",
                    metadata = mapOf("tokens" to 100)
                )
                
                StepVerifier.create(
                    repository.save(event)
                        .then(repository.findByEventId(event.eventId))
                )
                    .assertNext { found ->
                        found.eventId shouldBe event.eventId
                        found.metadata?.get("tokens") shouldBe 100
                    }
                    .verifyComplete()
            }
        }
    }
}
```

**Key Points:**
- Uses `AbstractKotestIntegrationTest` base class
- Uses injected `Clock` bean for time operations
- Uses `StepVerifier` for reactive assertions
- Real PostgreSQL container (no mocks)

### 4. Service Integration Tests

Integration tests with real PostgreSQL and Redis:

**Files:**
- `EventProcessingServiceIntegrationTest.kt` - Event processing with real dependencies

**Example:**
```kotlin
class EventProcessingServiceIntegrationTest : AbstractKotestIntegrationTest() {
    
    @Autowired
    lateinit var eventProcessingService: EventProcessingService
    
    init {
        describe("EventProcessingService") {
            it("should process event and store in Redis") {
                val request = UsageEventRequest(
                    eventId = "test-event-1",
                    tenantId = testTenantId.toString(),
                    customerId = testCustomerId,
                    timestamp = clock.instant(),
                    endpoint = "/api/completion",
                    metadata = EventMetadata(tokens = 100)
                )
                
                StepVerifier.create(
                    eventProcessingService.processEvent(request)
                )
                    .assertNext { response ->
                        response.status shouldBe "processed"
                    }
                    .verifyComplete()
            }
        }
    }
}
```

### 5. E2E Tests

End-to-end tests covering complete HTTP flow:

**Files:**
- `EventIngestionE2ETest.kt` - Full HTTP flow with WebTestClient

**Example:**
```kotlin
@AutoConfigureWebTestClient
class EventIngestionE2ETest : AbstractKotestIntegrationTest() {
    
    @Autowired
    lateinit var webTestClient: WebTestClient
    
    init {
        describe("Event Ingestion E2E Flow") {
            it("should ingest event via HTTP and return success") {
                val request = UsageEventRequest(
                    eventId = "e2e-event-1",
                    tenantId = testTenantId.toString(),
                    customerId = testCustomerId,
                    timestamp = clock.instant(),
                    endpoint = "/api/completion",
                    metadata = EventMetadata(tokens = 100)
                )
                
                webTestClient.post()
                    .uri("/api/v1/events")
                    .header("X-Tenant-Id", testTenantId.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isCreated
                    .expectBody(UsageEventResponse::class.java)
                    .value { response ->
                        response.status shouldBe "processed"
                    }
            }
        }
    }
}
```

### 6. JSONB Conversion Tests

Tests for JSONB `Map<String, Any>` conversion:

**Files:**
- `JsonbMapTest.kt` - Verifies JSONB mapping works correctly

**Example:**
```kotlin
class JsonbMapTest : AbstractKotestIntegrationTest() {
    
    init {
        describe("JSONB Map conversion") {
            it("should save and retrieve Map<String, Any> as JSONB") {
                val metadata = mapOf(
                    "tokens" to 100,
                    "model" to "gpt-4",
                    "inputTokens" to 50,
                    "outputTokens" to 50
                )
                
                val event = UsageEvent(
                    eventId = "test-jsonb-1",
                    tenantId = testTenantId,
                    customerId = testCustomerId,
                    timestamp = clock.instant(),
                    endpoint = "/api/completion",
                    metadata = metadata
                )
                
                StepVerifier.create(
                    extensions.saveWithJsonb(event)
                        .then(repository.findByEventId(event.eventId))
                )
                    .assertNext { found ->
                        found.metadata?.get("tokens") shouldBe 100
                        found.metadata?.get("inputTokens") shouldBe 50
                    }
                    .verifyComplete()
            }
        }
    }
}
```

## Code Coverage

Current test coverage focuses on:
- ✅ All domain models (UsageEvent, Tenant, Customer)
- ✅ All repository methods (with JSONB handling)
- ✅ All service methods (EventProcessingService, AggregationService)
- ✅ All controller endpoints (EventController)
- ✅ All business validation rules
- ✅ All exception scenarios
- ✅ JSONB conversion (Map<String, Any> ↔ JSONB)

**Coverage Report:**
```bash
# Generate coverage report
mvn clean test jacoco:report

# View report
open target/site/jacoco/index.html
```

## Best Practices

1. **Kotest BDD Style**: Use `DescribeSpec` for all tests
2. **No Mocks**: Never use mocks for database or Redis operations
3. **Testcontainers**: Always use Testcontainers for integration tests
4. **Shared Containers**: Use singleton pattern for PostgreSQL and Redis containers
5. **Time Consistency**: Always use injected `Clock` bean for time operations
6. **Sequential Execution**: Tests run in order for multi-step scenarios
7. **Cleanup Before Class**: Database and Redis cleanup happens before test class (not before each test)
8. **Reactive Assertions**: Use `StepVerifier` for reactive types (`Mono`/`Flux`)
9. **Meaningful Names**: Use descriptive test method names (`it("should ...")`)
10. **Independent Tests**: Each test should be independent (cleanup ensures this)

## Continuous Integration

Tests run automatically on every push via GitHub Actions:

**Workflow:** `.github/workflows/ci.yml`

```yaml
name: CI

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    services:
      docker:
        image: docker:24-dind
        options: --privileged
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      
      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: 'maven'
      
      - name: Run tests with coverage
        run: mvn clean test jacoco:report
      
      - name: Upload coverage reports to Codecov
        uses: codecov/codecov-action@v5
        with:
          token: ${{ secrets.CODECOV_TOKEN }}
          slug: rodolfodpk/takehome1
```

**Coverage:**
- Coverage reports are automatically uploaded to Codecov
- Coverage badge is displayed in README.md
- Coverage target: 80% (configured in `codecov.yml`)

## Troubleshooting

**Issue:** Tests are slow
- **Solution:** Ensure container reuse is enabled in `SharedTestContainers`

**Issue:** Database state leaks between tests
- **Solution:** Check that cleanup runs in `beforeSpec` (before test class)

**Issue:** Container start fails
- **Solution:** Ensure Docker is running and has sufficient resources allocated

**Issue:** Port conflicts
- **Solution:** Testcontainers automatically assigns random ports, but verify no other PostgreSQL/Redis instances are running

**Issue:** Time-dependent tests fail
- **Solution:** Always use injected `Clock` bean instead of `Instant.now()` or `LocalDateTime.now()`

## Additional Resources

- [Kotest Documentation](https://kotest.io/)
- [Kotest Spring Extension](https://kotest.io/docs/framework/extensions/spring.html)
- [Testcontainers Documentation](https://www.testcontainers.org/)
- [StepVerifier Guide](https://projectreactor.io/docs/core/release/reference/#testing)
- [R2DBC Testing Guide](https://docs.spring.io/spring-data/r2dbc/docs/current/reference/html/#r2dbc.testing)
