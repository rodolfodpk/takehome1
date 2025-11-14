#!/bin/bash
# Check all ports used by the application for conflicts

echo "=== Port Conflict Check ==="
echo ""

PORTS=(8080 8081 8082 5432 6379 3000 9090)
PORT_NAMES=("App/nginx" "App Instance 1" "App Instance 2" "PostgreSQL" "Redis" "Grafana" "Prometheus")

CONFLICTS=0

for i in "${!PORTS[@]}"; do
    PORT=${PORTS[$i]}
    NAME=${PORT_NAMES[$i]}
    echo -n "Port $PORT ($NAME): "
    
    if lsof -i :$PORT > /dev/null 2>&1; then
        echo "⚠️  IN USE"
        lsof -i :$PORT | tail -n +2 | while read line; do
            echo "   $line"
        done
        CONFLICTS=$((CONFLICTS + 1))
    else
        echo "✅ FREE"
    fi
    echo ""
done

echo "=== Summary ==="
if [ $CONFLICTS -eq 0 ]; then
    echo "✅ All ports are free - no conflicts detected"
    exit 0
else
    echo "⚠️  Found $CONFLICTS port(s) in use"
    echo ""
    echo "To resolve conflicts:"
    echo "  1. Stop services: make stop (or make stop-multi)"
    echo "  2. Kill process: kill -9 <PID> (from output above)"
    echo "  3. Change port: Set environment variable (e.g., APP_PORT=8083)"
    exit 1
fi

