--liquibase formatted sql

--changeset timur:007-fix-order-unique-constraint
-- Drop the full unique constraint that incorrectly blocked re-ordering after cancellation
ALTER TABLE orders DROP CONSTRAINT IF EXISTS uq_orders_listing;

-- Add a partial unique index: only one active (non-canceled) order per listing at a time
CREATE UNIQUE INDEX IF NOT EXISTS uq_orders_listing_active
    ON orders(listing_id)
    WHERE status NOT IN ('CANCELED');
