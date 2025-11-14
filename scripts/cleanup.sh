#!/bin/bash
# Cleanup script: Stop application server and all Docker containers with volumes

echo "🧹 Cleaning up..."

# Stop Spring Boot application
echo "  - Stopping Spring Boot application..."
pkill -f "spring-boot:run" 2>/dev/null || pkill -f "mvn spring-boot" 2>/dev/null || echo "    (No Spring Boot processes found)"

# Stop all Docker containers and remove volumes
echo "  - Stopping Docker containers and removing volumes..."
docker-compose down -v 2>/dev/null || echo "    (Docker Compose cleanup completed)"

# Kill any process on port 8080
echo "  - Freeing port 8080..."
lsof -ti:8080 | xargs kill -9 2>/dev/null || echo "    (Port 8080 is free)"

echo "✅ Cleanup complete!"
echo ""

