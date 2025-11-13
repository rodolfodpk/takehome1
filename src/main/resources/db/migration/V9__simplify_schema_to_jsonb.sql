-- Migration V9: Simplify schema to JSONB pattern
-- 
-- This migration consolidates non-queried columns into JSONB for:
-- 1. usage_events: Move endpoint, tokens, model, latency_ms, metadata -> data JSONB
-- 2. aggregation_windows: Move total_calls, total_tokens, avg_latency_ms -> aggregation_data JSONB
--
-- Strategy:
-- - Keep only queried/FK columns as regular columns (tenant_id, customer_id, timestamp, event_id, window_start, window_end)
-- - Move all other fields to JSONB for flexibility and to eliminate boilerplate code
-- - Use GIN indexes for efficient JSONB queries when needed
--
-- Rollback: If needed, can re-add columns and migrate data back (not recommended after deployment)

BEGIN;

-- ============================================================================
-- 1. usage_events table migration
-- ============================================================================

-- Step 1: Add new 'data' JSONB column with default
ALTER TABLE usage_events 
ADD COLUMN IF NOT EXISTS data JSONB NOT NULL DEFAULT '{}';

-- Step 2: Migrate existing data to JSONB
-- Combine endpoint, tokens, model, latency_ms, and existing metadata into single 'data' JSONB
UPDATE usage_events 
SET data = jsonb_build_object(
    'endpoint', endpoint,
    'tokens', tokens,
    'model', model,
    'latencyMs', latency_ms
) || COALESCE(metadata, '{}'::jsonb)
WHERE data = '{}'::jsonb;  -- Only update if data is still default (idempotent)

-- Step 3: Drop old columns (after data migration verified)
ALTER TABLE usage_events 
DROP COLUMN IF EXISTS endpoint,
DROP COLUMN IF EXISTS tokens,
DROP COLUMN IF EXISTS model,
DROP COLUMN IF EXISTS latency_ms,
DROP COLUMN IF EXISTS metadata;

-- Step 4: Drop unused index on endpoint (no longer a column)
DROP INDEX IF EXISTS idx_usage_events_endpoint;

-- Step 5: Create GIN index on data column for efficient JSONB queries
CREATE INDEX IF NOT EXISTS idx_usage_events_data_gin ON usage_events USING GIN (data);

-- ============================================================================
-- 2. aggregation_windows table migration
-- ============================================================================

-- Step 1: Migrate existing data to aggregation_data JSONB
-- Add totalCalls, totalTokens, avgLatencyMs to root level of aggregation_data
UPDATE aggregation_windows 
SET aggregation_data = aggregation_data || jsonb_build_object(
    'totalCalls', total_calls,
    'totalTokens', total_tokens,
    'avgLatencyMs', avg_latency_ms
)
WHERE aggregation_data IS NOT NULL;  -- Only update existing rows

-- Step 2: Drop old columns (after data migration verified)
ALTER TABLE aggregation_windows 
DROP COLUMN IF EXISTS total_calls,
DROP COLUMN IF EXISTS total_tokens,
DROP COLUMN IF EXISTS avg_latency_ms;

-- ============================================================================
-- Verification queries (commented out - run manually to verify)
-- ============================================================================

-- Verify usage_events migration:
-- SELECT COUNT(*) FROM usage_events WHERE data IS NULL;  -- Should be 0
-- SELECT data->>'endpoint' as endpoint, data->>'tokens' as tokens FROM usage_events LIMIT 5;

-- Verify aggregation_windows migration:
-- SELECT aggregation_data->>'totalCalls' as totalCalls, aggregation_data->>'totalTokens' as totalTokens 
-- FROM aggregation_windows LIMIT 5;

-- Verify indexes:
-- \d usage_events  -- Should show idx_usage_events_data_gin

COMMIT;

