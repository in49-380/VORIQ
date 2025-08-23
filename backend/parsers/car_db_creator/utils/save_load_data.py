import json
import os
from pathlib import Path

from utils.decorators import save_error_handler_json, load_error_handler_json


def find_project_root(marker_file='main.py'):
    """
    Поиск корня проекта по файлу-маркеру.

    Поднимается вверх по дереву директорий, пока не найдёт указанный файл.
    Используется для построения абсолютных путей от корня проекта.

    Args:
        marker_file (str): Имя файла, по которому определяется корень проекта.

    Returns:
        Path: Путь к корневой директории проекта.

    Raises:
        FileNotFoundError: Если файл-маркер не найден в иерархии директорий.

    Searches for the project root directory using a marker file.

    Traverses upward through the directory tree until the specified file is found.
    Used to construct absolute paths relative to the project root.

    Args:
        marker_file (str): The name of the file that identifies the project root.

    Returns:
        Path: Path to the project's root directory.

    Raises:
        FileNotFoundError: If the marker file is not found in the directory hierarchy.

    """
    current_dir = Path(__file__).resolve().parent

    while not (current_dir / marker_file).exists():

        if current_dir.parent == current_dir:
            raise FileNotFoundError(f"Не удалось найти корень проекта с маркером '{marker_file}'.")
        current_dir = current_dir.parent

    return current_dir


@save_error_handler_json
def save_json(data, file_name, subfolder=None):
    """
    Сохраняет данные в JSON-файл по указанному пути.

    Args:
        data (list | dict): Данные для сохранения.
        file_name (str): Имя файла JSON.
        subfolder (str | None): Подпапка внутри 'files_json' (например, 'db_json').
                                Если None — сохраняет прямо в 'files_json'.

    Returns:
        None

    Saves data to a JSON file at the specified path.

    Args:
        data (list | dict): The data to be saved.
        file_name (str): Name of the JSON file.
        subfolder (str | None): Subfolder inside 'files_json' (e.g., 'db_json').
                            If None, saves directly into 'files_json'.

    Returns:
        None
    """
    project_root = find_project_root()

    base_path = project_root / 'data' / 'files_json'
    if subfolder:
        base_path = base_path / subfolder
    file_path = base_path / file_name

    file_path.parent.mkdir(parents=True, exist_ok=True)

    with open(file_path, "w", encoding="utf-8") as file:
        json.dump(data, file, ensure_ascii=False, indent=4)


@load_error_handler_json
def load_json(file_name, subfolder=None):
    """
    Загружает данные из JSON-файла по указанному пути.

    Args:
        subfolder (str | None): Подпапка внутри 'files_json' (например, 'db_json').
                                Если None — загружает из 'files_json'.
        file_name (str): Имя файла JSON.

    Returns:
        Any: Загруженные данные (обычно список или словарь).

    Raises:
        FileNotFoundError: Если файл не найден.
        json.JSONDecodeError: Если файл повреждён или невалидный JSON.

    Loads data from a JSON file at the specified path.

    Args:
        subfolder (str | None): Subfolder inside 'files_json' (e.g., 'db_json').
                            If None, loads from 'files_json' directly.
        file_name (str): Name of the JSON file.

    Returns:
        Any: Loaded data (typically a list or dictionary).

    Raises:
        FileNotFoundError: If the file is not found.
        json.JSONDecodeError: If the file is corrupted or contains invalid JSON.
    """
    project_root = find_project_root()

    base_path = project_root / 'data' / 'files_json'
    if subfolder:
        base_path = base_path / subfolder
    file_path = base_path / file_name

    with open(file_path, "r", encoding="utf-8") as file:
        return json.load(file)


def append_to_json_file(new_data, filename="models_url.json"):
    """
    Добавляет новые данные в существующий JSON-файл или создаёт новый.

    Если файл существует — данные читаются и расширяются.
    Если файл повреждён или отсутствует — создаётся новый список.

    Args:
        new_data (list): Список новых данных для добавления.
        filename (str): Имя JSON-файла (по умолчанию "models_url.json").

    Returns:
        None

    Appends new data to an existing JSON file or creates a new one.

    If the file exists, its contents are read and extended.
    If the file is corrupted or missing, a new list is created.

    Args:
        new_data (list): List of new data to be added.
        filename (str): Name of the JSON file (default is "models_url.json").

    Returns:
        None
    """
    if os.path.exists(filename):
        with open(filename, "r", encoding="utf-8") as f:
            try:
                existing_data = json.load(f)
            except json.JSONDecodeError:
                existing_data = []
    else:
        existing_data = []

    existing_data.extend(new_data)

    with open(filename, "w", encoding="utf-8") as f:
        json.dump(existing_data, f, ensure_ascii=False, indent=4)
