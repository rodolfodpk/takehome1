import { check } from 'k6';
import { generateEventPayload, ingestEventWithValidation } from './common.js';

/**
 * Stress Test
 * 
 * Purpose: Find system breaking point
 * Duration: 3 minutes (faster ramp)
 * VUs: Ramp from 50 → 200 → 500
 * 
 * This test validates:
 * - Maximum sustainable throughput
 * - Point where errors start increasing
 * - Circuit breaker activation thresholds
 * - System behavior under extreme load
 */
export const options = {
  stages: [
    { duration: '30s', target: 50 },   // Ramp up to 50 VUs
    { duration: '30s', target: 200 },  // Gradually increase to 200 VUs
    { duration: '30s', target: 500 },   // Push to 500 VUs
    { duration: '1m30s', target: 500 }, // Hold at 500 VUs
  ],
  thresholds: {
    http_req_duration: ['p(95)<2000', 'p(99)<5000'],
    http_req_failed: ['rate<0.05'],  // Allow up to 5% failures under stress
  },
};

export default function () {
  // Generate a valid event payload
  const payload = generateEventPayload();
  
  // Ingest event with validation
  const { response, success } = ingestEventWithValidation(payload);
  
  // Additional checks (more lenient for stress test)
  check(response, {
    'stress: response time < 2000ms': (r) => r.timings.duration < 2000,
    'stress: response has body': (r) => r.body && r.body.length > 0,
  });
}
