# ⚠️ DEPRECATED

This file is no longer updated. Test results are now in the summary table in [K6_PERFORMANCE.md](K6_PERFORMANCE.md).

See the "Test Results Summary" section in K6_PERFORMANCE.md for the latest results.

---

# k6 Test Results (Historical)

This file previously contained results from k6 performance tests. Results are now automatically updated in the summary table in K6_PERFORMANCE.md.

**⚠️ Important:** All test results are from **multi-instance setup** (2 app instances behind nginx load balancer) testing distributed locks across instances.

**Related Documentation:**
- **[README](../README.md)** - Project overview and quick start
- **[K6 Performance Testing](K6_PERFORMANCE.md)** - Comprehensive k6 testing guide with results summary
- **[Multi-Instance Setup](MULTI_INSTANCE.md)** - Guide for multi-instance testing
- **[Development Guide](DEVELOPMENT.md)** - Complete development guide with all make commands

## Warm-up Test - 2025-11-15 13:02:38

**Metrics:**
- **Throughput:** 2001.711038/s
- **Error Rate:** N/A
- **p95 Latency:** N/A
- **Max VUs:** 2
- **Duration:** N/A
- **Status:** ✅ Pass


## Smoke Test - 2025-11-15 13:03:51

**Metrics:**
- **Throughput:** 5331.446678/s
- **Error Rate:** N/A
- **p95 Latency:** N/A
- **Max VUs:** 10
- **Duration:** N/A
- **Status:** ✅ Pass


## Load Test - 2025-11-15 13:05:55

**Metrics:**
- **Throughput:** N/A
- **Error Rate:** N/A
- **p95 Latency:** N/A
- **Max VUs:** 350
- **Duration:** N/A
- **Status:** ✅ Pass


## Stress Test - 2025-11-15 13:09:00

**Metrics:**
- **Throughput:** 13362.164105/s
- **Error Rate:** N/A
- **p95 Latency:** N/A
- **Max VUs:** 500
- **Duration:** N/A
- **Status:** ✅ Pass


## Spike Test - 2025-11-15 13:11:35

**Metrics:**
- **Throughput:** 13921.571243/s
- **Error Rate:** N/A
- **p95 Latency:** N/A
- **Max VUs:** 500
- **Duration:** N/A
- **Status:** ✅ Pass

