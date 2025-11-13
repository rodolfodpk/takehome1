package com.rdpk.metering.repository

import com.rdpk.metering.domain.LateEvent
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface LateEventRepository : ReactiveCrudRepository<LateEvent, Long> {
    // Late events are processed via findAll() in batches - no query methods needed
}

