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
| `make start` | Start application with full observability stack (PostgreSQL + Redis + Prometheus + Grafana). Runs Spring Boot via Maven. **Automatically kills any running server process.** **Prompts if Docker containers are running** - choose to start fresh (removes volumes) or use existing containers. Automatically frees port 8080 if in use. |
| `make start-obs` | Alias for `start` - same as above |
| `make start-multi` | Start multi-instance stack (2 app instances + nginx load balancer + full observability stack). Uses k6 profile by default for load testing. |
| `make start-multi-and-test` | Start multi-instance stack, then automatically run all k6 tests (warmup, smoke, load, stress, spike). Keeps stack running after tests. |

**Notes:**
- `make start` **automatically kills any running Spring Boot server process** (no prompt needed)
- `make start` will prompt you if Docker containers are already running - you can choose to start fresh (removes all volumes) or continue with existing containers
- `make start-multi-and-test` is a convenience command that starts the multi-instance stack and runs all k6 tests in one go
- All start commands automatically free port 8080 if it's in use (kills Spring Boot processes first, then any remaining processes)
- All start commands automatically create the `takehome1` PostgreSQL user if it doesn't exist
- The observability stack (Prometheus + Grafana) is always started for monitoring
- Single-instance application runs via `mvn spring-boot:run` (not as Docker container) for faster development
- Multi-instance setup runs applications in Docker containers

### Stopping the Application

| Command | Description |
|---------|-------------|
| `make stop` | Stop Spring Boot application and all Docker containers (stops everything started by `make start`) |
| `make stop-multi` | Stop multi-instance stack (stops all containers including app instances, nginx, PostgreSQL, Redis, Prometheus, Grafana) |

**See also:** `make cleanup` for complete cleanup including volumes and port 8080

## Build & Clean

| Command | Description |
|---------|-------------|
| `make build` | Build the application JAR (Maven clean package, skips tests) |
| `make clean` | Clean build artifacts (Maven clean) and remove Docker containers with volumes |
| `make cleanup` | Stop server and remove all Docker containers with volumes. Also frees port 8080. ⚠️ **WARNING:** Deletes all database data |

**See also:** [Troubleshooting Guide](DEVELOPMENT.md#complete-cleanup-stuck-processes-port-conflicts-fresh-start) for when to use cleanup

## Database Management

| Command | Description |
|---------|-------------|
| `make flyway-repair` | Repair Flyway schema history (fixes checksum mismatches after migration changes). Use when you see "Migration checksum mismatch" errors. |

**When to use:**
- After modifying existing migration files
- When adding new migrations between existing ones (e.g., adding V2 between V1 and V3)
- When Flyway reports checksum mismatches on startup

**Alternative:** When running `make start`, choose 'y' when prompted to start clean - this will remove all volumes and start with a fresh database.

## Testing

### Unit and Integration Tests

| Command | Description |
|---------|-------------|
| `make test` | Run all tests (unit and integration). Uses Testcontainers - no Docker Compose needed. |

**Note:** Tests use Testcontainers which automatically provides PostgreSQL and Redis. Docker must be running, but no Docker Compose setup is required.

**See also:** [Testing Guide](TESTING.md) for detailed test information

### K6 Performance Testing - Multi-Instance Only

All k6 tests run against multi-instance setup (2 app instances + nginx load balancer) to test distributed locks.

**Prerequisites:** Multi-instance stack must be running (`make start-multi`)

| Command | Description |
|---------|-------------|
| `make start-multi-and-test` | **Recommended:** Start multi-instance stack and run all tests automatically. |
| `make k6-test` | Run all k6 tests sequentially (warmup, smoke, load, stress, spike). Requires multi-instance stack to be running. |
| `make k6-warmup` | Warm-up test (2 VUs, 10 seconds) - quick validation. Requires multi-instance stack. |
| `make k6-smoke` | Smoke test (10 VUs, 1 minute). Requires multi-instance stack. |
| `make k6-load` | Load test (350 VUs, 2 minutes, target 2k+ events/sec). Tests distributed locks. Requires multi-instance stack. |
| `make k6-stress` | Stress test (ramp 50→500 VUs, 3 minutes, find breaking point). Requires multi-instance stack. |
| `make k6-spike` | Spike test (spike 50→500→50, 2.5 minutes, test circuit breakers). Requires multi-instance stack. |

**Workflow:**
1. Start multi-instance stack: `make start-multi`
2. Run tests: `make k6-test` or individual tests (`make k6-warmup`, etc.)
3. Stop stack when done: `make stop-multi`

**What each test does:**
1. Verifies multi-instance stack is running
2. Ensures Prometheus and Grafana are running
3. Cleans database and Redis
4. Runs the test against nginx load balancer (http://localhost:8080)
5. Updates test results document

**See also:** [K6 Performance Testing Guide](K6_PERFORMANCE.md) and [Multi-Instance Setup Guide](MULTI_INSTANCE.md) for detailed information

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

## Make Commands Testing

| Command | Description |
|---------|-------------|
| `make test-make-commands-smoke` | Run lightweight smoke test for make commands (fast, ~10-30s, for CI). Validates Makefile syntax and command existence without execution. |
| `make test-make-commands-full` | Run full integration test for make commands (slow, ~5-10min, for local use). Tests actual execution of commands including build, Docker, multi-instance, and k6 tests. |
| `make test-make-commands` | Alias for `test-make-commands-smoke` - same as smoke test |

**When to use:**
- **Smoke test (CI)**: Runs automatically in CI on every PR. Validates syntax and command existence quickly.
- **Full test (Local)**: Run locally before committing changes to make commands. Tests actual execution and catches runtime issues.

**What each test validates:**
- **Smoke test**: Makefile syntax, all documented commands exist, help output is correct
- **Full test**: Build works, Docker build works, multi-instance start/stop works, k6 commands work, stop commands work

**See also:** These tests are automatically run in CI (smoke test only). Full test should be run locally before committing Makefile changes.

## Related Documentation

- **[Development Guide](DEVELOPMENT.md)** - Basic workflows and common scenarios
- **[K6 Performance Testing](K6_PERFORMANCE.md)** - Detailed k6 testing guide
- **[Multi-Instance Setup](MULTI_INSTANCE.md)** - Complete multi-instance documentation
- **[Testing Guide](TESTING.md)** - Unit and integration test details
- **[Troubleshooting](DEVELOPMENT.md#troubleshooting)** - Common issues and solutions

