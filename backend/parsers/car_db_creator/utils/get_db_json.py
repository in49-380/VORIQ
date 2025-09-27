
from .decorators import logger, log_execution
from .save_load_data import load_json, save_json
from .tools import extract_unique_records, extract_displacement, extract_transmission_type


"""
This module contains a set of functions for processing and transforming raw car data
into a structured format for database insertion. It handles the processing of brands,
models, years, fuel types, engines, and combines this information to create a final
dataset of cars.
"""

@log_execution
def process_db_brands():
    """
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

@log_execution
def process_db_models():
    """
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
            "name": model["model"][:-5].capitalize()
        }
        models_db_list.append(model_item)
    return save_json(models_db_list, "models_db.json", "db_json")

@log_execution
def process_db_year():
    """
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

@log_execution
def process_db_fuel_typs():
    """
    Generates a list of fuel types and saves it to the database.

    The predefined list includes petrol, diesel, gas, and electric.

    Returns:
        bool: True if saving was successful, otherwise False.
    """
    fuel_types_db_list = [
        {"id": 1, "name": "Petrol"},
        {"id": 2, "name": "Diesel"},
        {"id": 3, "name": "Gas"},
        {"id": 4, "name": "Electric"}
    ]
    return save_json(fuel_types_db_list, "fuels_db.json", "db_json")

@log_execution
def process_db_engines():
    """
    Generates a list of engine types and saves it to the database.

    Each engine type is associated with a corresponding fuel_type_id.

    Returns:
        bool: True if saving was successful, otherwise False.
    """
    engines_db_list = [
        {"id": 101, "type": "ICE", "fuel_type_id": 1},
        {"id": 102, "type": "ICE", "fuel_type_id": 2},
        {"id": 103, "type": "ICE", "fuel_type_id": 3},
        {"id": 104, "type": "Hybrid", "fuel_type_id": 1},
        {"id": 105, "type": "Hybrid", "fuel_type_id": 2},
        {"id": 106, "type": "Hybrid", "fuel_type_id": 3},
        {"id": 107, "type": "Electric", "fuel_type_id": 4}
    ]
    return save_json(engines_db_list, "engines_type_db.json", "db_json")
