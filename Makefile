.PHONY: help build test start start-obs stop clean

help: ## Show this help message
	@echo 'Usage: make [target]'
	@echo ''
	@echo 'Available targets:'
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  %-15s %s\n", $$1, $$2}' $(MAKEFILE_LIST)

build: ## Build the application
	mvn clean package -DskipTests

test: ## Run all tests
	mvn test

start: ## Start application with Docker Compose (Postgres + Redis)
	docker-compose up -d postgres redis
	@echo "Waiting for services to be ready..."
	@sleep 5
	mvn spring-boot:run

start-obs: ## Start application with full observability stack
	docker-compose up -d
	@echo "Waiting for services to be ready..."
	@sleep 10
	mvn spring-boot:run

start-k6: ## Start application with K6 testing profile
	docker-compose up -d postgres redis
	@echo "Waiting for services to be ready..."
	@sleep 5
	SPRING_PROFILES_ACTIVE=k6 mvn spring-boot:run

start-k6-obs: ## Start with observability stack + K6 profile (for monitoring K6 tests)
	docker-compose down -v
	docker-compose up -d
	@echo "Waiting for services to be ready..."
	@sleep 10
	SPRING_PROFILES_ACTIVE=k6 mvn spring-boot:run

stop: ## Stop all Docker containers
	docker-compose down

clean: ## Clean build artifacts and Docker volumes
	mvn clean
	docker-compose down -v

docker-build: ## Build Docker image
	docker build -t takehome1:latest .

docker-run: ## Run Docker container
	docker run -p 8080:8080 --env-file .env takehome1:latest

k6-cleanup: ## Clean database, Redis, and reset circuit breakers before k6 tests
	@./scripts/k6-cleanup.sh

k6-setup: ## Setup environment for k6 tests (stop, clean volumes, start services, wait for app)
	@echo "🔧 Setting up k6 test environment..."
	@echo "  - Stopping and cleaning volumes..."
	@docker-compose down -v > /dev/null 2>&1 || true
	@echo "  - Starting PostgreSQL and Redis..."
	@docker-compose up -d postgres redis
	@echo "  - Waiting for services to be ready..."
	@sleep 5
	@echo "  - Checking PostgreSQL health..."
	@for i in 1 2 3 4 5; do \
		if docker-compose exec -T postgres pg_isready -U takehome1 > /dev/null 2>&1; then \
			break; \
		fi; \
		sleep 1; \
	done
	@echo "  - Checking Redis health..."
	@for i in 1 2 3 4 5; do \
		if docker-compose exec -T redis redis-cli -a takehome1 ping > /dev/null 2>&1; then \
			break; \
		fi; \
		sleep 1; \
	done
	@echo "✅ Services are ready!"

k6-warmup: ## Run K6 warm-up test (quick validation - 10 seconds)
	@echo "🔥 Running k6 warm-up test..."
	@./scripts/k6-run-test.sh k6/scripts/warmup-test.js

k6-smoke: ## Run K6 smoke test (10 VUs, 1 minute)
	@echo "💨 Running k6 smoke test..."
	@./scripts/k6-run-test.sh k6/scripts/smoke-test.js

k6-load: ## Run K6 load test (250 VUs, 2 minutes, target 2k+ events/sec)
	@echo "📊 Running k6 load test..."
	@./scripts/k6-run-test.sh k6/scripts/load-test.js

k6-stress: ## Run K6 stress test (ramp 50→500 VUs, 3 minutes, find breaking point)
	@echo "💪 Running k6 stress test..."
	@./scripts/k6-run-test.sh k6/scripts/stress-test.js

k6-spike: ## Run K6 spike test (spike 50→500→50, 2.5 minutes, test circuit breakers)
	@echo "⚡ Running k6 spike test..."
	@./scripts/k6-run-test.sh k6/scripts/spike-test.js

k6-test: ## Run all K6 tests sequentially (warmup, smoke, load, stress, spike)
	@echo "🧪 Running all k6 tests..."
	@make k6-warmup
	@echo ""
	@make k6-smoke
	@echo ""
	@make k6-load
	@echo ""
	@make k6-stress
	@echo ""
	@make k6-spike
	@echo "✅ All k6 tests completed!"
