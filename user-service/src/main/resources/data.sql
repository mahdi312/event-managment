INSERT INTO roles (id, name)
SELECT 1, 'ROLE_USER' 
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE id = 1 OR name = 'ROLE_USER');

INSERT INTO roles (id, name) 
SELECT 2, 'ROLE_ADMIN' 
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE id = 2 OR name = 'ROLE_ADMIN');

INSERT INTO roles (id, name) 
SELECT 3, 'ROLE_EVENT_MANAGER' 
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE id = 3 OR name = 'ROLE_EVENT_MANAGER');

INSERT INTO users (username, email, password)
SELECT 'mehdi-jv', 'mehdi-jv@example.com', '$2a$10$q/5RLVKG35umYLATZl/M8eQfSsXuVcXFf0nLDKNtafYeZ2rYNA64m'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'mehdi-jv');

INSERT INTO users (username, email, password) 
SELECT 'rasoul-nb', 'rasoul-nb@example.com', '$2a$10$WIvU/iBrQ4KlkfLNUmrcweTJHzQLQl7RL8fsGJ.JAsZJaHpRLMWWy'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'rasoul-nb');

INSERT INTO users (username, email, password) 
SELECT 'mahdi-mst', 'mahdi-mst@example.com', '$2a$10$.5woUipnO0dOxF8TdkXL9ukDkwEmjJAOF8y6W8SJ5bMCegpTQejkS'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'mahdi-mst');

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