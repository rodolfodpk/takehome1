# Real-Time API Metering & Aggregation Engine

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

## Quick Start

### Prerequisites

- **For Development**: Java 21, Maven 3.9+, Docker & Docker Compose
- **For Testing Only**: Java 21, Maven 3.9+ (Testcontainers provides PostgreSQL and Redis automatically)

### Running the Application

```bash
# Start application with PostgreSQL and Redis
docker-compose up -d postgres redis
make start

# Start application with observability stack (Prometheus + Grafana)
make start-obs

# Stop application
make stop
```

### Running Tests

```bash
# Run all tests (uses Testcontainers, no Docker Compose needed)
make test
```

### Available URLs

- **Application**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs
- **Health Check**: http://localhost:8080/actuator/health
- **Prometheus Metrics**: http://localhost:8080/actuator/prometheus
- **Grafana**: http://localhost:3000 (admin/admin)
- **Prometheus UI**: http://localhost:9090

## Key Features

- ✅ **Reactive Programming**: Non-blocking architecture with WebFlux/R2DBC
- ✅ **High Throughput**: Handles 10,000+ events/second per instance
- ✅ **Distributed State**: Redis-based real-time aggregation with Redisson
- ✅ **Multi-Tenancy**: Header-based tenant isolation with explicit validation (X-Tenant-Id)
- ✅ **Batch Aggregation**: 30-second window processing
- ✅ **Resilience Patterns**: Circuit breakers, retries, timeouts
- ✅ **Observability**: Structured logging, metrics, dashboards
- ✅ **API Documentation**: Auto-generated Swagger UI

## Architecture

See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for detailed architecture documentation.

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.

