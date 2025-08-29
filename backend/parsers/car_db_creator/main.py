import requests
from lxml import html

from utils.get_db_json import (
    process_db_brands, process_db_models, process_db_engines,
    process_db_fuel_typs, process_db_year, process_db_cars
)
from utils.get_list_url import info_car, url_models, url_element_auto
from utils.get_urls import get_urls_models, get_urls_cars
from utils.headers import headers
from utils.translator import translate_car_info
from utils.save_load_data import (
    save_json, load_json
)
from utils.decorators import log_execution

"""
This module contains functions to scrape car data from an external source,
process the information, and export it into a local database.

The workflow includes:
1. Fetching car brands.
2. Processing models and filtering by year.
3. Scraping detailed car specifications.
4. Exporting the processed data to a database.
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
    return {b.get("value"): b.text_content() for b in brands}

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
def process_models():
    """
    Loads the list of car brands, constructs model URLs,
    fetches data for each model, filters by year (>=2015),
    and saves the result to "models.json".

    :return: None
    """
    models_list = []

    brands_data = load_json("brands.json")
    url_list = get_urls_models(brands_data, url_models)
    save_json(url_list, "url_list.json")

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
                        # "model": model["nick"],
                        "model": model["title"],
                        "year": model["title"][-4:],
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

    for url_list in urls_list[:50]:

        response_car = requests.get(url_list["url"], headers=headers)
        tree = html.fromstring(response_car.content)
        cars_elements = tree.xpath('//tbody[@id="cat4"]/tr')

        i = 1
        car_data = {}
        while i < len(cars_elements) - 1:
            key = cars_elements[i].text_content().strip()
            value = cars_elements[i + 1].text_content().strip()
            car_data[key] = value
            i += 3
        item = {
            'id': url_list["id"],
            'brand': url_list["brand"],
            'name': url_list["name"],
            'year': url_list["year"],
            'car_info': car_data
        }
        car_data_list.append(item)

    save_json(car_data_list, "cars.json")

@log_execution
def process_cars_en(input_file="cars.json", output_file="cars_en.json"):

    cars_ru = load_json(input_file)
    cars_en = []

    for car in cars_ru:
        car_en = car.copy()
        car_en["car_info"] = translate_car_info(car.get("car_info", {}))
        cars_en.append(car_en)

    save_json(cars_en, output_file)


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
    process_db_cars()

@log_execution
def main():
    """
    Main entry point: initiates sequential processing of brands, models,
    cars, and exports the data to the database.

    :return: None
    """
    brand_dict = fetch_brand_dict()
    process_brands(brand_dict)
    process_models()
    process_cars()
    process_cars_en()
    export_to_db()


if __name__ == '__main__':
    main()
