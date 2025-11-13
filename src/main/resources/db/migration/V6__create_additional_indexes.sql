-- Additional composite indexes for optimized query patterns

-- Index for finding customers by tenant (already exists in V2, but ensuring it's there)
-- CREATE INDEX IF NOT EXISTS idx_customers_tenant_id ON customers(tenant_id);

-- Index for querying usage events by time range and customer (already exists in V3)
-- CREATE INDEX IF NOT EXISTS idx_usage_events_tenant_customer_timestamp ON usage_events(tenant_id, customer_id, timestamp);

-- Index for finding aggregation windows for reporting (already exists in V4)
-- CREATE INDEX IF NOT EXISTS idx_aggregation_windows_tenant_window ON aggregation_windows(tenant_id, window_start, window_end);

-- Additional index for window lookups by customer and time range
CREATE INDEX IF NOT EXISTS idx_aggregation_windows_customer_window ON aggregation_windows(customer_id, window_start, window_end);

-- Index for late events processing (find events by tenant and original timestamp)
CREATE INDEX IF NOT EXISTS idx_late_events_tenant_original_timestamp ON late_events(tenant_id, original_timestamp);

