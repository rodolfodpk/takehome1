#!/bin/bash
# Lightweight smoke test for make commands - runs in CI
# Validates syntax and command existence without execution
# Takes ~10-30 seconds

set -e

echo "🧪 Running make commands smoke test..."
echo ""

# Test makefile syntax
echo "  ✓ Checking Makefile syntax..."
if ! make -n help > /dev/null 2>&1; then
    echo "❌ Makefile syntax error"
    exit 1
fi
echo "    ✅ Makefile syntax valid"

# Test that all documented commands exist and can be parsed
echo "  ✓ Validating command existence..."
COMMANDS=(
    "help"
    "build"
    "test"
    "start"
    "start-obs"
    "stop"
    "start-multi"
    "stop-multi"
    "start-multi-and-test"
    "k6-cleanup"
    "k6-warmup"
    "k6-smoke"
    "k6-load"
    "k6-stress"
    "k6-spike"
    "k6-test"
    "docker-build"
    "docker-build-multi"
    "flyway-repair"
    "clean"
    "cleanup"
)

FAILED=0
for cmd in "${COMMANDS[@]}"; do
    if ! make -n "$cmd" > /dev/null 2>&1; then
        echo "    ❌ Command 'make $cmd' not found or has syntax error"
        FAILED=1
    fi
done

if [ $FAILED -eq 1 ]; then
    echo "❌ Some make commands failed validation"
    exit 1
fi
echo "    ✅ All commands validated ($(echo ${COMMANDS[@]} | wc -w) commands)"

# Test that help output contains expected commands
echo "  ✓ Validating help output..."
HELP_OUTPUT=$(make help)
EXPECTED_IN_HELP=("start-multi" "stop-multi" "start-multi-and-test" "start" "test-make-commands")

FAILED=0
for keyword in "${EXPECTED_IN_HELP[@]}"; do
    if ! echo "$HELP_OUTPUT" | grep -q "$keyword"; then
        echo "    ❌ Help output missing '$keyword'"
        FAILED=1
    fi
done

if [ $FAILED -eq 1 ]; then
    echo "❌ Help output validation failed"
    exit 1
fi
echo "    ✅ Help output validated"

echo ""
echo "✅ Make commands smoke test passed!"

