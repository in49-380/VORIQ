def get_urls_models(data, url):
    """
    Формирует список URL-ов для моделей на основе входных данных.

    Каждому элементу присваивается ID, имя и URL, сформированный из базового URL и `id_infocar`.

    Args:
        data (list[dict]): Список словарей с данными моделей. Каждый элемент должен содержать ключи:
            - "id": уникальный идентификатор модели
            - "name": название модели
            - "id_infocar": идентификатор для формирования ссылки
        url (str): Базовый URL, к которому добавляется `id_infocar`.

    Returns:
        list[dict]: Список словарей с ключами "id", "name", "url".

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
    Формирует список URL-ов для автомобилей на основе входных данных.

    Каждому элементу присваивается бренд, модель, год и URL, сформированный из `id_model_infocar`.

    Args:
        data (list[dict]): Список словарей с данными автомобилей. Каждый элемент должен содержать ключи:
            - "brand_id": ID бренда
            - "model": название модели
            - "year": год выпуска
            - "id_model_infocar": идентификатор модели для формирования ссылки
        url (str): Базовый URL, к которому добавляется `id_model_infocar` и расширение `.html`.

    Returns:
        list[dict]: Список словарей с ключами "id", "name", "year", "url".


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
