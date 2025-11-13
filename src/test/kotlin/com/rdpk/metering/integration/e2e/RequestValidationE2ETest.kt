package com.rdpk.metering.integration.e2e

import com.rdpk.metering.integration.AbstractKotestIntegrationTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

/**
 * E2E tests for request validation (Bean Validation constraints)
 * Tests that @NotNull, @NotBlank, and @Valid annotations are properly enforced
 * Uses WebTestClient for HTTP testing
 */
@AutoConfigureWebTestClient
class RequestValidationE2ETest : AbstractKotestIntegrationTest() {

    @Autowired
    lateinit var webTestClient: WebTestClient

    init {
        describe("Request Validation (Bean Validation Constraints)") {

            it("should reject request with missing eventId") {
                val invalidJson = """
                    {
                        "timestamp": "2024-01-01T00:00:00Z",
                        "tenantId": "1",
                        "customerId": "customer-1",
                        "apiEndpoint": "/api/completion",
                        "metadata": {}
                    }
                """.trimIndent()

                webTestClient.post()
                    .uri("/api/v1/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(invalidJson)
                    .exchange()
                    .expectStatus().isBadRequest
                    .expectBody()
                    .jsonPath("$.code").isEqualTo("VALIDATION_ERROR")
                    .jsonPath("$.message").exists()
            }

            it("should reject request with blank eventId") {
                val invalidJson = """
                    {
                        "eventId": "",
                        "timestamp": "2024-01-01T00:00:00Z",
                        "tenantId": "1",
                        "customerId": "customer-1",
                        "apiEndpoint": "/api/completion",
                        "metadata": {}
                    }
                """.trimIndent()

                webTestClient.post()
                    .uri("/api/v1/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(invalidJson)
                    .exchange()
                    .expectStatus().isBadRequest
                    .expectBody()
                    .jsonPath("$.code").isEqualTo("VALIDATION_ERROR")
            }

            it("should reject request with missing timestamp") {
                val invalidJson = """
                    {
                        "eventId": "test-event-1",
                        "tenantId": "1",
                        "customerId": "customer-1",
                        "apiEndpoint": "/api/completion",
                        "metadata": {}
                    }
                """.trimIndent()

                webTestClient.post()
                    .uri("/api/v1/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(invalidJson)
                    .exchange()
                    .expectStatus().isBadRequest
                    .expectBody()
                    .jsonPath("$.code").isEqualTo("VALIDATION_ERROR")
            }

            it("should reject request with missing tenantId") {
                val invalidJson = """
                    {
                        "eventId": "test-event-1",
                        "timestamp": "2024-01-01T00:00:00Z",
                        "customerId": "customer-1",
                        "apiEndpoint": "/api/completion",
                        "metadata": {}
                    }
                """.trimIndent()

                webTestClient.post()
                    .uri("/api/v1/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(invalidJson)
                    .exchange()
                    .expectStatus().isBadRequest
                    .expectBody()
                    .jsonPath("$.code").isEqualTo("VALIDATION_ERROR")
            }

            it("should reject request with blank tenantId") {
                val invalidJson = """
                    {
                        "eventId": "test-event-1",
                        "timestamp": "2024-01-01T00:00:00Z",
                        "tenantId": "",
                        "customerId": "customer-1",
                        "apiEndpoint": "/api/completion",
                        "metadata": {}
                    }
                """.trimIndent()

                webTestClient.post()
                    .uri("/api/v1/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(invalidJson)
                    .exchange()
                    .expectStatus().isBadRequest
                    .expectBody()
                    .jsonPath("$.code").isEqualTo("VALIDATION_ERROR")
            }

            it("should reject request with missing customerId") {
                val invalidJson = """
                    {
                        "eventId": "test-event-1",
                        "timestamp": "2024-01-01T00:00:00Z",
                        "tenantId": "1",
                        "apiEndpoint": "/api/completion",
                        "metadata": {}
                    }
                """.trimIndent()

                webTestClient.post()
                    .uri("/api/v1/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(invalidJson)
                    .exchange()
                    .expectStatus().isBadRequest
                    .expectBody()
                    .jsonPath("$.code").isEqualTo("VALIDATION_ERROR")
            }

            it("should reject request with blank customerId") {
                val invalidJson = """
                    {
                        "eventId": "test-event-1",
                        "timestamp": "2024-01-01T00:00:00Z",
                        "tenantId": "1",
                        "customerId": "",
                        "apiEndpoint": "/api/completion",
                        "metadata": {}
                    }
                """.trimIndent()

                webTestClient.post()
                    .uri("/api/v1/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(invalidJson)
                    .exchange()
                    .expectStatus().isBadRequest
                    .expectBody()
                    .jsonPath("$.code").isEqualTo("VALIDATION_ERROR")
            }

            it("should reject request with missing apiEndpoint") {
                val invalidJson = """
                    {
                        "eventId": "test-event-1",
                        "timestamp": "2024-01-01T00:00:00Z",
                        "tenantId": "1",
                        "customerId": "customer-1",
                        "metadata": {}
                    }
                """.trimIndent()

                webTestClient.post()
                    .uri("/api/v1/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(invalidJson)
                    .exchange()
                    .expectStatus().isBadRequest
                    .expectBody()
                    .jsonPath("$.code").isEqualTo("VALIDATION_ERROR")
            }

            it("should reject request with blank apiEndpoint") {
                val invalidJson = """
                    {
                        "eventId": "test-event-1",
                        "timestamp": "2024-01-01T00:00:00Z",
                        "tenantId": "1",
                        "customerId": "customer-1",
                        "apiEndpoint": "",
                        "metadata": {}
                    }
                """.trimIndent()

                webTestClient.post()
                    .uri("/api/v1/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(invalidJson)
                    .exchange()
                    .expectStatus().isBadRequest
                    .expectBody()
                    .jsonPath("$.code").isEqualTo("VALIDATION_ERROR")
            }

            it("should reject request with missing metadata") {
                val invalidJson = """
                    {
                        "eventId": "test-event-1",
                        "timestamp": "2024-01-01T00:00:00Z",
                        "tenantId": "1",
                        "customerId": "customer-1",
                        "apiEndpoint": "/api/completion"
                    }
                """.trimIndent()

                webTestClient.post()
                    .uri("/api/v1/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(invalidJson)
                    .exchange()
                    .expectStatus().isBadRequest
                    .expectBody()
                    .jsonPath("$.code").isEqualTo("VALIDATION_ERROR")
            }

            it("should reject request with null metadata") {
                val invalidJson = """
                    {
                        "eventId": "test-event-1",
                        "timestamp": "2024-01-01T00:00:00Z",
                        "tenantId": "1",
                        "customerId": "customer-1",
                        "apiEndpoint": "/api/completion",
                        "metadata": null
                    }
                """.trimIndent()

                webTestClient.post()
                    .uri("/api/v1/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(invalidJson)
                    .exchange()
                    .expectStatus().isBadRequest
                    .expectBody()
                    .jsonPath("$.code").isEqualTo("VALIDATION_ERROR")
            }

            it("should reject request with malformed JSON") {
                val invalidJson = """
                    {
                        "eventId": "test-event-1",
                        "timestamp": "2024-01-01T00:00:00Z",
                        "tenantId": "1",
                        "customerId": "customer-1",
                        "apiEndpoint": "/api/completion",
                        "metadata": {}
                """.trimIndent() // Missing closing brace

                webTestClient.post()
                    .uri("/api/v1/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(invalidJson)
                    .exchange()
                    .expectStatus().isBadRequest
            }

            it("should reject request with invalid timestamp format") {
                val invalidJson = """
                    {
                        "eventId": "test-event-1",
                        "timestamp": "invalid-date",
                        "tenantId": "1",
                        "customerId": "customer-1",
                        "apiEndpoint": "/api/completion",
                        "metadata": {}
                    }
                """.trimIndent()

                webTestClient.post()
                    .uri("/api/v1/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(invalidJson)
                    .exchange()
                    .expectStatus().isBadRequest
            }

            it("should include validation error details in response") {
                val invalidJson = """
                    {
                        "eventId": "",
                        "timestamp": "2024-01-01T00:00:00Z",
                        "tenantId": "",
                        "customerId": "customer-1",
                        "apiEndpoint": "/api/completion",
                        "metadata": {}
                    }
                """.trimIndent()

                webTestClient.post()
                    .uri("/api/v1/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(invalidJson)
                    .exchange()
                    .expectStatus().isBadRequest
                    .expectBody()
                    .jsonPath("$.code").isEqualTo("VALIDATION_ERROR")
                    .jsonPath("$.errors").isArray
                    .jsonPath("$.errors[0]").exists()
            }
        }
    }
}

