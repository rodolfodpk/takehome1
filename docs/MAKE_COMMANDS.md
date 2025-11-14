# Make Commands Reference

Complete reference of all available make commands for the Real-Time API Metering & Aggregation Engine.

**Note:** For basic workflows and common scenarios, see the [Development Guide](DEVELOPMENT.md).

## Quick Reference

```bash
make help  # Show all available commands with descriptions
```

## Application Management

### Starting the Application

| Command | Description |
|---------|-------------|
| `make start` | Start application with full observability stack (PostgreSQL + Redis + Prometheus + Grafana). Runs Spring Boot via Maven. |
| `make start-obs` | Alias for `start` - same as above |
| `make start-k6` | Start application with K6 testing profile and observability stack. Uses `k6` Spring profile (circuit breakers disabled). |
| `make start-k6-obs` | Alias for `start-k6` - same as above |

**Notes:**
- All start commands automatically create the `takehome1` PostgreSQL user if it doesn't exist
- The observability stack (Prometheus + Grafana) is always started for monitoring
- Application runs via `mvn spring-boot:run` (not as Docker container) for faster development

### Stopping the Application

| Command | Description |
|---------|-------------|
| `make stop` | Stop all Docker containers (PostgreSQL, Redis, Prometheus, Grafana) |

**See also:** `make cleanup` for complete cleanup including volumes and port 8080

## Build & Clean

| Command | Description |
|---------|-------------|
| `make build` | Build the application JAR (Maven clean package, skips tests) |
| `make clean` | Clean build artifacts (Maven clean) and remove Docker containers with volumes |
| `make cleanup` | Stop server and remove all Docker containers with volumes. Also frees port 8080. ⚠️ **WARNING:** Deletes all database data |

**See also:** [Troubleshooting Guide](DEVELOPMENT.md#complete-cleanup-stuck-processes-port-conflicts-fresh-start) for when to use cleanup

## Testing

### Unit and Integration Tests

| Command | Description |
|---------|-------------|
| `make test` | Run all tests (unit and integration). Uses Testcontainers - no Docker Compose needed. |

**Note:** Tests use Testcontainers which automatically provides PostgreSQL and Redis. Docker must be running, but no Docker Compose setup is required.

**See also:** [Testing Guide](TESTING.md) for detailed test information

### K6 Performance Testing - Single Instance

All k6 test commands are fully automated - they handle all dependencies (docker-compose, app startup, cleanup) automatically.

| Command | Description |
|---------|-------------|
| `make k6-warmup` | Warm-up test (2 VUs, 10 seconds) - quick validation |
| `make k6-smoke` | Smoke test (10 VUs, 1 minute) |
| `make k6-load` | Load test (350 VUs, 2 minutes, target 2k+ events/sec) |
| `make k6-stress` | Stress test (ramp 50→500 VUs, 3 minutes, find breaking point) |
| `make k6-spike` | Spike test (spike 50→500→50, 2.5 minutes, test circuit breakers) |
| `make k6-test` | Run all K6 tests sequentially (warmup, smoke, load, stress, spike) |

**What each test does automatically:**
1. Starts Prometheus and Grafana (if not already running)
2. Stops and cleans Docker volumes for PostgreSQL and Redis
3. Starts PostgreSQL and Redis with clean data
4. Starts application with k6 profile
5. Cleans database and Redis
6. Runs the test
7. Cleans up application process (keeps Grafana/Prometheus running)

**See also:** [K6 Performance Testing Guide](K6_PERFORMANCE.md) for detailed test information

### K6 Performance Testing - Multi-Instance

These tests run against a multi-instance setup (2 app instances behind nginx load balancer) to validate distributed locks.

**Prerequisites:** Multi-instance stack must be running (`make start-multi`)

| Command | Description |
|---------|-------------|
| `make k6-warmup-multi` | Warm-up test against multi-instance setup (2 app instances) |
| `make k6-smoke-multi` | Smoke test against multi-instance setup (2 app instances) |
| `make k6-load-multi` | Load test against multi-instance setup (2 app instances) - **Tests distributed locks** |
| `make k6-stress-multi` | Stress test against multi-instance setup (2 app instances) |
| `make k6-spike-multi` | Spike test against multi-instance setup (2 app instances). Circuit breakers enabled for validation. |
| `make k6-test-multi` | Run all K6 tests against multi-instance setup (warmup, smoke, load, stress, spike) |

**Notes:**
- All multi-instance tests automatically target `http://localhost:8080` (nginx load balancer)
- Tests detect that the application is already running and skip app startup
- For spike tests with circuit breakers, restart stack with: `SPRING_PROFILES_ACTIVE=k6,k6-spike make start-multi`

**See also:** [Multi-Instance Setup Guide](MULTI_INSTANCE.md) for complete documentation

### K6 Utilities

| Command | Description |
|---------|-------------|
| `make k6-cleanup` | Clean database, Redis, and reset circuit breakers before k6 tests |
| `make k6-setup` | Setup environment for k6 tests (stop, clean volumes, start services, wait for app) |

**Note:** Individual k6 test commands handle setup automatically. These utilities are for manual control if needed.

## Docker

### Docker Image Management

| Command | Description |
|---------|-------------|
| `make docker-build` | Build Docker image (automated - builds JAR then image). Creates `takehome1:latest` |
| `make docker-build-multi` | Build Docker image for multi-instance setup (same as docker-build) |
| `make docker-image-size` | Show Docker image size and details for `takehome1:latest` |
| `make docker-run` | Run Docker container (single instance). Requires `.env` file and built image. |

**Notes:**
- `docker-build` automatically runs `mvn clean package -DskipTests` before building the image
- Image is tagged as `takehome1:latest`
- `docker-run` runs the container with port 8080 exposed

## Multi-Instance Setup

| Command | Description |
|---------|-------------|
| `make start-multi` | Start multi-instance stack (2 app instances + nginx load balancer + full observability). Uses k6 profile by default. Automatically builds Docker image. |
| `make stop-multi` | Stop multi-instance stack |

**What `start-multi` does:**
1. Builds Docker image (if needed)
2. Starts PostgreSQL, Redis, Prometheus, and Grafana
3. Starts 2 app instances (app1 on port 8081, app2 on port 8082)
4. Starts nginx load balancer on port 8080
5. Waits for all services to be healthy (60-90 seconds)

**Service URLs:**
- Application (via nginx LB): http://localhost:8080
- App Instance 1 (direct): http://localhost:8081
- App Instance 2 (direct): http://localhost:8082
- Grafana: http://localhost:3000 (admin/admin)
- Prometheus: http://localhost:9090

**For spike tests with circuit breakers:**
```bash
SPRING_PROFILES_ACTIVE=k6,k6-spike make start-multi
```

**See also:** [Multi-Instance Setup Guide](MULTI_INSTANCE.md) for complete documentation

## Utilities

| Command | Description |
|---------|-------------|
| `make check-ports` | Check all application ports for conflicts (8080, 5432, 6379, 3000, 9090, 8081, 8082) |
| `make verify-urls` | Verify all URLs documented in README.md are working |
| `make help` | Show all available commands with descriptions |

**Notes:**
- `check-ports` runs `scripts/check-ports.sh` to detect port conflicts
- `verify-urls` runs `scripts/verify-urls.sh` to test all documented endpoints
- `help` uses the Makefile's built-in help system

## Related Documentation

- **[Development Guide](DEVELOPMENT.md)** - Basic workflows and common scenarios
- **[K6 Performance Testing](K6_PERFORMANCE.md)** - Detailed k6 testing guide
- **[Multi-Instance Setup](MULTI_INSTANCE.md)** - Complete multi-instance documentation
- **[Testing Guide](TESTING.md)** - Unit and integration test details
- **[Troubleshooting](DEVELOPMENT.md#troubleshooting)** - Common issues and solutions

