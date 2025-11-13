package com.rdpk.metering.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.rdpk.metering.domain.UsageEvent
import com.rdpk.metering.dto.AggregationResult
import com.rdpk.metering.dto.EndpointStats
import com.rdpk.metering.dto.ModelStats
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Instant

/**
 * Service for aggregating usage events into windows
 * Handles multi-dimensional aggregations: totals, by endpoint, by model
 */
@Service
class AggregationService(
    private val redisStateService: RedisStateService,
    private val objectMapper: ObjectMapper
) {
    
    private val log = LoggerFactory.getLogger(javaClass)
    
    /**
     * Aggregate window data from Redis counters and events
     * Returns aggregation result matching requirements format
     */
    fun aggregateWindow(
        tenantId: Long,
        customerId: Long,
        windowStart: Instant,
        windowEnd: Instant,
        events: List<UsageEvent>
    ): Mono<AggregationResult> {
        return redisStateService.getCounters(tenantId, customerId)
            .map { counters ->
                // Aggregate from events for detailed breakdowns
                val endpointStats = aggregateByEndpoint(events)
                val modelStats = aggregateByModel(events)
                
                // Calculate average latency
                val avgLatency = if (events.isNotEmpty()) {
                    events.mapNotNull { it.latencyMs?.toDouble() }.average()
                } else {
                    null
                }
                
                AggregationResult(
                    totalCalls = counters.calls,
                    totalTokens = counters.tokens,
                    totalInputTokens = counters.inputTokens,
                    totalOutputTokens = counters.outputTokens,
                    avgLatencyMs = avgLatency,
                    byEndpoint = endpointStats,
                    byModel = modelStats
                )
            }
    }
    
    private fun aggregateByEndpoint(events: List<UsageEvent>): Map<String, EndpointStats> {
        return events
            .groupBy { it.endpoint }
            .mapValues { (_, eventList) ->
                val totalCalls = eventList.size.toLong()
                val totalTokens = eventList.sumOf { it.tokens?.toLong() ?: 0L }
                EndpointStats(calls = totalCalls, tokens = totalTokens)
            }
    }
    
    private fun aggregateByModel(events: List<UsageEvent>): Map<String, ModelStats> {
        return events
            .filter { it.model != null }
            .groupBy { it.model!! }
            .mapValues { (_, eventList) ->
                val totalCalls = eventList.size.toLong()
                val totalTokens = eventList.sumOf { it.tokens?.toLong() ?: 0L }
                ModelStats(calls = totalCalls, tokens = totalTokens)
            }
    }
    
    /**
     * Serialize aggregation result to JSONB string
     */
    fun serializeAggregationData(result: AggregationResult): String {
        return try {
            objectMapper.writeValueAsString(result)
        } catch (e: Exception) {
            log.error("Error serializing aggregation data", e)
            "{}"
        }
    }
}

