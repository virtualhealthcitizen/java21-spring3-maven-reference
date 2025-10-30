DROP TABLE IF EXISTS widgets;

CREATE TABLE IF NOT EXISTS widgets
(
    id         UUID PRIMARY KEY,
    name       TEXT        NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    meta       JSONB
);

INSERT INTO widgets (id, name, meta)
VALUES (gen_random_uuid(), 'Widget X', '{"size":42}'),
       (gen_random_uuid(), 'Widget Y', '{"color":"blue"}');

-- Include updated_at value in insertion
INSERT INTO widgets (id, name, meta, created_at)
VALUES (gen_random_uuid(), 'Widget Z', '{"weight":10}', NOW());

-- Invalid record to test error handling
INSERT INTO widgets (id, name, meta)
VALUES (gen_random_uuid(), 'Another widget', '{"invalid":false}');

