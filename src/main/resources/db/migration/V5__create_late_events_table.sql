-- Create late_events table for events that arrive after window closure
CREATE TABLE IF NOT EXISTS late_events (
    id BIGSERIAL PRIMARY KEY,
    event_id VARCHAR(255) NOT NULL,
    original_timestamp TIMESTAMP NOT NULL,
    received_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tenant_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    data JSONB NOT NULL,
    CONSTRAINT fk_late_events_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE,
    CONSTRAINT fk_late_events_customer FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);

-- Create unique index on event_id to prevent duplicates
CREATE UNIQUE INDEX IF NOT EXISTS idx_late_events_event_id_unique ON late_events(event_id);

-- Create index on (tenant_id, customer_id) for tenant/customer queries
CREATE INDEX IF NOT EXISTS idx_late_events_tenant_customer ON late_events(tenant_id, customer_id);

-- Create index on original_timestamp for time-based queries
CREATE INDEX IF NOT EXISTS idx_late_events_original_timestamp ON late_events(original_timestamp);

-- Create index on received_timestamp for processing order
CREATE INDEX IF NOT EXISTS idx_late_events_received_timestamp ON late_events(received_timestamp);

