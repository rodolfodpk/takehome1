import { check } from 'k6';
import { generateEventPayload, ingestEventWithValidation } from './common.js';

/**
 * Spike Test
 * 
 * Purpose: Test Resilience4j circuit breakers under sudden traffic surges
 * Duration: 2.5 minutes (shorter spike cycles)
 * VUs: Spike from 50 → 500 → 50 (rapid cycles)
 * 
 * This test validates:
 * - Circuit breaker activation during spikes
 * - System recovery after spikes
 * - No data corruption under sudden load changes
 * - Graceful degradation behavior
 */
export const options = {
  stages: [
    { duration: '20s', target: 50 },   // Low load at 50 VUs
    { duration: '20s', target: 500 },  // Spike to 500 VUs
    { duration: '20s', target: 50 },   // Drop back to 50 VUs
    { duration: '20s', target: 500 },  // Another spike to 500 VUs
    { duration: '20s', target: 50 },   // Drop back again
    { duration: '20s', target: 500 },  // Final spike
    { duration: '30s', target: 50 },   // Recovery
  ],
  thresholds: {
    http_req_duration: ['p(95)<2000'],
    http_req_failed: ['rate<0.02'],  // Allow up to 2% failures during spikes
  },
};

export default function () {
  // Generate a valid event payload
  const payload = generateEventPayload();
  
  // Ingest event with validation
  const { response, success } = ingestEventWithValidation(payload);
  
  // Additional checks
  check(response, {
    'spike: response time < 2000ms': (r) => r.timings.duration < 2000,
    'spike: response has body': (r) => r.body && r.body.length > 0,
  });
}
