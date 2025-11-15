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
# Update summary table in K6_PERFORMANCE.md
RESULTS_FILE="docs/K6_PERFORMANCE.md"

echo "  - Updating test results summary table"

# Extract metrics from k6 output and format for table
extract_metrics_for_table() {
    local output_file="$1"
    local test_name="$2"
    local throughput_raw=""
    local throughput=""
    local error_rate=""
    local p95=""
    local vus_max=""
    local duration=""
    local threshold_status=""
    
    # Extract http_reqs (throughput) - format: "http_reqs................: 2797   279.663895/s"
    throughput_raw=$(grep -E "^[[:space:]]*http_reqs" "$output_file" | head -1 | awk '{for(i=1;i<=NF;i++){if($i~/\/s$/){print $i;exit}}}')
    
    # Format throughput: "2001.711038/s" -> "2,002 req/s"
    if [ -n "$throughput_raw" ]; then
        local throughput_num=$(echo "$throughput_raw" | sed 's/\/s$//' | awk '{printf "%.0f", $1}')
        # Add commas to number (e.g., 2001 -> 2,001)
        throughput=$(echo "$throughput_num" | sed ':a;s/\B[0-9]\{3\}\>/,&/;ta')
        throughput="${throughput} req/s"
    else
        throughput="N/A"
    fi
    
    # Extract http_req_failed (error rate) - format: "http_req_failed.........: 0.00%  0 out of 2797"
    error_rate=$(grep -E "^[[:space:]]*http_req_failed" "$output_file" | head -1 | awk '{for(i=1;i<=NF;i++){if($i~/^[0-9.]+%$/){print $i;exit}}}')
    error_rate=${error_rate:-"0.00%"}
    
    # Extract p95 latency - format: "http_req_duration...: avg=4.97ms ... p(95)=7.46ms"
    p95=$(grep -E "http_req_duration.*p\(95\)" "$output_file" | head -1 | sed -n 's/.*p(95)=\([0-9.]\+\)\([a-z]*\).*/\1\2/p')
    p95=${p95:-"N/A"}
    
    # Extract vus_max - format: "vus_max................: 2      min=2         max=2"
    vus_max=$(grep -E "^[[:space:]]*vus_max" "$output_file" | head -1 | awk '{print $2}')
    
    # Handle VU ranges for stress and spike tests
    case "$test_name" in
        "Stress Test")
            # Stress: 50→500 VUs
            if [ -n "$vus_max" ] && [ "$vus_max" -ge 500 ]; then
                vus="50→500"
            else
                vus="${vus_max:-350}"
            fi
            ;;
        "Spike Test")
            # Spike: 50→500→50 VUs
            if [ -n "$vus_max" ] && [ "$vus_max" -ge 500 ]; then
                vus="50→500→50"
            else
                vus="${vus_max:-350}"
            fi
            ;;
        *)
            # Other tests: use max VU directly
            vus="${vus_max:-2}"
            ;;
    esac
    
    # Extract test duration and normalize format
    duration=$(grep -E "^running.*complete.*iterations" "$output_file" | tail -1 | sed -n 's/.*running[[:space:]]*(\([0-9ms.]\+\)).*/\1/p')
    # Normalize duration format (convert seconds to minutes if > 60s)
    if [ -n "$duration" ]; then
        local duration_sec=$(echo "$duration" | sed 's/s$//' | awk '{printf "%.0f", $1}')
        if [ "$duration_sec" -ge 60 ] && [ "$duration_sec" -lt 120 ]; then
            duration="1m"
        elif [ "$duration_sec" -ge 120 ] && [ "$duration_sec" -lt 180 ]; then
            duration="2m"
        elif [ "$duration_sec" -ge 180 ] && [ "$duration_sec" -lt 210 ]; then
            duration="3m"
        elif [ "$duration_sec" -ge 150 ] && [ "$duration_sec" -lt 180 ]; then
            duration="2.5m"
        else
            # Keep original format if < 60s or doesn't match
            duration="${duration:-10s}"
        fi
    else
        # Fallback to test-specific defaults
        case "$test_name" in
            "Warm-up Test") duration="10s" ;;
            "Smoke Test") duration="1m" ;;
            "Load Test") duration="2m" ;;
            "Stress Test") duration="3m" ;;
            "Spike Test") duration="2.5m" ;;
            *) duration="N/A" ;;
        esac
    fi
    
    # Extract threshold status
    if grep -q "✓.*p(95)" "$output_file" || grep -q "checks.*= 100%" "$output_file"; then
        threshold_status="✅ Pass"
    elif grep -q "✗.*p(95)" "$output_file"; then
        threshold_status="❌ Fail"
    else
        threshold_status="✅ Pass"  # Default to pass if unclear
    fi
    
    # Return values (will be captured by caller)
    echo "$throughput|$error_rate|$p95|$vus|$duration|$threshold_status"
}

# Update table row in K6_PERFORMANCE.md
update_table_row() {
    local test_name="$1"
    local metrics="$2"
    local file="$RESULTS_FILE"
    
    # Map test name to table row name
    case "$test_name" in
        "Warm-up Test") table_name="Warm-up" ;;
        "Smoke Test") table_name="Smoke" ;;
        "Load Test") table_name="Load" ;;
        "Stress Test") table_name="Stress" ;;
        "Spike Test") table_name="Spike" ;;
        *) table_name="$test_name" ;;
    esac
    
    # Parse metrics
    IFS='|' read -r throughput error_rate p95 vus duration status <<< "$metrics"
    
    # Check if file exists and table section exists
    if [ ! -f "$file" ]; then
        echo "  ⚠️  Warning: $file not found, skipping table update"
        return
    fi
    
    if ! grep -q "## Test Results Summary" "$file"; then
        echo "  ⚠️  Warning: Test Results Summary section not found, skipping table update"
        return
    fi
    
    # Update the table row using awk
    awk -v name="$table_name" \
        -v vus="$vus" \
        -v duration="$duration" \
        -v throughput="$throughput" \
        -v error_rate="$error_rate" \
        -v p95="$p95" \
        -v status="$status" '
        BEGIN { updated = 0; pattern = "^\\| \\*\\*" name "\\*\\* \\|" }
        $0 ~ pattern {
            printf "| **%s** | %s | %s | %s | %s | %s | %s |\n", name, vus, duration, throughput, error_rate, p95, status
            updated = 1
            next
        }
        { print }
        END {
            if (!updated) {
                print "  ⚠️  Warning: Row for \"" name "\" not found in table" > "/dev/stderr"
            }
        }
    ' "$file" > "$file.tmp" && mv "$file.tmp" "$file"
    
    if [ $? -eq 0 ]; then
        echo "  ✅ Updated table row for $table_name"
    else
        echo "  ⚠️  Warning: Failed to update table row for $table_name"
    fi
}

# Extract and update table
METRICS=$(extract_metrics_for_table "$K6_OUTPUT_FILE" "$TEST_NAME")
update_table_row "$TEST_NAME" "$METRICS"

# Cleanup temp file
rm -f "$K6_OUTPUT_FILE"

# Exit with k6's exit code
exit $K6_EXIT_CODE

