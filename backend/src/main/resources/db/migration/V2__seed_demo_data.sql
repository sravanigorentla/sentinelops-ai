-- Seed Roles
INSERT INTO roles (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles (id, name) VALUES (2, 'ROLE_DEVELOPER');
INSERT INTO roles (id, name) VALUES (3, 'ROLE_VIEWER');

-- Seed Users (Password: Password123!)
-- Hash: $2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymY04n.1J/4/6.11J7.51K
INSERT INTO users (id, email, password_hash, full_name, role_id, created_at, updated_at) VALUES
(1, 'admin@sentinelops.io', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymY04n.1J/4/6.11J7.51K', 'Alex Vance (Admin)', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'dev@sentinelops.io', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymY04n.1J/4/6.11J7.51K', 'Sarah Connor (Dev Lead)', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'viewer@sentinelops.io', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymY04n.1J/4/6.11J7.51K', 'Dave Miller (SRE Viewer)', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Seed Services
INSERT INTO services (id, name, description, environment, owner, repository, version, health_endpoint, status, created_at, updated_at) VALUES
(1, 'payment-service', 'Handles core payment processing, Stripe integrations, and billing ledgers', 'production', 'Fintech Platform Team', 'github.com/sentinelops/payment-service', 'v2.4.1', 'http://payment-service:8080/health', 'DEGRADED', CURRENT_TIMESTAMP - INTERVAL '10' DAY, CURRENT_TIMESTAMP),
(2, 'auth-service', 'Identity management, OAuth2 tokens, and user credentials verification', 'production', 'Security & IAM Team', 'github.com/sentinelops/auth-service', 'v1.9.0', 'http://auth-service:8080/health', 'HEALTHY', CURRENT_TIMESTAMP - INTERVAL '20' DAY, CURRENT_TIMESTAMP),
(3, 'order-service', 'Manages checkout workflows, cart states, and order fulfillment triggers', 'production', 'E-Commerce Core', 'github.com/sentinelops/order-service', 'v3.1.0', 'http://order-service:8080/health', 'DOWN', CURRENT_TIMESTAMP - INTERVAL '15' DAY, CURRENT_TIMESTAMP),
(4, 'inventory-service', 'Real-time stock tracking and warehouse supply sync', 'staging', 'Logistics Eng', 'github.com/sentinelops/inventory-service', 'v1.2.0-rc1', 'http://inventory-service:8080/health', 'HEALTHY', CURRENT_TIMESTAMP - INTERVAL '5' DAY, CURRENT_TIMESTAMP);

-- Seed Recent Deployments
INSERT INTO deployments (id, service_id, version, commit_sha, branch, environment, deployed_by, deployment_time, status, rolled_back) VALUES
(1, 1, 'v2.4.1', 'a1b2c3d4e5f6789012345678901234567890a1b2', 'main', 'production', 'Sarah Connor', CURRENT_TIMESTAMP - INTERVAL '2' HOUR, 'SUCCESS', FALSE),
(2, 3, 'v3.1.0', 'f9e8d7c6b5a4321098765432109876543210f9e8', 'main', 'production', 'Alex Vance', CURRENT_TIMESTAMP - INTERVAL '45' MINUTE, 'FAILED', FALSE),
(3, 2, 'v1.9.0', 'c3d4e5f6a1b2789012345678901234567890c3d4', 'main', 'production', 'Sarah Connor', CURRENT_TIMESTAMP - INTERVAL '1' DAY, 'SUCCESS', FALSE);

-- Seed Historical Telemetry Metrics
INSERT INTO metrics (service_id, cpu_usage, memory_usage, request_rate, latency_ms, error_rate, timestamp) VALUES
(1, 45.2, 62.1, 120.0, 85.0, 0.01, CURRENT_TIMESTAMP - INTERVAL '30' MINUTE),
(1, 78.9, 88.4, 210.0, 320.0, 4.50, CURRENT_TIMESTAMP - INTERVAL '15' MINUTE),
(1, 92.5, 94.1, 240.0, 1250.0, 12.80, CURRENT_TIMESTAMP - INTERVAL '5' MINUTE),
(3, 98.9, 99.2, 0.0, 5000.0, 98.00, CURRENT_TIMESTAMP - INTERVAL '10' MINUTE),
(2, 22.1, 41.0, 450.0, 18.5, 0.00, CURRENT_TIMESTAMP - INTERVAL '5' MINUTE);

-- Seed System Logs
INSERT INTO logs (timestamp, service_id, environment, log_level, message, request_id, metadata) VALUES
(CURRENT_TIMESTAMP - INTERVAL '15' MINUTE, 1, 'production', 'WARN', 'Connection pool saturation detected: active connections 98/100', 'req-pay-8821', '{"db_host": "rds-prod-primary.internal"}'),
(CURRENT_TIMESTAMP - INTERVAL '12' MINUTE, 1, 'production', 'ERROR', 'Database query timeout exceeded 5000ms: SELECT * FROM transactions WHERE status = PENDING', 'req-pay-8845', '{"timeout_ms": 5000}'),
(CURRENT_TIMESTAMP - INTERVAL '10' MINUTE, 3, 'production', 'ERROR', 'HikariPool-1 - Connection is not available, request timed out after 30000ms', 'req-ord-9901', '{"pool_name": "OrderDS"}'),
(CURRENT_TIMESTAMP - INTERVAL '8' MINUTE, 3, 'production', 'ERROR', 'Fatal: java.lang.OutOfMemoryError: Java heap space at com.sentinelops.order.Service.processBatch', 'req-ord-9912', '{"heap_max": "2048M"}');

-- Seed Incidents
INSERT INTO incidents (id, title, description, severity, status, service_id, detected_at, acknowledged_at, assigned_to_user_id, root_cause, resolution, deployment_id, rollback_status) VALUES
(1, 'Database Connection Pool Saturation & Latency Spike', 'Payment service latency increased to 1250ms with 12.8% error rate following deployment v2.4.1.', 'HIGH', 'INVESTIGATING', 1, CURRENT_TIMESTAMP - INTERVAL '20' MINUTE, CURRENT_TIMESTAMP - INTERVAL '15' MINUTE, 2, 'Unindexed query in deployment v2.4.1 causing database connection exhaustion.', NULL, 1, 'NONE'),
(2, 'Order Service Outage (OutOfMemoryError)', 'Order service is completely non-responsive (5000ms latency, 98% error rate) after deployment v3.1.0 batch process leak.', 'CRITICAL', 'OPEN', 3, CURRENT_TIMESTAMP - INTERVAL '10' MINUTE, NULL, NULL, NULL, NULL, 2, 'PENDING');

-- Seed Incident Comments
INSERT INTO incident_comments (incident_id, user_id, comment, created_at) VALUES
(1, 2, 'Investigating connection pool leak. Checked CloudWatch RDS metrics, CPU is at 95%.', CURRENT_TIMESTAMP - INTERVAL '14' MINUTE),
(1, 1, 'Sarah, please check if deployment v2.4.1 introduced a missing SQL index.', CURRENT_TIMESTAMP - INTERVAL '10' MINUTE);

-- Seed Alert Rules
INSERT INTO alert_rules (service_id, metric_name, comparison_operator, threshold_value, severity, enabled) VALUES
(1, 'error_rate', '>', 5.0, 'HIGH', TRUE),
(1, 'latency_ms', '>', 1000.0, 'HIGH', TRUE),
(3, 'error_rate', '>', 10.0, 'CRITICAL', TRUE),
(2, 'cpu_usage', '>', 90.0, 'MEDIUM', TRUE);

-- Seed Audit Logs
INSERT INTO audit_logs (user_id, user_email, action, target_type, target_id, details, ip_address, timestamp) VALUES
(1, 'admin@sentinelops.io', 'USER_LOGIN', 'USER', '1', 'Successful admin login from local console', '127.0.0.1', CURRENT_TIMESTAMP - INTERVAL '1' HOUR),
(2, 'dev@sentinelops.io', 'INCIDENT_ACKNOWLEDGE', 'INCIDENT', '1', 'Acknowledged incident #1', '127.0.0.1', CURRENT_TIMESTAMP - INTERVAL '15' MINUTE);

-- Seed AI Analysis for Incident 1
INSERT INTO ai_analyses (incident_id, summary, probable_root_cause, evidence_json, recommended_actions_json, related_signals_json, confidence, generated_at, verified_by_user_id) VALUES
(1, 
 'The payment-service is experiencing a severe degradation with latency spiking to 1250ms and error rate jumping to 12.8%. This correlated directly with deployment v2.4.1.',
 'Deployment v2.4.1 introduced a slow SQL query in transaction status lookup that exhausts the Hikari connection pool under normal traffic loads.',
 '["Log ERROR: Database query timeout exceeded 5000ms: SELECT * FROM transactions WHERE status = PENDING", "Log WARN: Connection pool saturation detected: active connections 98/100", "Metric Latency spike from 85ms to 1250ms immediately after commit a1b2c3d4e5f6"]',
 '["Trigger deployment rollback for v2.4.1 to restore immediate operational stability", "Add composite index on transactions(status, created_at)", "Increase Hikari connection pool max-lifetime and timeout threshold"]',
 '["Deployment: v2.4.1 deployed 2 hours ago", "CPU Usage: 92.5%", "Connection pool active: 98/100"]',
 0.92,
 CURRENT_TIMESTAMP - INTERVAL '12' MINUTE,
 2
);

-- Fix sequence counts for auto-increment IDs in PostgreSQL
SELECT setval(pg_get_serial_sequence('roles', 'id'), 10);
SELECT setval(pg_get_serial_sequence('users', 'id'), 10);
SELECT setval(pg_get_serial_sequence('services', 'id'), 10);
SELECT setval(pg_get_serial_sequence('deployments', 'id'), 10);
SELECT setval(pg_get_serial_sequence('incidents', 'id'), 10);
SELECT setval(pg_get_serial_sequence('incident_comments', 'id'), 10);
SELECT setval(pg_get_serial_sequence('alert_rules', 'id'), 10);
SELECT setval(pg_get_serial_sequence('ai_analyses', 'id'), 10);
SELECT setval(pg_get_serial_sequence('audit_logs', 'id'), 10);

