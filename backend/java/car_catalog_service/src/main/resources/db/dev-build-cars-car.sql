-- src/main/resources/db/dev-build-cars-car.sql

SET search_path = public;

CREATE TABLE IF NOT EXISTS cars_car (
  id              BIGSERIAL PRIMARY KEY,
  brand_id        BIGINT NOT NULL,
  model_id        BIGINT NOT NULL,
  engine_id       BIGINT NOT NULL,
  fuel_type_id    BIGINT NOT NULL,
  year_id         BIGINT NOT NULL,
  transmission_id BIGINT NOT NULL,
  wheel_drive_id  BIGINT NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_cars_car_combo
  ON cars_car (brand_id, model_id, engine_id, fuel_type_id, year_id, transmission_id, wheel_drive_id);

CREATE INDEX IF NOT EXISTS ix_cars_car_brand        ON cars_car (brand_id);
CREATE INDEX IF NOT EXISTS ix_cars_car_model        ON cars_car (model_id);
CREATE INDEX IF NOT EXISTS ix_cars_car_engine       ON cars_car (engine_id);
CREATE INDEX IF NOT EXISTS ix_cars_car_fuel         ON cars_car (fuel_type_id);
CREATE INDEX IF NOT EXISTS ix_cars_car_year         ON cars_car (year_id);
CREATE INDEX IF NOT EXISTS ix_cars_car_transmission ON cars_car (transmission_id);
CREATE INDEX IF NOT EXISTS ix_cars_car_wheel_drive  ON cars_car (wheel_drive_id);

WITH
params AS (
  SELECT
    20000::int AS target_rows,
    30::int    AS models_per_brand,
    120::int   AS engines_pick,
    20::int    AS years_pick
),

sample_models AS (
  SELECT cm.id AS model_id, cm.brand_id
  FROM (
    SELECT cm.*,
           ROW_NUMBER() OVER (PARTITION BY cm.brand_id ORDER BY random()) AS rn
    FROM cars_carmodel cm
  ) cm
  JOIN params p ON TRUE
  WHERE cm.rn <= p.models_per_brand
),

sample_engines AS (
  SELECT id AS engine_id
  FROM (
    SELECT e.*,
           ROW_NUMBER() OVER (ORDER BY random()) AS rn
    FROM cars_engine e
  ) e
  JOIN params p ON TRUE
  WHERE e.rn <= p.engines_pick
),
sample_years AS (
  SELECT id AS year_id
  FROM (
    SELECT y.*,
           ROW_NUMBER() OVER (ORDER BY random()) AS rn
    FROM cars_year y
  ) y
  JOIN params p ON TRUE
  WHERE y.rn <= p.years_pick
),

sample_trans AS ( SELECT t.id AS transmission_id FROM cars_transmission t ),
sample_drive AS ( SELECT w.id AS wheel_drive_id  FROM cars_whilldrive  w ),

combos AS (
  SELECT DISTINCT
         m.brand_id, m.model_id,
         e.engine_id, y.year_id,
         t.transmission_id, d.wheel_drive_id
  FROM sample_models m
  CROSS JOIN sample_engines e
  CROSS JOIN sample_years  y
  CROSS JOIN sample_trans  t
  CROSS JOIN sample_drive  d
),

spread AS (
  SELECT c.*,
         ROW_NUMBER() OVER (PARTITION BY c.brand_id ORDER BY random()) AS brand_rn
  FROM combos c
),

ranked AS (
  SELECT c.*,
         ROW_NUMBER() OVER (ORDER BY c.brand_rn, c.brand_id) AS seq
  FROM spread c
)

INSERT INTO cars_car
  (brand_id, model_id, engine_id, fuel_type_id, year_id, transmission_id, wheel_drive_id)
SELECT
  r.brand_id,
  r.model_id,
  r.engine_id,
  e.fuel_type_id,
  r.year_id,
  r.transmission_id,
  r.wheel_drive_id
FROM ranked r
JOIN cars_engine e ON e.id = r.engine_id
JOIN params p ON TRUE
WHERE r.seq <= p.target_rows

  AND NOT EXISTS (SELECT 1 FROM cars_car LIMIT 1);
