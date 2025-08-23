import requests
from lxml import html

from utils.get_db_json import (
    process_db_brands, process_db_models, process_db_engines,
    process_db_fuel_typs, process_db_year, process_db_cars
)
from utils.get_list_url import info_car, url_models, url_element_auto
from utils.get_urls import get_urls_models, get_urls_cars
from utils.headers import headers
from utils.save_load_data import (
    save_json, load_json
)


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

    for url_list in urls_list[:100]:
        id = url_list["id"]
        name = url_list["name"]
        year = url_list["year"]
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
            'id': id,
            'name': name,
            'year': year,
            'car_info': car_data
        }
        car_data_list.append(item)

    save_json(car_data_list, "cars.json")


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
    export_to_db()


if __name__ == '__main__':
    main()
