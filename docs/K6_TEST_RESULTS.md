# k6 Test Results

This file contains concise results from k6 performance tests. Results are automatically updated after each test run.

**⚠️ Important:** All test results in this file are from **multi-instance setup** (2 app instances behind nginx load balancer) testing distributed locks across instances. This setup validates that distributed locks work correctly and prevents duplicate processing across multiple service instances.

**Related Documentation:**
- **[README](../README.md)** - Project overview and quick start
- **[K6 Performance Testing](K6_PERFORMANCE.md)** - Comprehensive k6 testing guide with results summary
- **[Multi-Instance Setup](MULTI_INSTANCE.md)** - Guide for multi-instance testing
- **[Development Guide](DEVELOPMENT.md)** - Complete development guide with all make commands

## Warm-up Test - 2025-11-14 18:17:54

**Metrics:**
- **Throughput:** 287.092254/s
- **Error Rate:** N/A
- **p95 Latency:** N/A
- **Max VUs:** 2
- **Duration:** N/A
- **Status:** ✅ Pass


## Smoke Test - 2025-11-14 18:18:58

**Metrics:**
- **Throughput:** 2303.185155/s
- **Error Rate:** N/A
- **p95 Latency:** N/A
- **Max VUs:** 10
- **Duration:** N/A
- **Status:** ✅ Pass


## Load Test - 2025-11-14 18:21:03

**Metrics:**
- **Throughput:** N/A
- **Error Rate:** N/A
- **p95 Latency:** N/A
- **Max VUs:** 350
- **Duration:** N/A
- **Status:** ✅ Pass


## Stress Test - 2025-11-14 18:24:07

**Metrics:**
- **Throughput:** 4981.794727/s
- **Error Rate:** N/A
- **p95 Latency:** N/A
- **Max VUs:** 500
- **Duration:** N/A
- **Status:** ✅ Pass


## Spike Test - 2025-11-14 18:26:41

**Metrics:**
- **Throughput:** 5022.60976/s
- **Error Rate:** N/A
- **p95 Latency:** N/A
- **Max VUs:** 500
- **Duration:** N/A
- **Status:** ✅ Pass

