# Observability

Comprehensive observability for the Real-Time API Metering & Aggregation Engine with structured logging, metrics collection, and visualization.

## Overview

The application includes a full observability stack with:
- **Structured Logging**: JSON-formatted logs with correlation IDs
- **Prometheus**: Metrics collection and time-series database
- **Grafana**: Visualization and dashboards
- **Spring Boot Actuator**: Health checks and metrics exposure
- **Micrometer**: Metrics instrumentation (automatic + custom)

## Quick Start

### Start Observability Stack

```bash
# Start application with Prometheus and Grafana
make start-obs

# Access Grafana
make grafana
# URL: http://localhost:3000
# Login: admin/admin

# Access Prometheus
make prometheus
# URL: http://localhost:9090

# View metrics
make metrics
# URL: http://localhost:8080/actuator/metrics
```

### Available Endpoints

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health
- **Prometheus Metrics**: http://localhost:8080/actuator/prometheus
- **All Metrics**: http://localhost:8080/actuator/metrics
- **Circuit Breakers**: http://localhost:8080/actuator/circuitbreakers
- **Circuit Breaker Events**: http://localhost:8080/actuator/circuitbreakerevents
- **Grafana**: http://localhost:3000 (admin/admin)
- **Prometheus UI**: http://localhost:9090

## Metrics with Micrometer

### What Micrometer Automatically Covers ✅

Micrometer provides automatic instrumentation for Spring Boot applications - **no code required**:

#### 1. JVM Metrics (Automatic)

- `jvm.memory.used` - Memory usage by area (heap, non-heap)
- `jvm.memory.max` - Maximum memory available
- `jvm.memory.committed` - Committed memory
- `jvm.gc.pause` - GC pause times (by GC type)
- `jvm.threads.live` - Active threads
- `jvm.threads.daemon` - Daemon threads
- `jvm.classes.loaded` - Loaded classes

**Example Prometheus output:**
```
jvm_memory_used_bytes{area="heap",id="PS Survivor Space"} 1.048576e+06
jvm_gc_pause_seconds_count{action="end of minor GC",cause="Allocation Failure"} 1234.0
```

#### 2. HTTP Metrics (Automatic from Spring WebFlux)

Spring WebFlux automatically instruments all HTTP requests:
- `http.server.requests` - Request count, latency, status codes
- Tags: `method`, `uri`, `status`, `exception`
- Includes P50, P95, P99 percentiles automatically

**Example Prometheus output:**
```
http_server_requests_seconds_count{method="POST",uri="/api/v1/events",status="201"} 10000.0
http_server_requests_seconds_sum{method="POST",uri="/api/v1/events",status="201"} 45.2
http_server_requests_seconds_max{method="POST",uri="/api/v1/events",status="201"} 0.098
```

**Key metrics:**
- `http_server_requests_seconds_count` - Total requests
- `http_server_requests_seconds_sum` - Total latency
- `http_server_requests_seconds_max` - Max latency
- Percentiles: `http_server_requests_seconds{quantile="0.95"}`

#### 3. R2DBC Connection Pool Metrics (Automatic)

Automatic metrics for database connection pool:
- `r2dbc.pool.acquired` - Currently acquired connections
- `r2dbc.pool.pending` - Pending connection requests
- `r2dbc.pool.idle` - Idle connections available
- `r2dbc.pool.timeout` - Connection timeout events
- `r2dbc.pool.max` - Maximum pool size

**Example Prometheus output:**
```
r2dbc_pool_acquired{name="connectionPool"} 5.0
r2dbc_pool_pending{name="connectionPool"} 0.0
r2dbc_pool_idle{name="connectionPool"} 15.0
```

#### 4. Resilience4j Metrics (Automatic via resilience4j-micrometer)

Automatically exported via `resilience4j-micrometer`:
- `resilience4j.circuitbreaker.calls` - Circuit breaker calls by state
- `resilience4j.circuitbreaker.state` - Current state (0=closed, 1=open, 2=half-open)
- `resilience4j.circuitbreaker.failure.rate` - Failure rate percentage
- `resilience4j.retry.calls` - Retry attempts (success/failure)
- `resilience4j.timelimiter.calls` - Timeout occurrences

**Example Prometheus output:**
```
resilience4j_circuitbreaker_calls_total{name="postgres",state="successful"} 9500.0
resilience4j_circuitbreaker_calls_total{name="postgres",state="failed"} 500.0
resilience4j_circuitbreaker_state{name="postgres"} 0.0  # 0=closed
resilience4j_circuitbreaker_failure_rate{name="postgres"} 5.0
```

**Circuit breaker states:**
- `0` = CLOSED (normal operation)
- `1` = OPEN (circuit is open, failing fast)
- `2` = HALF_OPEN (testing if backend recovered)

### Custom Business Metrics

Custom metrics are instrumented in services using `EventMetrics`:

#### Event Processing Metrics

- `metering.events.ingested` - Total events ingested
  - Tags: `type=total`, `type=by_tenant`, `type=by_customer`
- `metering.events.processing.latency` - Hot path processing latency
  - Percentiles: P50, P95, P99
- `metering.events.ingestion.errors` - Ingestion errors

**Instrumented in:**
- `EventProcessingService.processEvent()` - Records ingestion count, latency, errors

#### Late Event Metrics

- `metering.events.late.detected` - Late events detected
- `metering.events.late.processed` - Late events processed

**Instrumented in:**
- `LateEventService.checkAndHandleLateEvent()` - Records late event detection
- `LateEventProcessor.processLateEvents()` - Records late event processing

#### Redis Operation Metrics

- `metering.redis.storage.latency` - Event storage latency (P50, P95, P99)
- `metering.redis.counter.update.latency` - Counter update latency (P50, P95, P99)
- `metering.redis.read.latency` - Read operations latency (P50, P95, P99)

**Instrumented in:**
- `RedisEventStorageService.storeEvent()` - Records storage latency
- `RedisEventStorageService.getPendingEvents()` - Records read latency
- `RedisStateService.updateCounters()` - Records counter update latency

#### Database Operation Metrics

- `metering.db.persistence.latency` - Batch persistence latency (P50, P95, P99)
- `metering.db.persistence.batch.size` - Events per batch

**Instrumented in:**
- `EventPersistenceScheduler.batchPersistEvents()` - Records persistence latency and batch size

#### Aggregation Metrics

- `metering.aggregation.processing.latency` - Window processing latency (P50, P95, P99)
- `metering.aggregation.windows.processed` - Windows processed
- `metering.aggregation.windows.errors` - Processing errors

**Instrumented in:**
- `AggregationScheduler.processWindow()` - Records processing latency, success, and errors

#### Lock Metrics

- `metering.lock.acquisition.latency` - Lock acquisition time (P50, P95, P99)
- `metering.lock.contention` - Lock contention events (timeout/failure)

**Instrumented in:**
- `DistributedLockService.withLock()` - Records lock acquisition latency and contention
- `AggregationScheduler` - Records lock acquisition for window processing

## Metrics Export

### Prometheus Endpoint

Metrics are exported at: `http://localhost:8080/actuator/prometheus`

**Configuration:**
```properties
management.metrics.export.prometheus.enabled=true
management.endpoints.web.exposure.include=prometheus,metrics,circuitbreakers,circuitbreakerevents
```

### Metrics Endpoint

List all available metrics: `http://localhost:8080/actuator/metrics`

Get specific metric: `http://localhost:8080/actuator/metrics/http.server.requests`

## Prometheus Scraping

Prometheus is configured to scrape the application every 15 seconds:

```yaml
# monitoring/prometheus.yml
scrape_configs:
  - job_name: 'metering-application'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 15s
    static_configs:
      - targets: ['host.docker.internal:8080']
```

## Grafana Dashboards

### Available Metrics for Dashboards

1. **Throughput Dashboard**
   - `rate(http_server_requests_seconds_count[1m])` - Requests per second
   - `rate(metering_events_ingested_total[1m])` - Events per second

2. **Latency Dashboard**
   - `http_server_requests_seconds{quantile="0.95"}` - P95 HTTP latency
   - `metering_events_processing_latency_seconds{quantile="0.99"}` - P99 processing latency
   - `metering_redis_storage_latency_seconds{quantile="0.95"}` - P95 Redis latency
   - `metering_db_persistence_latency_seconds{quantile="0.95"}` - P95 DB latency

3. **Error Rate Dashboard**
   - `rate(metering_events_ingestion_errors_total[1m])` - Error rate
   - `resilience4j_circuitbreaker_failure_rate` - Circuit breaker failure rate
   - `rate(metering_aggregation_windows_errors_total[1m])` - Aggregation error rate

4. **Resource Usage Dashboard**
   - `jvm_memory_used_bytes` - Memory usage
   - `r2dbc_pool_acquired` - Database connections
   - `jvm_threads_live_threads` - Active threads

5. **Resilience Dashboard**
   - `resilience4j_circuitbreaker_state` - Circuit breaker states (postgres, redis)
   - `resilience4j_retry_calls_total` - Retry attempts
   - `metering_lock_contention_total` - Lock contention

6. **Business Metrics Dashboard**
   - `metering_events_ingested_total` - Total events ingested
   - `metering_events_late_detected_total` - Late events detected
   - `metering_aggregation_windows_processed_total` - Windows processed
   - `metering_db_persistence_batch_size_total` - Batch sizes

## Key Metrics for High Throughput

### Critical Metrics

1. **Throughput**: `rate(metering_events_ingested_total[1m])` - Target: ≥ 2,000/s (tested up to 3,700+ events/second)
2. **P99 Latency**: `metering_events_processing_latency_seconds{quantile="0.99"}` - Must be < 100ms
3. **Error Rate**: `rate(metering_events_ingestion_errors_total[1m])` - Should be < 0.1%
4. **Redis Latency**: `metering_redis_storage_latency_seconds{quantile="0.95"}` - Should be < 10ms
5. **DB Latency**: `metering_db_persistence_latency_seconds{quantile="0.95"}` - Should be < 50ms

### Example Prometheus Queries

#### Events per Second
```promql
rate(metering_events_ingested_total[1m])
```

#### P99 Processing Latency
```promql
metering_events_processing_latency_seconds{quantile="0.99"}
```

#### Error Rate Percentage
```promql
rate(metering_events_ingestion_errors_total[1m]) / rate(metering_events_ingested_total[1m]) * 100
```

#### Circuit Breaker Open Rate
```promql
rate(resilience4j_circuitbreaker_calls_total{state="not_permitted"}[1m])
```

#### Lock Contention Rate
```promql
rate(metering_lock_contention_total[1m])
```

## Structured Logging

### Configuration

Structured logging is configured via `logback-spring.xml`:

- **Development Profile**: Pretty-printed console output for readability
- **Production Profile**: JSON structured logs with Logstash encoder

### Log Format

**Development** (human-readable):
```
2025-01-28 18:00:00.123  INFO c.r.metering.service.EventProcessingService : Event processed successfully
```

**Production** (JSON):
```json
{
  "@timestamp": "2025-01-28T18:00:00.123Z",
  "level": "INFO",
  "logger": "com.rdpk.metering.service.EventProcessingService",
  "message": "Event processed successfully",
  "application": "takehome1",
  "thread": "reactor-http-nio-1",
  "trace": "12345678-1234-1234-1234-123456789012"
}
```

### Log Levels

Configurable per package and environment:

- **Application Code**: INFO, WARN, ERROR
- **Spring Framework**: INFO, WARN, ERROR
- **Database**: WARN, ERROR only
- **Resilience4j**: INFO, WARN, ERROR

### Logging Features

- **Correlation IDs**: Automatic request tracing
- **Structured Fields**: Consistent JSON schema
- **Context Propagation**: MDC context for correlation
- **Log Aggregation**: Ready for ELK stack or similar

## Actuator Endpoints

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

Response:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 500000000000,
        "free": 100000000000,
        "threshold": 10485760
      }
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

### Prometheus Metrics

```bash
curl http://localhost:8080/actuator/prometheus
```

Sample metrics:
```
# HELP http_server_requests_seconds Duration of HTTP server requests
# TYPE http_server_requests_seconds summary
http_server_requests_seconds_count{method="POST",uri="/api/v1/events",status="201"} 1234.0
http_server_requests_seconds_sum{method="POST",uri="/api/v1/events",status="201"} 567.8
http_server_requests_seconds_max{method="POST",uri="/api/v1/events",status="201"} 0.123

# HELP metering_events_ingested_total Total number of events ingested
# TYPE metering_events_ingested_total counter
metering_events_ingested_total{type="total"} 10000.0
```

## Metrics Interpretation

### Healthy System Indicators

**HTTP Metrics:**
- Request rate: Consistent with application load
- Duration p95: < 500ms
- Error rate: < 1%

**JVM Metrics:**
- Memory usage: < 80% of heap
- GC pauses: < 100ms
- Thread count: Stable

**Resilience4j Metrics:**
- Circuit breaker: CLOSED state (both postgres and redis)
- Failure rate: < 1%
- Retry attempts: Minimal

**Business Metrics:**
- Events/second: ≥ 2,000 (target met, tested up to 3,700+ events/second under stress)
- Processing latency P99: < 100ms
- Redis latency P95: < 10ms
- DB persistence latency P95: < 50ms
- Lock contention: < 1%

### Unhealthy System Indicators

**HTTP Metrics:**
- Request duration spike (> 1s)
- Error rate increase (> 5%)
- 500 status codes

**JVM Metrics:**
- Memory usage > 90%
- Frequent GC pauses
- Thread count growing

**Resilience4j Metrics:**
- Circuit breaker: OPEN state
- High failure rate
- Frequent retries

**Business Metrics:**
- Events/second dropping
- Processing latency P99 > 100ms
- Redis latency P95 > 10ms
- High lock contention
- Aggregation errors increasing

## Alerting (Future)

Potential alert conditions:

- Circuit breaker OPEN for > 5 minutes
- Error rate > 5%
- Response time p95 > 1s
- Memory usage > 90%
- Database connection pool exhausted
- Events/second < 8,000 (20% below target)
- Processing latency P99 > 100ms
- Lock contention > 10%

## Troubleshooting

### Issue: No metrics in Grafana

**Solution:**
1. Verify Prometheus is scraping: http://localhost:9090/targets
2. Check application is running: http://localhost:8080/actuator/health
3. Verify Prometheus can reach application

### Issue: Dashboard not showing data

**Solution:**
1. Check time range in Grafana (top right)
2. Verify Prometheus datasource is configured
3. Check dashboard queries in Grafana

### Issue: Structured logs not appearing

**Solution:**
1. Verify `spring.profiles.active=prod` in application.properties
2. Check logback-spring.xml is on classpath
3. Verify logstash-logback-encoder dependency

## Configuration

All metrics configuration is in `application.properties`:

```properties
# Metrics Configuration
management.metrics.export.prometheus.enabled=true
management.metrics.tags.application=takehome1
management.metrics.tags.environment=${ENVIRONMENT:production}
management.metrics.tags.version=0.0.1-SNAPSHOT

# Actuator Configuration
management.endpoints.web.exposure.include=health,info,prometheus,metrics,circuitbreakers,circuitbreakerevents
management.endpoint.health.show-details=when-authorized
```

## Docker Compose Services

```yaml
services:
  postgres:
    image: postgres:17.2
    environment:
      POSTGRES_DB: takehome1
      POSTGRES_USER: takehome1
      POSTGRES_PASSWORD: takehome1
    ports:
      - "5432:5432"

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

  prometheus:
    image: prom/prometheus:latest
    volumes:
      - ./monitoring/prometheus.yml:/etc/prometheus/prometheus.yml
    ports:
      - "9090:9090"

  grafana:
    image: grafana/grafana:latest
    volumes:
      - ./monitoring/datasources:/etc/grafana/provisioning/datasources
      - ./monitoring/dashboards:/var/lib/grafana/dashboards
    ports:
      - "3000:3000"
```

## Summary

**Automatic Metrics (No Code Required):**
- ✅ JVM metrics (memory, GC, threads)
- ✅ HTTP metrics (requests, latency, status codes)
- ✅ R2DBC connection pool metrics
- ✅ Resilience4j metrics (circuit breakers, retries)

**Custom Metrics (Instrumented in Services):**
- ✅ Event processing metrics (ingestion, latency, errors)
- ✅ Redis operation metrics (storage, counter updates, reads)
- ✅ Database persistence metrics (latency, batch sizes)
- ✅ Aggregation metrics (processing latency, windows processed/errors)
- ✅ Lock metrics (acquisition latency, contention)
- ✅ Late event metrics (detected, processed)

All metrics are automatically exported to Prometheus format at `/actuator/prometheus` and can be scraped by Prometheus for visualization in Grafana.

## Additional Resources

- [Spring Boot Actuator Docs](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Micrometer Documentation](https://micrometer.io/docs)
- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)

## Related Documentation

- **[README](../README.md)** - Project overview and quick start
- **[Development Guide](DEVELOPMENT.md)** - Complete development guide with all make commands
- **[Dashboards Documentation](DASHBOARDS.md)** - Grafana dashboard documentation and usage
- **[Architecture Documentation](ARCHITECTURE.md)** - System architecture and design patterns
