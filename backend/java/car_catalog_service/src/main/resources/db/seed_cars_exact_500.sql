WITH params AS (
  SELECT COALESCE(NULLIF(current_setting('voriq.seed_target_cars_car', true), '')::int, 500) AS target
),
existing AS (SELECT COUNT(*) AS cnt FROM cars_car),
need     AS (SELECT GREATEST((SELECT target FROM params) - (SELECT cnt FROM existing), 0) AS n),

m  AS (SELECT id FROM cars_carmodel     ORDER BY id         LIMIT 200),
e  AS (SELECT id FROM cars_engine       ORDER BY id         LIMIT 5),
mk AS (SELECT id FROM cars_market       ORDER BY id         LIMIT 4),
t  AS (SELECT id FROM cars_transmission ORDER BY id         LIMIT 3),
d  AS (SELECT id FROM cars_whilldrive   ORDER BY id         LIMIT 3),
y  AS (SELECT id FROM cars_year         ORDER BY "year", id LIMIT 6),

combos AS (
  SELECT m.id  AS model_id,
         e.id  AS engine_id,
         mk.id AS market_id,
         t.id  AS transmission_id,
         d.id  AS whill_drive_id,
         y.id  AS year_id
  FROM m CROSS JOIN e CROSS JOIN mk CROSS JOIN t CROSS JOIN d CROSS JOIN y
),
missing AS (
  SELECT c.*
  FROM combos c
  LEFT JOIN cars_car cc
    ON cc.model_id        = c.model_id
   AND cc.engine_id       = c.engine_id
   AND cc.market_id       = c.market_id
   AND cc.transmission_id = c.transmission_id
   AND cc.whill_drive_id  = c.whill_drive_id
   AND cc.year_id         = c.year_id
  WHERE cc.id IS NULL
),
ranked AS (
  SELECT m.*,
         ROW_NUMBER() OVER (
           PARTITION BY m.model_id
           ORDER BY m.engine_id, m.market_id, m.transmission_id, m.whill_drive_id, m.year_id
         ) AS rn_per_model
  FROM missing m
),
to_insert AS (
  SELECT r.*
  FROM ranked r
  ORDER BY r.rn_per_model, r.model_id
  LIMIT (SELECT n FROM need)
),
to_insert_rn AS (
  SELECT
    ROW_NUMBER() OVER (ORDER BY model_id, engine_id, market_id, transmission_id, whill_drive_id, year_id) AS rn,
    *
  FROM to_insert
),
seq  AS (SELECT pg_get_serial_sequence('cars_car','id') AS seqname),
base AS (SELECT COALESCE(MAX(id),0) AS base FROM cars_car)

INSERT INTO cars_car (id, model_id, engine_id, market_id, transmission_id, whill_drive_id, year_id)
SELECT
  CASE WHEN (SELECT seqname FROM seq) IS NOT NULL
         THEN nextval((SELECT seqname FROM seq))
       ELSE (SELECT base FROM base) + rn
  END,
  model_id, engine_id, market_id, transmission_id, whill_drive_id, year_id
FROM to_insert_rn;
