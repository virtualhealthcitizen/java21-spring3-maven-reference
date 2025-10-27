INSERT INTO widgets (id, name, meta)
VALUES (gen_random_uuid(), 'Widget X', '{"size":42}'),
       (gen_random_uuid(), 'Widget Y', '{"color":"blue"}');

-- Include updated_at value in insertion
INSERT INTO widgets (id, name, meta, updated_at)
VALUES (gen_random_uuid(), 'Widget Z', '{"weight":10}', NOW());
