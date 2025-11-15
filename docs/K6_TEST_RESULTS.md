# k6 Test Results

This file contains concise results from k6 performance tests. Results are automatically updated after each test run.

**⚠️ Important:** All test results in this file are from **multi-instance setup** (2 app instances behind nginx load balancer) testing distributed locks across instances. This setup validates that distributed locks work correctly and prevents duplicate processing across multiple service instances.

**Related Documentation:**
- **[README](../README.md)** - Project overview and quick start
- **[K6 Performance Testing](K6_PERFORMANCE.md)** - Comprehensive k6 testing guide with results summary
- **[Multi-Instance Setup](MULTI_INSTANCE.md)** - Guide for multi-instance testing
- **[Development Guide](DEVELOPMENT.md)** - Complete development guide with all make commands

## Warm-up Test - 2025-11-15 01:17:36

**Metrics:**
- **Throughput:** 572.973659/s
- **Error Rate:** N/A
- **p95 Latency:** N/A
- **Max VUs:** 2
- **Duration:** N/A
- **Status:** ✅ Pass


## Smoke Test - 2025-11-15 01:18:40

**Metrics:**
- **Throughput:** 2322.574748/s
- **Error Rate:** N/A
- **p95 Latency:** N/A
- **Max VUs:** 10
- **Duration:** N/A
- **Status:** ✅ Pass


## Load Test - 2025-11-15 01:20:44

**Metrics:**
- **Throughput:** N/A
- **Error Rate:** N/A
- **p95 Latency:** N/A
- **Max VUs:** 350
- **Duration:** N/A
- **Status:** ✅ Pass


## Stress Test - 2025-11-15 01:23:49

**Metrics:**
- **Throughput:** 4636.023596/s
- **Error Rate:** N/A
- **p95 Latency:** N/A
- **Max VUs:** 500
- **Duration:** N/A
- **Status:** ✅ Pass


## Spike Test - 2025-11-15 01:26:23

**Metrics:**
- **Throughput:** 5246.771368/s
- **Error Rate:** N/A
- **p95 Latency:** N/A
- **Max VUs:** 500
- **Duration:** N/A
- **Status:** ✅ Pass

