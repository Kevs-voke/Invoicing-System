CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE TABLE IF NOT EXISTS users (

-- remote    id UUID PRIMARY KEY DEFAULT gen_random_uuid_v7(),
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_no VARCHAR UNIQUE NOT NULL
    DEFAULT encode(gen_random_bytes(12), 'base64url'),
    email VARCHAR(40) UNIQUE NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    password VARCHAR(100),
    phone_number VARCHAR(30),
    account_non_expired BOOLEAN DEFAULT FALSE,
    account_non_locked BOOLEAN DEFAULT FALSE,
    disabled BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT NOW()
    );

CREATE TABLE IF NOT EXISTS roles(
    id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_with_roles (
     id BIGSERIAL PRIMARY KEY,
     role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE (user_id, role_id)
    );