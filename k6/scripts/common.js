import http from 'k6/http';
import { check } from 'k6';

// Base URL for API
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const API_BASE = `${BASE_URL}/api/v1/events`;

// Tenant and Customer mapping based on seeded data
// Tenant 1 (Acme Corporation): active, has 3 customers
// Tenant 2 (TechStart Inc): active, has 2 customers
// Tenant 3 (Global Services Ltd): inactive, avoid in tests
const TENANT_CUSTOMERS = {
  '1': ['acme-customer-001', 'acme-customer-002', 'acme-customer-003'],
  '2': ['techstart-customer-001', 'techstart-customer-002']
};

// API endpoint pools for realistic testing
const API_ENDPOINTS = [
  '/api/completion',
  '/api/chat',
  '/api/embedding',
  '/api/transcription',
  '/api/translation',
  '/api/image-generation',
  '/api/audio',
  '/api/vision'
];

// Model pools for realistic testing
const MODELS = [
  'gpt-4',
  'gpt-4-turbo',
  'gpt-3.5-turbo',
  'claude-3-opus',
  'claude-3-sonnet',
  'claude-3-haiku',
  'text-embedding-ada-002',
  'whisper-1',
  'dall-e-3'
];

// Counter for unique event IDs (increments per VU, resets per test run)
let eventIdCounter = 0;

/**
 * Generate a unique event ID
 * Uses timestamp + VU ID + iteration + counter + high-precision random for uniqueness
 * This ensures no collisions even with high concurrency across multiple VUs
 */
export function generateEventId() {
  const timestamp = Date.now();
  const counter = ++eventIdCounter;
  // Use multiple random components for better entropy
  const random1 = Math.floor(Math.random() * 1000000);
  const random2 = Math.floor(Math.random() * 1000000);
  const vuId = __VU || 0; // VU ID from k6 (1, 2, 3, ...)
  const iteration = __ITER || 0; // Iteration number from k6
  // Use performance.now() if available for microsecond precision, otherwise use additional random
  let microTime = '';
  try {
    if (typeof performance !== 'undefined' && performance.now) {
      microTime = '-' + Math.floor(performance.now() * 1000);
    }
  } catch (e) {
    // performance.now() not available, use extra random instead
    microTime = '-' + Math.floor(Math.random() * 1000000);
  }
  // Combine all components to ensure uniqueness across VUs and time
  return `event-${timestamp}${microTime}-${vuId}-${iteration}-${counter}-${random1}-${random2}`;
}

/**
 * Get a random tenant ID (only active tenants: "1" or "2")
 */
export function randomTenantId() {
  const tenants = ['1', '2'];
  return tenants[Math.floor(Math.random() * tenants.length)];
}

/**
 * Get a random customer external_id for the given tenant
 */
export function randomCustomerId(tenantId) {
  const customers = TENANT_CUSTOMERS[tenantId];
  if (!customers || customers.length === 0) {
    throw new Error(`No customers found for tenant ${tenantId}`);
  }
  return customers[Math.floor(Math.random() * customers.length)];
}

/**
 * Get a random API endpoint
 */
export function randomApiEndpoint() {
  return API_ENDPOINTS[Math.floor(Math.random() * API_ENDPOINTS.length)];
}

/**
 * Get a random model name
 */
export function randomModel() {
  return MODELS[Math.floor(Math.random() * MODELS.length)];
}

/**
 * Generate realistic token counts
 * Returns { inputTokens, outputTokens, tokens }
 */
export function randomTokenCounts() {
  // Realistic ranges:
  // Input: 100-10000 tokens
  // Output: 50-5000 tokens
  const inputTokens = Math.floor(Math.random() * 9900) + 100;
  const outputTokens = Math.floor(Math.random() * 4950) + 50;
  const tokens = inputTokens + outputTokens;
  return { inputTokens, outputTokens, tokens };
}

/**
 * Generate a random latency in milliseconds (50-500ms)
 */
export function randomLatencyMs() {
  return Math.floor(Math.random() * 450) + 50;
}

/**
 * Generate ISO-8601 timestamp string
 * @param {number} offsetMinutes - Optional offset in minutes (negative for past, positive for future)
 */
export function generateTimestamp(offsetMinutes = 0) {
  const now = new Date();
  if (offsetMinutes !== 0) {
    now.setMinutes(now.getMinutes() + offsetMinutes);
  }
  return now.toISOString();
}

/**
 * Generate a complete event payload
 * @param {Object} options - Optional overrides
 * @param {string} options.tenantId - Override tenant ID
 * @param {string} options.customerId - Override customer ID
 * @param {string} options.apiEndpoint - Override API endpoint
 * @param {string} options.model - Override model
 * @param {number} options.inputTokens - Override input tokens
 * @param {number} options.outputTokens - Override output tokens
 * @param {number} options.latencyMs - Override latency
 * @param {number} options.timestampOffsetMinutes - Offset timestamp (negative for past events)
 */
export function generateEventPayload(options = {}) {
  const tenantId = options.tenantId || randomTenantId();
  const customerId = options.customerId || randomCustomerId(tenantId);
  const apiEndpoint = options.apiEndpoint || randomApiEndpoint();
  const model = options.model || randomModel();
  const tokenCounts = randomTokenCounts();
  const inputTokens = options.inputTokens !== undefined ? options.inputTokens : tokenCounts.inputTokens;
  const outputTokens = options.outputTokens !== undefined ? options.outputTokens : tokenCounts.outputTokens;
  const latencyMs = options.latencyMs !== undefined ? options.latencyMs : randomLatencyMs();
  const timestamp = generateTimestamp(options.timestampOffsetMinutes);
  
  return {
    eventId: generateEventId(),
    timestamp: timestamp,
    tenantId: tenantId,
    customerId: customerId,
    apiEndpoint: apiEndpoint,
    metadata: {
      inputTokens: inputTokens,
      outputTokens: outputTokens,
      tokens: inputTokens + outputTokens,
      model: model,
      latencyMs: latencyMs
    }
  };
}

/**
 * Ingest a single event via POST /api/v1/events
 * @param {Object} payload - Event payload (from generateEventPayload)
 * @param {Object} tags - Optional k6 tags for metrics
 * @returns {Object} Response object
 */
export function ingestEvent(payload, tags = {}) {
  const jsonPayload = JSON.stringify(payload);
  
  const params = {
    headers: { 'Content-Type': 'application/json' },
    tags: { name: 'IngestEvent', ...tags }
  };
  
  const response = http.post(API_BASE, jsonPayload, params);
  
  return response;
}

/**
 * Ingest an event with validation checks
 * @param {Object} payload - Event payload (from generateEventPayload)
 * @param {Object} tags - Optional k6 tags for metrics
 * @returns {Object} Response object with validation results
 */
export function ingestEventWithValidation(payload, tags = {}) {
  const response = ingestEvent(payload, tags);
  
  const success = check(response, {
    'ingest event status is 201': (r) => r.status === 201,
    'ingest event has eventId in response': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.eventId !== undefined && body.eventId === payload.eventId;
      } catch (e) {
        return false;
      }
    },
    'ingest event has status in response': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.status !== undefined && body.status === 'PROCESSED';
      } catch (e) {
        return false;
      }
    },
    'ingest event has processedAt in response': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.processedAt !== undefined;
      } catch (e) {
        return false;
      }
    }
  });
  
  return { response, success };
}
