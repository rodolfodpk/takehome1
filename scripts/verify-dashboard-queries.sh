#!/bin/bash
# Script to verify dashboard queries against Prometheus metrics
# Usage: ./scripts/verify-dashboard-queries.sh

set -e

PROMETHEUS_URL="${PROMETHEUS_URL:-http://localhost:8080/actuator/prometheus}"

echo "🔍 Verifying Dashboard Queries Against Prometheus..."
echo "Prometheus URL: $PROMETHEUS_URL"
echo ""

# Fetch all metrics once
METRICS=$(curl -s "$PROMETHEUS_URL" 2>&1)

if [ -z "$METRICS" ]; then
    echo "❌ Error: Could not fetch metrics from $PROMETHEUS_URL"
    echo "   Make sure the application is running and Prometheus endpoint is accessible"
    exit 1
fi

echo "✅ Successfully fetched metrics"
echo ""

# Function to check if a metric exists
check_metric() {
    local metric_name=$1
    local description=$2
    
    if echo "$METRICS" | grep -q "^${metric_name}"; then
        echo "  ✅ $description"
        return 0
    else
        echo "  ❌ $description - NOT FOUND"
        return 1
    fi
}

# Function to check if a metric pattern exists
check_metric_pattern() {
    local pattern=$1
    local description=$2
    
    if echo "$METRICS" | grep -q "$pattern"; then
        echo "  ✅ $description"
        return 0
    else
        echo "  ⚠️  $description - NOT FOUND (may appear after app restart with new config)"
        return 1
    fi
}

errors=0

echo "📊 System Overview Dashboard:"
check_metric "metering_events_ingested_total{type=\"total\"" "Events ingested total" || ((errors++))
check_metric "metering_events_processing_latency_seconds{quantile=\"0.95\"" "P95 Processing Latency" || ((errors++))
check_metric "metering_events_processing_latency_seconds{quantile=\"0.99\"" "P99 Processing Latency" || ((errors++))
check_metric "metering_events_ingestion_errors_total" "Ingestion Errors" || ((errors++))
check_metric "resilience4j_circuitbreaker_state{name=\"postgres\"" "Postgres Circuit Breaker State" || ((errors++))
check_metric "resilience4j_circuitbreaker_state{name=\"redis\"" "Redis Circuit Breaker State" || ((errors++))
check_metric_pattern "jvm_memory_used_bytes.*area=\"heap\"" "JVM Memory Used (Heap)" || ((errors++))
check_metric_pattern "jvm_memory_max_bytes.*area=\"heap\"" "JVM Memory Max (Heap)" || ((errors++))

echo ""
echo "📊 Application & Infrastructure Dashboard:"
echo "  HTTP Metrics:"
check_metric_pattern "http_server_requests_seconds_count" "HTTP Request Count" || ((errors++))
check_metric_pattern "http_server_requests_seconds_bucket" "HTTP Request Histogram Buckets" || ((errors++))
check_metric_pattern "http_server_requests_seconds.*status=" "HTTP Status Codes" || ((errors++))

echo "  JVM Metrics:"
check_metric "jvm_threads_live_threads" "Live Threads" || ((errors++))
check_metric "jvm_threads_daemon_threads" "Daemon Threads" || ((errors++))
check_metric_pattern "jvm_gc_pause_seconds" "GC Pause Time" || ((errors++))

echo "  Database Metrics:"
check_metric "r2dbc_pool_acquired_connections{name=\"connectionFactory\"" "R2DBC Acquired Connections" || ((errors++))
check_metric "r2dbc_pool_pending_connections{name=\"connectionFactory\"" "R2DBC Pending Connections" || ((errors++))
check_metric "r2dbc_pool_idle_connections{name=\"connectionFactory\"" "R2DBC Idle Connections" || ((errors++))
check_metric "r2dbc_pool_max_allocated_connections{name=\"connectionFactory\"" "R2DBC Max Connections" || ((errors++))
check_metric_pattern "metering_db_persistence_latency_seconds" "DB Persistence Latency" || ((errors++))

echo "  Redis Metrics:"
check_metric_pattern "metering_redis_storage_latency_seconds" "Redis Storage Latency" || ((errors++))
check_metric_pattern "metering_redis_read_latency_seconds" "Redis Read Latency" || ((errors++))

echo "  Resilience4j Metrics:"
check_metric_pattern "resilience4j_circuitbreaker_calls_seconds_count" "Circuit Breaker Calls" || ((errors++))
check_metric_pattern "resilience4j_circuitbreaker_failure_rate" "Circuit Breaker Failure Rate" || ((errors++))
check_metric_pattern "resilience4j_retry_calls_total" "Retry Attempts" || ((errors++))
check_metric_pattern "resilience4j_timelimiter_calls_total" "Time Limiter Timeouts" || ((errors++))

echo ""
echo "📊 Business Metrics Dashboard:"
check_metric_pattern "metering_events_ingested_total.*type=\"by_tenant\"" "Events by Tenant" || ((errors++))
check_metric_pattern "metering_events_ingested_total.*type=\"by_customer\"" "Events by Customer" || ((errors++))

echo ""
if [ $errors -eq 0 ]; then
    echo "✅ All metrics verified successfully!"
    echo ""
    echo "💡 Note: Some metrics (histogram buckets, tenant/customer) may require:"
    echo "   1. App restart with new configuration"
    echo "   2. Some activity (HTTP requests, events) to generate data"
    exit 0
else
    echo "⚠️  Found $errors metric(s) that need attention"
    echo ""
    echo "💡 Next steps:"
    echo "   1. Restart the application to apply new configurations"
    echo "   2. Run some k6 tests to generate metrics data"
    echo "   3. Check Grafana time range matches when app was running"
    exit 1
fi

