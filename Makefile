.PHONY: help build test start start-obs stop clean docker-build docker-build-multi start-multi stop-multi

help: ## Show this help message
	@echo 'Usage: make [target]'
	@echo ''
	@echo 'Available targets:'
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  %-15s %s\n", $$1, $$2}' $(MAKEFILE_LIST)

build: ## Build the application
	mvn clean package -DskipTests

test: ## Run all tests
	mvn test

start: ## Start application with full observability stack (Postgres + Redis + Prometheus + Grafana)
	docker-compose up -d
	@echo "Waiting for services to be ready..."
	@sleep 10
	mvn spring-boot:run

start-obs: ## Alias for 'start' - Start application with full observability stack
	@make start

start-k6: ## Start application with K6 testing profile and observability stack
	docker-compose up -d
	@echo "Waiting for services to be ready..."
	@sleep 10
	SPRING_PROFILES_ACTIVE=k6 mvn spring-boot:run

start-k6-obs: ## Alias for 'start-k6' - Start with observability stack + K6 profile
	@make start-k6

stop: ## Stop all Docker containers
	docker-compose down

clean: ## Clean build artifacts and Docker volumes
	mvn clean
	docker-compose down -v

docker-build: ## Build Docker image (automated - builds from source)
	@echo "🔨 Building Docker image..."
	@echo "  - Building application JAR..."
	@mvn clean package -DskipTests -q
	@echo "  - Building Docker image..."
	@docker build -t takehome1:latest .
	@echo "✅ Docker image built: takehome1:latest"
	@docker images takehome1:latest --format "  Image size: {{.Size}}"

docker-build-multi: ## Build Docker image for multi-instance setup (same as docker-build)
	@make docker-build

docker-image-size: ## Show Docker image size and details
	@echo "📦 Docker Image Information:"
	@echo ""
	@if docker images takehome1:latest --format "{{.Repository}}" 2>/dev/null | grep -q "takehome1"; then \
		echo "Image: takehome1:latest"; \
		docker images takehome1:latest --format "  Size: {{.Size}}"; \
		docker images takehome1:latest --format "  Created: {{.CreatedAt}}"; \
		docker images takehome1:latest --format "  Tag: {{.Tag}}"; \
		echo ""; \
		echo "Detailed size breakdown:"; \
		docker history takehome1:latest --format "  {{.Size}}\t{{.CreatedBy}}" --no-trunc | head -10; \
		echo ""; \
		echo "Full image details:"; \
		docker image inspect takehome1:latest --format "  Virtual Size: {{.VirtualSize}} bytes"; \
		docker image inspect takehome1:latest --format "  Actual Size: {{.Size}} bytes"; \
	else \
		echo "❌ Image 'takehome1:latest' not found. Build it first with 'make docker-build'"; \
	fi

start-multi: docker-build-multi ## Start multi-instance stack (2 app instances + nginx load balancer + full observability). Uses k6 profile by default for load testing.
	@echo "🚀 Starting multi-instance stack..."
	@echo "  - Stopping any existing containers..."
	@docker-compose -f docker-compose.yml -f docker-compose.multi.yml down > /dev/null 2>&1 || true
	@echo "  - Starting infrastructure services (Postgres, Redis, Prometheus, Grafana)..."
	@docker-compose -f docker-compose.yml -f docker-compose.multi.yml up -d postgres redis prometheus grafana
	@echo "  - Waiting for infrastructure to be ready..."
	@sleep 5
	@echo "  - Starting 2 app instances with nginx load balancer..."
	@echo "    (Note: Base 'app' service from docker-compose.yml is excluded)"
	@docker-compose -f docker-compose.yml -f docker-compose.multi.yml up -d app1 app2 nginx
	@echo "  - Waiting for services to be healthy..."
	@echo "    (This may take 60-90 seconds for app instances to start)..."
	@for i in 1 2 3 4 5 6 7 8 9 10; do \
		if docker-compose -f docker-compose.yml -f docker-compose.multi.yml exec -T nginx wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health > /dev/null 2>&1; then \
			echo "  ✅ All services are ready!"; \
			break; \
		fi; \
		if [ $$i -eq 10 ]; then \
			echo "  ⚠️  Services may still be starting. Check logs with: docker-compose -f docker-compose.yml -f docker-compose.multi.yml logs"; \
		fi; \
		sleep 6; \
	done
	@echo ""
	@echo "📊 Service URLs:"
	@echo "  - Application (via nginx LB): http://localhost:8080"
	@echo "  - App Instance 1 (direct): http://localhost:8081"
	@echo "  - App Instance 2 (direct): http://localhost:8082"
	@echo "  - Grafana: http://localhost:3000 (admin/admin)"
	@echo "  - Prometheus: http://localhost:9090"
	@echo ""
	@echo "💡 Run k6 tests against http://localhost:8080 to test distributed locks!"
	@echo "   Example: make k6-load-multi"

stop-multi: ## Stop multi-instance stack
	@echo "🛑 Stopping multi-instance stack..."
	@docker-compose -f docker-compose.yml -f docker-compose.multi.yml down
	@echo "✅ Multi-instance stack stopped"

docker-run: ## Run Docker container (single instance)
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

k6-warmup-multi: ## Run K6 warm-up test against multi-instance setup (2 app instances)
	@echo "🔥 Running k6 warm-up test against multi-instance setup..."
	@echo "  Note: Ensure multi-instance stack is running (make start-multi)"
	@BASE_URL=http://localhost:8080 ./scripts/k6-run-test.sh k6/scripts/warmup-test.js

k6-smoke-multi: ## Run K6 smoke test against multi-instance setup (2 app instances)
	@echo "💨 Running k6 smoke test against multi-instance setup..."
	@BASE_URL=http://localhost:8080 ./scripts/k6-run-test.sh k6/scripts/smoke-test.js

k6-load-multi: ## Run K6 load test against multi-instance setup (2 app instances) - tests distributed locks
	@echo "📊 Running k6 load test against multi-instance setup..."
	@echo "  Note: Ensure multi-instance stack is running (make start-multi)"
	@echo "  This test will verify distributed locks work correctly across 2 instances"
	@BASE_URL=http://localhost:8080 ./scripts/k6-run-test.sh k6/scripts/load-test.js

k6-stress-multi: ## Run K6 stress test against multi-instance setup (2 app instances)
	@echo "💪 Running k6 stress test against multi-instance setup..."
	@BASE_URL=http://localhost:8080 ./scripts/k6-run-test.sh k6/scripts/stress-test.js

k6-spike-multi: ## Run K6 spike test against multi-instance setup (2 app instances). Circuit breakers enabled for validation.
	@echo "⚡ Running k6 spike test against multi-instance setup..."
	@echo "  Note: Circuit breakers are enabled for spike test validation"
	@echo "  To enable circuit breakers, restart stack with: SPRING_PROFILES_ACTIVE=k6,k6-spike make start-multi"
	@BASE_URL=http://localhost:8080 ./scripts/k6-run-test.sh k6/scripts/spike-test.js

k6-test-multi: ## Run all K6 tests against multi-instance setup (warmup, smoke, load, stress, spike)
	@echo "🧪 Running all k6 tests against multi-instance setup..."
	@echo "  Testing distributed locks across 2 app instances..."
	@make k6-warmup-multi
	@echo ""
	@make k6-smoke-multi
	@echo ""
	@make k6-load-multi
	@echo ""
	@make k6-stress-multi
	@echo ""
	@make k6-spike-multi
	@echo "✅ All k6 tests completed against multi-instance setup!"
