import { check } from 'k6';
import { generateEventPayload, ingestEventWithValidation } from './common.js';

/**
 * Smoke Test
 * 
 * Purpose: Validate basic functionality with minimal load
 * Duration: 1 minute (30s ramp-up, 30s steady)
 * VUs: 10
 * 
 * This test validates:
 * - Basic event ingestion works correctly
 * - Response structure is correct
 * - System handles steady low load
 * - Latency is acceptable under minimal load
 */
export const options = {
  stages: [
    { duration: '30s', target: 10 },  // Ramp up to 10 VUs
    { duration: '30s', target: 10 },  // Stay at 10 VUs
  ],
  thresholds: {
    http_req_duration: ['p(95)<200', 'p(99)<500'],
    http_req_failed: ['rate<0.01'],  // Less than 1% failures
  },
};

export default function () {
  // Generate a valid event payload
  const payload = generateEventPayload();
  
  // Ingest event with validation
  const { response, success } = ingestEventWithValidation(payload);
  
  // Additional checks
  check(response, {
    'smoke: response time < 200ms': (r) => r.timings.duration < 200,
    'smoke: response has body': (r) => r.body && r.body.length > 0,
  });
}
