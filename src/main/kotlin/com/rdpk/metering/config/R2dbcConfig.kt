package com.rdpk.metering.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.dialect.PostgresDialect
import java.util.*

/**
 * R2DBC configuration for custom type conversions
 * Handles Map<String, Any> <-> JSONB conversion for PostgreSQL
 * 
 * Based on Spring Data R2DBC documentation:
 * https://docs.spring.io/spring-data/relational/reference/r2dbc/mapping.html
 * 
 * Property-level converters are applied to individual properties.
 * We use reflection to work with io.r2dbc.postgresql.codec.Json since it's not directly importable.
 * 
 * Note: Spring's converter resolution requires exact type matching. Since we can't import
 * the Json class directly, we use Converter<Any, Map<String, Any>> and verify the type at runtime.
 * However, Spring may not match this converter. If this doesn't work, consider using
 * custom repository methods with explicit SQL casting (see UsageEventRepositoryCustom).
 */
@Configuration
class R2dbcConfig {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    /**
     * Writing converter: Map<String, Any> -> Json
     * Converts Kotlin Map to PostgreSQL Json type for JSONB columns
     * Uses reflection to create Json.of() since Json class is not directly importable
     */
    @WritingConverter
    class MapToJsonConverter(private val objectMapper: ObjectMapper) : Converter<Map<String, Any>, Any> {
        override fun convert(source: Map<String, Any>): Any {
            val jsonString = objectMapper.writeValueAsString(source)
            // Use reflection to create Json.of() - this is the proper way per r2dbc-postgresql
            return try {
                val jsonClass = Class.forName("io.r2dbc.postgresql.codec.Json")
                val ofMethod = jsonClass.getMethod("of", String::class.java)
                ofMethod.invoke(null, jsonString)!!
            } catch (e: Exception) {
                throw IllegalStateException("Failed to create Json type for JSONB conversion", e)
            }
        }
    }

    /**
     * Reading converter: String -> Map<String, Any>
     * Converts JSON string (from JSONB column cast to text) to Kotlin Map
     * 
     * We use @Query with "metadata::text" to read JSONB as text, then this converter
     * parses it back to Map. This avoids the Json type matching issue.
     */
    @ReadingConverter
    class StringToMapConverter(private val objectMapper: ObjectMapper) : Converter<String, Map<String, Any>> {
        @Suppress("UNCHECKED_CAST")
        override fun convert(source: String): Map<String, Any> {
            if (source.isBlank()) return emptyMap()
            return objectMapper.readValue(source, Map::class.java) as Map<String, Any>
        }
    }

    /**
     * Register custom converters with Spring Data R2DBC
     * 
     * Per Spring Data R2DBC documentation:
     * https://docs.spring.io/spring-data/relational/reference/r2dbc/mapping.html
     * 
     * Property-level converters are applied to singular properties.
     */
    @Bean
    fun r2dbcCustomConversions(): R2dbcCustomConversions {
        val converters = ArrayList<Any>().apply {
            // Add our custom property converters
            add(MapToJsonConverter(objectMapper))
            add(StringToMapConverter(objectMapper)) // For reading JSONB as text (via ::text casting)
            // Include PostgresDialect converters (includes JSONB codecs)
            addAll(PostgresDialect.INSTANCE.converters)
        }
        return R2dbcCustomConversions(R2dbcCustomConversions.STORE_CONVERSIONS, converters)
    }
}
