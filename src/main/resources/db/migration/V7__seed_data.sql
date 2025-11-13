-- Seed data for testing and development
-- This migration provides sample data for testing multi-tenancy, aggregations, and API functionality

-- Insert sample tenants
INSERT INTO tenants (id, name, active, created, updated)
VALUES 
    (1, 'Acme Corporation', true, NOW(), NOW()),
    (2, 'TechStart Inc', true, NOW(), NOW()),
    (3, 'Global Services Ltd', false, NOW(), NOW())  -- Inactive tenant for testing
ON CONFLICT (id) DO NOTHING;

-- Reset sequence to avoid conflicts
SELECT setval('tenants_id_seq', (SELECT MAX(id) FROM tenants));

-- Insert sample customers for tenant 1 (Acme Corporation)
INSERT INTO customers (id, tenant_id, external_id, name, created, updated)
VALUES 
    (1, 1, 'acme-customer-001', 'Acme Customer Alpha', NOW(), NOW()),
    (2, 1, 'acme-customer-002', 'Acme Customer Beta', NOW(), NOW()),
    (3, 1, 'acme-customer-003', 'Acme Customer Gamma', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Insert sample customers for tenant 2 (TechStart Inc)
INSERT INTO customers (id, tenant_id, external_id, name, created, updated)
VALUES 
    (4, 2, 'techstart-customer-001', 'TechStart Customer One', NOW(), NOW()),
    (5, 2, 'techstart-customer-002', 'TechStart Customer Two', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Reset sequence to avoid conflicts
SELECT setval('customers_id_seq', (SELECT MAX(id) FROM customers));

-- Note: Usage events and aggregation windows are not seeded as they should be generated
-- through the API during testing. This keeps the seed data minimal and realistic.

