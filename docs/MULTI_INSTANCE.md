# Multi-Instance Setup & Distributed Lock Testing

Complete guide for running the application with multiple instances behind a load balancer to test distributed locks and multi-instance behavior.

## Overview

The multi-instance setup runs **2 application instances** behind an **nginx load balancer** to test distributed locking behavior across multiple service instances. This setup is essential for validating that:

- ✅ Distributed locks prevent duplicate window processing across instances
- ✅ Redis-based locking works correctly under load
- ✅ Lock contention is properly handled
- ✅ Aggregation windows are processed exactly once (idempotency)

## Architecture

```
                    ┌─────────────┐
                    │   k6 Tests   │
                    └──────┬───────┘
                           │
                           ▼
                    ┌─────────────┐
                    │   nginx LB   │  Port 8080
                    │ (round-robin)│
                    └──────┬───────┘
                           │
            ┌──────────────┴──────────────┐
            │                             │
            ▼                             ▼
    ┌──────────────┐            ┌──────────────┐
    │  App Instance │            │  App Instance │
    │      1        │            │      2        │
    │  Port 8081    │            │  Port 8082    │
    └──────┬────────┘            └──────┬────────┘
           │                            │
           └────────────┬───────────────┘
                        │
        ┌───────────────┼───────────────┐
        │               │               │
        ▼               ▼               ▼
  ┌─────────┐    ┌─────────┐    ┌─────────┐
  │PostgreSQL│    │  Redis  │    │Prometheus│
  │          │    │(Locks)  │    │ Grafana  │
  └──────────┘    └──────────┘    └──────────┘
```

### Components

- **nginx Load Balancer**: Round-robin distribution across 2 app instances (port 8080)
  - Resource limits: 0.5 CPU, 128MB RAM
- **App Instance 1**: First application instance (port 8081, direct access)
  - Resource limits: 1.5 CPU, 768MB RAM (ensures fair resource distribution)
- **App Instance 2**: Second application instance (port 8082, direct access)
  - Resource limits: 1.5 CPU, 768MB RAM (ensures fair resource distribution)
- **Shared Infrastructure**: PostgreSQL, Redis, Prometheus, Grafana (shared by both instances)
- **Distributed Locks**: Redis-based locks (Redisson) ensure only one instance processes each aggregation window

**Resource Limits**: Each app instance is limited to 1.5 CPU cores and 768MB RAM to ensure fair resource distribution during performance tests and prevent one instance from starving the other.

## Quick Start

### 1. Build and Start Multi-Instance Stack

```bash
# Build Docker image and start full stack (2 app instances + nginx + observability)
# Uses k6 profile by default (circuit breakers disabled for pure performance metrics)
make start-multi

# For spike tests with circuit breaker validation:
SPRING_PROFILES_ACTIVE=k6,k6-spike make start-multi
```

This command:
1. Builds the application JAR
2. Builds the Docker image
3. Starts PostgreSQL, Redis, Prometheus, and Grafana
4. Starts 2 app instances (app1, app2) with **k6 profile** (circuit breakers disabled)
5. Starts nginx load balancer
6. Waits for all services to be healthy

**Expected startup time:** 60-90 seconds (app instances need time to initialize)

**Note:** The multi-instance setup uses the `k6` profile by default, which disables circuit breakers and time limiters for pure performance metrics. For spike tests that need circuit breaker validation, use `SPRING_PROFILES_ACTIVE=k6,k6-spike`.

### 2. Verify Services

After startup, you should see:

```
📊 Service URLs:
  - Application (via nginx LB): http://localhost:8080
  - App Instance 1 (direct): http://localhost:8081
  - App Instance 2 (direct): http://localhost:8082
  - Grafana: http://localhost:3000 (admin/admin)
  - Prometheus: http://localhost:9090
```

### 3. Run k6 Tests Against Multi-Instance Setup

**Important**: The multi-instance stack must be running before running k6 tests. The k6 test scripts will detect that the application is already running and skip app startup.

```bash
# Run load test against 2 instances (tests distributed locks)
make k6-load-multi

# Or run all k6 tests
make k6-test-multi
```

**Note**: If you see port conflict errors, ensure the multi-instance stack is running (`make start-multi`) and no other application is using port 8080.

## Make Commands

### Multi-Instance Management

```bash
# Build Docker image for multi-instance
make docker-build-multi

# Start multi-instance stack (includes build)
make start-multi

# Stop multi-instance stack
make stop-multi
```

### k6 Testing Against Multi-Instance

All k6 test commands support multi-instance testing:

```bash
# Individual tests
make k6-warmup-multi      # Warm-up test (2 VUs, 10 seconds)
make k6-smoke-multi       # Smoke test (10 VUs, 1 minute)
make k6-load-multi        # Load test (350 VUs, 2 minutes) - **Tests distributed locks**
make k6-stress-multi      # Stress test (ramp 50→500 VUs, 3 minutes)
make k6-spike-multi       # Spike test (spike 50→500→50, 2.5 minutes)

# Run all tests sequentially
make k6-test-multi        # Runs all k6 tests against multi-instance setup
```

**Note:** All multi-instance k6 tests automatically target `http://localhost:8080` (nginx load balancer).

## Configuration

### Spring Profiles

The multi-instance setup uses the **`k6` profile by default** (optimized for load testing):

- **Circuit breakers DISABLED** - Provides pure performance metrics without circuit breaker overhead
- **Time limiters DISABLED** - Avoids artificial timeouts during load tests
- **Retries reduced** - 1 attempt (effectively disabled)

**For spike tests that need circuit breaker validation:**

```bash
# Restart with circuit breakers enabled
SPRING_PROFILES_ACTIVE=k6,k6-spike make start-multi
```

This enables circuit breakers with relaxed thresholds (90% failure rate) for spike test validation.

## How Distributed Locks Work

### Lock Key Format

```
aggregation:lock:{tenantId}:{customerId}:{windowStart}
```

Example:
```
aggregation:lock:1:acme-customer-001:1704067200
```

### Lock Behavior

1. **Window Aggregation Trigger**: Every 30 seconds, both app instances attempt to process aggregation windows
2. **Lock Acquisition**: First instance to acquire the lock processes the window
3. **Lock Contention**: Second instance fails to acquire lock and skips processing (logs `lockContention` metric)
4. **Idempotency**: Each window is processed exactly once, regardless of which instance processes it

### Lock Configuration

- **Timeout**: 30 seconds (time to wait for lock acquisition)
- **Lease Time**: 60 seconds (lock duration)
- **Metrics**: `lockContention` counter tracks failed acquisitions

## Monitoring Distributed Locks

### Grafana Dashboards

Access Grafana at http://localhost:3000 (admin/admin) to monitor:

- **Lock Contention**: Number of failed lock acquisitions
- **Lock Acquisition Latency**: Time to acquire locks
- **Aggregation Windows**: Number of windows processed per instance
- **Request Distribution**: Requests per instance (via nginx)

### Prometheus Metrics

Key metrics for distributed locks:

```promql
# Lock contention (failed acquisitions)
metering_lock_contention_total

# Lock acquisition latency
metering_lock_acquisition_latency_seconds

# Aggregation windows processed
metering_aggregation_windows_processed_total
```

Access Prometheus at http://localhost:9090

### Direct Instance Monitoring

You can also monitor each instance directly:

```bash
# Check app1 health
curl http://localhost:8081/actuator/health

# Check app2 health
curl http://localhost:8082/actuator/health

# Check nginx (load balancer) health
curl http://localhost:8080/actuator/health
```

## Testing Distributed Locks

### What to Verify

When running k6 tests against the multi-instance setup, verify:

1. **No Duplicate Processing**: Each aggregation window is processed exactly once
2. **Lock Contention**: `lockContention` metric shows failed acquisitions (expected behavior)
3. **Load Distribution**: Requests are distributed across both instances (check nginx logs)
4. **Correct Aggregations**: Aggregation results are correct despite multiple instances

### Expected Behavior

- ✅ **Lock Contention is Normal**: With 2 instances, you should see some lock contention (one instance acquires, other fails)
- ✅ **No Duplicate Windows**: Each window should appear only once in the database
- ✅ **Load Balancing**: Requests should be distributed roughly 50/50 across instances
- ✅ **Metrics Match**: Lock metrics should correlate with aggregation activity

### Verifying Lock Behavior

```bash
# 1. Start multi-instance stack
make start-multi

# 2. Run load test
make k6-load-multi

# 3. Check lock contention in Grafana
# Navigate to: http://localhost:3000
# Dashboard: System Overview
# Metric: metering_lock_contention_total

# 4. Verify no duplicate windows in database
docker-compose -f docker-compose.yml -f docker-compose.multi.yml exec postgres psql -U takehome1 -d takehome1 -c "SELECT tenant_id, customer_id, window_start, COUNT(*) FROM aggregation_windows GROUP BY tenant_id, customer_id, window_start HAVING COUNT(*) > 1;"
# Should return 0 rows (no duplicates)
```

## Configuration Files

### docker-compose.multi.yml

Multi-instance docker-compose configuration:
- Defines `app1` and `app2` services (2 app instances)
- Defines `nginx` service (load balancer)
- Uses same network and infrastructure as base `docker-compose.yml`

### nginx.conf

Nginx load balancer configuration:
- Round-robin distribution
- Health check endpoint support
- Gzip compression
- Proper proxy headers

## Troubleshooting

### Services Not Starting

**Problem:** App instances fail to start

**Solution:**
```bash
# Check logs
docker-compose -f docker-compose.yml -f docker-compose.multi.yml logs app1
docker-compose -f docker-compose.yml -f docker-compose.multi.yml logs app2

# Restart services
make stop-multi
make start-multi
```

### nginx Not Routing

**Problem:** Requests to port 8080 fail

**Solution:**
```bash
# Check nginx logs
docker-compose -f docker-compose.yml -f docker-compose.multi.yml logs nginx

# Verify app instances are healthy
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health

# Restart nginx
docker-compose -f docker-compose.yml -f docker-compose.multi.yml restart nginx
```

### High Lock Contention

**Problem:** Too many lock contention events

**Analysis:**
- Some lock contention is **expected** with 2 instances
- High contention may indicate:
  - Too many concurrent aggregation windows
  - Lock timeout too short
  - Processing taking too long

**Solution:**
- Check aggregation window processing time
- Verify lock timeout/lease time configuration
- Monitor lock acquisition latency

### Port Conflicts

**Problem:** Ports 8080, 8081, or 8082 already in use

**Solution:**
```bash
# Find processes using ports
lsof -i :8080
lsof -i :8081
lsof -i :8082

# Stop conflicting services
make stop-multi
make stop  # Stop single-instance setup if running
```

## Comparison: Single vs Multi-Instance

| Aspect | Single Instance | Multi-Instance |
|--------|----------------|----------------|
| **Setup** | `make start` | `make start-multi` |
| **App Instances** | 1 (port 8080) | 2 (ports 8081, 8082) |
| **Load Balancer** | None | nginx (port 8080) |
| **Distributed Locks** | Not tested | Fully tested |
| **k6 Tests** | `make k6-*` | `make k6-*-multi` |
| **Use Case** | Development | Production testing |

## Best Practices

1. **Always Test Distributed Locks**: Before deploying to production, run `make k6-test-multi` to verify distributed lock behavior
2. **Monitor Lock Contention**: High contention may indicate configuration issues
3. **Verify No Duplicates**: Always verify no duplicate aggregation windows after tests
4. **Check Load Distribution**: Ensure requests are distributed across instances
5. **Use Grafana**: Monitor metrics in real-time during tests

## Related Documentation

- **[Architecture](ARCHITECTURE.md)** - Distributed locking strategy details
- **[Development Guide](DEVELOPMENT.md)** - General development workflows
- **[K6 Performance Testing](K6_PERFORMANCE.md)** - k6 testing guide
- **[Observability](OBSERVABILITY.md)** - Monitoring and metrics

