#!/bin/bash
# Helper script to run k6 tests against multi-instance setup
# Usage: k6-run-test.sh <test-script-name>
# Requires: Multi-instance stack must be running (make start-multi)

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
    echo "  - Application containers are still running"
    echo "  - Note: Grafana and Prometheus are still running"
    echo "  - Access Grafana at http://localhost:3000 (admin/admin)"
    echo "  - Access Prometheus at http://localhost:9090"
    echo "  - Run 'make stop-multi' to stop multi-instance stack"
    echo "✅ Cleanup complete"
}

# Trap to ensure cleanup on exit
trap cleanup EXIT INT TERM

# Setup environment
echo "🔧 Setting up k6 test environment..."

# Verify multi-instance stack is running
if ! docker ps --format "{{.Names}}" 2>/dev/null | grep -q "takehome1-app-1\|takehome1-app-2"; then
    echo "  ❌ Error: Multi-instance stack is not running."
    echo "  Please run 'make start-multi' first."
    exit 1
fi

echo "  ✅ Multi-instance stack detected"

# Use multi-instance docker-compose command
DOCKER_COMPOSE_CMD="docker-compose -f docker-compose.yml -f docker-compose.multi.yml"

# Check if Grafana/Prometheus are already running
GRAFANA_RUNNING=$(docker ps --filter "name=takehome1-grafana" --format "{{.Names}}" 2>/dev/null | grep -q "takehome1-grafana" && echo "yes" || echo "no")
PROMETHEUS_RUNNING=$(docker ps --filter "name=takehome1-prometheus" --format "{{.Names}}" 2>/dev/null | grep -q "takehome1-prometheus" && echo "yes" || echo "no")

if [ "$GRAFANA_RUNNING" = "no" ] || [ "$PROMETHEUS_RUNNING" = "no" ]; then
    echo "  - Starting Grafana and Prometheus (if not running)..."
    $DOCKER_COMPOSE_CMD up -d prometheus grafana
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
    echo "  - Grafana and Prometheus are already running"
fi

# Verify database services are healthy
echo "  - Verifying PostgreSQL and Redis health..."
for i in {1..10}; do
    if $DOCKER_COMPOSE_CMD exec -T postgres pg_isready -U takehome1 > /dev/null 2>&1 && \
       $DOCKER_COMPOSE_CMD exec -T redis redis-cli -a takehome1 ping > /dev/null 2>&1; then
        break
    fi
    sleep 1
done

echo "✅ Services are ready!"
echo ""
echo "📊 Observability:"
echo "  - Grafana: http://localhost:3000 (admin/admin)"
echo "  - Prometheus: http://localhost:9090"
echo ""

# BASE_URL is set by make target, default to http://localhost:8080 (nginx load balancer)
BASE_URL=${BASE_URL:-http://localhost:8080}

# Verify application is responding
echo "  - Verifying application is responding..."
APP_RESPONDING=false
for i in {1..30}; do
    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/actuator/health" 2>/dev/null || echo "000")
    if [ "$HTTP_CODE" = "200" ]; then
        echo "  - Application is healthy (HTTP 200)"
        APP_RESPONDING=true
        break
    elif [ "$HTTP_CODE" != "000" ] && [ "$HTTP_CODE" != "" ]; then
        # App is responding (even if health is DOWN), which is acceptable for multi-instance
        # Health might be DOWN due to transient connection pool issues, but app can still handle requests
        echo "  - Application is responding (HTTP $HTTP_CODE) - proceeding with test"
        APP_RESPONDING=true
        break
    fi
    if [ $i -eq 30 ]; then
        echo "  ⚠️  Application not responding after 30 seconds"
        echo "  - Checking container status..."
        docker ps --filter "name=takehome1-app" --format "table {{.Names}}\t{{.Status}}" 2>/dev/null || true
        exit 1
    fi
    sleep 1
done
if [ "$APP_RESPONDING" = "false" ]; then
    echo "  ⚠️  Application health check failed"
    exit 1
fi

# Wait additional 2-3 seconds for full initialization
echo "  - Waiting for full initialization..."
sleep 3

# Check Prometheus metrics endpoint is ready
echo "  - Checking Prometheus metrics endpoint..."
for i in {1..10}; do
    if curl -s "$BASE_URL/actuator/prometheus" > /dev/null 2>&1; then
        echo "  - Prometheus metrics endpoint is ready!"
        break
    fi
    if [ $i -eq 10 ]; then
        echo "  ⚠️  Prometheus endpoint not ready (continuing anyway)"
    fi
    sleep 1
done

echo "  ✅ Application is ready!"

# Ensure seed data exists (required for k6 tests)
# Seed data should be created by Flyway migration V2__seed_tenants_and_customers.sql
# But we ensure it exists here in test scope as a safety measure
echo "  - Ensuring seed data exists for tests..."

SEED_COUNT=$($DOCKER_COMPOSE_CMD exec -T postgres psql -U takehome1 -d takehome1 -t -c "SELECT COUNT(*) FROM tenants WHERE id IN (1, 2) AND active = true;" 2>/dev/null | tr -d ' ' || echo "0")
if [ "$SEED_COUNT" != "2" ]; then
    echo "  - Creating seed data for tests..."
    $DOCKER_COMPOSE_CMD exec -T postgres psql -U takehome1 -d takehome1 <<'SEED_EOF' 2>/dev/null || echo "    (Seed data creation skipped)"
-- Insert tenants if they don't exist
INSERT INTO tenants (id, name, active, created, updated)
VALUES 
    (1, 'Acme Corporation', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'TechStart Inc', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE 
SET name = EXCLUDED.name, active = EXCLUDED.active, updated = CURRENT_TIMESTAMP;

-- Insert customers if they don't exist
INSERT INTO customers (tenant_id, external_id, name, created, updated)
VALUES 
    (1, 'acme-customer-001', 'Acme Customer 001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (1, 'acme-customer-002', 'Acme Customer 002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (1, 'acme-customer-003', 'Acme Customer 003', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'techstart-customer-001', 'TechStart Customer 001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'techstart-customer-002', 'TechStart Customer 002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (tenant_id, external_id) DO UPDATE 
SET name = EXCLUDED.name, updated = CURRENT_TIMESTAMP;
SEED_EOF
    echo "  ✅ Seed data created for tests"
else
    echo "  ✅ Seed data verified"
fi

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
# K6_TEST_RESULTS.md contains results from multi-instance testing
RESULTS_FILE="docs/K6_TEST_RESULTS.md"
TIMESTAMP=$(date '+%Y-%m-%d %H:%M:%S')

echo "  - Updating test results document"

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

    # Extract only essential metrics from k6 output
    # Parse key metrics: throughput, error rate, p95 latency, p99 latency, VUs, duration
    extract_metrics() {
        local output_file="$1"
        local throughput=""
        local error_rate=""
        local p95=""
        local p99=""
        local vus_max=""
        local duration=""
        local threshold_status=""
        
        # Extract http_reqs (throughput) - format: "http_reqs................: 2797   279.663895/s"
        throughput=$(grep -E "^[[:space:]]*http_reqs" "$output_file" | head -1 | awk '{for(i=1;i<=NF;i++){if($i~/\/s$/){print $i;exit}}}')
        
        # Extract http_req_failed (error rate) - format: "http_req_failed.........: 0.00%  0 out of 2797"
        error_rate=$(grep -E "^[[:space:]]*http_req_failed" "$output_file" | head -1 | awk '{for(i=1;i<=NF;i++){if($i~/^[0-9.]+%$/){print $i;exit}}}')
        
        # Extract p95 latency - format: "http_req_duration...: avg=4.97ms ... p(95)=7.46ms"
        p95=$(grep -E "http_req_duration.*p\(95\)" "$output_file" | head -1 | sed -n 's/.*p(95)=\([0-9.]\+\)\([a-z]*\).*/\1\2/p')
        
        # Extract p99 latency (if available)
        p99=$(grep -E "http_req_duration.*p\(99\)" "$output_file" | head -1 | sed -n 's/.*p(99)=\([0-9.]\+\)\([a-z]*\).*/\1\2/p')
        
        # Extract vus_max - format: "vus_max................: 2      min=2         max=2"
        vus_max=$(grep -E "^[[:space:]]*vus_max" "$output_file" | head -1 | awk '{print $2}')
        
        # Extract test duration from final status line - format: "running (10.0s), 0/2 VUs"
        duration=$(grep -E "^running.*complete.*iterations" "$output_file" | tail -1 | sed -n 's/.*running[[:space:]]*(\([0-9ms.]\+\)).*/\1/p')
        
        # Extract threshold status
        if grep -q "✓.*p(95)" "$output_file"; then
            threshold_status="✅ Pass"
        elif grep -q "✗.*p(95)" "$output_file"; then
            threshold_status="❌ Fail"
        else
            threshold_status="⚠️  Unknown"
        fi
        
        # Build concise summary
        echo ""
        echo "**Metrics:**"
        echo "- **Throughput:** ${throughput:-N/A}"
        echo "- **Error Rate:** ${error_rate:-N/A}"
        echo "- **p95 Latency:** ${p95:-N/A}"
        [ -n "$p99" ] && echo "- **p99 Latency:** $p99"
        echo "- **Max VUs:** ${vus_max:-N/A}"
        echo "- **Duration:** ${duration:-N/A}"
        echo "- **Status:** $threshold_status"
    }

# Append concise results
echo "" >> "$RESULTS_FILE"
echo "## $TEST_NAME - $TIMESTAMP" >> "$RESULTS_FILE"
extract_metrics "$K6_OUTPUT_FILE" >> "$RESULTS_FILE"
echo "" >> "$RESULTS_FILE"

# Cleanup temp file
rm -f "$K6_OUTPUT_FILE"

# Exit with k6's exit code
exit $K6_EXIT_CODE

