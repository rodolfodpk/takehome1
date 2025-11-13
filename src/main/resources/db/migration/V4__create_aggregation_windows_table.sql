-- Create aggregation_windows table for completed aggregation results
CREATE TABLE IF NOT EXISTS aggregation_windows (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    window_start TIMESTAMP NOT NULL,
    window_end TIMESTAMP NOT NULL,
    total_calls BIGINT NOT NULL DEFAULT 0,
    total_tokens BIGINT NOT NULL DEFAULT 0,
    avg_latency_ms DOUBLE PRECISION,
    aggregation_data JSONB NOT NULL,
    CONSTRAINT fk_aggregation_windows_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE,
    CONSTRAINT fk_aggregation_windows_customer FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);

-- Create unique constraint on (tenant_id, customer_id, window_start) to ensure idempotency
CREATE UNIQUE INDEX IF NOT EXISTS idx_aggregation_windows_unique ON aggregation_windows(tenant_id, customer_id, window_start);

-- Create index on (tenant_id, customer_id) for customer-based queries
CREATE INDEX IF NOT EXISTS idx_aggregation_windows_tenant_customer ON aggregation_windows(tenant_id, customer_id);

-- Create index on window_start for time-range queries
CREATE INDEX IF NOT EXISTS idx_aggregation_windows_window_start ON aggregation_windows(window_start);

-- Create composite index on (tenant_id, window_start, window_end) for reporting queries
CREATE INDEX IF NOT EXISTS idx_aggregation_windows_tenant_window ON aggregation_windows(tenant_id, window_start, window_end);

