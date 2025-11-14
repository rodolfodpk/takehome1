# Plan: Fix PostgreSQL User "takehome1" Does Not Exist Error + Health Endpoint Details

## Problem Analysis

**Error:** `FATAL: role "takehome1" does not exist`

**Root Cause:**
PostgreSQL Docker containers only create the user specified in `POSTGRES_USER` when the data volume is **first initialized** (empty volume). If a volume already exists from a previous run:
- PostgreSQL reuses the existing data
- The user specified in `POSTGRES_USER` is **NOT** automatically created
- This causes connection failures when the app tries to connect

**When This Happens:**
1. Volume was created with different `POSTGRES_USER` value
2. Volume was created without `POSTGRES_USER` (uses default `postgres` user)
3. Volume was manually created or imported
4. Previous run had different environment variables

## Current Configuration

**docker-compose.yml:**
```yaml
postgres:
  environment:
    POSTGRES_USER: ${POSTGRES_USER:-takehome1}
    POSTGRES_DB: ${POSTGRES_DB:-takehome1}
    POSTGRES_PASSWORD: ${POSTGRES_PASSWORD:-takehome1}
  volumes:
    - postgres_data:/var/lib/postgresql/data
```

**application.properties:**
```properties
spring.r2dbc.username=${SPRING_R2DBC_USERNAME:takehome1}
spring.flyway.user=${SPRING_FLYWAY_USER:takehome1}
```

## Solution Options

### Option 1: Verify and Create User (Recommended)
**Approach:** Check if user exists before app starts, create if missing

**Implementation:**
1. Add pre-start check in `make start` target
2. Use `psql` to check if user exists
3. Create user if missing
4. Grant necessary permissions

**Pros:**
- Works with existing volumes
- Doesn't require volume deletion
- Preserves existing data

**Cons:**
- Requires PostgreSQL to be running first
- Adds complexity to startup

### Option 2: Always Use Fresh Volume
**Approach:** Remove volume before starting (like `make start-fresh`)

**Implementation:**
- Modify `make start` to always remove volumes
- Or create `make start-fresh` that removes volumes

**Pros:**
- Simple solution
- Guarantees clean state

**Cons:**
- Loses all data on every start
- Not suitable for development with existing data

### Option 3: Use Default PostgreSQL User
**Approach:** Change app to use `postgres` superuser instead of `takehome1`

**Implementation:**
- Change `POSTGRES_USER` to `postgres` (default)
- Update application.properties to use `postgres`

**Pros:**
- Simplest solution
- Always works

**Cons:**
- Security concern (using superuser)
- Not best practice

## Recommended Solution: Option 1

**Plan:**
1. Modify `make start` to:
   - Start PostgreSQL container
   - Wait for PostgreSQL to be ready
   - Check if `takehome1` user exists
   - Create user if missing (with proper permissions)
   - Then start the application

2. Apply same logic to:
   - `make start-k6`
   - `make start-multi` (if needed)

3. Create helper script or inline logic in Makefile

## Implementation Steps

1. **Add user verification to Makefile:**
   ```makefile
   start: ## Start application with full observability stack
       docker-compose up -d postgres redis prometheus grafana
       @echo "Waiting for PostgreSQL to be ready..."
       @sleep 5
       @echo "Checking if PostgreSQL user 'takehome1' exists..."
       @docker-compose exec -T postgres psql -U postgres -tc "SELECT 1 FROM pg_roles WHERE rolname='takehome1'" | grep -q 1 || \
           (echo "Creating user 'takehome1'..." && \
            docker-compose exec -T postgres psql -U postgres -c "CREATE USER takehome1 WITH PASSWORD 'takehome1';" && \
            docker-compose exec -T postgres psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE takehome1 TO takehome1;")
       @echo "Starting application..."
       mvn spring-boot:run
   ```

2. **Alternative: Create dedicated script**
   - `scripts/ensure-postgres-user.sh`
   - Called from Makefile targets

3. **Document the fix:**
   - Add to troubleshooting section
   - Explain when this happens
   - Provide manual fix commands

## Additional Requirement: Health Endpoint Details

**Requirement:** The health endpoint (`/actuator/health`) should report detailed status for all components (Redis, PostgreSQL, etc.), not just a generic "UP" status.

**Current Configuration:**
```properties
management.endpoint.health.show-details=when-authorized
```

**Problem:**
- Health endpoint only shows `{"status": "UP"}` without component details
- No visibility into individual component health (Redis, PostgreSQL, R2DBC, etc.)

**Solution:**
Change `show-details` to `always` in both:
- `src/main/resources/application.properties`
- `src/main/resources/application-k6.properties`

**Implementation:**
```properties
management.endpoint.health.show-details=always
```

**Expected Result:**
```json
{
  "status": "UP",
  "components": {
    "r2dbc": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "redis": {
      "status": "UP",
      "details": {
        "version": "7.x"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 500000000000,
        "free": 250000000000,
        "threshold": 10485760
      }
    }
  }
}
```

**Files to Update:**
1. `src/main/resources/application.properties`
2. `src/main/resources/application-k6.properties`

## Manual Fix (For Immediate Resolution)

If user needs immediate fix:

```bash
# Start PostgreSQL
docker-compose up -d postgres
sleep 5

# Create user manually
docker-compose exec postgres psql -U postgres -c "CREATE USER takehome1 WITH PASSWORD 'takehome1';"
docker-compose exec postgres psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE takehome1 TO takehome1;"

# Or remove volume and start fresh
docker-compose down -v
docker-compose up -d postgres
```

