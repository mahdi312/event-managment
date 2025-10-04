-- Local Database Setup Script
-- Run this script to set up all databases for local development

-- ===========================================
-- USER DATABASE SETUP
-- ===========================================

-- Create user database
CREATE DATABASE user_db;

-- Connect to user_db and create tables
\c user_db;

-- Create roles table
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(20) UNIQUE NOT NULL
);

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- Create user_roles junction table
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role_id ON user_roles(role_id);

-- Insert default roles
INSERT INTO roles (id, name) VALUES (1, 'ROLE_USER') ON CONFLICT (id) DO NOTHING;
INSERT INTO roles (id, name) VALUES (2, 'ROLE_ADMIN') ON CONFLICT (id) DO NOTHING;
INSERT INTO roles (id, name) VALUES (3, 'ROLE_EVENT_MANAGER') ON CONFLICT (id) DO NOTHING;

-- Insert default users
INSERT INTO users (username, email, password)
SELECT 'mehdi-jv', 'mehdi-jv@example.com', '$2a$10$q/5RLVKG35umYLATZl/M8eQfSsXuVcXFf0nLDKNtafYeZ2rYNA64m'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'mehdi-jv');

INSERT INTO users (username, email, password) 
SELECT 'rasoul-nb', 'rasoul-nb@example.com', '$2a$10$WIvU/iBrQ4KlkfLNUmrcweTJHzQLQl7RL8fsGJ.JAsZJaHpRLMWWy'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'rasoul-nb');

INSERT INTO users (username, email, password) 
SELECT 'mahdi-mst', 'mahdi-mst@example.com', '$2a$10$.5woUipnO0dOxF8TdkXL9ukDkwEmjJAOF8y6W8SJ5bMCegpTQejkS'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'mahdi-mst');

-- Insert user roles
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, 2 
FROM users u 
WHERE u.username = 'mehdi-jv' 
AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role_id = 2);

INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, 3 
FROM users u 
WHERE u.username = 'rasoul-nb' 
AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role_id = 3);

INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, 1 
FROM users u 
WHERE u.username = 'mahdi-mst' 
AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role_id = 1);

-- ===========================================
-- EVENT DATABASE SETUP
-- ===========================================

-- Create event database
CREATE DATABASE event_db;

-- Connect to event_db and create tables
\c event_db;

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

-- Add constraints
ALTER TABLE events ADD CONSTRAINT IF NOT EXISTS chk_events_time_order 
    CHECK (end_time > start_time);
ALTER TABLE events ADD CONSTRAINT IF NOT EXISTS chk_events_positive_tickets 
    CHECK (total_tickets > 0 AND available_tickets >= 0);
ALTER TABLE events ADD CONSTRAINT IF NOT EXISTS chk_events_positive_price 
    CHECK (ticket_price >= 0);

-- ===========================================
-- TICKETING DATABASE SETUP
-- ===========================================

-- Create ticketing database
CREATE DATABASE ticketing_db;

-- Connect to ticketing_db and create tables
\c ticketing_db;

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

-- Add constraints
ALTER TABLE bookings ADD CONSTRAINT IF NOT EXISTS chk_bookings_positive_tickets 
    CHECK (number_of_tickets > 0);
ALTER TABLE bookings ADD CONSTRAINT IF NOT EXISTS chk_bookings_positive_amount 
    CHECK (total_amount >= 0);
ALTER TABLE bookings ADD CONSTRAINT IF NOT EXISTS chk_bookings_payment_status 
    CHECK (payment_status IN ('PENDING', 'PAID', 'FAILED', 'REFUNDED'));
ALTER TABLE bookings ADD CONSTRAINT IF NOT EXISTS chk_bookings_cancellation_time 
    CHECK (cancellation_time IS NULL OR cancellation_time >= booking_time);

-- ===========================================
-- COMPLETION MESSAGE
-- ===========================================

\echo '==========================================='
\echo 'Local database setup completed successfully!'
\echo 'Created databases: user_db, event_db, ticketing_db'
\echo 'All tables, indexes, and constraints are ready.'
\echo '==========================================='
