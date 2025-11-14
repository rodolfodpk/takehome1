import { check } from 'k6';
import { generateEventPayload, ingestEventWithValidation } from './common.js';

/**
 * Warm-up Test
 * 
 * Purpose: Quick validation of happy path - ensure basic API works
 * Duration: 10 seconds (5s ramp-up, 5s steady)
 * VUs: 2
 * 
 * This test runs quickly to catch basic issues before longer tests:
 * - API endpoint is correct
 * - Payload format is valid
 * - Tenant/customer lookup works
 * - Basic response structure is correct
 */
export const options = {
  stages: [
    { duration: '5s', target: 2 },  // Ramp up to 2 VUs
    { duration: '5s', target: 2 },  // Stay at 2 VUs
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'],  // 95% of requests should complete in < 500ms
    http_req_failed: ['rate<0.01'],     // Less than 1% failures
  },
};

export default function () {
  // Generate a valid event payload
  const payload = generateEventPayload();
  
  // Ingest event with validation
  const { response, success } = ingestEventWithValidation(payload);
  
  // Additional checks
  check(response, {
    'warmup: response time < 500ms': (r) => r.timings.duration < 500,
    'warmup: response has body': (r) => r.body && r.body.length > 0,
  });
}

