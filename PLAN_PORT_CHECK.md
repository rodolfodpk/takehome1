# Plan: Check All Ports for Conflicts

## Ports Used by the Application

### Application Ports
- **8080**: Main application (single instance) or nginx load balancer (multi-instance)
- **8081**: App instance 1 (multi-instance setup)
- **8082**: App instance 2 (multi-instance setup)

### Database & Cache Ports
- **5432**: PostgreSQL
- **6379**: Redis

### Monitoring Ports
- **3000**: Grafana
- **9090**: Prometheus

## Plan Steps

1. **Identify all ports used:**
   - Extract ports from:
     - `docker-compose.yml`
     - `docker-compose.multi.yml`
     - `application.properties`
     - `application-k6.properties`
     - `nginx.conf`
     - `Makefile` (if any hardcoded ports)

2. **Check each port for conflicts:**
   - Use `lsof -i :PORT` to check if port is in use
   - Use `netstat -an | grep PORT` as alternative
   - Check for both listening and established connections

3. **Report findings:**
   - List all ports checked
   - Show which ports are free
   - Show which ports are in use (with process info)
   - Identify conflicts

4. **Provide recommendations:**
   - If conflicts found, suggest solutions:
     - Kill conflicting processes
     - Change port configuration
     - Use environment variables to override ports

## Implementation

### Step 1: Extract Port List
Create a list of all ports to check:
- 8080, 8081, 8082 (application)
- 5432 (PostgreSQL)
- 6379 (Redis)
- 3000 (Grafana)
- 9090 (Prometheus)

### Step 2: Check Each Port
For each port, run:
```bash
lsof -i :PORT
# or
netstat -an | grep :PORT
```

### Step 3: Generate Report
Create a summary showing:
- Port number
- Status (FREE or IN USE)
- Process using the port (if in use)
- PID of process (if in use)

### Step 4: Provide Solutions
If conflicts are found:
- Show command to kill process: `kill -9 PID`
- Show how to change port via environment variable
- Document port configuration in relevant files

## Implementation Status

✅ **Completed:**
- Port list identified (7 ports total)
- Script created to check all ports
- All ports verified as FREE

⚠️ **Recommended Improvements:**
1. Add `make check-ports` target to Makefile for easy access
2. Create permanent script in `scripts/` directory
3. Add to troubleshooting documentation
4. Integrate with `make start` to warn about conflicts before starting

