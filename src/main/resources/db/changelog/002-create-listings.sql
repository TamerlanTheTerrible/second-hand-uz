--liquibase formatted sql

--changeset timur:002-create-listings
CREATE TABLE IF NOT EXISTS listings (
    id          BIGSERIAL        PRIMARY KEY,
    seller_id   BIGINT           NOT NULL REFERENCES users (id),
    title       VARCHAR(255)     NOT NULL,
    description TEXT,
    price       NUMERIC(15, 2)   NOT NULL,
    size        VARCHAR(50),
    brand       VARCHAR(100),
    condition   VARCHAR(20)      NOT NULL,
    status      VARCHAR(20)      NOT NULL DEFAULT 'ACTIVE',
    image_urls  TEXT[],
    created_at  TIMESTAMP        NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_listings_seller ON listings (seller_id);
CREATE INDEX IF NOT EXISTS idx_listings_status ON listings (status);
CREATE INDEX IF NOT EXISTS idx_listings_title   ON listings (title);
CREATE INDEX IF NOT EXISTS idx_listings_brand   ON listings (brand);
--rollback DROP TABLE IF EXISTS listings;
