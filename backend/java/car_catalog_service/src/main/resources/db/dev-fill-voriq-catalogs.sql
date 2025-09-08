-- dev-fill-voriq-catalogs.sql
SET search_path = public;

WITH base AS (
  SELECT *
  FROM (VALUES ('FWD'), ('RWD'), ('AWD'), ('4WD')) v(name)
),
start_id AS ( SELECT COALESCE(MAX(id), 0) AS s FROM cars_whilldrive )
INSERT INTO cars_whilldrive (id, name)
SELECT (s.s + ROW_NUMBER() OVER ())::bigint AS id, b.name
FROM base b CROSS JOIN start_id s
WHERE NOT EXISTS (SELECT 1 FROM cars_whilldrive LIMIT 1);

WITH base AS (
  SELECT *
  FROM (VALUES
    (5,  'Getrag',  'G5',    'Getrag-5',   true),
    (6,  'Getrag',  'G6',    'Getrag-6',   true),
    (6,  'Aisin',   'TF-60', 'Aisin-6',    false),
    (8,  'Aisin',   'TB-80', 'Aisin-8',    false),
    (7,  'Getrag',  'DCT7',  'DCT-7',      false),
    (7,  'VW',      'DQ200', 'DSG-7',      false),
    (8,  'ZF',      '8HP',   'ZF-8HP',     false),
    (9,  'ZF',      '9HP',   'ZF-9HP',     false),
    (10, 'Ford',    '10R',   'Ford-10AT',  false),
    (10, 'GM',      '10L',   'GM-10AT',    false),
    (7,  'Hyundai', '7DCT',  '7-DCT',      false),
    (0,  'Jatco',   'JFCVT', 'CVT',        false)
  ) t(gears, supplier, family_code, marketing_name, manual)
),
start_id AS ( SELECT COALESCE(MAX(id), 0) AS s FROM cars_transmission )
INSERT INTO cars_transmission (id, gears, supplier, family_code, marketing_name, manual)
SELECT (s.s + ROW_NUMBER() OVER ())::bigint AS id,
       b.gears, b.supplier, b.family_code, b.marketing_name, b.manual
FROM base b CROSS JOIN start_id s
WHERE NOT EXISTS (SELECT 1 FROM cars_transmission LIMIT 1);

WITH base AS (
  SELECT *
  FROM (VALUES
    ('I3',    'B38',  'B38A15',   1499, 1),
    ('I4',    'B48',  'B48B20',   1998, 1),
    ('I6',    'B58',  'B58B30',   2998, 1),
    ('V8',    'N63',  'N63B44',   4395, 1),
    ('I4',    'TDI',  'EA288',    1968, 2),
    ('V6',    'TDI',  'EA897',    2967, 2),
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
start_id AS ( SELECT COALESCE(MAX(id), 0) AS s FROM cars_engine )
INSERT INTO cars_engine (id, type, series_code, engine_code, displacement_cc, fuel_type_id)
SELECT (s.s + ROW_NUMBER() OVER ())::bigint AS id,
       b.type, b.series_code, b.engine_code, b.displacement_cc, b.fuel_type_id
FROM base b CROSS JOIN start_id s
WHERE NOT EXISTS (SELECT 1 FROM cars_engine LIMIT 1);
