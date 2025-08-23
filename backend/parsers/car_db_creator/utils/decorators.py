import json
import logging
from functools import wraps
from pathlib import Path

# Configures the path to the log file.
LOG_DIR = Path("data") / "files_log"
LOG_FILE = LOG_DIR / "parser.log"

# Creates a folder for logs if it doesn't already exist.
LOG_DIR.mkdir(parents=True, exist_ok=True)

# Logger configuration
logging.basicConfig(
    filename=str(LOG_FILE),
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
    encoding="utf-8"
)

logger = logging.getLogger("parser_logger")


# Decorator for safe JSON loading.
def load_error_handler_json(func):
    """
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
            logger.error(f"❌ File not found: {e}")
            return None
        except json.JSONDecodeError as e:
            logger.error(f"❌ Invalid JSON format: {e}")
            return None
        except Exception as e:
            logger.exception(f"❌ Error while loading JSON: {e}")
            return None

    return wrapper


# Decorator for safe JSON saving.
def save_error_handler_json(func):
    """
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
            logger.info("✅ Data saved successfully.")
            return result
        except IOError as e:
            logger.error(f"❌ Input/output error: {e}")
        except Exception as e:
            logger.exception(f"❌ Error while saving JSON: {e}")

    return wrapper
