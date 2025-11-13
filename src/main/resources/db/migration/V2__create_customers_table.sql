-- Create customers table with tenant relationship
CREATE TABLE IF NOT EXISTS customers (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    external_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_customers_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE
);

-- Create index on tenant_id for faster tenant-based queries
CREATE INDEX IF NOT EXISTS idx_customers_tenant_id ON customers(tenant_id);

-- Create unique index on (tenant_id, external_id) to ensure uniqueness per tenant
CREATE UNIQUE INDEX IF NOT EXISTS idx_customers_tenant_external_unique ON customers(tenant_id, external_id);

-- Create index on external_id for lookups
CREATE INDEX IF NOT EXISTS idx_customers_external_id ON customers(external_id);

