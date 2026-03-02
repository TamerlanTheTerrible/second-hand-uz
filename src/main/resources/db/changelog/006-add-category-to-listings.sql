--liquibase formatted sql

--changeset timur:006-add-category-to-listings
ALTER TABLE listings ADD COLUMN IF NOT EXISTS category VARCHAR(30);
