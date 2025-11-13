-- Add audit fields (created, updated) to usage_events and aggregation_windows tables
-- Per requirements: "Include audit fields (created, updated timestamps)"

-- Add audit fields to usage_events table
ALTER TABLE usage_events 
    ADD COLUMN IF NOT EXISTS created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN IF NOT EXISTS updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- Add audit fields to aggregation_windows table
ALTER TABLE aggregation_windows 
    ADD COLUMN IF NOT EXISTS created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN IF NOT EXISTS updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- Note: For existing rows, the DEFAULT CURRENT_TIMESTAMP will set the initial value
-- For new rows, these will be automatically set

