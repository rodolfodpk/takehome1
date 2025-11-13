package com.rdpk.metering.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import reactor.core.publisher.Mono

/**
 * Global exception handler for REST API
 * Provides consistent error responses
 */
@RestControllerAdvice
class GlobalExceptionHandler {
    
    private val log = LoggerFactory.getLogger(javaClass)
    
    @ExceptionHandler(WebExchangeBindException::class)
    fun handleValidationException(ex: WebExchangeBindException): Mono<ResponseEntity<ErrorResponse>> {
        val errors = ex.bindingResult.fieldErrors.map { error ->
            "${error.field}: ${error.defaultMessage}"
        }
        
        log.warn("Validation error: {}", errors)
        
        return Mono.just(
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse("VALIDATION_ERROR", "Invalid request data", errors))
        )
    }
    
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): Mono<ResponseEntity<ErrorResponse>> {
        log.warn("Invalid argument: {}", ex.message)
        
        return Mono.just(
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse("INVALID_ARGUMENT", ex.message ?: "Invalid argument", emptyList()))
        )
    }
    
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): Mono<ResponseEntity<ErrorResponse>> {
        log.error("Unexpected error", ex)
        
        return Mono.just(
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred", emptyList()))
        )
    }
    
    data class ErrorResponse(
        val code: String,
        val message: String,
        val details: List<String> = emptyList()
    )
}

