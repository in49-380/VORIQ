
"""
This module provides functions for dynamically generating URLs used in a web scraping
process. It contains two main functions: one for creating URLs for car models based
on a base URL and brand IDs, and another for generating URLs for individual car pages
using model IDs. The module ensures that URLs are structured correctly for data fetching.
"""

def get_urls_models(data, url):
    """
    Generates a list of URLs for car models based on input data.

    Each item is assigned an ID, name, and a URL constructed from the base URL and `id_infocar`.

    Args:
        data (list[dict]): A list of dictionaries containing model data. Each item must include:
            - "id": unique identifier of the model
            - "name": name of the model
            - "id_infocar": identifier used to construct the URL
        url (str): Base URL to which `id_infocar` is appended.

    Returns:
        list[dict]: A list of dictionaries with keys "id", "name", and "url".
    """
    url_list = []
    for element in data:
        item = {
            "id": element["id"],
            "name": element["name"],
            "url": f"{url}{element['id_infocar']}"
        }
        url_list.append(item)
    return url_list


def get_urls_cars(data, url):
    """
    Generates a list of URLs for cars based on input data.

    Each item is assigned a brand, model, year, and a URL constructed using `id_model_infocar`.

    Args:
        data (list[dict]): A list of dictionaries containing car data. Each item must include:
            - "brand_id": ID of the brand
            - "model": name of the model
            - "year": year of manufacture
            - "id_model_infocar": identifier used to construct the URL
        url (str): Base URL to which `id_model_infocar` and the `.html` extension are appended.

    Returns:
        list[dict]: A list of dictionaries with keys "id", "name", "year", and "url".

    """
    elem_url_html = ".html"
    url_list = []
    for element in data:
        item = {
            "id": element["brand_id"],
            "name": element["model"].capitalize(),
            "year": element["year"],
            "url": f"{url}{element['id_model_infocar']}-{element['id_model_infocar']}{elem_url_html}"
        }
        url_list.append(item)
    return url_list
