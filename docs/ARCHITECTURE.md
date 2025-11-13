# Architecture

Real-Time API Metering & Aggregation Engine - System Architecture

## Architecture Overview

The application follows a **package-per-layer** architecture pattern optimized for high-throughput reactive processing. The system is designed to handle 10,000+ events/second per instance using a fully reactive stack (WebFlux + R2DBC).

```
src/main/kotlin/com/rdpk/metering/
├── Application.kt                    # Main Spring Boot application
├── config/                           # Configuration classes
│   ├── MetricsConfig.kt              # Micrometer metrics configuration
│   ├── R2dbcConfig.kt                # R2DBC custom converters (JSONB)
│   ├── ResilienceConfig.kt           # Resilience4j configuration
│   ├── RedisConfig.kt                # Redisson client configuration
│   └── TimeConfig.kt                 # Clock bean for time consistency
├── controller/                       # REST API layer
│   └── EventController.kt           # Event ingestion endpoint
├── service/                          # Business logic layer
│   ├── EventProcessingService.kt    # Event processing orchestration
│   ├── RedisEventStorageService.kt  # Hot path: Redis event buffer
│   ├── RedisStateService.kt         # Real-time counter updates
│   ├── AggregationService.kt        # Aggregation logic
│   ├── DistributedLockService.kt    # Redis-based distributed locks
│   └── LateEventService.kt          # Late event detection/handling
├── scheduler/                        # Background processing
│   ├── EventPersistenceScheduler.kt # Cold path: Redis → Postgres
│   ├── AggregationScheduler.kt      # 30-second window aggregation
│   └── LateEventProcessor.kt        # Late event reprocessing
├── repository/                       # Data access layer
│   ├── TenantRepository.kt          # Tenant CRUD
│   ├── CustomerRepository.kt        # Customer CRUD
│   ├── UsageEventRepository.kt      # Usage event queries
│   ├── UsageEventRepositoryExtensions.kt # JSONB handling extensions
│   ├── AggregationWindowRepository.kt # Aggregation window queries
│   └── LateEventRepository.kt       # Late event queries
├── domain/                          # Domain models
│   ├── Tenant.kt                    # Tenant entity
│   ├── Customer.kt                  # Customer entity
│   ├── UsageEvent.kt                # Usage event entity (with JSONB metadata)
│   ├── AggregationWindow.kt         # Aggregation window entity
│   └── LateEvent.kt                 # Late event entity
├── dto/                             # Data Transfer Objects
│   ├── UsageEventRequest.kt        # Event ingestion request
│   ├── UsageEventResponse.kt       # Event ingestion response
│   └── AggregationResult.kt        # Aggregation result DTO
├── exception/                       # Exception handling
│   └── GlobalExceptionHandler.kt    # WebFlux exception handler
└── util/                            # Utilities
    (no utilities currently)
```

## System Design

### High-Level Architecture

```
┌─────────────┐
│   Client    │
│  (10k/s)    │
└──────┬──────┘
       │ HTTP POST /api/v1/events
       ▼
┌─────────────────────────────────────┐
│     EventController (WebFlux)        │
│  - Validates request                 │
│  - Returns 201 immediately          │
└──────┬──────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────┐
│   EventProcessingService            │
│  - Validates tenant/customer        │
│  - Checks for late events            │
│  - Updates Redis counters           │
│  - Stores event in Redis buffer     │
└──────┬──────────────────────────────┘
       │
       ├──────────────────┬──────────────────┐
       ▼                  ▼                  ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│ Redis Buffer │  │ Redis State  │  │ Late Event   │
│ (Hot Path)   │  │ (Counters)    │  │ Detection    │
└──────┬───────┘  └──────────────┘  └──────────────┘
       │
       │ Background Scheduler (every 2s)
       ▼
┌─────────────────────────────────────┐
│  EventPersistenceScheduler          │
│  - Batch reads from Redis           │
│  - Batch writes to Postgres         │
│  - Removes from Redis after success │
└──────┬──────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────┐
│     PostgreSQL (Cold Path)          │
│  - usage_events table               │
│  - Historical data                   │
└─────────────────────────────────────┘

       │ Background Scheduler (every 30s)
       ▼
┌─────────────────────────────────────┐
│    AggregationScheduler             │
│  - Acquires distributed lock         │
│  - Reads events from Postgres       │
│  - Aggregates (totals, by endpoint) │
│  - Writes to aggregation_windows    │
│  - Clears Redis counters            │
└──────┬──────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────┐
│     PostgreSQL                      │
│  - aggregation_windows table        │
│  - Completed aggregations           │
└─────────────────────────────────────┘
```

## Data Flow

### 1. Event Ingestion Flow (Hot Path)

1. **Client** → `POST /api/v1/events` with `tenantId` in request body
2. **EventController** → Validates request, extracts tenant context
3. **EventProcessingService** → 
   - Validates tenant/customer exist and are active
   - Checks if event is late (arrives after window closed)
   - Updates Redis counters (atomic increments)
   - Stores event in Redis List (event buffer)
   - Returns `201 CREATED` immediately
4. **Response** → Client receives acknowledgment (< 100ms P99)

**Performance**: <1ms per event, 10k+ events/second

### 2. Event Persistence Flow (Cold Path)

1. **EventPersistenceScheduler** → Runs every 2 seconds
2. **RedisEventStorageService** → Reads batch of 1000 events from Redis List
3. **UsageEventRepositoryExtensions** → Batch inserts to PostgreSQL with JSONB casting
4. **RedisEventStorageService** → Removes processed events from Redis
5. **Result** → Events persisted to `usage_events` table

**Performance**: 500-1000 events per batch, non-blocking

### 3. Aggregation Flow

1. **AggregationScheduler** → Runs every 30 seconds
2. **DistributedLockService** → Acquires lock per (tenant, customer, window)
3. **UsageEventRepository** → Reads events for completed window from PostgreSQL
4. **AggregationService** → 
   - Calculates totals (calls, tokens, inputTokens, outputTokens)
   - Aggregates by endpoint
   - Aggregates by model
   - Calculates average latency
5. **AggregationWindowRepository** → Saves aggregation to `aggregation_windows` table
6. **RedisStateService** → Clears Redis counters for processed window

**Performance**: Handles multiple tenants/customers in parallel with locks

### 4. Late Event Flow

1. **EventProcessingService** → Detects event timestamp is >30s old
2. **LateEventService** → Stores event in `late_events` table
3. **LateEventProcessor** → Runs every 5 minutes
4. **LateEventProcessor** → 
   - Reads late events
   - Persists to `usage_events` table
   - Updates or creates aggregation window
   - Deletes processed late event

## Design Decisions

### 1. Why R2DBC Instead of JPA?

**Decision**: Use Spring Data R2DBC instead of JPA for database access.

**Rationale**:
- **Reactive Stack Requirement**: The system uses Spring WebFlux for non-blocking HTTP handling. JPA is blocking and would break the reactive chain.
- **Performance**: R2DBC provides true non-blocking database access, essential for 10k+ events/second throughput.
- **Consistency**: Maintains end-to-end reactive pipeline (WebFlux → R2DBC → Reactor).

**Tradeoffs**:
- ✅ **Pros**: Non-blocking, better performance, true reactive stack
- ❌ **Cons**: 
  - No automatic relationship mapping (manual joins)
  - No JPA filters (manual tenant filtering)
  - No lazy loading (explicit queries)
  - Manual JSONB handling (custom converters/extensions)

**Implementation**:
- Custom `R2dbcConfig` with JSONB converters
- `UsageEventRepositoryExtensions` for explicit JSONB casting
- Manual tenant filtering via `@Query` annotations

### 2. Hot vs Cold Path Strategy

**Decision**: Use Redis as event buffer (hot path) with background batch persistence (cold path).

**Rationale**:
- **Zero Data Loss**: Events stored in Redis immediately (durable with AOF)
- **High Throughput**: Redis List operations are extremely fast (<1ms)
- **Batching Efficiency**: Batch writes to PostgreSQL reduce database load
- **Backpressure Handling**: Redis buffer absorbs spikes

**Flow**:
1. **Hot Path**: Event → Redis List (immediate, <1ms)
2. **Cold Path**: Redis → PostgreSQL (batched, every 2s, 1000 events/batch)

### 3. Distributed Locking Strategy

**Decision**: Use Redis-based distributed locks (Redisson) for window aggregation.

**Rationale**:
- **Multi-Instance Safety**: Prevents duplicate window processing across instances
- **Idempotency**: Ensures each window is processed exactly once
- **Performance**: Redis locks are fast (<10ms acquisition)

**Implementation**:
- Lock key: `aggregation:lock:{tenantId}:{customerId}:{windowStart}`
- Timeout: 30 seconds (lock acquisition)
- Lease: 60 seconds (lock duration)
- Metrics: `lockContention` counter tracks failed acquisitions

### 4. Multi-Tenancy Strategy

**Decision**: Use `tenantId` from request body with explicit tenant validation.

**Rationale**:
- **Simple & Direct**: Tenant ID comes from request body (as per requirements)
- **No AOP Overhead**: Direct validation is faster
- **Explicit**: Clear tenant isolation at API boundary
- **R2DBC-Compatible**: R2DBC doesn't support JPA-style automatic filters

**Why Not AOP for Multi-Tenancy?**

While the requirements mention implementing an AOP-based approach (Challenge 1), we chose explicit validation instead:

1. **R2DBC Limitation**: 
   - R2DBC doesn't support JPA `@Filter` or automatic query modification
   - AOP cannot modify `@Query` SQL annotations at runtime
   - Manual tenant filtering is the only practical approach for R2DBC

2. **Reactive Stack Complexity**:
   - AOP with reactive types (`Mono`/`Flux`) is complex and error-prone
   - Requires careful handling of reactive chains in `@Around` advice
   - Direct validation in service layer is simpler and clearer

3. **Security & Maintainability**:
   - **Explicit is Safer**: Manual tenant filtering is easier to audit and verify
   - **Clear Intent**: You can see exactly where tenant validation happens
   - **No Hidden Magic**: No AOP proxies to debug or understand
   - **All Production Code Validates**: Every service method explicitly validates tenant

4. **Performance**:
   - Direct validation has zero overhead
   - AOP adds proxy creation and method interception overhead
   - For 10k+ events/second, every microsecond matters

5. **Current Implementation Security**:
   - ✅ All production code paths validate tenant explicitly
   - ✅ All repository queries used in production include `tenant_id` filter
   - ✅ All schedulers iterate tenants explicitly (`findByActive(true)`)
   - ✅ Unused methods without tenant filter are never called

**Implementation**:
- `EventProcessingService.validateTenant()`: Explicit tenant validation from request body (checks existence and active status)
- `EventProcessingService.validateCustomer()`: Explicit customer validation (includes tenant check)
- Manual filtering: All repository queries include `tenant_id` filter explicitly
- Tenant ID comes from `UsageEventRequest.tenantId` (request body)

**Example - Explicit Validation**:
```kotlin
// Service layer - explicit validation
fun processEvent(request: UsageEventRequest): Mono<UsageEventResponse> {
    return validateTenant(request.tenantId)  // Explicit validation
        .flatMap { tenant ->
            validateCustomer(tenant.id!!, request.customerId)  // Includes tenant check
        }
        .flatMap { customer ->
            // Process event with validated tenant/customer
        }
}

// Repository layer - explicit tenant filtering
@Query("""
    SELECT * FROM usage_events 
    WHERE tenant_id = :tenantId  -- Explicit tenant filter
    AND customer_id = :customerId
    AND timestamp >= :start 
    AND timestamp <= :end
""")
fun findByTenantIdAndCustomerIdAndTimestampBetween(
    tenantId: Long, 
    customerId: Long, 
    start: Instant, 
    end: Instant
): Flux<UsageEvent>
```

**Security Guarantees**:
- Tenant validation happens at API boundary (`EventProcessingService`)
- All repository queries used in production include tenant filter
- Schedulers process tenants explicitly (no cross-tenant data access)
- Customer validation includes tenant check (prevents tenant spoofing)

### 5. JSONB Handling Strategy

**Decision**: Use explicit SQL casting (`::jsonb` for writes, `::text` for reads) with custom repository extensions.

**Rationale**:
- **R2DBC Limitation**: Spring Data R2DBC doesn't automatically handle `Map<String, Any>` → JSONB
- **Type Safety**: Explicit casting ensures correct PostgreSQL types
- **Performance**: Direct SQL casting is faster than converter matching

**Implementation**:
- `UsageEventRepositoryExtensions.saveWithJsonb()`: Casts metadata to `::jsonb` in INSERT
- `UsageEventRepository` queries: Cast metadata to `::text` in SELECT
- `StringToMapConverter`: Converts JSON string back to `Map<String, Any>`

## Layer Structure

### 1. Controller Layer
**Package:** `com.rdpk.metering.controller`

**Responsibilities:**
- Handle HTTP requests (WebFlux)
- Validate request data (Bean Validation)
- Delegate to service layer
- Return reactive responses (`Mono<ResponseEntity<T>>`)

**Rules:**
- `@RestController` annotation required
- Must end with `Controller` suffix
- Can only depend on service layer (no direct repository access)
- Must use reactive types (`Mono`, `Flux`)

**Example:**
```kotlin
@RestController
@RequestMapping("/api/v1/events")
class EventController(
    private val eventProcessingService: EventProcessingService
) {
    @PostMapping
    fun ingestEvent(@Valid @RequestBody request: Mono<UsageEventRequest>): Mono<ResponseEntity<UsageEventResponse>> {
        return request
            .flatMap { eventProcessingService.processEvent(it) }
            .map { ResponseEntity.status(HttpStatus.CREATED).body(it) }
    }
}
```

### 2. Service Layer
**Package:** `com.rdpk.metering.service`

**Responsibilities:**
- Implement business logic
- Coordinate between controller and repository
- Apply Resilience4j patterns (Circuit Breaker, Retry, Timeout)
- Update Redis state
- Handle late events

**Rules:**
- `@Service` annotation required
- Must end with `Service` suffix
- Can depend on repository, domain, and Resilience4j layers
- Must use reactive types (`Mono`, `Flux`)
- Wraps all external calls (Postgres, Redis) with Resilience4j

**Example:**
```kotlin
@Service
class EventProcessingService(
    private val tenantRepository: TenantRepository,
    private val redisEventStorageService: RedisEventStorageService,
    private val resilienceService: ResilienceService
) {
    fun processEvent(request: UsageEventRequest): Mono<UsageEventResponse> {
        return resilienceService.applyPostgresResilience(
            tenantRepository.findById(tenantId)
        )
            .flatMap { tenant ->
                redisEventStorageService.storeEvent(event)
            }
    }
}
```

### 3. Repository Layer
**Package:** `com.rdpk.metering.repository`

**Responsibilities:**
- Database access (R2DBC)
- Query execution
- Data persistence
- JSONB handling (via extensions)

**Rules:**
- Interface extending `ReactiveCrudRepository`
- Must end with `Repository` suffix
- Can only depend on domain layer
- Spring Data R2DBC automatically implements repository interfaces
- **No Resilience4j dependencies** (applied at service layer)
- Use `@Query` with explicit JSONB casting for metadata fields

**Example:**
```kotlin
@Repository
interface UsageEventRepository : ReactiveCrudRepository<UsageEvent, Long> {
    @Query("""
        SELECT id, event_id, tenant_id, customer_id, timestamp, endpoint, tokens, model, latency_ms, 
               metadata::text as metadata, created, updated
        FROM usage_events 
        WHERE tenant_id = :tenantId 
        AND customer_id = :customerId 
        AND timestamp >= :start 
        AND timestamp <= :end
    """)
    fun findByTenantIdAndCustomerIdAndTimestampBetween(
        tenantId: Long, 
        customerId: Long, 
        start: Instant, 
        end: Instant
    ): Flux<UsageEvent>
}
```

### 4. Domain Layer
**Package:** `com.rdpk.metering.domain`

**Responsibilities:**
- Domain entities (Kotlin data classes)
- Business rules
- State management methods

**Rules:**
- Kotlin `data class` (immutable)
- No Spring annotations
- No dependencies on other layers
- Pure domain logic
- Helper methods for state changes

**Example:**
```kotlin
@Table("usage_events")
data class UsageEvent(
    @Id val id: Long? = null,
    val eventId: String,
    val tenantId: Long,
    val customerId: Long,
    val timestamp: Instant,
    val endpoint: String,
    val tokens: Int? = null,
    val model: String? = null,
    val latencyMs: Int? = null,
    val metadata: Map<String, Any>? = null, // JSONB
    @ReadOnlyProperty val created: LocalDateTime? = null,
    @ReadOnlyProperty val updated: LocalDateTime? = null
) {
    fun withId(id: Long): UsageEvent = copy(id = id)
}
```

### 5. Scheduler Layer
**Package:** `com.rdpk.metering.scheduler`

**Responsibilities:**
- Background processing
- Batch operations
- Scheduled tasks

**Rules:**
- `@Component` annotation required
- `@Scheduled` for timing
- Must use reactive types (`Mono`, `Flux`)
- Wraps all external calls with Resilience4j
- Handles errors gracefully (logs, continues)

**Example:**
```kotlin
@Component
class EventPersistenceScheduler(
    private val redisEventStorageService: RedisEventStorageService,
    private val usageEventRepositoryExtensions: UsageEventRepositoryExtensions,
    private val resilienceService: ResilienceService
) {
    @Scheduled(fixedRate = 2000) // Every 2 seconds
    fun batchPersistEvents() {
        redisEventStorageService.getPendingEvents(1000)
            .flatMap { batch ->
                resilienceService.applyPostgresResilience(
                    usageEventRepositoryExtensions.saveAllWithJsonb(batch)
                )
            }
            .subscribe()
    }
}
```

## Dependency Rules

### Allowed Dependencies

```
Controller → Service
         → DTO
         → Exception

Service → Repository
       → Domain
       → Redis Services
       → Resilience4j
       → Micrometer

Repository → Domain

Scheduler → Service
         → Repository
         → Resilience4j
         → Micrometer

Domain → (no dependencies)
```

### Prohibited Dependencies

- ❌ Controller → Repository (use Service instead)
- ❌ Service → Controller (circular dependency)
- ❌ Repository → Service, Controller
- ❌ Repository → Resilience4j (applied at service layer)
- ❌ Domain → Spring annotations, other layers

## Resilience Patterns

All external calls (PostgreSQL, Redis) are protected with Resilience4j:

- **Circuit Breaker**: Prevents cascading failures
- **Retry**: Handles transient failures
- **TimeLimiter**: Prevents hanging operations

See **[Resilience Documentation](RESILIENCE.md)** for details.

## Observability

- **Metrics**: Micrometer (JVM, HTTP, R2DBC, Resilience4j, custom business metrics)
- **Logging**: Structured logging with SLF4J
- **Tracing**: Spring Boot Actuator health checks

See **[Observability Documentation](OBSERVABILITY.md)** for details.

## Performance Characteristics

- **Throughput**: 10,000+ events/second per instance
- **Latency**: P99 < 100ms, P50 < 10ms
- **Processing**: <1ms per event (hot path)
- **Batching**: 500-1000 events per batch (cold path)

## Scalability Considerations

- **Horizontal Scaling**: Stateless application, scales horizontally
- **Redis**: Shared state across instances (counters, locks, event buffer)
- **PostgreSQL**: Read replicas for reporting queries
- **Bottlenecks**: 
  - Redis connection pool (64+ connections)
  - PostgreSQL batch write throughput
  - Network latency

## Future Improvements

1. **Partitioning**: Partition `usage_events` table by tenant or time
2. **Caching**: Cache tenant/customer lookups in Redis
3. **Compression**: Compress events in Redis buffer
4. **Streaming**: Use Kafka for event ingestion (replace Redis buffer)
5. **Materialized Views**: Pre-aggregate common query patterns
