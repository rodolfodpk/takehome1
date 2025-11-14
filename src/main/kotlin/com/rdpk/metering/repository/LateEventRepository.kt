package com.rdpk.metering.repository

import com.rdpk.metering.domain.LateEvent
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.Instant

@Repository
interface LateEventRepository : ReactiveCrudRepository<LateEvent, Long> {
    // Late events are processed via findAll() in batches - no query methods needed
    
    /**
     * Save late event with explicit JSONB casting for data field
     * Uses explicit SQL casting - the JSON string must be valid JSON
     * Implementation provided by LateEventRepositoryImpl using DatabaseClient with Json codec
     */
    fun saveWithJsonb(
        eventId: String,
        originalTimestamp: Instant,
        receivedTimestamp: Instant,
        tenantId: Long,
        customerId: Long,
        data: String
    ): Mono<LateEvent>
}

