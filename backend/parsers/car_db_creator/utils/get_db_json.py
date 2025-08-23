from utils.decorators import logger
from utils.save_load_data import load_json, save_json


def process_db_brands():
    """
    Обрабатывает список брендов и сохраняет его в формате базы данных.

    Загружает данные из 'brands.json', извлекает поля 'id' и 'name',
    и сохраняет результат в 'brands_db.json'.

    Returns:
        bool: True, если сохранение прошло успешно, иначе False.

    Processes the list of car brands and saves it in a database-friendly format.

    Loads data from 'brands.json', extracts the 'id' and 'name' fields,
    and saves the result to 'brands_db.json'.

    Returns:
        bool: True if saving was successful, otherwise False.
    """
    brands = load_json("brands.json")
    brands_db_list = []
    for brand in brands:
        brand_item = {
            'id': brand['id'],
            'name': brand["name"]
        }
        brands_db_list.append(brand_item)
    return save_json(brands_db_list, "brands_db.json", "db_json")


def process_db_models():
    """
    Обрабатывает список моделей и сохраняет его в формате базы данных.

    Загружает данные из 'models.json', присваивает уникальный ID каждой модели,
    и сохраняет результат в 'models_db.json'.

    Returns:
        bool: True, если сохранение прошло успешно, иначе False.

    Processes the list of car models and saves it in a database-friendly format.

    Loads data from 'models.json', assigns a unique ID to each model,
    and saves the result to 'models_db.json'.

    Returns:
        bool: True if saving was successful, otherwise False.
    """
    models = load_json("models.json")
    models_db_list = []
    for index, model in enumerate(models, start=1):
        model_item = {
            "brand_id": model["brand_id"],
            "id": index,
            "name": model["model"].capitalize()
        }
        models_db_list.append(model_item)
    return save_json(models_db_list, "models_db.json", "db_json")


def process_db_year():
    """
    Извлекает уникальные года выпуска из моделей и сохраняет их в базу.

    Загружает 'models.json', собирает уникальные года, сортирует их,
    и сохраняет в 'years_db.json'.

    Returns:
        bool: True, если сохранение прошло успешно, иначе False.

    Extracts unique production years from car models and saves them to the database.

    Loads data from 'models.json', collects unique years, sorts them,
    and saves the result to 'years_db.json'.

    Returns:
        bool: True if saving was successful, otherwise False.
    """
    models = load_json("models.json")
    year_db_set = set()
    years_db_list = []
    for model in models:
        year_db_set.add(model["year"])
    sorted_years = sorted(year_db_set, key=int)
    for index, year in enumerate(sorted_years, start=1):
        model_item = {
            "id": index,
            "year": year
        }
        years_db_list.append(model_item)
    return save_json(years_db_list, "years_db.json", "db_json")


def process_db_fuel_typs():
    """
    Формирует список типов топлива и сохраняет его в базу.

    Предопределённый список включает бензин, дизель, газ и электро.

    Returns:
        bool: True, если сохранение прошло успешно, иначе False.

    Generates a list of fuel types and saves it to the database.

    The predefined list includes petrol, diesel, gas, and electric.

    Returns:
        bool: True if saving was successful, otherwise False.
    """
    fuel_types_db_list = [
        {"id": 1, "name": "Бензин"},
        {"id": 2, "name": "Дизель"},
        {"id": 3, "name": "Газ"},
        {"id": 4, "name": "Электро"}
    ]
    return save_json(fuel_types_db_list, "fuels_db.json", "db_json")


def process_db_engines():
    """
    Формирует список типов двигателей и сохраняет его в базу.

    Каждому типу двигателя соответствует fuel_type_id.

    Returns:
        bool: True, если сохранение прошло успешно, иначе False.

    Generates a list of engine types and saves it to the database.

    Each engine type is associated with a corresponding fuel_type_id.

    Returns:
        bool: True if saving was successful, otherwise False.
    """
    engines_db_list = [
        {"id": 101, "type": "ДВС", "fuel_type_id": 1},
        {"id": 102, "type": "ДВС", "fuel_type_id": 2},
        {"id": 103, "type": "ДВС", "fuel_type_id": 3},
        {"id": 104, "type": "Гибрид", "fuel_type_id": 1},
        {"id": 105, "type": "Гибрид", "fuel_type_id": 2},
        {"id": 106, "type": "Гибрид", "fuel_type_id": 3},
        {"id": 107, "type": "Электро", "fuel_type_id": 4}
    ]
    return save_json(engines_db_list, "engines_db.json", "db_json")


def process_db_cars():
    """
    Обрабатывает данные автомобилей и формирует финальную структуру для базы данных.

    Загружает вспомогательные данные (бренды, модели, двигатели, топливо, года),
    сопоставляет их с исходными данными автомобилей, определяет недостающие поля
    (например, тип топлива), и сохраняет результат в файл `cars_db.json`.

    Returns:
        bool: True, если данные успешно сохранены, иначе False.

    Processes car data and builds the final structure for the database.

    Loads auxiliary data (brands, models, engines, fuel types, years),
    matches it with the original car data, determines missing fields
    (e.g., fuel type), and saves the result to the `cars_db.json` file.

    Returns:
        bool: True if the data was successfully saved, otherwise False.
    """
    cars = []
    cars_time = load_json("cars.json")
    brands = load_json("brands_db.json", "db_json")
    years = load_json("years_db.json", "db_json")
    models = load_json("models_db.json", "db_json")
    engines = load_json("engines_db.json", "db_json")
    fuels = load_json("fuels_db.json", "db_json")

    brands_lookup = {brand["name"]: brand["id"] for brand in brands}
    models_lookup = {model["name"]: model["id"] for model in models}
    fuels_lookup = {fuel["name"]: fuel["id"] for fuel in fuels}
    years_lookup = {year["year"]: year["id"] for year in years}
    engines_lookup = {
        (engine["type"], engine["fuel_type_id"]): engine["id"]
        for engine in engines
    }

    # for index, model in enumerate(models, start=1):
    for index, car in enumerate(cars_time, start=1):
        logger.info(f"🚗 Обработка машины: {car['name']} (id: {car['id']})")

        model_id = models_lookup.get(car["name"])
        year_id = years_lookup.get(car["year"])
        car_info = car.get("car_info", {})

        engine_type = car_info.get("Тип двигателя")
        fuel_type_name = car_info.get("Тип топлива")

        # 🔧 Подстановка топлива, если оно отсутствует
        if not fuel_type_name:
            if engine_type and engine_type.lower() != "ДВС" or "Гибрид":
                fuel_type_name = "Электро"
                logger.info(f"⚡ Автоматически установлен тип топлива: '{fuel_type_name}'")
            else:
                fuel_type_name = "Бензин"
                logger.info(f"⛽ Тип топлива не указан, установлен по умолчанию: '{fuel_type_name}'")

        fuel_type_id = fuels_lookup.get(fuel_type_name)
        if fuel_type_id is None:
            logger.warning(f"❌ Не найден fuel_type_id для '{fuel_type_name}'")
            continue

        engine_id = engines_lookup.get((engine_type, fuel_type_id))
        if engine_id is None:
            logger.warning(f"❌ Не найден engine_id для пары: ({engine_type}, {fuel_type_id})")
            continue

        car_entry = {
            "id": index,
            "model_id": model_id,
            "engine_id": engine_id,
            "year_id": year_id
        }
        logger.info(f"✅ Добавлена запись: {car_entry}")
        cars.append(car_entry)

    logger.info(f"\n📦 Всего обработано машин: {len(cars)}")
    return save_json(cars, "cars_db.json", "db_json")
