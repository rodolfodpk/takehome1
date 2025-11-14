#!/bin/bash
# Cleanup script for k6 tests
# Cleans database, Redis, and resets circuit breakers

set -e

echo "🧹 Cleaning up before k6 test..."

# Detect if multi-instance setup is running
USE_MULTI_INSTANCE=false
if docker ps --format "{{.Names}}" | grep -q "takehome1-app-1\|takehome1-app-2"; then
    USE_MULTI_INSTANCE=true
    DOCKER_COMPOSE_CMD="docker-compose -f docker-compose.yml -f docker-compose.multi.yml"
else
    DOCKER_COMPOSE_CMD="docker-compose"
fi

# Check if containers are running
if ! $DOCKER_COMPOSE_CMD ps postgres | grep -q "Up"; then
    echo "  ⚠️  PostgreSQL container is not running. Starting it..."
    $DOCKER_COMPOSE_CMD up -d postgres
    sleep 3
fi

if ! $DOCKER_COMPOSE_CMD ps redis | grep -q "Up"; then
    echo "  ⚠️  Redis container is not running. Starting it..."
    $DOCKER_COMPOSE_CMD up -d redis
    sleep 2
fi

# Clean PostgreSQL - TRUNCATE all tables but keep seed data (tenants, customers)
echo "  - Cleaning PostgreSQL..."
$DOCKER_COMPOSE_CMD exec -T postgres psql -U takehome1 -d takehome1 <<EOF 2>/dev/null || echo "    (PostgreSQL cleanup skipped - container may not be ready)"
TRUNCATE TABLE late_events CASCADE;
TRUNCATE TABLE aggregation_windows CASCADE;
TRUNCATE TABLE usage_events CASCADE;
-- Keep tenants and customers (seed data)
EOF

# Clean Redis - Flush all keys
echo "  - Cleaning Redis..."
$DOCKER_COMPOSE_CMD exec -T redis redis-cli -a takehome1 FLUSHDB > /dev/null 2>&1 || echo "    (Redis cleanup skipped - container may not be ready)"

# Note about circuit breakers
echo "  - Circuit breakers:"
if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "    (App is running - circuit breakers will reset naturally as new requests come in)"
else
    echo "    (App not running - circuit breakers will reset when app starts)"
fi

echo "✅ Cleanup complete!"

