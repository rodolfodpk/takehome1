-- Create tenants table for multi-tenant isolation
CREATE TABLE IF NOT EXISTS tenants (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index on active status for faster queries
CREATE INDEX IF NOT EXISTS idx_tenants_active ON tenants(active);

-- Create unique index on name
CREATE UNIQUE INDEX IF NOT EXISTS idx_tenants_name_unique ON tenants(name);

