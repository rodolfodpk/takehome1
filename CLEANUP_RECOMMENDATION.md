# Cleanup Recommendation for Unused Methods

## Strategy: **Pragmatic Cleanup**

### ✅ **REMOVE** (Dead Code / Unsafe)

#### Repository Methods - Remove These:
1. **`UsageEventRepository.findByCustomerId()`** ❌
   - **Reason**: Unsafe (no tenant filter), documented as such in tests
   - **Risk**: Security issue if accidentally used

2. **`LateEventRepository` unused methods** ❌
   - `findByEventId()` - Not used anywhere
   - `findByTenantIdAndCustomerId()` - Not used
   - `findByTenantIdAndOriginalTimestampBetween()` - Not used
   - `findByTenantIdAndCustomerIdAndOriginalTimestampBetween()` - Not used
   - **Reason**: Late events are processed via `findAll()` in batches, no need for these queries

3. **`TenantRepository.findByName()`** ❌
   - **Reason**: Not required, not used, not in requirements

4. **`DistributedLockService.isLocked()`** ❌
   - **Reason**: Not used, `withLock()` is sufficient

#### AggregationWindowRepository - Remove These:
5. **`findByCustomerIdAndWindowStartBetween()`** ❌
   - **Reason**: Unsafe (no tenant filter), not in requirements

### ⚠️ **KEEP** (Useful for Tests / Future APIs)

#### Repository Methods - Keep These:
1. **`UsageEventRepository.findByEventId()`** ✅
   - **Reason**: Used in E2E tests, useful for debugging, might be needed for future "get event by ID" API

2. **`UsageEventRepository.findByTenantIdAndTimestampBetween()`** ✅
   - **Reason**: Used in tests, might be useful for tenant-level reporting API

3. **`UsageEventRepository.findByTenantIdAndCustomerId()`** ✅
   - **Reason**: Used in tests, useful for customer-level queries

4. **`AggregationWindowRepository.findByTenantIdAndWindowStartBetween()`** ✅
   - **Reason**: Requirements mention "finding aggregation windows for reporting" - this supports tenant-level reporting

5. **`AggregationWindowRepository.findByTenantIdAndCustomerIdAndWindowStartBetween()`** ✅
   - **Reason**: Requirements mention "finding aggregation windows for reporting" - this supports customer-level reporting

6. **`AggregationWindowRepository.findByTenantIdAndCustomerId()`** ✅
   - **Reason**: Used in tests, useful for listing all windows for a customer

### ✅ **KEEP** (Domain Helpers)

All domain helper methods (`withId()`, `withUpdated()`, `withName()`, `deactivate()`, `activate()`):
- **Reason**: Useful for tests, don't add maintenance burden, improve test readability
- **Cost**: Minimal (just convenience methods)

## Summary

**Remove**: 6 methods (unsafe or completely unused)
**Keep**: 6 methods (useful for tests/future APIs)
**Keep**: All domain helpers (test convenience)

## Impact

- **Reduces**: Dead code, security risks (unsafe queries)
- **Maintains**: Testability, future API flexibility
- **Balance**: Clean codebase without over-engineering

