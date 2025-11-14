import { check } from 'k6';
import { generateEventPayload, ingestEventWithValidation } from './common.js';

/**
 * Load Test
 * 
 * Purpose: Simulate normal production load (target: 2k+ events/sec)
 * Duration: 2 minutes (30s ramp-up, 1m30s steady)
 * VUs: 350 (to achieve 2k+ events/sec, more is better)
 * 
 * This test validates:
 * - System can handle production-level load
 * - Latency remains acceptable under steady load
 * - Throughput meets target (2k+ events/sec, more is better)
 * - No memory leaks or degradation over time
 */
export const options = {
  stages: [
    { duration: '30s', target: 350 },   // Ramp up to 350 VUs
    { duration: '1m30s', target: 350 },  // Stay at 350 VUs
  ],
  thresholds: {
    http_req_duration: ['p(95)<500', 'p(99)<1000'],
    http_req_failed: ['rate<0.001'],  // Less than 0.1% failures
    http_reqs: ['rate>2000'],  // Throughput > 2k events/sec (more is better)
  },
};

export default function () {
  // Generate a valid event payload
  const payload = generateEventPayload();
  
  // Ingest event with validation
  const { response, success } = ingestEventWithValidation(payload);
  
  // Additional checks
  check(response, {
    'load: response time < 500ms': (r) => r.timings.duration < 500,
    'load: response has body': (r) => r.body && r.body.length > 0,
  });
}
