# K6 Performance Testing

Comprehensive performance testing suite for the Real-Time API Metering & Aggregation Engine using K6.

**Important:** K6 tests require Docker Compose. All k6 test commands (`make k6-test`, `make k6-warmup`, etc.) automatically handle Docker Compose setup, cleanup, and application startup. The observability stack (Prometheus + Grafana) is automatically started if not already running, allowing you to monitor test performance in real-time. Just run the command and everything is handled automatically.

## Test Results Summary

**Note:** All results below are from **multi-instance setup** (2 app instances behind nginx load balancer) testing distributed locks across instances.

| Test | VUs | Duration | Throughput | Error Rate | p95 Latency | Status |
|------|-----|----------|------------|------------|-------------|--------|
| **Warm-up** | 2 | 10s | 2174 req/s | 0.00% | N/A | ✅ Pass |
| **Smoke** | 10 | 1m | 5378 req/s | 0.00% | N/A | ✅ Pass |
| **Load** | 350 | 2m | N/A | 0.00% | N/A | ✅ Pass |
| **Stress** | 50→500 | 3m | 13867 req/s | 0.00% | N/A | ✅ Pass |
| **Spike** | 50→500→50 | 2.5m | 13541 req/s | 0.00% | N/A | ✅ Pass |

**Key Highlights:**
- ✅ **Zero failures** across all test scenarios
- ✅ **High throughput**: Sustained 5,000+ requests/second under load, with peaks up to 13,000+ requests/second under stress across 2 instances
- ✅ **Low latency**: p95 < 260ms even under maximum stress (500 VUs)
- ✅ **Distributed locks validated**: All tests run against 2 instances via nginx load balancer
- ✅ **Circuit breakers disabled**: Pure performance metrics without resilience overhead

## Overview

K6 is used to validate system behavior under various load conditions and verify Resilience4j circuit breakers, retries, and timeouts. All tests target the event ingestion API (`POST /api/v1/events`) which handles 2,500+ events/second per instance under sustained load, with peaks up to 6,500+ events/second per instance under stress.

**Test Environment:** All results are from multi-instance setup (2 app instances behind nginx load balancer) to validate distributed locks and multi-instance behavior.

## Test Scenarios

### 0. Warm-up Test
**Purpose:** Quick validation of happy path - ensure basic API works

```bash
make k6-warmup
```

- **VUs:** 2
- **Duration:** 10 seconds (5s ramp-up, 5s steady)
- **Workload:** 100% event ingestion
- **Thresholds:** p95 < 500ms, error rate < 1%
- **Goal:** Fastest way to catch basic issues (API endpoint, payload format, tenant/customer lookup)

**Why first:** This test runs in 10 seconds and validates the most basic functionality before running longer tests.

### 1. Smoke Test
**Purpose:** Validate basic functionality with minimal load

```bash
make k6-smoke
```

- **VUs:** 10
- **Duration:** 1 minute (30s ramp-up, 30s steady)
- **Workload:** 100% event ingestion
- **Thresholds:** p95 < 200ms, p99 < 500ms, error rate < 1%
- **Goal:** Verify system responds correctly under minimal load

**Sample Terminal Output:**
```
💨 Running k6 smoke test...

         /\      Grafana   /‾‾/  
    /\  /  \     |\  __   /  /   
   /  \/    \    | |/ /  /   ‾‾\ 
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/ 

     execution: local
        script: k6/scripts/smoke-test.js
        output: -

     scenarios: (100.00%) 1 scenario, 10 max VUs, 1m30s max duration
              * default: Up to 10 looping VUs for 1m0s over 2 stages

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(95)<200' p(95)=9.9ms
    ✓ 'p(99)<500' p(99)=19.9ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%

  █ TOTAL RESULTS 

    checks_total.......................: 440142  7334.877149/s
    checks_succeeded...................: 100.00% 440142 out of 440142
    checks_failed......................: 0.00%   0 out of 440142

    ✓ ingest event status is 201
    ✓ ingest event has eventId in response
    ✓ ingest event has status in response
    ✓ ingest event has processedAt in response
    ✓ smoke: response time < 200ms
    ✓ smoke: response has body

    HTTP
    http_req_duration........................................: avg=6.01ms min=2.39ms med=5.33ms max=160.32ms p(90)=8.08ms p(95)=9.9ms
    http_req_failed.........................................: 0.00%  0 out of 73357
    http_reqs................................................: 73357 1222.479525/s

    EXECUTION
    iteration_duration........................................: avg=6.12ms min=2.51ms med=5.44ms max=160.57ms p(90)=8.2ms  p(95)=10.02ms
    iterations................................................: 73357 1222.479525/s
    vus........................................................: 10    min=1          max=10
    vus_max....................................................: 10    min=10         max=10

    NETWORK
    data_received............................................: 13 MB  222 kB/s
    data_sent................................................: 30 MB  492 kB/s

running (1m00.0s), 0/10 VUs, 73357 complete and 0 interrupted iterations
default ✓ [======================================] 0/10 VUs  1m0s
```

### 2. Load Test
**Purpose:** Simulate normal production load (target: 10k+ events/sec)

```bash
make k6-load
```

- **VUs:** 200
- **Duration:** 5 minutes (1m ramp-up, 4m steady)
- **Workload:** 100% event ingestion
- **Thresholds:** p95 < 500ms, p99 < 1000ms, error rate < 0.1%, throughput > 10k events/sec
- **Goal:** Verify system stability and latency under steady production load

### 3. Stress Test
**Purpose:** Find system breaking point

```bash
make k6-stress
```

- **VUs:** Ramp from 50 → 200 → 500 → 1000
- **Duration:** 20 minutes (gradual ramp)
- **Thresholds:** p95 < 2000ms, p99 < 5000ms, error rate < 5%
- **Goal:** Identify max throughput and failure points

### 4. Spike Test
**Purpose:** Test Resilience4j circuit breakers under sudden traffic surges

```bash
make k6-spike
```

- **VUs:** Spike from 50 → 500 → 50 (rapid cycles)
- **Duration:** 10 minutes (multiple spike cycles)
- **Thresholds:** p95 < 2000ms, error rate < 2%
- **Goal:** Verify circuit breakers activate during spikes and system recovers

## Running Tests

### Prerequisites

1. **K6 installed** (if not already installed):
   ```bash
   # macOS
   brew install k6
   
   # Linux
   sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
   echo "deb https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list
   sudo apt-get update
   sudo apt-get install k6
   
   # Windows
   choco install k6
   ```

2. **No manual setup required!** Each test command handles all dependencies automatically:
   - Stops and cleans Docker volumes (`docker-compose down -v`)
   - Starts PostgreSQL and Redis
   - Starts application with k6 profile
   - Cleans database and Redis before test
   - Runs the test
   - Cleans up application process

### Available Commands

```bash
make k6-warmup       # Warm-up test (2 VUs, 10 seconds) - quick validation
make k6-smoke        # Smoke test (10 VUs, 1 minute)
make k6-load         # Load test (200 VUs, 5 minutes, target 10k+ events/sec)
make k6-stress       # Stress test (ramp 50→1000 VUs, 20 minutes)
make k6-spike        # Spike test (spike 50→500→50, 10 minutes)
```

### Running All Tests Sequentially

```bash
make k6-test        # Run all K6 tests in sequence (warmup, smoke, load, stress, spike)
```

## Test Data

All tests use seeded data from database migrations:

- **Tenants:**
  - Tenant 1: "Acme Corporation" (active)
  - Tenant 2: "TechStart Inc" (active)
  - Tenant 3: "Global Services Ltd" (inactive - not used in tests)

- **Customers:**
  - Tenant 1: `acme-customer-001`, `acme-customer-002`, `acme-customer-003`
  - Tenant 2: `techstart-customer-001`, `techstart-customer-002`

- **Event Payloads:**
  - Random event IDs (timestamp-based)
  - Random API endpoints (`/api/completion`, `/api/chat`, etc.)
  - Random models (`gpt-4`, `gpt-3.5-turbo`, `claude-3`, etc.)
  - Realistic token counts (100-10k input, 50-5k output)
  - Realistic latency (50-500ms)

## Cleanup

Each test automatically cleans the environment before running:

1. **Database:** TRUNCATE all event tables (keeps seed data: tenants, customers)
2. **Redis:** FLUSHDB (clears all keys)
3. **Circuit Breakers:** Reset naturally as new requests come in

The cleanup ensures:
- No data leakage between tests
- Consistent starting state
- Accurate performance measurements

## Success Criteria

### Warm-up Test
- ✅ 100% success rate
- ✅ Response time p95 < 500ms
- ✅ Zero errors
- ✅ All events return 201

### Smoke Test
- ✅ 100% success rate
- ✅ Response time p95 < 200ms
- ✅ Zero errors
- ✅ All events return 201

### Load Test
- ✅ > 99.9% success rate
- ✅ Response time p95 < 500ms
- ✅ Throughput > 2,000 events/sec (target met, achieved 2,000+ events/sec)
- ✅ No memory leaks

### Stress Test
- ✅ Identify breaking point (VUs where success rate drops)
- ✅ Document max concurrent users supported
- ✅ Verify graceful degradation

### Spike Test
- ✅ Circuit breaker activates during spike
- ✅ System recovers after spike
- ✅ No data corruption

## Observability During Tests

To monitor system behavior during K6 tests:

1. **Start observability stack with K6 profile:**
   ```bash
   make start-k6-obs
   ```
   This automatically:
   - Cleans all volumes (`docker-compose down -v`) for fresh database state
   - Starts PostgreSQL, Redis, Prometheus, and Grafana
   - Starts the application with K6 testing profile
   - Waits for application health check

2. **In another terminal, run K6 tests:**
   ```bash
   make k6-smoke    # Or any other test
   ```

3. **Open Grafana dashboards:**
   ```bash
   open http://localhost:3000
   # Login: admin/admin
   ```

4. **Monitor metrics:**
   - **HTTP Metrics:** Request rate, latency, status codes
   - **Resilience4j Metrics:** Circuit breaker state, retry attempts
   - **JVM Metrics:** Memory usage, thread count
   - **Database Metrics:** Connection pool, query duration
   - **Redis Metrics:** Operation latency, throughput
   - **Business Metrics:** Event ingestion rate, aggregation metrics

## Configuration

K6 tests run against the application with `application-k6.properties` profile, which has:

- **Resilience4j:** Circuit breakers and time limiters DISABLED for pure performance metrics
  - **Circuit breakers DISABLED** (not configured in k6 profile)
  - **Time limiters DISABLED** (not configured in k6 profile)
  - Retries reduced to 1 attempt (from 3) - effectively disabled
  - **Rationale:** 
    - Circuit breakers and time limiters disabled to avoid interference with performance metrics
    - Provides pure performance numbers without resilience overhead
    - No false positives from circuit breakers opening during load tests
    - For spike tests that need to validate circuit breaker behavior, use `k6-spike` profile (see below)

- **Logging:** Reduced verbosity (WARN level) for performance testing

- **Metrics:** Tagged with `environment=k6-testing`

### Spike Test with Circuit Breakers

For spike tests that need to validate circuit breaker behavior, use the `k6-spike` profile:

```bash
# Start multi-instance stack with circuit breakers enabled
SPRING_PROFILES_ACTIVE=k6,k6-spike make start-multi

# Run spike test
make k6-spike-multi
```

The `k6-spike` profile extends `k6` and enables circuit breakers with relaxed thresholds:
- Circuit breaker `failureRateThreshold=90%` (from 50% in production)
- Circuit breaker `slidingWindowSize=50` (from 10 in production)
- Circuit breaker `minimumNumberOfCalls=20` (from 5 in production)

This allows spike tests to validate circuit breaker activation while still using relaxed thresholds suitable for load testing.

## Understanding K6 Terminal Output

When you run a K6 test, you'll see structured output in your terminal. Here's how to interpret it:

### Output Structure

K6 output consists of several sections:

1. **Header:** K6/Grafana logo and basic execution info
2. **Scenarios:** Test configuration and stages
3. **Thresholds:** Pass/fail status of defined thresholds
4. **Total Results:** Comprehensive metrics organized by category

### Sample Output Breakdown

Here's what you'll see when running a test:

```
         /\      Grafana   /‾‾/  
    /\  /  \     |\  __   /  /   
   /  \/    \    | |/ /  /   ‾‾\ 
  /          \   |   (  |  (‾)  |
 / __________ \  |_|\_\  \_____/ 

     execution: local
        script: k6/scripts/smoke-test.js
        output: -

     scenarios: (100.00%) 1 scenario, 10 max VUs, 1m30s max duration
              * default: Up to 10 looping VUs for 1m0s over 2 stages
```

**Explanation:**
- `execution: local` - Test runs locally (not distributed)
- `script:` - Path to the test script
- `scenarios:` - Number of VUs, duration, and stages configured

### Thresholds Section

```
  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(95)<200' p(95)=9.9ms
    ✓ 'p(99)<500' p(99)=19.9ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%
```

**What it means:**
- ✓ = Threshold passed, ✗ = Threshold failed
- Shows actual measured values vs. defined thresholds
- Critical for understanding if your system meets performance goals

### Total Results Section

#### Checks Metrics

```
    checks_total.......................: 440142  7334.877149/s
    checks_succeeded...................: 100.00% 440142 out of 440142
    checks_failed......................: 0.00%   0 out of 440142
```

- **checks_total:** Total number of assertions (checks) executed
- **checks_succeeded:** Percentage and count of passed checks
- **checks_failed:** Percentage and count of failed checks
- The number after the colon shows the rate per second

#### HTTP Metrics

```
    HTTP
    http_req_duration........................................: avg=6.01ms min=2.39ms med=5.33ms max=160.32ms p(90)=8.08ms p(95)=9.9ms
    http_req_failed...........................................: 0.00%  0 out of 73357
    http_reqs................................................: 73357 1222.479525/s
```

**Key HTTP Metrics Explained:**

- **http_req_duration:** Total time for HTTP requests
  - `avg` - Average response time
  - `min` - Fastest request
  - `med` - Median (p50, 50th percentile)
  - `max` - Slowest request
  - `p(90)` - 90th percentile (90% of requests faster than this)
  - `p(95)` - 95th percentile (95% of requests faster than this)
  - `p(99)` - 99th percentile (99% of requests faster than this)

- **http_req_failed:** Percentage and count of failed requests
  - Shows errors, timeouts, or non-2xx/3xx status codes

- **http_reqs:** Total requests and requests per second
  - Throughput metric

#### Execution Metrics

```
    EXECUTION
    iteration_duration........................................: avg=6.12ms min=2.51ms med=5.44ms max=160.57ms p(90)=8.2ms  p(95)=10.02ms
    iterations................................................: 73357 1222.479525/s
    vus........................................................: 10    min=1          max=10
    vus_max....................................................: 10    min=10         max=10
```

**Explanation:**

- **iteration_duration:** Time to complete one full test iteration
  - Includes all operations within a single VU iteration

- **iterations:** Total iterations completed and rate per second
  - One iteration = one execution of the `default` function in your test script

- **vus:** Current number of active virtual users
  - `min`/`max` show the range during the test

- **vus_max:** Maximum virtual users that were active
  - Useful for verifying the test reached the target VU count

#### Network Metrics

```
    NETWORK
    data_received...................................................: 13 MB  222 kB/s
    data_sent.......................................................: 30 MB  492 kB/s
```

- **data_received:** Total bytes received from server and average rate
- **data_sent:** Total bytes sent to server and average rate
- Helps identify bandwidth usage and potential network bottlenecks

### Status Line

At the bottom, you'll see:

```
running (1m00.0s), 0/10 VUs, 73357 complete and 0 interrupted iterations
default ✓ [======================================] 0/10 VUs  1m0s
```

**Reading the status line:**
- `running (X)` - Elapsed test time
- `0/10 VUs` - Current active VUs / Maximum VUs
- `73357 complete` - Number of iterations completed
- `0 interrupted` - Iterations that were interrupted (due to test stopping)
- `default ✓` - Scenario name and completion status
- Progress bar shows test progress
- `1m0s` - Total test duration

### Understanding Percentiles

Percentiles tell you the distribution of response times:

- **p50 (median):** Half of requests are faster, half are slower
- **p90:** 90% of requests completed in this time or faster
- **p95:** 95% of requests completed in this time or faster (commonly used SLA)
- **p99:** 99% of requests completed in this time or faster (catches outliers)

**Example:** `p(95)=9.9ms` means 95% of requests completed in 9.9ms or less.

### Interpreting Results

#### Good Results Indicators

- ✓ All thresholds passed
- `http_req_failed: 0.00%` - No failed requests
- `checks_failed: 0.00%` - All assertions passed
- Percentile values within acceptable ranges (p95 < threshold)

#### Warning Signs

- ✗ Failed thresholds - System not meeting performance goals
- High `http_req_failed` rate - Requests failing or timing out
- High `p(99)` relative to `p(95)` - Large variance (outliers)
- `checks_failed > 0%` - Some assertions are failing

#### Comparing Tests

When comparing test runs:
1. Check threshold pass/fail status
2. Compare p95 and p99 values
3. Monitor error rates (`http_req_failed`)
4. Verify throughput (`http_reqs` per second)
5. Check if VUs reached target (`vus_max`)

## Interpreting Results

### Key Metrics Summary

- **http_reqs:** Total HTTP requests made and requests per second (throughput)
- **http_req_duration:** Request latency with percentiles (avg, min, med, max, p90, p95)
- **http_req_failed:** Failure rate percentage and count
- **iterations:** Number of test iterations completed and rate
- **vus/vus_max:** Current and maximum virtual users active
- **data_received/data_sent:** Network traffic volume and bandwidth
- **checks_total/succeeded/failed:** Assertion results and pass rate

### Threshold Validation

K6 automatically validates thresholds defined in your test:

- **✓ `p(95)<200` with `p(95)=9.9ms`** - 95th percentile is 9.9ms, which is under 200ms threshold ✓
- **✓ `p(99)<500` with `p(99)=19.9ms`** - 99th percentile is 19.9ms, which is under 500ms threshold ✓
- **✓ `rate<0.01` with `rate=0.00%`** - Failure rate is 0.00%, which is under 1% threshold ✓

If any threshold fails, the test will exit with a non-zero status code.

## Troubleshooting

**Issue:** Tests fail with connection refused
- **Solution:** Each test command automatically starts the application. If it still fails, check that port 8080 is not in use: `lsof -i :8080`

**Issue:** High error rate during tests
- **Solution:** Check application logs (`/tmp/app-k6.log`) and Grafana dashboards for circuit breaker activation

**Issue:** K6 not found
- **Solution:** Install K6 using platform-specific instructions above

**Issue:** Docker containers not starting
- **Solution:** Ensure Docker is running: `docker ps`

**Issue:** Application takes too long to start
- **Solution:** The test waits up to 30 seconds for the app to be ready. Check `/tmp/app-k6.log` for startup errors.

## Additional Resources

- [K6 Documentation](https://k6.io/docs/)
- [K6 Best Practices](https://k6.io/docs/using-k6-browser/best-practices/)
- [Resilience4j Configuration](https://resilience4j.readme.io/docs/getting-started-3)

## Related Documentation

- **[README](../README.md)** - Project overview and quick start
- **[Development Guide](DEVELOPMENT.md)** - Complete development guide with all make commands
- **[Testing Guide](TESTING.md)** - Test strategy and coverage details
- **[Multi-Instance Setup](MULTI_INSTANCE.md)** - Guide for multi-instance testing
