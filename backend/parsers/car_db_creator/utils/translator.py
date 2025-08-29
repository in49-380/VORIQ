import re

from collections import OrderedDict
from googletrans import Translator
from .decorators import logger, log_execution
from .save_load_data import load_json, save_json

TRANSLATION_FILE = "translation_dict.json"
translation_dict=load_json(TRANSLATION_FILE)

translator = Translator()

def contains_cyrillic(text):
    return bool(re.search(r'[а-яА-ЯёЁ]', text))

@log_execution
def translate(term, src="ru", dest="en"):
    dictionary = translation_dict

    # ✅ Сначала проверяем словарь
    if term in dictionary:
        return dictionary[term]

    # ❗ Только потом фильтруем по кириллице
    if not contains_cyrillic(term):
        return term

    # 🌐 Переводим и сохраняем только кириллические термины
    try:
        result = translator.translate(term, src=src, dest=dest)
        translated = result.text
        dictionary[term] = translated
        save_json(dictionary, TRANSLATION_FILE)
        return translated
    except Exception as e:
        logger.error(f"❌ Translation error: {e}")
        return term


@log_execution
def translate_car_info(car_info):
    translated_info = OrderedDict()
    for key, value in car_info.items():
        key_en = translate(key)
        value_en = translate(value) if isinstance(value, str) else value
        translated_info[key_en] = value_en
    return translated_info

