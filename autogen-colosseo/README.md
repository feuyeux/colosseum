# autogen-colosseo

autogen implementation for the Colosseum project.

## Overview

This project demonstrates the use of autogen (version 0.7.5) for building AI agent applications. It provides a multilingual translation and grammar analysis service.

## Framework Information

- **Framework**: autogen
- **Version**: 0.7.5
- **Official Documentation**: https://github.com/microsoft/autogen/tags
- **Project Type**: Python

## Prerequisites

- Python 3.8 or higher
- pip package manager
- Ollama running locally (default: http://localhost:11434)

## Installation

1. Clone the repository and navigate to this project:
   ```bash
   cd autogen-colosseo
   ```

2. Create a virtual environment (recommended):
   ```bash
   python -m venv .venv
   source .venv/bin/activate  # On Windows: .venv\Scripts\activate
   ```

3. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```

## Running the Application

Start the application:

```bash
python main.py
```

The application will start on `http://localhost:8000`.

## Project Structure

```
autogen-colosseo/
├── main.py                # Application entry point
├── config.py              # Configuration settings
├── models.py              # Data models
├── services.py            # Business logic
├── chains.py              # Agent chains (if applicable)
├── requirements.txt       # Python dependencies
├── static/                # Static files (HTML, CSS, JS)
└── README.md              # This file
```

## API Endpoints

### Health Check
- **GET** `/health` - Check application health status

### Translation Service
- **POST** `/translate` - Translate text between languages
  ```json
  {
    "text": "Hello, world!",
    "source_language": "english",
    "target_language": "chinese"
  }
  ```

### Grammar Analysis
- **POST** `/analyze` - Analyze grammar structure
  ```json
  {
    "text": "I love programming",
    "language": "english"
  }
  ```

## Configuration

Configuration is managed through `config.py`:

- `OLLAMA_BASE_URL`: Ollama service URL (default: http://localhost:11434)
- `MODEL_NAME`: LLM model to use (default: qwen2.5:7b)
- `PORT`: Application port (default: 8000)

## Related Links

- [Main Colosseum Project](../../README.md)
- [autogen Documentation](https://github.com/microsoft/autogen/tags)