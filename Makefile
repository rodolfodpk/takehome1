.PHONY: help build test start start-obs stop clean cleanup docker-build docker-build-multi start-multi stop-multi start-multi-and-test check-ports verify-urls flyway-repair k6-cleanup k6-warmup k6-smoke k6-load k6-stress k6-spike k6-test test-make-commands test-make-commands-smoke test-make-commands-full

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
	@echo "🛑 Stopping any running application instances..."
	@pkill -f "spring-boot:run" 2>/dev/null || true
	@pkill -f "mvn spring-boot" 2>/dev/null || true
	@sleep 1
	@echo "🔍 Checking for Docker containers..."
	@if docker ps --format "{{.Names}}" 2>/dev/null | grep -q "takehome1"; then \
		echo ""; \
		echo "⚠️  Docker containers are already running."; \
		echo "   Do you want to start fresh? (This will stop all containers and remove volumes)"; \
		echo "   [y/N]: "; \
		read -r response; \
		if [ "$$response" = "y" ] || [ "$$response" = "Y" ]; then \
			echo "  🧹 Stopping containers and removing volumes..."; \
			docker-compose down -v > /dev/null 2>&1 || true; \
			sleep 2; \
			echo "  ✅ Cleanup complete"; \
		else \
			echo "  ℹ️  Continuing with existing containers..."; \
		fi; \
	fi
	@echo "🔍 Checking port 8080..."
	@if lsof -i :8080 > /dev/null 2>&1; then \
		echo "  - Port 8080 is in use, freeing it..."; \
		pkill -f "spring-boot:run" 2>/dev/null || true; \
		pkill -f "mvn spring-boot" 2>/dev/null || true; \
		sleep 1; \
		if lsof -i :8080 > /dev/null 2>&1; then \
			echo "  - Killing remaining processes on port 8080..."; \
			lsof -ti:8080 | xargs kill -9 2>/dev/null || true; \
			sleep 1; \
		fi; \
		if lsof -i :8080 > /dev/null 2>&1; then \
			echo "  ⚠️  Warning: Port 8080 may still be in use. Continuing anyway..."; \
		else \
			echo "  ✅ Port 8080 is now free"; \
		fi; \
	else \
		echo "  ✅ Port 8080 is free"; \
	fi
	@echo "🚀 Starting infrastructure services..."
	@docker-compose up -d postgres redis prometheus grafana
	@echo "Waiting for services to be ready..."
	@sleep 5
	@echo "  - Verifying PostgreSQL is ready..."
	@for i in 1 2 3 4 5 6 7 8 9 10; do \
		if docker-compose exec -T postgres pg_isready -U postgres > /dev/null 2>&1; then \
			break; \
		fi; \
		if [ $$i -eq 10 ]; then \
			echo "  ⚠️  PostgreSQL may not be ready. Check logs with: docker-compose logs postgres"; \
		fi; \
		sleep 1; \
	done
	@echo "  - Checking if database user 'takehome1' exists..."
	@USER_EXISTS=$$(docker-compose exec -T postgres psql -U postgres -tc "SELECT 1 FROM pg_roles WHERE rolname='takehome1'" 2>/dev/null | tr -d ' ' || echo ""); \
	if [ -z "$$USER_EXISTS" ]; then \
		echo "  - User 'takehome1' does not exist. Creating user and granting permissions..."; \
		docker-compose exec -T postgres psql -U postgres -c "CREATE USER takehome1 WITH PASSWORD 'takehome1';" 2>/dev/null || true; \
		docker-compose exec -T postgres psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE takehome1 TO takehome1;" 2>/dev/null || true; \
		docker-compose exec -T postgres psql -U postgres -d takehome1 -c "GRANT ALL ON SCHEMA public TO takehome1;" 2>/dev/null || true; \
		echo "  ✅ User 'takehome1' created successfully"; \
	else \
		echo "  ✅ User 'takehome1' already exists"; \
	fi
	@echo "✅ Infrastructure services are ready!"
	@echo "Starting Spring Boot application..."
	@mvn spring-boot:run

start-obs: ## Alias for 'start' - Start application with full observability stack
	@make start



stop: ## Stop Spring Boot application and all Docker containers (stops everything started by make start)
	@echo "🛑 Stopping Spring Boot application..."
	@pkill -f "spring-boot:run" 2>/dev/null || true
	@pkill -f "mvn spring-boot" 2>/dev/null || true
	@sleep 1
	@echo "🛑 Stopping Docker containers..."
	@docker-compose down
	@echo "✅ All services stopped"

flyway-repair: ## Repair Flyway schema history (fixes checksum mismatches after migration changes)
	@echo "🔧 Repairing Flyway schema history..."
	@mvn flyway:repair -Dflyway.url=jdbc:postgresql://localhost:5432/takehome1 -Dflyway.user=takehome1 -Dflyway.password=takehome1
	@echo "✅ Flyway repair complete!"

check-ports: ## Check all application ports for conflicts
	@./scripts/check-ports.sh

verify-urls: ## Verify all URLs documented in README.md
	@./scripts/verify-urls.sh

clean: ## Clean build artifacts and Docker volumes
	mvn clean
	@./scripts/cleanup.sh

cleanup: ## Stop server and remove all Docker containers with volumes
	@./scripts/cleanup.sh

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
	@echo "   Example: make k6-load"

stop-multi: ## Stop multi-instance stack
	@echo "🛑 Stopping multi-instance stack..."
	@docker-compose -f docker-compose.yml -f docker-compose.multi.yml down
	@echo "✅ Multi-instance stack stopped"

docker-run: ## Run Docker container (single instance)
	docker run -p 8080:8080 --env-file .env takehome1:latest

k6-cleanup: ## Clean database, Redis, and reset circuit breakers before k6 tests
	@./scripts/k6-cleanup.sh

k6-warmup: ## Run K6 warm-up test against multi-instance setup (requires make start-multi first)
	@echo "🔥 Running k6 warm-up test..."
	@echo "  Note: Ensure multi-instance stack is running (make start-multi)"
	@if ! docker ps --format "{{.Names}}" 2>/dev/null | grep -q "takehome1-app-1\|takehome1-app-2"; then \
		echo "  ❌ Error: Multi-instance stack is not running."; \
		echo "  Please run 'make start-multi' first."; \
		exit 1; \
	fi
	@BASE_URL=http://localhost:8080 ./scripts/k6-run-test.sh k6/scripts/warmup-test.js

k6-smoke: ## Run K6 smoke test against multi-instance setup (requires make start-multi first)
	@echo "💨 Running k6 smoke test..."
	@echo "  Note: Ensure multi-instance stack is running (make start-multi)"
	@if ! docker ps --format "{{.Names}}" 2>/dev/null | grep -q "takehome1-app-1\|takehome1-app-2"; then \
		echo "  ❌ Error: Multi-instance stack is not running."; \
		echo "  Please run 'make start-multi' first."; \
		exit 1; \
	fi
	@BASE_URL=http://localhost:8080 ./scripts/k6-run-test.sh k6/scripts/smoke-test.js

k6-load: ## Run K6 load test against multi-instance setup - tests distributed locks (requires make start-multi first)
	@echo "📊 Running k6 load test..."
	@echo "  Note: Ensure multi-instance stack is running (make start-multi)"
	@echo "  This test will verify distributed locks work correctly across 2 instances"
	@if ! docker ps --format "{{.Names}}" 2>/dev/null | grep -q "takehome1-app-1\|takehome1-app-2"; then \
		echo "  ❌ Error: Multi-instance stack is not running."; \
		echo "  Please run 'make start-multi' first."; \
		exit 1; \
	fi
	@BASE_URL=http://localhost:8080 ./scripts/k6-run-test.sh k6/scripts/load-test.js

k6-stress: ## Run K6 stress test against multi-instance setup (requires make start-multi first)
	@echo "💪 Running k6 stress test..."
	@echo "  Note: Ensure multi-instance stack is running (make start-multi)"
	@if ! docker ps --format "{{.Names}}" 2>/dev/null | grep -q "takehome1-app-1\|takehome1-app-2"; then \
		echo "  ❌ Error: Multi-instance stack is not running."; \
		echo "  Please run 'make start-multi' first."; \
		exit 1; \
	fi
	@BASE_URL=http://localhost:8080 ./scripts/k6-run-test.sh k6/scripts/stress-test.js

k6-spike: ## Run K6 spike test against multi-instance setup (requires make start-multi first)
	@echo "⚡ Running k6 spike test..."
	@echo "  Note: Ensure multi-instance stack is running (make start-multi)"
	@echo "  Note: Circuit breakers are enabled for spike test validation"
	@echo "  To enable circuit breakers, restart stack with: SPRING_PROFILES_ACTIVE=k6,k6-spike make start-multi"
	@if ! docker ps --format "{{.Names}}" 2>/dev/null | grep -q "takehome1-app-1\|takehome1-app-2"; then \
		echo "  ❌ Error: Multi-instance stack is not running."; \
		echo "  Please run 'make start-multi' first."; \
		exit 1; \
	fi
	@BASE_URL=http://localhost:8080 ./scripts/k6-run-test.sh k6/scripts/spike-test.js

k6-test: ## Run all K6 tests sequentially against multi-instance setup (requires make start-multi first)
	@echo "🧪 Running all k6 tests against multi-instance setup..."
	@echo "  Testing distributed locks across 2 app instances..."
	@if ! docker ps --format "{{.Names}}" 2>/dev/null | grep -q "takehome1-app-1\|takehome1-app-2"; then \
		echo "  ❌ Error: Multi-instance stack is not running."; \
		echo "  Please run 'make start-multi' first."; \
		exit 1; \
	fi
	@echo "🧹 Running initial cleanup before tests..."
	@make k6-cleanup
	@echo ""
	@-make k6-warmup || echo "  ⚠️  k6-warmup failed (continuing with other tests)"
	@echo ""
	@-make k6-smoke || echo "  ⚠️  k6-smoke failed (continuing with other tests)"
	@echo ""
	@-make k6-load || echo "  ⚠️  k6-load failed (continuing with other tests)"
	@echo ""
	@-make k6-stress || echo "  ⚠️  k6-stress failed (continuing with other tests)"
	@echo ""
	@-make k6-spike || echo "  ⚠️  k6-spike failed (continuing with other tests)"
	@echo "✅ All k6 tests completed!"

start-multi-and-test: ## Start multi-instance stack and run all k6 tests in one command
	@echo "🚀 Starting multi-instance stack and running all k6 tests..."
	@echo ""
	@make start-multi
	@echo ""
	@echo "⏳ Waiting additional time for services to fully stabilize..."
	@sleep 10
	@echo ""
	@make k6-test
	@echo ""
	@echo "✅ Multi-instance k6 testing complete!"
	@echo "💡 Multi-instance stack is still running. Use 'make stop-multi' to stop it."

test-make-commands-smoke: ## Run lightweight smoke test for make commands (fast, for CI)
	@chmod +x scripts/test-make-commands-smoke.sh
	@./scripts/test-make-commands-smoke.sh

test-make-commands-full: ## Run full integration test for make commands (slow, for local use)
	@chmod +x scripts/test-make-commands-full.sh
	@./scripts/test-make-commands-full.sh

test-make-commands: test-make-commands-smoke ## Alias for smoke test
