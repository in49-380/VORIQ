INSERT INTO brands (id, name) VALUES (1, 'BMW');
INSERT INTO brands (id, name) VALUES (2, 'Audi');

INSERT INTO models (id, name, brand_id) VALUES (1, '1 Series', 1);
INSERT INTO models (id, name, brand_id) VALUES (2, 'A4', 2);

INSERT INTO engines (id, type) VALUES (1, '120i / 120d EQ Boost');
INSERT INTO engines (id, type) VALUES (2, 'A4 40 TFSI e');

INSERT INTO fuel_types (id, name) VALUES (1, 'Mild Hybrid');
INSERT INTO fuel_types (id, name) VALUES (2, 'PHEV');

INSERT INTO cars (id, engine_id, fuel_type_id, model_id, picture_car) VALUES (1, 1, 1, 1, null);
INSERT INTO cars (id, engine_id, fuel_type_id, model_id, picture_car) VALUES (2, 2, 2, 2, null);
