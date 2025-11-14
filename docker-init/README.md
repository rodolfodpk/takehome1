# Docker Initialization Scripts

This directory contains initialization scripts that run automatically when PostgreSQL is first initialized (empty volume).

## How It Works

PostgreSQL Docker image automatically executes scripts in `/docker-entrypoint-initdb.d/` when:
- The data volume is **empty** (first initialization)
- Scripts run in alphabetical order
- Only runs once per volume initialization

## Scripts

### `01-create-user.sql`
Creates the `takehome1` user and grants necessary permissions:
- Creates user if it doesn't exist
- Grants all privileges on the database
- Grants privileges on the public schema

## Important Notes

⚠️ **These scripts only run on first initialization!**

If you have an existing PostgreSQL volume:
- These scripts **will NOT run**
- The user may not exist
- Use `make start` which includes a check to create the user if missing
- Or manually create: `docker-compose exec postgres psql -U postgres -c "CREATE USER takehome1 WITH PASSWORD 'takehome1';"`

## For Fresh Volumes

When starting with a fresh volume (e.g., `docker-compose down -v` then `docker-compose up`):
- Scripts run automatically
- User is created automatically
- No manual intervention needed

## For Existing Volumes

The `make start` command includes a check that:
1. Verifies PostgreSQL is ready
2. Checks if `takehome1` user exists
3. Creates user if missing
4. Grants necessary permissions

This ensures compatibility with both fresh and existing volumes.

