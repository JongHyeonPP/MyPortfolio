-- Initial database setup
CREATE TABLE IF NOT EXISTS roles (
    role_id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO roles (role_name) VALUES ('DOCTOR'), ('NURSE'), ('ADMIN'), ('IT_ADMIN') ON CONFLICT DO NOTHING;
