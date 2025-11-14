#!/usr/bin/env python3
"""Test script to verify configuration loading."""

import os
import sys

# Test without environment variables
print("=== Testing default configuration ===")
import config

print(f"OLLAMA_BASE_URL: {config.OLLAMA_BASE_URL}")
print(f"OLLAMA_MODEL_NAME: {config.OLLAMA_MODEL_NAME}")
print(f"OLLAMA_TIMEOUT: {config.OLLAMA_TIMEOUT}")
print(f"MAX_TEXT_LENGTH: {config.MAX_TEXT_LENGTH}")
print(f"SERVER_HOST: {config.SERVER_HOST}")
print(f"SERVER_PORT: {config.SERVER_PORT}")
print(f"SUPPORTED_LANGUAGES: {config.SUPPORTED_LANGUAGES}")
print(f"LANGUAGE_NAMES count: {len(config.LANGUAGE_NAMES)}")
print(f"TRANSLATION_PROMPT length: {len(config.TRANSLATION_PROMPT)}")
print(f"ANALYSIS_PROMPT length: {len(config.ANALYSIS_PROMPT)}")

# Verify all required constants exist
assert config.OLLAMA_BASE_URL == "http://localhost:11434"
assert config.OLLAMA_MODEL_NAME == "qwen2.5:latest"
assert config.OLLAMA_TIMEOUT == 60
assert config.MAX_TEXT_LENGTH == 1000
assert config.SERVER_HOST == "0.0.0.0"
assert config.SERVER_PORT == 60900
assert len(config.SUPPORTED_LANGUAGES) == 11
assert len(config.LANGUAGE_NAMES) == 11
assert len(config.TRANSLATION_PROMPT) > 0
assert len(config.ANALYSIS_PROMPT) > 0

print("\n✓ All configuration values loaded correctly!")
print("\n=== Testing environment variable overrides ===")

# Test environment variable override (would need to restart Python to test properly)
print("Note: Environment variable overrides would be tested by setting env vars before import")
print("Example: OLLAMA_BASE_URL=http://custom:11434 python test_config.py")

print("\n✓ Configuration module implementation complete!")
