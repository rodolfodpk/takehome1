-- Create usage_events table for persisted metering events
CREATE TABLE IF NOT EXISTS usage_events (
    id BIGSERIAL PRIMARY KEY,
    event_id VARCHAR(255) NOT NULL,
    tenant_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    endpoint VARCHAR(500) NOT NULL,
    tokens INTEGER,
    model VARCHAR(255),
    latency_ms INTEGER,
    metadata JSONB,
    CONSTRAINT fk_usage_events_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE,
    CONSTRAINT fk_usage_events_customer FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);

-- Create unique index on event_id to prevent duplicates
CREATE UNIQUE INDEX IF NOT EXISTS idx_usage_events_event_id_unique ON usage_events(event_id);

-- Create composite index on (tenant_id, customer_id, timestamp) for time-range queries
CREATE INDEX IF NOT EXISTS idx_usage_events_tenant_customer_timestamp ON usage_events(tenant_id, customer_id, timestamp);

-- Create index on timestamp for time-range queries
CREATE INDEX IF NOT EXISTS idx_usage_events_timestamp ON usage_events(timestamp);

-- Create index on customer_id for customer lookups
CREATE INDEX IF NOT EXISTS idx_usage_events_customer_id ON usage_events(customer_id);

-- Create index on endpoint for endpoint-based queries
CREATE INDEX IF NOT EXISTS idx_usage_events_endpoint ON usage_events(endpoint);

