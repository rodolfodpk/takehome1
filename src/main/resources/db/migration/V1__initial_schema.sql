-- Initial database schema
-- Consolidates all table creation, indexes, and audit fields into a single migration
-- Since there's no production database yet, this simplifies the migration history

-- ============================================================================
-- 1. Tenants table
-- ============================================================================
CREATE TABLE IF NOT EXISTS tenants (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for tenants
CREATE INDEX IF NOT EXISTS idx_tenants_active ON tenants(active);
CREATE UNIQUE INDEX IF NOT EXISTS idx_tenants_name_unique ON tenants(name);

-- ============================================================================
-- 2. Customers table
-- ============================================================================
CREATE TABLE IF NOT EXISTS customers (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    external_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_customers_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE
);

-- Indexes for customers
CREATE INDEX IF NOT EXISTS idx_customers_tenant_id ON customers(tenant_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_customers_tenant_external_unique ON customers(tenant_id, external_id);
CREATE INDEX IF NOT EXISTS idx_customers_external_id ON customers(external_id);

-- ============================================================================
-- 3. Usage events table
-- ============================================================================
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
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usage_events_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE,
    CONSTRAINT fk_usage_events_customer FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);

-- Indexes for usage_events
CREATE UNIQUE INDEX IF NOT EXISTS idx_usage_events_event_id_unique ON usage_events(event_id);
CREATE INDEX IF NOT EXISTS idx_usage_events_tenant_customer_timestamp ON usage_events(tenant_id, customer_id, timestamp);
CREATE INDEX IF NOT EXISTS idx_usage_events_timestamp ON usage_events(timestamp);
CREATE INDEX IF NOT EXISTS idx_usage_events_customer_id ON usage_events(customer_id);
-- Note: idx_usage_events_endpoint is not created here since it will be dropped in V3 migration

-- ============================================================================
-- 4. Aggregation windows table
-- ============================================================================
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
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_aggregation_windows_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE,
    CONSTRAINT fk_aggregation_windows_customer FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);

-- Indexes for aggregation_windows
CREATE UNIQUE INDEX IF NOT EXISTS idx_aggregation_windows_unique ON aggregation_windows(tenant_id, customer_id, window_start);
CREATE INDEX IF NOT EXISTS idx_aggregation_windows_tenant_customer ON aggregation_windows(tenant_id, customer_id);
CREATE INDEX IF NOT EXISTS idx_aggregation_windows_window_start ON aggregation_windows(window_start);
CREATE INDEX IF NOT EXISTS idx_aggregation_windows_tenant_window ON aggregation_windows(tenant_id, window_start, window_end);
CREATE INDEX IF NOT EXISTS idx_aggregation_windows_customer_window ON aggregation_windows(customer_id, window_start, window_end);

-- ============================================================================
-- 5. Late events table
-- ============================================================================
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

-- Indexes for late_events
CREATE UNIQUE INDEX IF NOT EXISTS idx_late_events_event_id_unique ON late_events(event_id);
CREATE INDEX IF NOT EXISTS idx_late_events_tenant_customer ON late_events(tenant_id, customer_id);
CREATE INDEX IF NOT EXISTS idx_late_events_original_timestamp ON late_events(original_timestamp);
CREATE INDEX IF NOT EXISTS idx_late_events_received_timestamp ON late_events(received_timestamp);
CREATE INDEX IF NOT EXISTS idx_late_events_tenant_original_timestamp ON late_events(tenant_id, original_timestamp);

