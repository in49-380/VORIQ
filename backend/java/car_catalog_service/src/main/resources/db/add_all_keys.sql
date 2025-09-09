
CREATE INDEX IF NOT EXISTS ix_cars_car_model_id        ON cars_car(model_id);
CREATE INDEX IF NOT EXISTS ix_cars_car_engine_id       ON cars_car(engine_id);
CREATE INDEX IF NOT EXISTS ix_cars_car_market_id       ON cars_car(market_id);
CREATE INDEX IF NOT EXISTS ix_cars_car_transmission_id ON cars_car(transmission_id);
CREATE INDEX IF NOT EXISTS ix_cars_car_whill_drive_id  ON cars_car(whill_drive_id);
CREATE INDEX IF NOT EXISTS ix_cars_car_year_id         ON cars_car(year_id);

DROP INDEX IF EXISTS ux_cars_car_combo;
CREATE UNIQUE INDEX IF NOT EXISTS ux_cars_car_combo
  ON cars_car (model_id, engine_id, market_id, transmission_id, whill_drive_id, year_id);

CREATE INDEX IF NOT EXISTS ix_cars_carmodel_brand_id ON cars_carmodel(brand_id);
ALTER TABLE cars_carmodel DROP CONSTRAINT IF EXISTS fk_carmodel_brand;
ALTER TABLE cars_carmodel
  ADD CONSTRAINT fk_carmodel_brand
  FOREIGN KEY (brand_id) REFERENCES cars_brand(id)
  ON UPDATE RESTRICT ON DELETE RESTRICT;

CREATE INDEX IF NOT EXISTS ix_cars_engine_fuel_type_id ON cars_engine(fuel_type_id);
ALTER TABLE cars_engine DROP CONSTRAINT IF EXISTS fk_engine_fueltype;
ALTER TABLE cars_engine
  ADD CONSTRAINT fk_engine_fueltype
  FOREIGN KEY (fuel_type_id) REFERENCES cars_fueltype(id)
  ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE cars_car DROP CONSTRAINT IF EXISTS fk_car_model;
ALTER TABLE cars_car ADD  CONSTRAINT fk_car_model
  FOREIGN KEY (model_id) REFERENCES cars_carmodel(id)
  ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE cars_car DROP CONSTRAINT IF EXISTS fk_car_engine;
ALTER TABLE cars_car ADD  CONSTRAINT fk_car_engine
  FOREIGN KEY (engine_id) REFERENCES cars_engine(id)
  ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE cars_car DROP CONSTRAINT IF EXISTS fk_car_market;
ALTER TABLE cars_car ADD  CONSTRAINT fk_car_market
  FOREIGN KEY (market_id) REFERENCES cars_market(id)
  ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE cars_car DROP CONSTRAINT IF EXISTS fk_car_transmission;
ALTER TABLE cars_car ADD  CONSTRAINT fk_car_transmission
  FOREIGN KEY (transmission_id) REFERENCES cars_transmission(id)
  ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE cars_car DROP CONSTRAINT IF EXISTS fk_car_whilldrive;
ALTER TABLE cars_car ADD  CONSTRAINT fk_car_whilldrive
  FOREIGN KEY (whill_drive_id) REFERENCES cars_whilldrive(id)
  ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE cars_car DROP CONSTRAINT IF EXISTS fk_car_year;
ALTER TABLE cars_car ADD  CONSTRAINT fk_car_year
  FOREIGN KEY (year_id) REFERENCES cars_year(id)
  ON UPDATE RESTRICT ON DELETE RESTRICT;

CREATE UNIQUE INDEX IF NOT EXISTS ux_cars_searchquery_id_idx ON cars_searchquery(id);

CREATE INDEX IF NOT EXISTS ix_cars_searchquery_brand_id         ON cars_searchquery(brand_id);
CREATE INDEX IF NOT EXISTS ix_cars_searchquery_model_id         ON cars_searchquery(model_id);
CREATE INDEX IF NOT EXISTS ix_cars_searchquery_engine_id        ON cars_searchquery(engine_id);
CREATE INDEX IF NOT EXISTS ix_cars_searchquery_fuel_type_id     ON cars_searchquery(fuel_type_id);
CREATE INDEX IF NOT EXISTS ix_cars_searchquery_market_id        ON cars_searchquery(market_id);
CREATE INDEX IF NOT EXISTS ix_cars_searchquery_transmission_id  ON cars_searchquery(transmission_id);
CREATE INDEX IF NOT EXISTS ix_cars_searchquery_drive_layout_id  ON cars_searchquery(drive_layout_id);
CREATE INDEX IF NOT EXISTS ix_cars_searchquery_year_id          ON cars_searchquery(year_id);

ALTER TABLE cars_searchquery DROP CONSTRAINT IF EXISTS fk_searchquery_brand;
ALTER TABLE cars_searchquery ADD  CONSTRAINT fk_searchquery_brand
  FOREIGN KEY (brand_id) REFERENCES cars_brand(id)
  ON UPDATE RESTRICT ON DELETE SET NULL;

ALTER TABLE cars_searchquery DROP CONSTRAINT IF EXISTS fk_searchquery_model;
ALTER TABLE cars_searchquery ADD  CONSTRAINT fk_searchquery_model
  FOREIGN KEY (model_id) REFERENCES cars_carmodel(id)
  ON UPDATE RESTRICT ON DELETE SET NULL;

ALTER TABLE cars_searchquery DROP CONSTRAINT IF EXISTS fk_searchquery_engine;
ALTER TABLE cars_searchquery ADD  CONSTRAINT fk_searchquery_engine
  FOREIGN KEY (engine_id) REFERENCES cars_engine(id)
  ON UPDATE RESTRICT ON DELETE SET NULL;

ALTER TABLE cars_searchquery DROP CONSTRAINT IF EXISTS fk_searchquery_fueltype;
ALTER TABLE cars_searchquery ADD  CONSTRAINT fk_searchquery_fueltype
  FOREIGN KEY (fuel_type_id) REFERENCES cars_fueltype(id)
  ON UPDATE RESTRICT ON DELETE SET NULL;

ALTER TABLE cars_searchquery DROP CONSTRAINT IF EXISTS fk_searchquery_market;
ALTER TABLE cars_searchquery ADD  CONSTRAINT fk_searchquery_market
  FOREIGN KEY (market_id) REFERENCES cars_market(id)
  ON UPDATE RESTRICT ON DELETE SET NULL;

ALTER TABLE cars_searchquery DROP CONSTRAINT IF EXISTS fk_searchquery_transmission;
ALTER TABLE cars_searchquery ADD  CONSTRAINT fk_searchquery_transmission
  FOREIGN KEY (transmission_id) REFERENCES cars_transmission(id)
  ON UPDATE RESTRICT ON DELETE SET NULL;

ALTER TABLE cars_searchquery DROP CONSTRAINT IF EXISTS fk_searchquery_drive_layout;
ALTER TABLE cars_searchquery ADD  CONSTRAINT fk_searchquery_drive_layout
  FOREIGN KEY (drive_layout_id) REFERENCES cars_whilldrive(id)
  ON UPDATE RESTRICT ON DELETE SET NULL;

ALTER TABLE cars_searchquery DROP CONSTRAINT IF EXISTS fk_searchquery_year;
ALTER TABLE cars_searchquery ADD  CONSTRAINT fk_searchquery_year
  FOREIGN KEY (year_id) REFERENCES cars_year(id)
  ON UPDATE RESTRICT ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS ix_cars_searchresult_car_id          ON cars_searchresult(car_id);
CREATE INDEX IF NOT EXISTS ix_cars_searchresult_search_query_id ON cars_searchresult(search_query_id);

ALTER TABLE cars_searchresult DROP CONSTRAINT IF EXISTS fk_searchresult_car;
ALTER TABLE cars_searchresult ADD  CONSTRAINT fk_searchresult_car
  FOREIGN KEY (car_id) REFERENCES cars_car(id)
  ON UPDATE RESTRICT ON DELETE CASCADE;

ALTER TABLE cars_searchresult DROP CONSTRAINT IF EXISTS fk_searchresult_query;
ALTER TABLE cars_searchresult ADD  CONSTRAINT fk_searchresult_query
  FOREIGN KEY (search_query_id) REFERENCES cars_searchquery(id)
  ON UPDATE RESTRICT ON DELETE CASCADE;
