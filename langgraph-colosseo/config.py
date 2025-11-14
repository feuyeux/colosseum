import os
import yaml
from pathlib import Path
from typing import List, Dict

# Load configuration from YAML file
config_path = Path(__file__).parent / "config.yaml"
with open(config_path, "r", encoding="utf-8") as f:
    _config = yaml.safe_load(f)

# Load prompts from YAML file
prompts_path = Path(__file__).parent / "prompts.yaml"
with open(prompts_path, "r", encoding="utf-8") as f:
    _prompts = yaml.safe_load(f)

# Ollama Configuration (environment variables can override)
OLLAMA_BASE_URL = os.getenv("OLLAMA_BASE_URL", _config["ollama"]["base-url"])
OLLAMA_MODEL_NAME = os.getenv("OLLAMA_MODEL_NAME", _config["ollama"]["model-name"])
OLLAMA_TIMEOUT = int(os.getenv("OLLAMA_TIMEOUT", str(_config["ollama"]["timeout"])))

# Application Configuration
MAX_TEXT_LENGTH = int(os.getenv("MAX_TEXT_LENGTH", str(_config["application"]["max-text-length"])))
SUPPORTED_LANGUAGES = _config["application"]["supported-languages"]

# Language Names Mapping
LANGUAGE_NAMES = _config["language-names"]

# Server Configuration
SERVER_HOST = os.getenv("SERVER_HOST", _config["server"]["host"])
SERVER_PORT = int(os.getenv("SERVER_PORT", str(_config["server"]["port"])))

# Prompts
TRANSLATION_PROMPT = _prompts["translation"]["prompt"]
VALIDATION_MESSAGES = _prompts["messages"]["validation"]
SERVICE_MESSAGES = _prompts["messages"]["service"]