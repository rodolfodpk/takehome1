# k6 Test Results

This file contains concise results from k6 performance tests. Results are automatically updated after each test run.

**⚠️ Important:** All test results in this file are from **multi-instance setup** (2 app instances behind nginx load balancer) testing distributed locks across instances. This setup validates that distributed locks work correctly and prevents duplicate processing across multiple service instances.

**Related Documentation:**
- **[README](../README.md)** - Project overview and quick start
- **[K6 Performance Testing](K6_PERFORMANCE.md)** - Comprehensive k6 testing guide with results summary
- **[Multi-Instance Setup](MULTI_INSTANCE.md)** - Guide for multi-instance testing
- **[Development Guide](DEVELOPMENT.md)** - Complete development guide with all make commands
