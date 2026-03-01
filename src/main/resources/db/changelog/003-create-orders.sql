--liquibase formatted sql

--changeset timur:003-create-orders
CREATE TABLE IF NOT EXISTS orders (
    id          BIGSERIAL      PRIMARY KEY,
    buyer_id    BIGINT         NOT NULL REFERENCES users (id),
    listing_id  BIGINT         NOT NULL REFERENCES listings (id),
    total_price NUMERIC(15, 2) NOT NULL,
    status      VARCHAR(20)    NOT NULL DEFAULT 'CREATED',
    created_at  TIMESTAMP      NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_orders_listing UNIQUE (listing_id)
);

CREATE INDEX IF NOT EXISTS idx_orders_buyer  ON orders (buyer_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders (status);
--rollback DROP TABLE IF EXISTS orders;
