-- seed_cars_exact_500.sql
SET search_path = public;

-- Без DO $$: безопасная синхронизация sequence для cars_car
WITH seq AS (
  SELECT pg_get_serial_sequence('cars_car','id') AS s
),
mx AS (
  SELECT COALESCE(MAX(id), 0) AS m FROM cars_car
)
SELECT CASE
         WHEN seq.s IS NULL THEN NULL                                 -- нет sequence у столбца
         WHEN mx.m = 0        THEN setval(seq.s::regclass, 1, false)  -- пустая таблица: следующий nextval() = 1
         ELSE                        setval(seq.s::regclass, mx.m, true)   -- есть данные: следующий nextval() = MAX(id)+1
       END
FROM seq, mx;

-- Сколько записей нужно довставить
WITH params AS (
  SELECT COALESCE(NULLIF(current_setting('voriq.seed_target_cars_car', true), '')::int, 500) AS target
),
existing AS (SELECT COUNT(*) AS cnt FROM cars_car),
need AS (
  SELECT GREATEST((SELECT target FROM params) - (SELECT cnt FROM existing), 0) AS n
),

-- Используем все модели (без LIMIT), остальные справочники ограничены разумно
m  AS (SELECT id FROM cars_carmodel     ORDER BY id),          -- все модели
e  AS (SELECT id FROM cars_engine       ORDER BY id         LIMIT 5),
mk AS (SELECT id FROM cars_market       ORDER BY id         LIMIT 4),
t  AS (SELECT id FROM cars_transmission ORDER BY id         LIMIT 3),
d  AS (SELECT id FROM cars_whilldrive   ORDER BY id         LIMIT 3),
y  AS (SELECT id FROM cars_year         ORDER BY "year", id LIMIT 6),

-- Все потенциальные комбинации
combos AS (
  SELECT m.id  AS model_id,
         e.id  AS engine_id,
         mk.id AS market_id,
         t.id  AS transmission_id,
         d.id  AS whill_drive_id,
         y.id  AS year_id
  FROM m CROSS JOIN e CROSS JOIN mk CROSS JOIN t CROSS JOIN d CROSS JOIN y
),

-- Оставляем только те, которых ещё нет
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

-- Нумерация внутри каждой модели (для round-robin)
ranked AS (
  SELECT
    ROW_NUMBER() OVER (
      PARTITION BY model_id
      ORDER BY engine_id, market_id, transmission_id, whill_drive_id, year_id
    ) AS rn_by_model,
    *
  FROM missing
),

-- Глобальный порядок: round-robin по моделям
ordered AS (
  SELECT
    ROW_NUMBER() OVER (
      ORDER BY rn_by_model, model_id, engine_id, market_id, transmission_id, whill_drive_id, year_id
    ) AS global_rn,
    *
  FROM ranked
),

-- Берём столько, сколько нужно довставить
to_insert AS (
  SELECT *
  FROM ordered
  WHERE global_rn <= (SELECT n FROM need)
),

-- Подготовка к выбору ID (через sequence, если есть; иначе от MAX(id))
seqname AS (SELECT pg_get_serial_sequence('cars_car','id') AS s),
base    AS (SELECT COALESCE(MAX(id),0) AS base FROM cars_car)

INSERT INTO cars_car (id, model_id, engine_id, market_id, transmission_id, whill_drive_id, year_id)
SELECT
  CASE WHEN (SELECT s FROM seqname) IS NOT NULL
         THEN nextval((SELECT s FROM seqname))
       ELSE (SELECT base FROM base) + global_rn
  END,
  model_id, engine_id, market_id, transmission_id, whill_drive_id, year_id
FROM to_insert;
