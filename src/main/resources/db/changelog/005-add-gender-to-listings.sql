--liquibase formatted sql

--changeset timur:005-add-gender-to-listings
ALTER TABLE listings ADD COLUMN IF NOT EXISTS gender VARCHAR(20);
--rollback ALTER TABLE listings DROP COLUMN IF EXISTS gender;
