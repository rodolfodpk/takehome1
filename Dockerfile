# Multi-stage build for production-ready Docker image
# Target: ~275MB image size (optimized from 340MB)
# Optimizations:
# - Spring Boot layered JAR extraction (better caching, same content)
# - Combined RUN commands to reduce layers
# - Minimal base image (eclipse-temurin:21-jre-alpine)
# Note: Base JRE (~162MB) + dependencies (~66MB) + app (~47MB) = ~275MB
# Further reduction would require jlink (custom minimal JRE) or distroless images

# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml and download dependencies (layer caching optimization)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# Extract layered JAR for optimization
RUN java -Djarmode=layertools -jar /app/target/*.jar extract

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

# Install wget for health checks (minimal size)
RUN apk add --no-cache wget && \
    # Create non-root user in same layer
    addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copy Spring Boot layers (only necessary layers, not full JAR)
# Dependencies layer (largest, changes least frequently)
COPY --from=build --chown=appuser:appgroup /app/dependencies/ ./
# Spring Boot loader (small, rarely changes)
COPY --from=build --chown=appuser:appgroup /app/spring-boot-loader/ ./
# Snapshot dependencies (medium, changes occasionally)
COPY --from=build --chown=appuser:appgroup /app/snapshot-dependencies/ ./
# Application layer (small, changes most frequently)
COPY --from=build --chown=appuser:appgroup /app/application/ ./

# Switch to non-root user
USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Expose port
EXPOSE 8080

# Run application with layered JAR structure
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "org.springframework.boot.loader.launch.JarLauncher"]

