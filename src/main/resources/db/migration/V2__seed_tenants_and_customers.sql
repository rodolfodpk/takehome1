-- Migration V2: Seed tenants and customers for testing
-- 
-- This migration creates the seed data required for k6 tests:
-- - Tenant 1: "Acme Corporation" (active) with 3 customers
-- - Tenant 2: "TechStart Inc" (active) with 2 customers
-- - Tenant 3: "Global Services Ltd" (inactive) - not used in tests
--
-- The migration is idempotent - it will not create duplicates if run multiple times.

-- ============================================================================
-- 1. Insert Tenants
-- ============================================================================

-- Tenant 1: Acme Corporation (active)
INSERT INTO tenants (id, name, active, created, updated)
VALUES (1, 'Acme Corporation', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE 
SET name = EXCLUDED.name, active = EXCLUDED.active, updated = CURRENT_TIMESTAMP;

-- Tenant 2: TechStart Inc (active)
INSERT INTO tenants (id, name, active, created, updated)
VALUES (2, 'TechStart Inc', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE 
SET name = EXCLUDED.name, active = EXCLUDED.active, updated = CURRENT_TIMESTAMP;

-- Tenant 3: Global Services Ltd (inactive - not used in k6 tests)
INSERT INTO tenants (id, name, active, created, updated)
VALUES (3, 'Global Services Ltd', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE 
SET name = EXCLUDED.name, active = EXCLUDED.active, updated = CURRENT_TIMESTAMP;

-- Reset sequence to ensure next auto-generated ID is 4
SELECT setval('tenants_id_seq', GREATEST(3, (SELECT MAX(id) FROM tenants)), true);

-- ============================================================================
-- 2. Insert Customers
-- ============================================================================

-- Tenant 1 customers
INSERT INTO customers (tenant_id, external_id, name, created, updated)
VALUES 
    (1, 'acme-customer-001', 'Acme Customer 001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (1, 'acme-customer-002', 'Acme Customer 002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (1, 'acme-customer-003', 'Acme Customer 003', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (tenant_id, external_id) DO UPDATE 
SET name = EXCLUDED.name, updated = CURRENT_TIMESTAMP;

-- Tenant 2 customers
INSERT INTO customers (tenant_id, external_id, name, created, updated)
VALUES 
    (2, 'techstart-customer-001', 'TechStart Customer 001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'techstart-customer-002', 'TechStart Customer 002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (tenant_id, external_id) DO UPDATE 
SET name = EXCLUDED.name, updated = CURRENT_TIMESTAMP;

