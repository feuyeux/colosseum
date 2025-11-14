"""
Configuration loader for Agno Colosseo service.

Loads configuration from config.yaml and prompts.yaml files,
with support for environment variable overrides.
"""

import os
from pathlib import Path
from typing import Dict, List
import yaml


# Get the directory containing this config file
CONFIG_DIR = Path(__file__).parent

# Load configuration from config.yaml
def load_config() -> dict:
    """Load configuration from config.yaml file."""
    config_path = CONFIG_DIR / "config.yaml"
    with open(config_path, 'r', encoding='utf-8') as f:
        return yaml.safe_load(f)


# Load prompts from prompts.yaml
def load_prompts() -> dict:
    """Load prompts from prompts.yaml file."""
    prompts_path = CONFIG_DIR / "prompts.yaml"
    with open(prompts_path, 'r', encoding='utf-8') as f:
        return yaml.safe_load(f)


# Load configurations
_config = load_config()
_prompts = load_prompts()

# Ollama Configuration with environment variable overrides
OLLAMA_BASE_URL: str = os.getenv(
    'OLLAMA_BASE_URL',
    _config.get('ollama', {}).get('base-url', 'http://localhost:11434')
)

OLLAMA_MODEL_NAME: str = os.getenv(
    'OLLAMA_MODEL_NAME',
    _config.get('ollama', {}).get('model-name', 'qwen2.5:latest')
)

OLLAMA_TIMEOUT: int = int(os.getenv(
    'OLLAMA_TIMEOUT',
    str(_config.get('ollama', {}).get('timeout', 60))
))

# Application Configuration with environment variable overrides
MAX_TEXT_LENGTH: int = int(os.getenv(
    'MAX_TEXT_LENGTH',
    str(_config.get('application', {}).get('max-text-length', 1000))
))

SUPPORTED_LANGUAGES: List[str] = (
    os.getenv('SUPPORTED_LANGUAGES', '').split(',') 
    if os.getenv('SUPPORTED_LANGUAGES') 
    else _config.get('application', {}).get('supported-languages', [])
)

# Language names mapping
LANGUAGE_NAMES: Dict[str, str] = _config.get('language-names', {})

# Server Configuration with environment variable overrides
SERVER_HOST: str = os.getenv(
    'SERVER_HOST',
    _config.get('server', {}).get('host', '0.0.0.0')
)

SERVER_PORT: int = int(os.getenv(
    'SERVER_PORT',
    str(_config.get('server', {}).get('port', 60900))
))

# Prompts
TRANSLATION_PROMPT: str = _prompts.get('translation', {}).get('prompt', '')
ANALYSIS_PROMPT: str = _prompts.get('analysis', {}).get('prompt', '')

# Validation messages
VALIDATION_MESSAGES: Dict[str, str] = _prompts.get('messages', {}).get('validation', {})
SERVICE_MESSAGES: Dict[str, str] = _prompts.get('messages', {}).get('service', {})
