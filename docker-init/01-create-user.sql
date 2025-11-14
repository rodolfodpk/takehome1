-- Create takehome1 user if it doesn't exist
-- This script runs automatically when PostgreSQL container is first initialized (empty volume)
-- For existing volumes, use the Makefile check or run manually

DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'takehome1') THEN
        CREATE USER takehome1 WITH PASSWORD 'takehome1';
        RAISE NOTICE 'User takehome1 created';
    ELSE
        RAISE NOTICE 'User takehome1 already exists';
    END IF;
END
$$;

-- Grant all privileges on the database
GRANT ALL PRIVILEGES ON DATABASE takehome1 TO takehome1;

-- Grant privileges on the public schema (for existing and future tables)
GRANT ALL ON SCHEMA public TO takehome1;

