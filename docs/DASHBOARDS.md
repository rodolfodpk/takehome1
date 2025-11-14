# Grafana Dashboards Documentation

## Overview

The metering system provides three hierarchical dashboards for monitoring system health, application performance, and business metrics. Dashboards are automatically provisioned by Grafana and located in the "Metering" folder.

**Access**: http://localhost:3000 (admin/admin)

## Dashboard Hierarchy

The dashboards are organized from high-level to detailed:

1. **System Overview** - Executive/business view with key metrics
2. **Application & Infrastructure** - Technical deep dive for operations teams
3. **Business Metrics** - Business analytics and event insights

---

## 1. System Overview Dashboard

**Purpose**: High-level system health monitoring for executives and business stakeholders.

**Target Audience**: Executives, product managers, business stakeholders

**Time Range**: 1 hour (default)

**Refresh Interval**: 30 seconds

### Key Metrics (8 panels)

1. **Events per Second** (Gauge)
   - Metric: `rate(metering_events_ingested_total{type="total"}[1m])`
   - Shows current throughput
   - Thresholds: 0 (red), 5k (yellow), 10k (green)

2. **P95 Latency** (Stat)
   - Metric: `metering_events_processing_latency_seconds{quantile="0.95"}`
   - 95th percentile processing latency
   - Thresholds: <100ms (green), <200ms (yellow), >200ms (red)

3. **P99 Latency** (Stat)
   - Metric: `metering_events_processing_latency_seconds{quantile="0.99"}`
   - 99th percentile processing latency
   - Thresholds: <100ms (green), <200ms (yellow), >200ms (red)

4. **Error Rate %** (Stat)
   - Metric: `(rate(metering_events_ingestion_errors_total[1m]) / rate(metering_events_ingested_total{type="total"}[1m])) * 100`
   - Percentage of failed event ingestions
   - Thresholds: <0.1% (green), <1% (yellow), >1% (red)

5. **Circuit Breaker - Postgres** (Stat)
   - Metric: `resilience4j_circuitbreaker_state{name="postgres"}`
   - Database circuit breaker state
   - Values: CLOSED (green), OPEN (red), HALF_OPEN (yellow)

6. **Circuit Breaker - Redis** (Stat)
   - Metric: `resilience4j_circuitbreaker_state{name="redis"}`
   - Redis circuit breaker state
   - Values: CLOSED (green), OPEN (red), HALF_OPEN (yellow)

7. **Memory Usage %** (Gauge)
   - Metric: `(jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"}) * 100`
   - JVM heap memory utilization
   - Thresholds: <80% (green), <90% (yellow), >90% (red)

8. **Events per Second Trend** (Time Series)
   - Metric: `rate(metering_events_ingested_total{type="total"}[1m])`
   - Historical throughput trend over time

---

## 2. Application & Infrastructure Dashboard

**Purpose**: Comprehensive technical monitoring for operations, SRE, and engineering teams.

**Target Audience**: DevOps, SRE, backend engineers

**Time Range**: 15 minutes (default)

**Refresh Interval**: 10 seconds

### Sections

#### HTTP Metrics (4 panels)
- **HTTP Request Rate**: Request rate by method and URI
- **HTTP Request Duration**: P50, P95, P99 latencies
- **HTTP Error Rate**: 4xx and 5xx error rates
- **HTTP Status Code Distribution**: Pie chart of status codes

#### JVM Metrics (4 panels)
- **JVM Memory Usage**: Heap and non-heap memory over time
- **JVM Memory Usage Percentage**: Heap utilization gauge
- **JVM Thread Count**: Live and daemon threads
- **GC Pause Time**: Garbage collection pause times

#### Database Metrics (6 panels)
- **R2DBC Connection Pool - Acquired**: Active connections
- **R2DBC Connection Pool - Pending**: Waiting connection requests
- **R2DBC Connection Pool - Idle**: Available connections
- **R2DBC Connection Pool Utilization**: Pool usage percentage
- **Database Persistence Latency**: P50, P95, P99 latencies
- **Events Persisted per Second**: Database write throughput

#### Redis Metrics (3 panels)
- **Redis Storage Latency**: P50, P95, P99 for event storage
- **Redis Read Latency**: P50, P95, P99 for batch reads
- **Events Stored per Second**: Redis write throughput

#### Resilience4j Metrics (8 panels)
- **Circuit Breaker - Postgres**: State (CLOSED/OPEN/HALF_OPEN)
- **Circuit Breaker - Redis**: State (CLOSED/OPEN/HALF_OPEN)
- **Circuit Breaker State Transitions**: Historical state changes
- **Postgres Circuit Breaker Failure Rate**: Failure percentage
- **Redis Circuit Breaker Failure Rate**: Failure percentage
- **Circuit Breaker Calls - Postgres**: Successful/failed call rates
- **Circuit Breaker Calls - Redis**: Successful/failed call rates
- **Retry Attempts**: Postgres and Redis retry rates
- **Time Limiter Timeouts**: Timeout occurrences

**Total Panels**: 27

---

## 3. Business Metrics Dashboard

**Purpose**: Business analytics and event insights for product and business teams.

**Target Audience**: Product managers, business analysts, data teams

**Time Range**: 1 hour (default)

**Refresh Interval**: 30 seconds

### Key Metrics

- Events ingested per second (total and by tenant)
- Events ingested by customer
- Aggregation window processing
- Late event detection and processing
- Database persistence metrics
- Distributed lock metrics

See dashboard for complete list of business metrics.

---

## Metric Naming Conventions

### Custom Metrics (metering_*)
- `metering_events_ingested_total`: Total events ingested
- `metering_events_processing_latency_seconds`: Event processing latency histogram
- `metering_events_ingestion_errors_total`: Event ingestion errors
- `metering_redis_storage_latency_seconds`: Redis storage latency histogram
- `metering_redis_read_latency_seconds`: Redis read latency histogram
- `metering_redis_counter_update_latency_seconds`: Redis counter update latency histogram
- `metering_db_persistence_latency_seconds`: Database persistence latency histogram
- `metering_db_persistence_batch_size_total`: Batch size for database persistence

### Standard Metrics
- `http_server_requests_seconds_*`: HTTP request metrics (Spring Boot Actuator)
- `jvm_memory_*`: JVM memory metrics
- `jvm_threads_*`: JVM thread metrics
- `jvm_gc_*`: Garbage collection metrics
- `r2dbc_pool_*`: R2DBC connection pool metrics
- `resilience4j_*`: Resilience4j circuit breaker, retry, and timeout metrics

### PromQL Query Patterns

**Rate Calculation**:
```promql
rate(metric_name[1m])
```

**Percentile Calculation**:
```promql
histogram_quantile(0.95, rate(metric_name_bucket[1m]))
```

**Percentage Calculation**:
```promql
(metric_a / metric_b) * 100
```

**Error Rate**:
```promql
(rate(errors_total[1m]) / rate(total[1m])) * 100
```

---

## Accessing Dashboards

1. **Grafana URL**: http://localhost:3000
2. **Default Credentials**: admin/admin
3. **Folder**: Navigate to "Metering" folder in Grafana
4. **Dashboard List**:
   - Metering - System Overview
   - Metering - Application & Infrastructure
   - Metering - Business Metrics

---

## Troubleshooting

### No Data in Panels

1. **Check Prometheus**: Verify metrics are being scraped at http://localhost:9090
2. **Check Application**: Verify `/actuator/prometheus` endpoint returns metrics
3. **Check Time Range**: Ensure selected time range contains data
4. **Check Metric Names**: Verify metric names match actual Prometheus metrics

### Circuit Breaker States

- **CLOSED (0)**: Normal operation, requests allowed
- **OPEN (1)**: Circuit is open, requests rejected immediately
- **HALF_OPEN (2)**: Testing if service recovered, limited requests allowed

### Common Issues

- **High Latency**: Check database and Redis connection pools, network latency
- **High Error Rate**: Check circuit breaker states, application logs
- **Memory Issues**: Check JVM heap usage, GC pause times
- **Connection Pool Exhaustion**: Check R2DBC pool metrics, increase pool size if needed

---

## Dashboard Updates

Dashboards are automatically provisioned from JSON files in `monitoring/dashboards/`. Changes to JSON files are picked up by Grafana within 10 seconds (configured in `dashboard.yml`).

To manually refresh:
1. Go to dashboard settings
2. Click "Save dashboard"
3. Changes will be persisted

## Related Documentation

- **[README](../README.md)** - Project overview and quick start
- **[Observability Documentation](OBSERVABILITY.md)** - Monitoring setup, metrics, and Prometheus configuration
- **[Development Guide](DEVELOPMENT.md)** - Complete development guide with all make commands

