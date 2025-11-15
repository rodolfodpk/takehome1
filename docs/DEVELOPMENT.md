# Development Guide

Complete development guide for the Real-Time API Metering & Aggregation Engine including all make commands and workflows.

## Quick Start for Developers

Three essential workflows to get started:

### 1. Running Tests
**No Docker Compose needed** - Tests use Testcontainers which automatically provides PostgreSQL and Redis.

```bash
make test
```

- Runs all unit and integration tests (~10-15 seconds)
- Docker must be running, but no Docker Compose setup is required
- Testcontainers automatically provides PostgreSQL and Redis containers
- See [Testing Guide](TESTING.md) for details

### 2. Running the Application
**Requires Docker Compose** - Starts PostgreSQL, Redis, Prometheus, and Grafana, then runs the Spring Boot application.

```bash
make start
```

- Automatically starts PostgreSQL, Redis, Prometheus, and Grafana via Docker Compose
- Waits for services to be ready
- Runs the Spring Boot application
- Access:
  - **Swagger UI**: http://localhost:8080/swagger-ui.html
  - **Health Check**: http://localhost:8080/actuator/health
  - **Grafana**: http://localhost:3000 (admin/admin)
  - **Prometheus**: http://localhost:9090

**Note:** The observability stack is always started with the application for monitoring.

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

For detailed k6 testing information, see [K6 Performance Testing](K6_PERFORMANCE.md).

## Make Commands

For a complete reference of all make commands, see [Make Commands Reference](MAKE_COMMANDS.md).

This section covers the most commonly used commands for basic workflows:

### Application Management

#### Starting the Application

```bash
make start          # Start application with full observability stack (PostgreSQL + Redis + Prometheus + Grafana)
make start-obs      # Alias for 'start' - same as above
make start-multi    # Start multi-instance stack (2 app instances + nginx load balancer)
make start-multi-and-test  # Start multi-instance stack and run all k6 tests
```

**Note:** The observability stack (Prometheus + Grafana) is always started with the application for monitoring. This allows you to monitor metrics, dashboards, and system health in real-time.

#### Managing the Application

```bash
make stop           # Stop Spring Boot application and all Docker containers (stops everything started by make start)
make stop-multi     # Stop multi-instance stack (stops all containers)
```

### Testing

#### Make Commands Testing

Validate that make commands work correctly:

```bash
# Fast smoke test (runs in CI, ~10-30 seconds)
make test-make-commands-smoke

# Full integration test (local use, ~5-10 minutes)
make test-make-commands-full
```

**Note:** The smoke test runs automatically in CI on every PR. The full test should be run locally before committing Makefile changes.

#### Unit and Integration Tests

```bash
make test           # Run all tests (10 test files, ~10-15 seconds)
```

**Note:** Tests use Testcontainers, so Docker must be running. PostgreSQL and Redis are automatically provided by Testcontainers - no Docker Compose needed.

#### K6 Performance Testing

All k6 tests run against multi-instance setup (2 app instances + nginx load balancer) to test distributed locks.

**Prerequisites:** Multi-instance stack must be running (`make start-multi`)

**Recommended approach:**
```bash
# Start multi-instance stack and run all tests automatically
make start-multi-and-test
```

**Alternative approach:**
```bash
# Start multi-instance stack manually, then run tests
make start-multi

# Individual tests (each requires multi-instance stack to be running)
make k6-warmup  # Warm-up test (2 VUs, 10 seconds) - quick validation
make k6-smoke   # Smoke test (10 VUs, 1 minute)
make k6-load    # Load test (350 VUs, 2 minutes) - **Tests distributed locks**
make k6-stress  # Stress test (ramp 50→500 VUs, 3 minutes)
make k6-spike   # Spike test (spike 50→500→50, 2.5 minutes)

# Run all tests sequentially
make k6-test    # Run all K6 tests (warmup, smoke, load, stress, spike)
```

**What each test does:**
1. Verifies multi-instance stack is running
2. Ensures Prometheus and Grafana are running
3. Cleans database and Redis
4. Runs the test against nginx load balancer (http://localhost:8080)
5. Updates test results document

**See [Multi-Instance Setup Guide](MULTI_INSTANCE.md) for complete documentation.**

### Observability

The observability stack (Prometheus + Grafana) is automatically started with the application:

```bash
# Start application (includes observability stack)
make start          # Starts PostgreSQL, Redis, Prometheus, and Grafana

# Access dashboards
make grafana        # Open Grafana dashboard (http://localhost:3000, admin/admin)
make prometheus     # Open Prometheus UI (http://localhost:9090)
make metrics        # View application metrics
```

**Note:** When running k6 tests, the observability stack is automatically started if not already running, allowing you to monitor test performance in real-time.

### Development Workflow

```bash
# Setup
make setup          # Initial setup (clean + build + start)

# Building
make build          # Build the application
make clean          # Clean build artifacts and containers

# Development cycle
make dev-cycle      # Complete dev cycle (clean + build + start)
```

### Database Management

```bash
# Database operations
make db-reset       # Reset database (stop, remove volumes, restart)
make db-clean-quick # Quick database cleanup (TRUNCATE tables)
```

### Help

```bash
make help          # Show all available commands
```

## Development Workflow

### 1. Initial Setup

```bash
# Clone the repository
git clone https://github.com/rodolfodpk/takehome1.git
cd takehome1

# Run tests to verify everything works
make test

# Start the application
make start
```

### 2. Running Tests

#### Unit and Integration Tests

All tests use Testcontainers for PostgreSQL. No Docker Compose needed:

```bash
# Run all tests
make test

# Output: 10 test files, ~10-15 seconds
```

Test Categories:
- Domain Tests (3 files: UsageEvent, Tenant, Customer)
- Service Unit Tests (1 file: AggregationService)
- Repository Integration Tests (1 file: UsageEventRepository)
- Service Integration Tests (1 file: EventProcessingService)
- E2E Tests (1 file: EventIngestion)
- JSONB Tests (1 file: JsonbMap)
- Additional integration tests (2 files)

#### K6 Performance Tests

K6 tests are fully automated - no manual setup required:

```bash
# Recommended: Start multi-instance stack and run all tests in one command
make start-multi-and-test  # Starts multi-instance stack, runs all tests, keeps stack running

# Or run tests individually (requires multi-instance stack to be running)
make k6-warmup    # Quick validation (10 seconds)
make k6-smoke     # Basic functionality (1 minute)
make k6-load      # Production load (2 minutes) - tests distributed locks
make k6-stress    # Find breaking point (3 minutes)
make k6-spike     # Test circuit breakers (2.5 minutes)

# Or run all tests sequentially (requires multi-instance stack to be running)
make k6-test      # Runs warmup, smoke, load, stress, spike
```

**Prerequisites:** Multi-instance stack must be running (`make start-multi`)

Each test:
- Verifies multi-instance stack is running
- Ensures Prometheus and Grafana are running
- Cleans database and Redis
- Runs the test against nginx load balancer (http://localhost:8080)
- Updates test results document

### 3. Development Cycle

```bash
# Clean, build, and start
make dev-cycle

# Make changes to code...

# Run tests
make test

# Check logs
make logs

# Stop when done
make stop
```

### 4. Observability

The observability stack is automatically started with the application:

```bash
make start

# Access Grafana
make grafana
# Login: admin / admin
# URL: http://localhost:3000

# Access Prometheus
make prometheus
# URL: http://localhost:9090

# View metrics
make metrics
# URL: http://localhost:8080/actuator/metrics
```

## K6 Performance Testing

### Prerequisites

1. Application must be running (use `make start-k6`)
2. K6 installed: `brew install k6` or `curl https://github.com/grafana/k6/releases/latest/download/k6 -L -o /usr/local/bin/k6`

### Running Tests

#### Quick Validation (Smoke Test)
```bash
make k6-smoke
# 5 VUs, 1 minute duration
# Validates basic functionality
```

#### Normal Load
```bash
make k6-load
# 50 VUs, 9 minutes duration
# Simulates expected production load
```

#### Find Breaking Point (Stress Test)
```bash
make k6-stress
# Ramps from 10 to 300 VUs over 20 minutes
# Identifies system limits
```

#### Sudden Traffic Spike
```bash
make k6-spike
# Spikes from 10→200→10 VUs over 5 minutes
# Tests circuit breakers
```

#### Automated Full Workflow
```bash
make k6-test-automated
# Automated workflow:
# 1. Stop existing processes
# 2. Reset database
# 3. Start application
# 4. Run smoke test
# 5. Run load test
# 6. Cleanup
```

### K6 Test Results

K6 outputs results to terminal including:
- Request duration (min/avg/max)
- Requests per second
- HTTP status codes
- Data transferred
- Iteration duration
- Error rate

Example output:
```
http_req_duration........: avg=45ms  min=12ms  med=38ms  max=234ms
http_reqs................: 15000 500/s
http_req_failed..........: 0%   0/15000
```

## Environment Variables

### Application Profiles

- `default` - Production profile (standard settings)
- `k6` - K6 testing profile (circuit breakers and time limiters DISABLED for pure performance metrics)
  - Circuit breakers DISABLED (not configured - provides pure performance metrics)
  - Time limiters DISABLED (not configured - avoids artificial timeouts)
  - Extended timeouts (10s connection/statement timeouts)
  - Reduced retries (1 attempt - effectively disabled)
  - Reduced logging verbosity (WARN level)
- `k6-spike` - K6 spike test profile (extends k6, enables circuit breakers for spike test validation)
  - Circuit breakers ENABLED with relaxed thresholds (90% failure rate)
  - Use with: `SPRING_PROFILES_ACTIVE=k6,k6-spike`

### Spring Profiles

Set via environment variable or command line:

```bash
# Using environment variable
export SPRING_PROFILES_ACTIVE=k6
make start

# Using command line
mvn spring-boot:run -Dspring-boot.run.profiles=k6
```

## Docker Commands

### Docker Image Management

```bash
# Build Docker image (automated - builds JAR then image)
make docker-build

# Build for multi-instance (same as docker-build)
make docker-build-multi
```

### Single Instance Setup

```bash
# Start PostgreSQL only
docker-compose up -d postgres

# Stop PostgreSQL
docker-compose stop postgres

# View PostgreSQL logs
docker-compose logs postgres

# Access PostgreSQL
docker exec -it takehome1-postgres psql -U takehome1 -d takehome1
```

### Multi-Instance Setup

```bash
# Start multi-instance stack (2 app instances + nginx + observability)
make start-multi

# Stop multi-instance stack
make stop-multi

# View logs
docker-compose -f docker-compose.yml -f docker-compose.multi.yml logs -f

# View specific service logs
docker-compose -f docker-compose.yml -f docker-compose.multi.yml logs app1
docker-compose -f docker-compose.yml -f docker-compose.multi.yml logs app2
docker-compose -f docker-compose.yml -f docker-compose.multi.yml logs nginx
```

**See [Multi-Instance Setup Guide](MULTI_INSTANCE.md) for complete documentation.**

### Full Observability Stack

```bash
# Start all services (PostgreSQL, Redis, Prometheus, Grafana)
docker-compose up -d

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v

# View all logs
docker-compose logs -f
```

## Troubleshooting

### Tests Failing

**Problem:** Tests fail with PostgreSQL connection errors

**Solution:**
```bash
# Ensure Docker is running
docker ps

# Restart Testcontainers
docker system prune -f

# Run tests again
make test
```

### Application Won't Start

**Problem:** Port 8080 already in use

**Solution:**
```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>

# Or stop all services
make stop

# For complete cleanup (removes volumes, frees port, stops everything)
make cleanup
```

### Complete Cleanup (Stuck Processes, Port Conflicts, Fresh Start)

**Problem:** Application won't start, port conflicts, stuck processes, or need a completely fresh start

**Solution:** Use the cleanup script to stop everything and remove all Docker volumes:
```bash
# Complete cleanup: stops app, removes all Docker containers and volumes, frees port 8080
make cleanup
```

**What it does:**
- Stops any running Spring Boot application processes
- Stops all Docker containers and removes volumes (⚠️ **WARNING:** This deletes all database data)
- Frees port 8080 by killing any processes using it

**When to use:**
- Port 8080 is stuck/in use and `make stop` doesn't work
- Docker containers are in a bad state
- Need a completely fresh start with clean database
- Application processes won't stop normally
- After switching between single-instance and multi-instance setups

**When NOT to use:**
- If you want to preserve database data (use `make stop` instead)
- If you're actively debugging and need to preserve state

**After cleanup:**
```bash
# Start application (will prompt if containers are running - choose to start clean or use existing)
make start
```

### K6 Tests Not Running

**Problem:** K6 tests fail with connection refused

**Solution:**
```bash
# Ensure application is running
make start-k6

# Wait for application to be ready
sleep 30

# Run K6 test
make k6-smoke
```

## CI/CD

### GitHub Actions Workflow

The project uses GitHub Actions for continuous integration:

- **Workflow File**: `.github/workflows/ci.yml`
- **Triggers**: Push and pull requests to `main` branch
- **Java Version**: 21 (Temurin distribution)
- **Services**: Docker-in-Docker for Testcontainers
- **Steps**:
  1. Checkout code
  2. Set up JDK 21 with Maven cache
  3. Run tests with coverage (`mvn clean test jacoco:report`)
  4. Upload coverage to Codecov

### Code Coverage

- **Tool**: JaCoCo (configured in `pom.xml`)
- **Report Location**: `target/site/jacoco/index.html`
- **Online**: Coverage reports are automatically uploaded to Codecov
- **Badges**: CI and Codecov badges are displayed in README.md

### Codecov Configuration

- **Config File**: `codecov.yml`
- **Coverage Target**: 80% (project and patch)
- **Threshold**: 1% (allows small decreases)

### Viewing Coverage

```bash
# Generate local coverage report
mvn jacoco:report

# View report
open target/site/jacoco/index.html
```

### CI Badges

The README displays two badges:
- **CI Badge**: Shows build status (passing/failing)
- **Codecov Badge**: Shows code coverage percentage

Badges update automatically after each CI run.

### Manual Trigger

```bash
# Push to trigger CI
git push origin main
```

## Available URLs

When application is running:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs
- **Health Check**: http://localhost:8080/actuator/health
- **Prometheus Metrics**: http://localhost:8080/actuator/prometheus
- **All Metrics**: http://localhost:8080/actuator/metrics
- **Grafana**: http://localhost:3000 (admin/admin)
- **Prometheus UI**: http://localhost:9090

## Maven Commands

If you prefer Maven directly:

```bash
# Run tests
mvn test

# Build
mvn clean package

# Run application
mvn spring-boot:run

# Run with profile
mvn spring-boot:run -Dspring-boot.run.profiles=k6
```

## Performance Optimization

### For Fast Test Execution

```bash
# Use existing PostgreSQL container (faster)
docker-compose up -d postgres

# Reuse test containers
export TESTCONTAINERS_REUSE_ENABLE=true

# Run tests
make test
```

### For Faster Builds

```bash
# Skip tests during build
mvn clean package -DskipTests

# Use offline mode (if dependencies cached)
mvn clean package -o
```

## Related Documentation

- **[README](../README.md)** - Project overview and quick start
- **[Architecture Documentation](ARCHITECTURE.md)** - Package structure and dependency rules
- **[Testing Guide](TESTING.md)** - Test strategy and coverage details
- **[Resilience Documentation](RESILIENCE.md)** - Circuit Breaker, Retry, and Timeout patterns
- **[K6 Performance Testing](K6_PERFORMANCE.md)** - Detailed K6 testing guide
- **[Multi-Instance Setup](MULTI_INSTANCE.md)** - Multi-instance setup and distributed lock testing
- **[Observability Documentation](OBSERVABILITY.md)** - Monitoring setup and dashboards

