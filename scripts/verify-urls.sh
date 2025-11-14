#!/bin/bash
# Verify all URLs documented in README.md
# Tests application endpoints, external services, and validates response content

set -e

echo "=== URL Verification Script ==="
echo ""

FAILED=0
PASSED=0
WARNINGS=0

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to test a URL
test_url() {
    local url=$1
    local description=$2
    local expected_status=${3:-200}
    local validate_content=${4:-false}
    local content_check=${5:-""}
    
    echo -n "Testing: $description ... "
    
    # Make request with timeout
    response=$(curl -s -w "\n%{http_code}" --max-time 5 "$url" 2>&1) || {
        echo -e "${RED}FAILED${NC} - Connection error or timeout"
        ((FAILED++))
        return 1
    }
    
    # Extract status code (last line)
    status_code=$(echo "$response" | tail -n 1)
    body=$(echo "$response" | sed '$d')
    
    # Check status code
    if [ "$status_code" = "$expected_status" ]; then
        # Validate content if requested
        if [ "$validate_content" = "true" ] && [ -n "$content_check" ]; then
            if echo "$body" | grep -q "$content_check"; then
                echo -e "${GREEN}PASS${NC} (Status: $status_code, Content validated)"
                ((PASSED++))
            else
                echo -e "${YELLOW}WARNING${NC} (Status: $status_code, but content validation failed)"
                ((WARNINGS++))
            fi
        else
            echo -e "${GREEN}PASS${NC} (Status: $status_code)"
            ((PASSED++))
        fi
    else
        echo -e "${RED}FAILED${NC} (Expected: $expected_status, Got: $status_code)"
        ((FAILED++))
        return 1
    fi
}

# Check prerequisites
echo "=== Checking Prerequisites ==="
echo ""

# Check if application is running
if ! curl -s --max-time 2 http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo -e "${YELLOW}⚠️  Warning: Application may not be running on port 8080${NC}"
    echo "   Start application with: make start"
    echo "   (This will start all services: PostgreSQL, Redis, Prometheus, Grafana, and the app)"
    echo ""
    ((WARNINGS++))
fi

# Check if Grafana is running
if ! curl -s --max-time 2 http://localhost:3000 > /dev/null 2>&1; then
    echo -e "${YELLOW}⚠️  Warning: Grafana may not be running on port 3000${NC}"
    echo "   Start with: make start (starts all services including Grafana)"
    echo ""
    ((WARNINGS++))
fi

# Check if Prometheus is running
if ! curl -s --max-time 2 http://localhost:9090 > /dev/null 2>&1; then
    echo -e "${YELLOW}⚠️  Warning: Prometheus may not be running on port 9090${NC}"
    echo "   Start with: make start (starts all services including Prometheus)"
    echo ""
    ((WARNINGS++))
fi

echo ""
echo "=== Testing Application Endpoints (Port 8080) ==="
echo ""

# 1. Application Root
test_url "http://localhost:8080/" "Application root" 200 false

# 2. Swagger UI - test both paths
echo ""
echo "Testing Swagger UI paths:"
SWAGGER_WORKING=false
if test_url "http://localhost:8080/swagger-ui.html" "Swagger UI (/swagger-ui.html)" 200 false; then
    SWAGGER_WORKING=true
    SWAGGER_PATH="/swagger-ui.html"
fi
if test_url "http://localhost:8080/swagger-ui/index.html" "Swagger UI (/swagger-ui/index.html)" 200 false; then
    SWAGGER_WORKING=true
    SWAGGER_PATH="/swagger-ui/index.html"
fi

if [ "$SWAGGER_WORKING" = false ]; then
    echo -e "${RED}⚠️  Neither Swagger UI path works${NC}"
fi

# 3. OpenAPI Spec
echo ""
test_url "http://localhost:8080/v3/api-docs" "OpenAPI Spec" 200 true '"info"'

# 4. Health Check - validate it has components
echo ""
echo -n "Testing: Health Check (with component details) ... "
health_response=$(curl -s --max-time 5 "http://localhost:8080/actuator/health" 2>&1) || {
    echo -e "${RED}FAILED${NC} - Connection error"
    ((FAILED++))
}
if echo "$health_response" | grep -q '"status"'; then
    if echo "$health_response" | grep -q '"components"'; then
        # Check for specific components
        if echo "$health_response" | grep -q '"r2dbc"\|"redis"'; then
            echo -e "${GREEN}PASS${NC} (Status: 200, Components: r2dbc, redis found)"
            ((PASSED++))
        else
            echo -e "${YELLOW}WARNING${NC} (Status: 200, but missing component details)"
            ((WARNINGS++))
        fi
    else
        echo -e "${YELLOW}WARNING${NC} (Status: 200, but no 'components' object - may need show-details=always)"
        ((WARNINGS++))
    fi
else
    status_code=$(curl -s -o /dev/null -w "%{http_code}" --max-time 5 "http://localhost:8080/actuator/health" 2>&1)
    echo -e "${RED}FAILED${NC} (Status: $status_code, Invalid response)"
    ((FAILED++))
fi

# 5. Prometheus Metrics
echo ""
test_url "http://localhost:8080/actuator/prometheus" "Prometheus Metrics" 200 true "# HELP\|# TYPE"

# 6. All Metrics
echo ""
test_url "http://localhost:8080/actuator/metrics" "All Metrics" 200 true '"names"'

echo ""
echo "=== Testing External Services ==="
echo ""

# 7. Grafana
test_url "http://localhost:3000" "Grafana UI" 200 true "Grafana\|login"

# 8. Prometheus UI
test_url "http://localhost:9090" "Prometheus UI" 200 true "Prometheus"

echo ""
echo "=== Testing External Badges (Optional) ==="
echo ""

# 9. CI Badge
test_url "https://github.com/rodolfodpk/takehome1/workflows/CI/badge.svg" "CI Badge" 200 false

# 10. Codecov Badge
test_url "https://codecov.io/gh/rodolfodpk/takehome1/branch/main/graph/badge.svg" "Codecov Badge" 200 false

echo ""
echo "=== Summary ==="
echo ""
echo -e "Passed: ${GREEN}$PASSED${NC}"
echo -e "Failed: ${RED}$FAILED${NC}"
echo -e "Warnings: ${YELLOW}$WARNINGS${NC}"
echo ""

if [ $FAILED -eq 0 ] && [ $WARNINGS -eq 0 ]; then
    echo -e "${GREEN}✅ All URLs verified successfully!${NC}"
    exit 0
elif [ $FAILED -eq 0 ]; then
    echo -e "${YELLOW}⚠️  All URLs accessible, but some have warnings (check details above)${NC}"
    exit 0
else
    echo -e "${RED}❌ Some URLs failed verification. Check details above.${NC}"
    exit 1
fi

