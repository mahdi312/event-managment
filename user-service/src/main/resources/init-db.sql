-- User Service Database Initialization Script
-- This script creates the necessary tables for the user service

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

-- Insert default roles if they don't exist
INSERT INTO roles (id, name) VALUES (1, 'ROLE_USER') ON CONFLICT (id) DO NOTHING;
INSERT INTO roles (id, name) VALUES (2, 'ROLE_ADMIN') ON CONFLICT (id) DO NOTHING;
INSERT INTO roles (id, name) VALUES (3, 'ROLE_EVENT_MANAGER') ON CONFLICT (id) DO NOTHING;

-- Insert default users if they don't exist
INSERT INTO users (username, email, password)
SELECT 'mehdi-jv', 'mehdi-jv@example.com', '$2a$10$q/5RLVKG35umYLATZl/M8eQfSsXuVcXFf0nLDKNtafYeZ2rYNA64m'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'mehdi-jv');

INSERT INTO users (username, email, password) 
SELECT 'rasoul-nb', 'rasoul-nb@example.com', '$2a$10$WIvU/iBrQ4KlkfLNUmrcweTJHzQLQl7RL8fsGJ.JAsZJaHpRLMWWy'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'rasoul-nb');

INSERT INTO users (username, email, password) 
SELECT 'mahdi-mst', 'mahdi-mst@example.com', '$2a$10$.5woUipnO0dOxF8TdkXL9ukDkwEmjJAOF8y6W8SJ5bMCegpTQejkS'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'mahdi-mst');

-- Insert user roles if they don't exist
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
