# Unused Methods Analysis

Based on requirements and actual usage in main code:

## Requirements (from requirements.md):
- Finding customers by tenant ✅
- Querying usage events by time range and customer ✅
- Finding aggregation windows for reporting ✅

## Methods NOT Used in Main Code

### Repository Methods (Only in Tests or Unused)

#### UsageEventRepository
- ❌ `findByEventId()` - Only used in tests
- ❌ `findByTenantIdAndTimestampBetween()` - Only used in tests
- ❌ `findByTenantIdAndCustomerId()` - Only used in tests
- ❌ `findByCustomerId()` - Only used in tests, **documented as unsafe** (no tenant filter)

#### AggregationWindowRepository
- ❌ `findByTenantIdAndCustomerId()` - Only used in tests
- ❌ `findByTenantIdAndWindowStartBetween()` - **NOT USED** (not required)
- ❌ `findByCustomerIdAndWindowStartBetween()` - **NOT USED** (not required)
- ❌ `findByTenantIdAndCustomerIdAndWindowStartBetween()` - **NOT USED** (not required)

#### LateEventRepository
- ❌ `findByEventId()` - **NOT USED**
- ❌ `findByTenantIdAndCustomerId()` - **NOT USED**
- ❌ `findByTenantIdAndOriginalTimestampBetween()` - **NOT USED**
- ❌ `findByTenantIdAndCustomerIdAndOriginalTimestampBetween()` - **NOT USED**

#### TenantRepository
- ❌ `findByName()` - **NOT USED** (not required)

### Service Methods
- ❌ `DistributedLockService.isLocked()` - **NOT USED**

### Domain Helper Methods (Only in Tests)
- ❌ `Customer.withUpdated()` - Only in tests
- ❌ `Customer.withName()` - Only in tests
- ❌ `Tenant.withUpdated()` - Only in tests
- ❌ `Tenant.deactivate()` - Only in tests
- ❌ `Tenant.activate()` - Only in tests
- ❌ `UsageEvent.withId()` - Only in tests
- ❌ `AggregationWindow.withId()` - Only in tests
- ❌ `LateEvent.withId()` - Only in tests

## Methods Actually Used in Main Code ✅

### Repository Methods (Required)
- ✅ `tenantRepository.findById()`
- ✅ `tenantRepository.findByActive()`
- ✅ `customerRepository.findByTenantId()`
- ✅ `customerRepository.findByTenantIdAndExternalId()`
- ✅ `usageEventRepository.findByTenantIdAndCustomerIdAndTimestampBetween()`
- ✅ `usageEventRepository.save()`
- ✅ `aggregationWindowRepository.findByTenantIdAndCustomerIdAndWindowStart()`
- ✅ `aggregationWindowRepository.save()`
- ✅ `lateEventRepository.findAll()`
- ✅ `lateEventRepository.delete()`

## Recommendation

**Remove unused methods** that are:
1. Not required by requirements
2. Not used in main code
3. Only used in tests (domain helpers can stay for test convenience, but repository methods should be removed)

**Keep for future reporting API:**
- Consider keeping some repository methods if planning to add reporting endpoints later
- But if not planned, remove them to reduce maintenance burden

