-- Event Service Database Initialization Script
-- This script creates the necessary tables for the event service

-- Create events table
CREATE TABLE IF NOT EXISTS events (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    location VARCHAR(255) NOT NULL,
    total_tickets INTEGER NOT NULL,
    available_tickets INTEGER,
    ticket_price DECIMAL(10,2) NOT NULL,
    created_by_user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_events_title ON events(title);
CREATE INDEX IF NOT EXISTS idx_events_location ON events(location);
CREATE INDEX IF NOT EXISTS idx_events_start_time ON events(start_time);
CREATE INDEX IF NOT EXISTS idx_events_created_by_user_id ON events(created_by_user_id);
CREATE INDEX IF NOT EXISTS idx_events_created_at ON events(created_at);

-- Add constraint to ensure end_time is after start_time
ALTER TABLE events ADD CONSTRAINT IF NOT EXISTS chk_events_time_order 
    CHECK (end_time > start_time);

-- Add constraint to ensure positive ticket values
ALTER TABLE events ADD CONSTRAINT IF NOT EXISTS chk_events_positive_tickets 
    CHECK (total_tickets > 0 AND available_tickets >= 0);

-- Add constraint to ensure positive ticket price
ALTER TABLE events ADD CONSTRAINT IF NOT EXISTS chk_events_positive_price 
    CHECK (ticket_price >= 0);
