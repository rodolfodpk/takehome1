package com.rdpk.metering.integration

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

/**
 * Singleton Testcontainers for reuse across all tests
 * Prevents starting new containers for each test class
 */
object SharedTestContainers {

    val postgresContainer: PostgreSQLContainer<*> = PostgreSQLContainer(
        DockerImageName.parse("postgres:17.2")
    )
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test")
        .withReuse(true)
        .waitingFor(Wait.forListeningPort())

    val redisContainer: GenericContainer<*> = GenericContainer(
        DockerImageName.parse("redis:7-alpine")
    )
        .withExposedPorts(6379)
        .withReuse(true)
        .waitingFor(Wait.forLogMessage(".*Ready to accept connections.*", 1))

    init {
        // Start containers once
        if (!postgresContainer.isRunning) {
            postgresContainer.start()
        }
        if (!redisContainer.isRunning) {
            redisContainer.start()
        }
    }
}

