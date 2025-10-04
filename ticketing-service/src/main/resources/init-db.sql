-- Ticketing Service Database Initialization Script
-- This script creates the necessary tables for the ticketing service

-- Create bookings table
CREATE TABLE IF NOT EXISTS bookings (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    number_of_tickets INTEGER NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    payment_status VARCHAR(20) NOT NULL,
    booking_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cancellation_time TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_bookings_event_id ON bookings(event_id);
CREATE INDEX IF NOT EXISTS idx_bookings_user_id ON bookings(user_id);
CREATE INDEX IF NOT EXISTS idx_bookings_payment_status ON bookings(payment_status);
CREATE INDEX IF NOT EXISTS idx_bookings_booking_time ON bookings(booking_time);

-- Add constraint to ensure positive ticket count
ALTER TABLE bookings ADD CONSTRAINT IF NOT EXISTS chk_bookings_positive_tickets 
    CHECK (number_of_tickets > 0);

-- Add constraint to ensure positive total amount
ALTER TABLE bookings ADD CONSTRAINT IF NOT EXISTS chk_bookings_positive_amount 
    CHECK (total_amount >= 0);

-- Add constraint to ensure valid payment status
ALTER TABLE bookings ADD CONSTRAINT IF NOT EXISTS chk_bookings_payment_status 
    CHECK (payment_status IN ('PENDING', 'PAID', 'FAILED', 'REFUNDED'));

-- Add constraint to ensure cancellation_time is after booking_time when set
ALTER TABLE bookings ADD CONSTRAINT IF NOT EXISTS chk_bookings_cancellation_time 
    CHECK (cancellation_time IS NULL OR cancellation_time >= booking_time);
