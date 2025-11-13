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

stop: ## Stop all Docker containers
	docker-compose down

clean: ## Clean build artifacts and Docker volumes
	mvn clean
	docker-compose down -v

docker-build: ## Build Docker image
	docker build -t takehome1:latest .

docker-run: ## Run Docker container
	docker run -p 8080:8080 --env-file .env takehome1:latest
