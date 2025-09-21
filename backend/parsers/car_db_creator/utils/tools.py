import re
from .save_load_data import load_json, save_json

def extract_displacement(raw_value):
    """
    Extracts the first numeric value (integer or float) from a raw string input.
    Returns the value as a float if found, otherwise returns None.

    :param raw_value: Raw input value (typically a string) containing numeric data
    :return: Float representation of the first number found, or None if no valid number is present
    """
    match = re.search(r"\d+(\.\d+)?", str(raw_value))
    if match:
        return float(match.group())
    return None

def extract_transmission_type(car: dict):
    """
    Normalizes specific fields in a car dictionary:
    - Converts 'displacement' to float.
    - Converts 'transmission_type' to boolean: False if 'Mechanics', else True.

    :param car: Dictionary containing car attributes.
    :return: Updated dictionary with normalized fields.
    """
    for key in car:
        if key == "displacement":
            car[key] = extract_displacement(car[key])
        elif key == "transmission_type":
            car[key] = False if car[key] == "Mechanics" else True
    return car

def parser_table(input_table):
    """
    Parses a structured HTML table represented as a list of rows and extracts key-value pairs.

    The function assumes that each key is followed by its corresponding value in the next row,
    and skips every third row to move to the next pair.

    :param input_table: List of HTML row elements (e.g., from XPath)
    :return: Dictionary with extracted key-value pairs from the table
    """
    elements_data = {}
    i = 1

    while i < len(input_table) - 1:
        key_row = input_table[i]
        value_row = input_table[i + 1]

        key = key_row.xpath('.//td/text()')
        value = value_row.xpath('.//td/text()')

        key = key[0].strip() if key else None
        value = value[0].strip() if value else None

        if key and value:
            elements_data[key] = value

        i += 3

    return elements_data

def extract_unique_records(name_file='cars_en.json', find_element=''):
    """
    Extracts unique values for a specified key from a list of dictionaries in a JSON file
    and saves them as a list of dictionaries to a new JSON file.

    Useful for building reference tables (e.g., engines, transmissions) for database import.

    :param name_file: Name of the input JSON file containing car data
    :param find_element: Key to search for in each dictionary
    :return: Boolean indicating whether the data was successfully saved
    """

    data = load_json(name_file)
    elements_set = set()
    elements_list = []
    for elem in data:
        if find_element not in elem:
            continue

        value = elem[find_element]
        if value not in elements_set:
            elements_set.add(value)
            elements_list.append({'name': value})
    file_name_db = f"{find_element}_db.json"
    return save_json(elements_list, file_name_db.lower(), "db_json")


def add_if_exists(target_dict, source_dict, key):
    """
    Adds a key-value pair from the source dictionary to the target dictionary
    only if the key exists and its value is not None.

    :param target_dict: Dictionary to which the key-value pair will be added
    :param source_dict: Dictionary from which the value will be retrieved
    :param key: Key to check and copy from source to target
    :return: None
    """

    value = source_dict.get(key)
    if value is not None:
        target_dict[key] = value

def add_element(source_dict,key):

    value = source_dict.get(key)
    return value





