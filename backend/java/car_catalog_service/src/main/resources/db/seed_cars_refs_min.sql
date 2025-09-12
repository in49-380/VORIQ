SET search_path = public;

WITH start_id AS (SELECT COALESCE(MAX(id), 0) AS s FROM cars_market)
INSERT INTO cars_market (id, code, name)
SELECT s.s + 1, 'GEN', 'Generic'
FROM start_id s
WHERE NOT EXISTS (SELECT 1 FROM cars_market);

WITH seq AS (SELECT pg_get_serial_sequence('cars_market','id') AS s),
     mx  AS (SELECT COALESCE(MAX(id), 0) AS m FROM cars_market)
SELECT CASE WHEN seq.s IS NULL THEN NULL
            WHEN mx.m = 0     THEN setval(seq.s::regclass, 1, false)
            ELSE                    setval(seq.s::regclass, mx.m, true)
       END
FROM seq, mx;

WITH seq AS (SELECT pg_get_serial_sequence('cars_brand','id') AS s),
     mx  AS (SELECT COALESCE(MAX(id), 0) AS m FROM cars_brand)
SELECT CASE WHEN seq.s IS NULL THEN NULL
            WHEN mx.m = 0     THEN setval(seq.s::regclass, 1, false)
            ELSE                    setval(seq.s::regclass, mx.m, true)
       END
FROM seq, mx;

WITH seq AS (SELECT pg_get_serial_sequence('cars_carmodel','id') AS s),
     mx  AS (SELECT COALESCE(MAX(id), 0) AS m FROM cars_carmodel)
SELECT CASE WHEN seq.s IS NULL THEN NULL
            WHEN mx.m = 0     THEN setval(seq.s::regclass, 1, false)
            ELSE                    setval(seq.s::regclass, mx.m, true)
       END
FROM seq, mx;

WITH seq AS (SELECT pg_get_serial_sequence('cars_year','id') AS s),
     mx  AS (SELECT COALESCE(MAX(id), 0) AS m FROM cars_year)
SELECT CASE WHEN seq.s IS NULL THEN NULL
            WHEN mx.m = 0     THEN setval(seq.s::regclass, 1, false)
            ELSE                    setval(seq.s::regclass, mx.m, true)
       END
FROM seq, mx;

-- ===== BRANDS =====
INSERT INTO cars_brand (name)
SELECT 'BMW'
WHERE NOT EXISTS (SELECT 1 FROM cars_brand WHERE name='BMW');

INSERT INTO cars_brand (name)
SELECT 'Volkswagen'
WHERE NOT EXISTS (SELECT 1 FROM cars_brand WHERE name='Volkswagen');

INSERT INTO cars_brand (name)
SELECT 'Toyota'
WHERE NOT EXISTS (SELECT 1 FROM cars_brand WHERE name='Toyota');

INSERT INTO cars_brand (name)
SELECT 'Tesla'
WHERE NOT EXISTS (SELECT 1 FROM cars_brand WHERE name='Tesla');

-- ===== MODELS =====
INSERT INTO cars_carmodel (name, brand_id)
SELECT '3 Series', b.id
FROM cars_brand b
WHERE b.name='BMW'
  AND NOT EXISTS (SELECT 1 FROM cars_carmodel m WHERE m.name='3 Series' AND m.brand_id=b.id);

INSERT INTO cars_carmodel (name, brand_id)
SELECT '5 Series', b.id
FROM cars_brand b
WHERE b.name='BMW'
  AND NOT EXISTS (SELECT 1 FROM cars_carmodel m WHERE m.name='5 Series' AND m.brand_id=b.id);

INSERT INTO cars_carmodel (name, brand_id)
SELECT 'Golf', b.id
FROM cars_brand b
WHERE b.name='Volkswagen'
  AND NOT EXISTS (SELECT 1 FROM cars_carmodel m WHERE m.name='Golf' AND m.brand_id=b.id);

INSERT INTO cars_carmodel (name, brand_id)
SELECT 'Passat', b.id
FROM cars_brand b
WHERE b.name='Volkswagen'
  AND NOT EXISTS (SELECT 1 FROM cars_carmodel m WHERE m.name='Passat' AND m.brand_id=b.id);

INSERT INTO cars_carmodel (name, brand_id)
SELECT 'Corolla', b.id
FROM cars_brand b
WHERE b.name='Toyota'
  AND NOT EXISTS (SELECT 1 FROM cars_carmodel m WHERE m.name='Corolla' AND m.brand_id=b.id);

INSERT INTO cars_carmodel (name, brand_id)
SELECT 'Model 3', b.id
FROM cars_brand b
WHERE b.name='Tesla'
  AND NOT EXISTS (SELECT 1 FROM cars_carmodel m WHERE m.name='Model 3' AND m.brand_id=b.id);

-- ===== YEARS =====
INSERT INTO cars_year ("year") SELECT 2019 WHERE NOT EXISTS (SELECT 1 FROM cars_year WHERE "year"=2019);
INSERT INTO cars_year ("year") SELECT 2020 WHERE NOT EXISTS (SELECT 1 FROM cars_year WHERE "year"=2020);
INSERT INTO cars_year ("year") SELECT 2021 WHERE NOT EXISTS (SELECT 1 FROM cars_year WHERE "year"=2021);
INSERT INTO cars_year ("year") SELECT 2022 WHERE NOT EXISTS (SELECT 1 FROM cars_year WHERE "year"=2022);
INSERT INTO cars_year ("year") SELECT 2023 WHERE NOT EXISTS (SELECT 1 FROM cars_year WHERE "year"=2023);
INSERT INTO cars_year ("year") SELECT 2024 WHERE NOT EXISTS (SELECT 1 FROM cars_year WHERE "year"=2024);
