-- ===== BRANDS =====
INSERT INTO cars_brand (id, name)
SELECT COALESCE((SELECT nextval(pg_get_serial_sequence('cars_brand','id'))),
                (SELECT COALESCE(MAX(id),0)+1 FROM cars_brand)),
       'BMW'
WHERE NOT EXISTS (SELECT 1 FROM cars_brand WHERE name='BMW');

INSERT INTO cars_brand (id, name)
SELECT COALESCE((SELECT nextval(pg_get_serial_sequence('cars_brand','id'))),
                (SELECT COALESCE(MAX(id),0)+1 FROM cars_brand)),
       'Volkswagen'
WHERE NOT EXISTS (SELECT 1 FROM cars_brand WHERE name='Volkswagen');

INSERT INTO cars_brand (id, name)
SELECT COALESCE((SELECT nextval(pg_get_serial_sequence('cars_brand','id'))),
                (SELECT COALESCE(MAX(id),0)+1 FROM cars_brand)),
       'Toyota'
WHERE NOT EXISTS (SELECT 1 FROM cars_brand WHERE name='Toyota');

INSERT INTO cars_brand (id, name)
SELECT COALESCE((SELECT nextval(pg_get_serial_sequence('cars_brand','id'))),
                (SELECT COALESCE(MAX(id),0)+1 FROM cars_brand)),
       'Tesla'
WHERE NOT EXISTS (SELECT 1 FROM cars_brand WHERE name='Tesla');

-- ===== MODELS =====
INSERT INTO cars_carmodel (id, name, brand_id)
SELECT COALESCE((SELECT nextval(pg_get_serial_sequence('cars_carmodel','id'))),
                (SELECT COALESCE(MAX(id),0)+1 FROM cars_carmodel)),
       '3 Series', b.id
FROM cars_brand b
WHERE b.name='BMW'
  AND NOT EXISTS (SELECT 1 FROM cars_carmodel m WHERE m.name='3 Series' AND m.brand_id=b.id);

INSERT INTO cars_carmodel (id, name, brand_id)
SELECT COALESCE((SELECT nextval(pg_get_serial_sequence('cars_carmodel','id'))),
                (SELECT COALESCE(MAX(id),0)+1 FROM cars_carmodel)),
       '5 Series', b.id
FROM cars_brand b
WHERE b.name='BMW'
  AND NOT EXISTS (SELECT 1 FROM cars_carmodel m WHERE m.name='5 Series' AND m.brand_id=b.id);

INSERT INTO cars_carmodel (id, name, brand_id)
SELECT COALESCE((SELECT nextval(pg_get_serial_sequence('cars_carmodel','id'))),
                (SELECT COALESCE(MAX(id),0)+1 FROM cars_carmodel)),
       'Golf', b.id
FROM cars_brand b
WHERE b.name='Volkswagen'
  AND NOT EXISTS (SELECT 1 FROM cars_carmodel m WHERE m.name='Golf' AND m.brand_id=b.id);

INSERT INTO cars_carmodel (id, name, brand_id)
SELECT COALESCE((SELECT nextval(pg_get_serial_sequence('cars_carmodel','id'))),
                (SELECT COALESCE(MAX(id),0)+1 FROM cars_carmodel)),
       'Passat', b.id
FROM cars_brand b
WHERE b.name='Volkswagen'
  AND NOT EXISTS (SELECT 1 FROM cars_carmodel m WHERE m.name='Passat' AND m.brand_id=b.id);

INSERT INTO cars_carmodel (id, name, brand_id)
SELECT COALESCE((SELECT nextval(pg_get_serial_sequence('cars_carmodel','id'))),
                (SELECT COALESCE(MAX(id),0)+1 FROM cars_carmodel)),
       'Corolla', b.id
FROM cars_brand b
WHERE b.name='Toyota'
  AND NOT EXISTS (SELECT 1 FROM cars_carmodel m WHERE m.name='Corolla' AND m.brand_id=b.id);

INSERT INTO cars_carmodel (id, name, brand_id)
SELECT COALESCE((SELECT nextval(pg_get_serial_sequence('cars_carmodel','id'))),
                (SELECT COALESCE(MAX(id),0)+1 FROM cars_carmodel)),
       'Model 3', b.id
FROM cars_brand b
WHERE b.name='Tesla'
  AND NOT EXISTS (SELECT 1 FROM cars_carmodel m WHERE m.name='Model 3' AND m.brand_id=b.id);

-- ===== MARKETS  =====
INSERT INTO cars_market (id, code, name)
SELECT COALESCE((SELECT nextval(pg_get_serial_sequence('cars_market','id'))),
                (SELECT COALESCE(MAX(id),0)+1 FROM cars_market)),
       'EU', 'EU'
WHERE NOT EXISTS (SELECT 1 FROM cars_market WHERE code='EU' OR name='EU');

INSERT INTO cars_market (id, code, name)
SELECT COALESCE((SELECT nextval(pg_get_serial_sequence('cars_market','id'))),
                (SELECT COALESCE(MAX(id),0)+1 FROM cars_market)),
       'US', 'US'
WHERE NOT EXISTS (SELECT 1 FROM cars_market WHERE code='US' OR name='US');

INSERT INTO cars_market (id, code, name)
SELECT COALESCE((SELECT nextval(pg_get_serial_sequence('cars_market','id'))),
                (SELECT COALESCE(MAX(id),0)+1 FROM cars_market)),
       'JP', 'JP'
WHERE NOT EXISTS (SELECT 1 FROM cars_market WHERE code='JP' OR name='JP');

INSERT INTO cars_market (id, code, name)
SELECT COALESCE((SELECT nextval(pg_get_serial_sequence('cars_market','id'))),
                (SELECT COALESCE(MAX(id),0)+1 FROM cars_market)),
       'CN', 'CN'
WHERE NOT EXISTS (SELECT 1 FROM cars_market WHERE code='CN' OR name='CN');


-- ===== YEARS =====
INSERT INTO cars_year (id, "year")
SELECT COALESCE((SELECT nextval(pg_get_serial_sequence('cars_year','id'))),
                (SELECT COALESCE(MAX(id),0)+1 FROM cars_year)), 2019
WHERE NOT EXISTS (SELECT 1 FROM cars_year WHERE "year"=2019);

INSERT INTO cars_year (id, "year")
SELECT COALESCE((SELECT nextval(pg_get_serial_sequence('cars_year','id'))),
                (SELECT COALESCE(MAX(id),0)+1 FROM cars_year)), 2020
WHERE NOT EXISTS (SELECT 1 FROM cars_year WHERE "year"=2020);

INSERT INTO cars_year (id, "year")
SELECT COALESCE((SELECT nextval(pg_get_serial_sequence('cars_year','id'))),
                (SELECT COALESCE(MAX(id),0)+1 FROM cars_year)), 2021
WHERE NOT EXISTS (SELECT 1 FROM cars_year WHERE "year"=2021);

INSERT INTO cars_year (id, "year")
SELECT COALESCE((SELECT nextval(pg_get_serial_sequence('cars_year','id'))),
                (SELECT COALESCE(MAX(id),0)+1 FROM cars_year)), 2022
WHERE NOT EXISTS (SELECT 1 FROM cars_year WHERE "year"=2022);

INSERT INTO cars_year (id, "year")
SELECT COALESCE((SELECT nextval(pg_get_serial_sequence('cars_year','id'))),
                (SELECT COALESCE(MAX(id),0)+1 FROM cars_year)), 2023
WHERE NOT EXISTS (SELECT 1 FROM cars_year WHERE "year"=2023);

INSERT INTO cars_year (id, "year")
SELECT COALESCE((SELECT nextval(pg_get_serial_sequence('cars_year','id'))),
                (SELECT COALESCE(MAX(id),0)+1 FROM cars_year)), 2024
WHERE NOT EXISTS (SELECT 1 FROM cars_year WHERE "year"=2024);
