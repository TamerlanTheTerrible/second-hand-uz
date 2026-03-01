--liquibase formatted sql

--changeset timur:004-create-reviews
CREATE TABLE IF NOT EXISTS reviews (
    id               BIGSERIAL  PRIMARY KEY,
    reviewer_id      BIGINT     NOT NULL REFERENCES users (id),
    reviewed_user_id BIGINT     NOT NULL REFERENCES users (id),
    order_id         BIGINT     NOT NULL REFERENCES orders (id),
    rating           INTEGER    NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment          TEXT,
    created_at       TIMESTAMP  NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_reviews_order_reviewer UNIQUE (order_id, reviewer_id)
);

CREATE INDEX IF NOT EXISTS idx_reviews_reviewed_user ON reviews (reviewed_user_id);
CREATE INDEX IF NOT EXISTS idx_reviews_reviewer      ON reviews (reviewer_id);
--rollback DROP TABLE IF EXISTS reviews;
