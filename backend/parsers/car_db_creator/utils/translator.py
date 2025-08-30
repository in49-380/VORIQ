import re

from collections import OrderedDict
from googletrans import Translator
from .decorators import logger
from .save_load_data import load_json, save_json

"""
Translation utilities for converting car information from Russian to English.

This module provides functions to:
- Detect Cyrillic characters in text
- Translate individual terms using Google Translate with caching
- Translate structured car information dictionaries
- Persist translations to a local JSON dictionary for reuse

Dependencies:
- googletrans for translation
- Custom modules for logging and JSON file operations
"""


TRANSLATION_FILE = "translation_dict.json"
translation_dict=load_json(TRANSLATION_FILE)

translator = Translator()

def contains_cyrillic(text):
    """
    Checks whether the given text contains any Cyrillic characters.

    :param text: Input string to check
    :return: True if Cyrillic characters are found, False otherwise
    """
    return bool(re.search(r'[а-яА-ЯёЁ]', text))


def translate(term, src="ru", dest="en"):
    """
    Translates a single term from Russian to English using Google Translate,
    with caching to avoid redundant translations.

    - If the term is already in the local dictionary, returns the cached translation.
    - If the term does not contain Cyrillic characters, returns it unchanged.
    - Otherwise, translates the term and updates the dictionary.

    :param term: The word or phrase to translate
    :param src: Source language code (default: "ru")
    :param dest: Destination language code (default: "en")
    :return: Translated term as a string
    """
    dictionary = translation_dict


    if term in dictionary:
        return dictionary[term]


    if not contains_cyrillic(term):
        return term


    try:
        result = translator.translate(term, src=src, dest=dest)
        translated = result.text
        dictionary[term] = translated
        save_json(dictionary, TRANSLATION_FILE)
        return translated
    except Exception as e:
        logger.error(f"❌ Translation error: {e}")
        return term



def translate_car_info(car_info):
    """
    Translates the keys and string values of a car information dictionary from Russian to English.

    Non-string values are preserved as-is. The result is returned as an OrderedDict
    to maintain the original key order.

    :param car_info: Dictionary containing car attributes in Russian
    :return: OrderedDict with translated keys and values
    """
    translated_info = OrderedDict()
    for key, value in car_info.items():
        key_en = translate(key)
        value_en = translate(value) if isinstance(value, str) else value
        translated_info[key_en] = value_en
    return translated_info

