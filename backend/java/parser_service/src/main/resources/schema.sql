drop table if exists cars CASCADE;
drop table if exists engines CASCADE;
drop table if exists fuel_types CASCADE;
drop table if exists models CASCADE;
drop table if exists brands CASCADE;

create table brands
(
    id       BIGSERIAL PRIMARY KEY,
    name     TEXT NOT NULL UNIQUE
);

create table models
(
    id       BIGSERIAL PRIMARY KEY,
    brand_id BIGINT NOT NULL REFERENCES brands(id) ON DELETE CASCADE,
    name     TEXT NOT NULL UNIQUE
);

create table engines
(
    id       BIGSERIAL PRIMARY KEY,
    type     TEXT NOT NULL UNIQUE
);

create table fuel_types
(
    id       BIGSERIAL PRIMARY KEY,
    name     TEXT NOT NULL UNIQUE
);

create table cars
(
    id                  BIGSERIAL PRIMARY KEY,
    engine_id           BIGINT NOT NULL REFERENCES engines(id) ON DELETE CASCADE,
    fuel_type_id        BIGINT NOT NULL REFERENCES fuel_types(id) ON DELETE CASCADE,
    model_id            BIGINT NOT NULL REFERENCES models(id) ON DELETE CASCADE,
    picture_car         TEXT
);
