SET search_path = public;

WITH base(name) AS (
  VALUES ('FWD'), ('RWD'), ('AWD'), ('4WD')
),
start_id AS (
  SELECT COALESCE(MAX(id), 0) AS s FROM cars_whilldrive
),
to_ins AS (
  SELECT b.name
  FROM base b
  WHERE NOT EXISTS (
    SELECT 1 FROM cars_whilldrive d WHERE LOWER(d.name) = LOWER(b.name)
  )
)
INSERT INTO cars_whilldrive (id, name)
SELECT s.s + ROW_NUMBER() OVER (), name
FROM to_ins, start_id s;

WITH base(marketing_name, gears, manual) AS (
  VALUES
    ('Manual',    5, TRUE),
    ('Automatic', 6, FALSE),
    ('CVT',       0, FALSE)
),
start_id AS (
  SELECT COALESCE(MAX(id), 0) AS s FROM cars_transmission
),
to_ins AS (
  SELECT b.*
  FROM base b
  WHERE NOT EXISTS (
    SELECT 1 FROM cars_transmission t
    WHERE LOWER(t.marketing_name) = LOWER(b.marketing_name)
  )
)
INSERT INTO cars_transmission (id, gears, supplier, family_code, marketing_name, manual)
SELECT s.s + ROW_NUMBER() OVER (),
       b.gears,
       NULL::varchar,   -- supplier
       NULL::varchar,   -- family_code
       b.marketing_name,
       b.manual
FROM to_ins b, start_id s;

WITH base AS (
  SELECT *
  FROM (VALUES
    ('I3',    'B38',  'B38A15',   1499, 1),
    ('I4',    'B48',  'B48B20',   1998, 1),
    ('I6',    'B58',  'B58B30',   2998, 1),
    ('V8',    'N63',  'N63B44',   4395, 1),
    ('I4',    'EA288','TDI',      1968, 2),
    ('V6',    'EA897','TDI',      2967, 2),
    ('Hybrid','A25A', 'A25A-FXS', 2487, 3),
    ('Hybrid','B48',  'B48-PHEV', 1998, 3),
    ('EV',    'EM',   'IPM-150',     0, 4),
    ('EV',    'EM',   'IPM-200',     0, 4),
    ('I4',    'K20',  'K20C1',    1996, 1),
    ('I4',    'M274', 'M274',     1991, 1),
    ('I4',    'M256', 'M256',     2999, 3),
    ('I4',    'EA211','CZEA',     1395, 1),
    ('I4',    'EA888','DNPA',     1984, 1),
    ('I4',    'J20A', 'J20A',     1995, 1)
  ) e(type, series_code, engine_code, displacement_cc, fuel_type_id)
),
start_id AS (
  SELECT COALESCE(MAX(id), 0) AS s FROM cars_engine
)
INSERT INTO cars_engine (id, type, series_code, engine_code, displacement_cc, fuel_type_id)
SELECT (s.s + ROW_NUMBER() OVER ())::bigint,
       b.type, b.series_code, b.engine_code, b.displacement_cc, b.fuel_type_id
FROM base b, start_id s
WHERE NOT EXISTS (SELECT 1 FROM cars_engine LIMIT 1);
