import json
import requests
import re
from lxml import html

from utils.get_db_json import (
    process_db_brands, process_db_models, process_db_engines,
    process_db_fuel_typs, process_db_year, process_db_cars,
)
from utils.get_list_url import info_car, url_models, url_element_auto
from utils.get_urls import get_urls_models, get_urls_cars
from utils.headers import headers
from utils.tools import parser_table, extract_unique_records, add_if_exists
from utils.translator import translate_car
from utils.save_load_data import (
    save_json, load_json
)
from utils.decorators import log_execution

"""
This module implements an ETL pipeline to extract car data from infocar.ua,
transform it into structured JSON format, and load it into a local database.

Workflow steps:
1. Fetch car brands from HTML.
2. Generate model URLs and filter models by year (>=2015).
3. Scrape detailed car specifications from individual pages.
4. Translate car data to English.
5. Export structured data to a local database.

Generated files:
- brands.json: List of car brands with internal and external IDs.
- url_list.json: URLs for car models.
- models.json: Filtered car models with metadata.
- cars.json: Raw car specifications in Russian.
- cars_en.json / cars_en_db.json: Translated car data for database import.

Technologies used:
- requests, lxml, XPath for web scraping.
- JSON for data serialization.
- Custom decorators for logging.
- Translation via internal utility.

Note:
- Only the first 100 car URLs are processed to reduce load.
- The main() function orchestrates the entire pipeline.
"""

@log_execution
def fetch_brand_dict():
    """
    Fetches an HTML page containing car brand information,
    extracts the list of brands, and builds a dictionary with each brand's ID and name.

    :return: Dictionary of brands in the format {id: name}
    """
    response = requests.get(info_car, headers=headers)
    tree = html.fromstring(response.content)
    brands = tree.xpath('//select[@class="smark mui-select"]/option')
    cyrillic_pattern = re.compile('[\u0400-\u04FF]+')
    return {
        b.get("value"): b.text_content()
        for b in brands
        if not cyrillic_pattern.search(b.text_content())
    }

@log_execution
def process_brands(brands):
    """
    Converts a dictionary of car brands into a list of dictionaries with additional fields
    and saves it to the "brands.json" file.

    :param brands: Dictionary of brands in the format {id: name}
    :return: None
    """
    brands_list = []
    for index, (brand_id, name) in enumerate(brands.items(), start=1):
        item = {
            'id': index,
            'name': name,
            'id_infocar': brand_id
        }
        brands_list.append(item)

    save_json(brands_list, "brands.json")

@log_execution
def process_url_list():
    """
    Generates a list of model URLs based on brand data and saves it to a file.

    Loads brand information from 'brands.json', constructs a list of model URLs
    using the `get_urls_models` function, and writes the result to 'url_list.json'.

    Returns:
        bool: True if the data was successfully saved, otherwise False.
    """
    brands_data = load_json("brands.json")
    urls_list = get_urls_models(brands_data, url_models)
    return save_json(urls_list, "url_list.json")

@log_execution
def process_models():
    """
    Loads the list of car brands, constructs model URLs,
    fetches data for each model, filters by year (>=2015),
    and saves the result to "models.json".

    :return: None
    """
    models_list = []

    urls_list = load_json("url_list.json")

    for url_list in urls_list:
        id = url_list["id"]
        name = url_list["name"]
        response_model = requests.get(url_list["url"], headers=headers)
        response_model.raise_for_status()
        models_data = response_model.json()
        models = models_data["models"]

        for model in models:
            year = model["title"][-4:]

            if year.isdigit():
                year = int(year)
                if year < 2015:
                    continue
                else:
                    item = {
                        "brand_name": name,
                        "brand_id": id,
                        "year": year,
                        "model": model["title"],
                        "id_model_infocar": model["id"]
                    }
                    models_list.append(item)

    save_json(models_list, "models.json")

@log_execution
def process_cars():
    """

    Loads the list of car models, constructs URLs for individual cars,
    extracts specifications of each car from the HTML page,
    and saves them to "cars.json".

    :return: None
    """
    models_car_list = load_json("models.json")
    url_list_car = get_urls_cars(models_car_list, url_element_auto)
    save_json(url_list_car, "url_cars_list.json")
    urls_list = load_json("url_cars_list.json")
    car_data_list = []

    for url_list in urls_list[:500]:

        response_car = requests.get(url_list["url"], headers=headers)
        tree = html.fromstring(response_car.content)
        cars_elements_engine = tree.xpath('//tbody[@id="cat4"]/tr')
        cars_elements_transmission = tree.xpath('//*[@id="cat43"]/tr')
        item_engine = parser_table(cars_elements_engine)
        item_transmission = parser_table(cars_elements_transmission)

        item = {
            'brand': url_list["brand"],
            'model': url_list["name"][:-5],
            'year': url_list["year"],
        }

        add_if_exists(item, item_engine, 'Двигатель')
        add_if_exists(item, item_engine, 'Тип двигателя')
        add_if_exists(item, item_engine, 'Тип топлива')
        add_if_exists(item, item_transmission, 'Тип коробки передач')
        add_if_exists(item, item_transmission, 'Кол-во передач')
        add_if_exists(item, item_transmission, 'Привод')

        car_data_list.append(item)
    save_json(car_data_list, "cars.json")


@log_execution
def process_cars_en(input_file="cars.json", output_file="cars_en.json"):
    """
    Loads a list of car dictionaries from a JSON file, translates the 'car_info' field into English,
    and saves the updated list to a new JSON file.

    :param input_file: Name of the input JSON file containing car data in Russian.
    :param output_file: Name of the output JSON file to save translated car data.
    :return: None
    """

    cars_ru = load_json(input_file)
    cars_en = []
    cars_en = [translate_car(car) for car in cars_ru]
    # for car in cars_ru:
    #     car_en = translate_car(car)
    #     cars_en.append(car_en)

    save_json(cars_en, output_file)
    save_json(cars_en, "cars_en_db.json", "db_json")

@log_execution
def export_to_db():
    """
    Calls functions to export data from intermediate JSON files into the database.

    :return: None
    """
    process_db_brands()
    process_db_models()
    process_db_engines()
    process_db_fuel_typs()
    process_db_year()
    # process_db_cars()
    extract_unique_records(find_element='engine')
    extract_unique_records(find_element='drive')
    extract_unique_records(find_element='transmission_type')
    extract_unique_records(find_element='number_gears')

@log_execution
def main():
    """
    Main entry point: initiates sequential processing of brands, models,
    cars, and exports the data to the database.

    :return: None
    """
    brand_dict = fetch_brand_dict()
    process_brands(brand_dict)
    process_url_list()
    process_models()
    process_cars()
    process_cars_en()
    export_to_db()


if __name__ == '__main__':
    main()
