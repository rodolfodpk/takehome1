#!/bin/bash
# Helper script to run k6 tests with full setup and teardown
# Usage: k6-run-test.sh <test-script-name>

set -e

TEST_SCRIPT=$1
if [ -z "$TEST_SCRIPT" ]; then
    echo "Error: Test script name required"
    echo "Usage: $0 <test-script-name>"
    exit 1
fi

# Cleanup function
cleanup() {
    echo ""
    echo "🧹 Cleaning up..."
    if [ -f /tmp/app-k6.pid ]; then
        PID=$(cat /tmp/app-k6.pid)
        if ps -p $PID > /dev/null 2>&1; then
            echo "  - Stopping application (PID: $PID)..."
            kill $PID 2>/dev/null || true
            sleep 2
            # Force kill if still running
            if ps -p $PID > /dev/null 2>&1; then
                kill -9 $PID 2>/dev/null || true
            fi
        fi
        rm -f /tmp/app-k6.pid
    fi
    echo "  - Note: Grafana and Prometheus are still running"
    echo "  - Access Grafana at http://localhost:3000 (admin/admin)"
    echo "  - Access Prometheus at http://localhost:9090"
    echo "  - Run 'make stop' to stop all services"
    echo "✅ Cleanup complete"
}

# Trap to ensure cleanup on exit
trap cleanup EXIT INT TERM

# Setup environment
echo "🔧 Setting up k6 test environment..."

# Check if Grafana/Prometheus are already running
GRAFANA_RUNNING=$(docker ps --filter "name=takehome1-grafana" --format "{{.Names}}" 2>/dev/null | grep -q "takehome1-grafana" && echo "yes" || echo "no")
PROMETHEUS_RUNNING=$(docker ps --filter "name=takehome1-prometheus" --format "{{.Names}}" 2>/dev/null | grep -q "takehome1-prometheus" && echo "yes" || echo "no")

if [ "$GRAFANA_RUNNING" = "no" ] || [ "$PROMETHEUS_RUNNING" = "no" ]; then
    echo "  - Starting Grafana and Prometheus (if not running)..."
    docker-compose up -d prometheus grafana
    echo "  - Waiting for Grafana and Prometheus to be ready..."
    sleep 5
    
    echo "  - Checking Grafana health..."
    for i in {1..15}; do
        if curl -s http://localhost:3000/api/health > /dev/null 2>&1; then
            echo "  - Grafana is ready!"
            break
        fi
        sleep 1
    done
    
    echo "  - Checking Prometheus health..."
    for i in {1..10}; do
        if curl -s http://localhost:9090/-/healthy > /dev/null 2>&1; then
            echo "  - Prometheus is ready!"
            break
        fi
        sleep 1
    done
else
    echo "  - Grafana and Prometheus are already running (session will persist!)"
fi

# Restart PostgreSQL and Redis with clean volumes
echo "  - Restarting PostgreSQL and Redis with clean data..."
docker-compose stop postgres redis > /dev/null 2>&1 || true
docker-compose rm -f postgres redis > /dev/null 2>&1 || true
docker volume rm takehome1_postgres_data takehome1_redis_data > /dev/null 2>&1 || true
docker-compose up -d postgres redis

echo "  - Waiting for PostgreSQL and Redis to be ready..."
sleep 3

echo "  - Checking PostgreSQL health..."
for i in {1..10}; do
    if docker-compose exec -T postgres pg_isready -U takehome1 > /dev/null 2>&1; then
        break
    fi
    sleep 1
done

echo "  - Checking Redis health..."
for i in {1..10}; do
    if docker-compose exec -T redis redis-cli -a takehome1 ping > /dev/null 2>&1; then
        break
    fi
    sleep 1
done

echo "✅ Services are ready!"
echo ""
echo "📊 Observability:"
echo "  - Grafana: http://localhost:3000 (admin/admin) - Session persists!"
echo "  - Prometheus: http://localhost:9090"
echo ""

# Start application
echo "  - Starting application with k6 profile..."
cd "$(dirname "$0")/.."
SPRING_PROFILES_ACTIVE=k6 mvn spring-boot:run > /tmp/app-k6.log 2>&1 &
echo $! > /tmp/app-k6.pid

echo "  - Waiting for application to be ready..."
# Wait for health endpoint to return 200
for i in {1..30}; do
    if curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health | grep -q "200"; then
        echo "  - Health endpoint is responding"
        break
    fi
    if [ $i -eq 30 ]; then
        echo "  ⚠️  Health endpoint not ready after 30 seconds"
        exit 1
    fi
    sleep 1
done

# Wait additional 2-3 seconds for full initialization
echo "  - Waiting for full initialization..."
sleep 3

# Verify health endpoint is stable (check multiple times)
echo "  - Verifying application stability..."
health_checks_passed=0
for i in {1..5}; do
    if curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health | grep -q "200"; then
        health_checks_passed=$((health_checks_passed + 1))
    fi
    sleep 1
done

if [ $health_checks_passed -lt 3 ]; then
    echo "  ⚠️  Health endpoint not stable (passed $health_checks_passed/5 checks)"
    echo "  - Checking application logs..."
    tail -20 /tmp/app-k6.log 2>&1 | grep -E "(ERROR|Exception|Started)" | tail -5
    exit 1
fi

# Check Prometheus metrics endpoint is ready
echo "  - Checking Prometheus metrics endpoint..."
for i in {1..10}; do
    if curl -s http://localhost:8080/actuator/prometheus > /dev/null 2>&1; then
        echo "  - Prometheus metrics endpoint is ready!"
        break
    fi
    if [ $i -eq 10 ]; then
        echo "  ⚠️  Prometheus endpoint not ready (continuing anyway)"
    fi
    sleep 1
done

echo "  ✅ Application is ready!"

# Run cleanup script
./scripts/k6-cleanup.sh

# Run the test
echo ""
echo "🚀 Running k6 test: $TEST_SCRIPT"
echo "💡 Tip: Open Grafana at http://localhost:3000 to monitor metrics in real-time!"
echo ""

# Extract test name from script path
TEST_NAME=$(basename "$TEST_SCRIPT" | sed 's/-test\.js$//' | sed 's/\.js$//' | sed 's/-/ /g' | awk '{for(i=1;i<=NF;i++)sub(/./,toupper(substr($i,1,1)),$i)}1')
# Handle special cases
case "$TEST_NAME" in
    "Warmup") TEST_NAME="Warm-up Test" ;;
    "Smoke") TEST_NAME="Smoke Test" ;;
    "Load") TEST_NAME="Load Test" ;;
    "Stress") TEST_NAME="Stress Test" ;;
    "Spike") TEST_NAME="Spike Test" ;;
esac

# Capture k6 output
K6_OUTPUT_FILE="/tmp/k6-output-$$.log"
k6 run "$TEST_SCRIPT" 2>&1 | tee "$K6_OUTPUT_FILE"
K6_EXIT_CODE=${PIPESTATUS[0]}

# Update test results document
RESULTS_FILE="docs/K6_TEST_RESULTS.md"
TIMESTAMP=$(date '+%Y-%m-%d %H:%M:%S')

# Remove previous section for this test (if exists)
if [ -f "$RESULTS_FILE" ]; then
    # Use awk to remove the section between "## $TEST_NAME" and next "##" or end of file
    awk -v test_name="$TEST_NAME" '
        BEGIN { in_section = 0; skip_blank = 0 }
        /^## / {
            if (in_section) { in_section = 0; skip_blank = 0 }
            if (index($0, test_name) > 0) { in_section = 1; skip_blank = 1; next }
        }
        in_section && /^$/ && skip_blank { skip_blank = 0; next }
        !in_section { print }
    ' "$RESULTS_FILE" > "$RESULTS_FILE.tmp" && mv "$RESULTS_FILE.tmp" "$RESULTS_FILE"
fi

# Append new results
echo "" >> "$RESULTS_FILE"
echo "## $TEST_NAME - $TIMESTAMP" >> "$RESULTS_FILE"
echo "" >> "$RESULTS_FILE"
cat "$K6_OUTPUT_FILE" >> "$RESULTS_FILE"
echo "" >> "$RESULTS_FILE"

# Cleanup temp file
rm -f "$K6_OUTPUT_FILE"

# Exit with k6's exit code
exit $K6_EXIT_CODE

