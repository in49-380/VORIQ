import json
import logging
from functools import wraps
from pathlib import Path

# 📁 Настройка пути к лог-файлу
# Configures the path to the log file.
LOG_DIR = Path("data") / "files_log"
LOG_FILE = LOG_DIR / "parser.log"

# 📦 Создание папки для логов, если её нет
# Creates a folder for logs if it doesn't already exist.
LOG_DIR.mkdir(parents=True, exist_ok=True)

# 🛠️ Конфигурация логгера
# Logger configuration
logging.basicConfig(
    filename=str(LOG_FILE),
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
    encoding="utf-8"
)

logger = logging.getLogger("parser_logger")


# 📥 Декоратор для безопасной загрузки JSON
# Decorator for safe JSON loading.
def load_error_handler_json(func):
    """
    Оборачивает функцию загрузки JSON, добавляя обработку ошибок.

    Логирует ошибки: отсутствие файла, повреждённый JSON, другие исключения.

    Args:
        func (Callable): Функция загрузки JSON.

    Returns:
        Callable: Обёрнутая функция с обработкой ошибок.

    Wraps a JSON loading function with error handling.

    Logs errors such as missing files, corrupted JSON, and other exceptions.

    Args:
        func (Callable): The JSON loading function to be wrapped.

    Returns:
        Callable: The wrapped function with error handling.
    """

    @wraps(func)
    def wrapper(*args, **kwargs):
        try:
            return func(*args, **kwargs)
        except FileNotFoundError as e:
            logger.error(f"❌ Файл не найден: {e}")
            return None
        except json.JSONDecodeError as e:
            logger.error(f"❌ Неверный формат JSON: {e}")
            return None
        except Exception as e:
            logger.exception(f"❌ Ошибка при загрузке JSON: {e}")
            return None

    return wrapper


# 📤 Декоратор для безопасного сохранения JSON
# Decorator for safe JSON saving.
def save_error_handler_json(func):
    """
    Оборачивает функцию сохранения JSON, добавляя обработку ошибок.

    Логирует успешное сохранение и возможные исключения.

    Args:
        func (Callable): Функция сохранения JSON.

    Returns:
        Callable: Обёрнутая функция с обработкой ошибок.

    Wraps a JSON saving function with error handling.

    Logs successful saves and any exceptions that may occur.

    Args:
        func (Callable): The JSON saving function to be wrapped.

    Returns:
        Callable: The wrapped function with error handling.
    """

    @wraps(func)
    def wrapper(*args, **kwargs):
        try:
            result = func(*args, **kwargs)
            logger.info("✅ Данные успешно сохранены.")
            return result
        except IOError as e:
            logger.error(f"❌ Ошибка ввода-вывода: {e}")
        except Exception as e:
            logger.exception(f"❌ Ошибка при сохранении JSON: {e}")

    return wrapper
