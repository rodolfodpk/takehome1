#!/bin/bash
# Cleanup script for k6 tests
# Cleans database, Redis, and resets circuit breakers

set -e

echo "🧹 Cleaning up before k6 test..."

# Check if containers are running
if ! docker-compose ps postgres | grep -q "Up"; then
    echo "  ⚠️  PostgreSQL container is not running. Starting it..."
    docker-compose up -d postgres
    sleep 3
fi

if ! docker-compose ps redis | grep -q "Up"; then
    echo "  ⚠️  Redis container is not running. Starting it..."
    docker-compose up -d redis
    sleep 2
fi

# Clean PostgreSQL - TRUNCATE all tables but keep seed data (tenants, customers)
echo "  - Cleaning PostgreSQL..."
docker-compose exec -T postgres psql -U takehome1 -d takehome1 <<EOF 2>/dev/null || echo "    (PostgreSQL cleanup skipped - container may not be ready)"
TRUNCATE TABLE late_events CASCADE;
TRUNCATE TABLE aggregation_windows CASCADE;
TRUNCATE TABLE usage_events CASCADE;
-- Keep tenants and customers (seed data)
EOF

# Clean Redis - Flush all keys
echo "  - Cleaning Redis..."
docker-compose exec -T redis redis-cli -a takehome1 FLUSHDB > /dev/null 2>&1 || echo "    (Redis cleanup skipped - container may not be ready)"

# Note about circuit breakers
echo "  - Circuit breakers:"
if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "    (App is running - circuit breakers will reset naturally as new requests come in)"
else
    echo "    (App not running - circuit breakers will reset when app starts)"
fi

echo "✅ Cleanup complete!"

