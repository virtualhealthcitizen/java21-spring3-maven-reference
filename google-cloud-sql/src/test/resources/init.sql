CREATE TABLE IF NOT EXISTS widgets
(
    id         UUID PRIMARY KEY,
    name       TEXT        NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    meta       JSONB
);
