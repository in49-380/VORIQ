import time
import json
import logging
from functools import wraps
from pathlib import Path

"""
This module provides utility functions for handling JSON data with robust logging. 
It includes decorators to wrap JSON loading and saving operations, ensuring that 
potential errors (e.g., FileNotFoundError, JSONDecodeError) are caught and logged, 
while successful operations are also documented. The module is configured with a 
custom logger to save all events to a designated log file.
"""

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
    Wraps a JSON saving function with error handling and logs filename and execution time.
    """
    @wraps(func)
    def wrapper(*args, **kwargs):
        filename = kwargs.get("filename") or (args[1] if len(args) > 1 else "unknown")
        start_time = time.time()

        try:
            result = func(*args, **kwargs)
            duration = time.time() - start_time
            logger.info(f"✅ Data saved successfully to '{filename}' in {duration:.2f} seconds")
            return result
        except IOError as e:
            logger.error(f"❌ Input/output error while saving '{filename}': {e}")
        except Exception as e:
            logger.exception(f"❌ Error while saving JSON to '{filename}': {e}")

    return wrapper

def log_execution(func):
    """
    Logs the start, end, and execution time of a function.

    Args:
        func (Callable): The function to be wrapped.

    Returns:
        Callable: The wrapped function with logging.
    """
    @wraps(func)
    def wrapper(*args, **kwargs):
        logger.info(f"🚀 Starting: {func.__name__}")
        start_time = time.time()

        result = func(*args, **kwargs)

        end_time = time.time()
        duration = end_time - start_time
        logger.info(f"✅ Finished: {func.__name__} in {duration:.2f} seconds")
        return result

    return wrapper
