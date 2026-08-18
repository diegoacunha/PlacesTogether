-- Initialization script for PlacesTogether PostgreSQL database
-- Execute this script against the postgres admin database to create the application database
-- and its schema from scratch.

\set ON_ERROR_STOP on

SELECT 'CREATE DATABASE placestogether OWNER postgres'
WHERE NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'placestogether')\gexec

\connect placestogether

CREATE SCHEMA IF NOT EXISTS public;
ALTER SCHEMA public OWNER TO postgres;
GRANT ALL ON SCHEMA public TO postgres;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO postgres;

CREATE TABLE IF NOT EXISTS places (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    description TEXT,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    visit_date DATE,
    cover_image_url TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS photos (
    id BIGSERIAL PRIMARY KEY,
    place_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content BYTEA NOT NULL,
    content_type VARCHAR(255),
    file_name VARCHAR(255),
    file_size BIGINT NOT NULL DEFAULT 0,
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_photos_place
        FOREIGN KEY (place_id) REFERENCES places (id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_photos_place_id ON photos (place_id);
CREATE INDEX IF NOT EXISTS idx_places_created_at ON places (created_at);
