test# Real-Time API Metering & Aggregation Engine

![CI](https://github.com/rodolfodpk/takehome1/workflows/CI/badge.svg)
![codecov](https://codecov.io/gh/rodolfodpk/takehome1/branch/main/graph/badge.svg)

Production-ready API metering and aggregation engine that processes high-volume events, maintains distributed state in Redis, and produces accurate aggregations across multiple service instances.

## Technology Stack

- **Framework**: Spring Boot 3.5.7 (Kotlin 2.2.21, Java 21 LTS)
- **Reactive Stack**: Spring WebFlux, Project Reactor, R2DBC
- **Database**: PostgreSQL 17.2 (accessed via R2DBC)
- **Cache/Distributed State**: Redis 7 (via Redisson)
- **Resilience**: Resilience4j (Circuit Breaker, Retry, Timeout)
- **Observability**: Prometheus, Grafana, Spring Boot Actuator
- **Testing**: Kotest (BDD style), Testcontainers (PostgreSQL + Redis), K6

## Quick Start for Developers

Three essential workflows to get started:

### 1. Running Tests
**No Docker Compose needed** - Tests use Testcontainers which automatically provides PostgreSQL and Redis.

```bash
make test
```

This runs all unit and integration tests (~10-15 seconds). Docker must be running, but no Docker Compose setup is required.

### 2. Running the Application
**Requires Docker Compose** - Starts PostgreSQL, Redis, Prometheus, and Grafana, then runs the Spring Boot application.

```bash
make start
```

This automatically:
- Starts PostgreSQL, Redis, Prometheus, and Grafana via Docker Compose
- Waits for services to be ready
- Runs the Spring Boot application

Access:
- **Grafana**: http://localhost:3000 (admin/admin)
- **Prometheus**: http://localhost:9090
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health

### 3. Running All K6 Performance Tests
**Requires Multi-Instance Setup** - All k6 tests run against a multi-instance configuration (2 app instances + nginx load balancer) to test distributed locks.

**Recommended approach:**
```bash
# Start multi-instance stack and run all tests automatically
make start-multi-and-test
```
- Starts multi-instance stack (2 app instances + nginx + PostgreSQL + Redis + Prometheus + Grafana)
- Runs all k6 tests sequentially (warmup, smoke, load, stress, spike)
- Keeps stack running after tests for monitoring
- Run `make stop-multi` to stop the stack

**Alternative approach:**
```bash
# Start multi-instance stack manually, then run tests
make start-multi
make k6-test    # Run all tests
# or run individual tests:
make k6-warmup  # Quick validation (10 seconds)
make k6-smoke   # Basic functionality (1 minute)
make k6-load    # Production load (2 minutes) - tests distributed locks
make k6-stress  # Find breaking point (3 minutes)
make k6-spike   # Test circuit breakers (2.5 minutes)
```
- All k6 tests require multi-instance stack to be running first
- Tests verify distributed locks work correctly across 2 instances
- Run `make stop-multi` to stop the stack when done

**Monitoring:** During k6 tests, you can monitor metrics in Grafana at http://localhost:3000 (admin/admin)

For detailed information, see the [Development Guide](docs/DEVELOPMENT.md).

## Quick Start

### Prerequisites

- **For Development**: Java 21, Maven 3.9+, Docker & Docker Compose
- **For Testing Only**: Java 21, Maven 3.9+ (Testcontainers provides PostgreSQL and Redis automatically)

### Running the Application

```bash
# Start application with full observability stack (PostgreSQL + Redis + Prometheus + Grafana)
# Prompts if containers are running: choose to start clean or use existing containers
# Automatically frees port 8080 if needed
make start

# Start multi-instance setup (2 app instances + nginx load balancer)
make start-multi

# Stop application (stops Spring Boot process and Docker containers)
make stop

# Stop multi-instance stack
make stop-multi
```

**Note:** 
- `make start` automatically kills any running server process (no prompt needed)
- `make start` will prompt you if Docker containers are already running - choose to start fresh (removes volumes) or use existing containers
- `make stop` stops both the Spring Boot application and all Docker containers
- Automatically frees port 8080 if it's in use
- The observability stack (Prometheus + Grafana) is always started with the application for monitoring

**Multi-Instance Setup:** 
- Use `make start-multi` to start 2 application instances behind an nginx load balancer
- All k6 tests require multi-instance setup - run `make k6-test` or individual tests after `make start-multi`
- Or use `make start-multi-and-test` to start and run all tests in one command
- Run `make stop-multi` to stop the multi-instance stack
- See [Multi-Instance Setup Guide](docs/MULTI_INSTANCE.md) for details

### Running Tests

```bash
# Run all tests (uses Testcontainers, no Docker Compose needed)
make test
```

### Available URLs

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs
- **Health Check**: http://localhost:8080/actuator/health
- **Prometheus Metrics**: http://localhost:8080/actuator/prometheus
- **Grafana**: http://localhost:3000 (admin/admin)
- **Prometheus UI**: http://localhost:9090

## Key Features

- ✅ **Reactive Programming**: Non-blocking architecture with WebFlux/R2DBC
- ✅ **High Throughput**: Handles 2,000+ events/second per instance (tested up to 3,700+ events/second)
- ✅ **Distributed State**: Redis-based real-time aggregation with Redisson
- ✅ **Multi-Tenancy**: Request body tenant isolation with explicit validation
- ✅ **Batch Aggregation**: 30-second window processing
- ✅ **Resilience Patterns**: Circuit breakers, retries, timeouts
- ✅ **Observability**: Structured logging, metrics, dashboards
- ✅ **API Documentation**: Auto-generated Swagger UI

## Documentation

Comprehensive documentation is available in the `docs/` directory:

### Getting Started
- **[Development Guide](docs/DEVELOPMENT.md)** - Complete development guide with all make commands and workflows
- **[Testing Guide](docs/TESTING.md)** - Test strategy, coverage, and running tests with Testcontainers

### Architecture & Design
- **[Architecture](docs/ARCHITECTURE.md)** - System architecture, package structure, and design patterns
- **[Resilience](docs/RESILIENCE.md)** - Circuit Breaker, Retry, and Timeout patterns with Resilience4j

### Operations
- **[Observability](docs/OBSERVABILITY.md)** - Monitoring setup, metrics, and Prometheus configuration
- **[Dashboards](docs/DASHBOARDS.md)** - Grafana dashboard documentation and usage

### Performance
- **[K6 Performance Testing](docs/K6_PERFORMANCE.md)** - Comprehensive k6 testing guide with scenarios, thresholds, and test results summary
- **[Multi-Instance Setup](docs/MULTI_INSTANCE.md)** - Multi-instance setup and distributed lock testing guide

## Architecture

See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for detailed architecture documentation.

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.

