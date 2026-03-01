--liquibase formatted sql

--changeset timur:001-create-users
CREATE TABLE IF NOT EXISTS users (
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(255)     NOT NULL UNIQUE,
    password_hash VARCHAR(255)     NOT NULL,
    role          VARCHAR(20)      NOT NULL DEFAULT 'BUYER',
    rating        DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    created_at    TIMESTAMP        NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users (email);
--rollback DROP TABLE IF EXISTS users;
