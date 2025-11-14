# agno-colosseo

Agno framework implementation for the Colosseum project.

## Overview

This project demonstrates the use of agno (version 2.2.11) for building AI agent applications. It provides a multilingual translation and grammar analysis service using agno's agent-based approach for LLM interactions.

## Framework Information

- **Framework**: agno
- **Version**: 2.2.11
- **Official Documentation**: https://github.com/agno-agi/agno/tags
- **Project Type**: Python
- **Port**: 60900

## Prerequisites

- Python 3.10 or higher
- pip package manager
- Ollama running locally (default: http://localhost:11434)
- Ollama model: qwen2.5:latest

## Installation

1. Clone the repository and navigate to this project:
   ```bash
   cd agno-colosseo
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

4. Ensure Ollama is running and pull the required model:
   ```bash
   ollama pull qwen2.5:latest
   ```

## Configuration

Configuration is managed through two YAML files:

### config.yaml
Contains service configuration:
- **ollama**: Ollama service settings (base-url, model-name, timeout)
- **application**: Application settings (max-text-length, supported-languages)
- **server**: Server settings (host, port)
- **language-names**: Mapping of language codes to display names

### prompts.yaml
Contains LLM prompts for:
- **translation**: Translation agent instructions
- **analysis**: Grammar analysis agent instructions
- **messages**: Validation and service messages

Environment variables can override configuration values:
- `OLLAMA_BASE_URL`: Ollama service URL
- `OLLAMA_MODEL_NAME`: LLM model name
- `OLLAMA_TIMEOUT`: Request timeout in seconds
- `MAX_TEXT_LENGTH`: Maximum text length for requests
- `SERVER_HOST`: Server host address
- `SERVER_PORT`: Server port number

## Running the Application

### Using the startup script:

```bash
./start.sh
```

### Manual start:

```bash
uvicorn main:app --host 0.0.0.0 --port 60900
```

The application will start on `http://localhost:60900`.

### 访问 Web 界面

启动后可通过浏览器访问：
- **翻译服务界面：** http://localhost:60900

### 使用 Agno Playground（可选）

Agno 框架提供交互式开发环境 Agno Playground，用于测试和调试 Agent。

**启动 Playground：**
```bash
pip install agno
agno playground
```

**访问地址：** http://localhost:7777

**功能：** Agent 交互式测试、工作流调试、实时日志查看

## Project Structure

```
agno-colosseo/
├── main.py                # FastAPI application entry point
├── config.py              # Configuration loader
├── models.py              # Pydantic data models
├── services.py            # Translation and analysis services
├── agents.py              # Agno agent implementations
├── ollama_client.py       # Ollama LLM client
├── config.yaml            # Service configuration
├── prompts.yaml           # LLM prompts and messages
├── requirements.txt       # Python dependencies
├── start.sh               # Startup script
├── static/                # Static web interface files
│   ├── index.html         # Main web interface
│   ├── css/
│   │   └── styles.css     # Styling
│   └── js/
│       ├── translation.js # Translation functionality
│       └── ui-interactions.js # UI interactions
└── README.md              # This file
```

## API Endpoints

### Health Check
- **GET** `/health` - Check application and Ollama service health status
  
  Response:
  ```json
  {
    "status": "healthy",
    "ollama_connected": true
  }
  ```

### Translation Service
- **POST** `/api/translateText` - Translate text to multiple target languages
  
  Request:
  ```json
  {
    "sourceText": "Hello, world!",
    "targetLanguages": ["chinese", "spanish", "french"]
  }
  ```
  
  Response:
  ```json
  {
    "sourceText": "Hello, world!",
    "translations": {
      "chinese": "你好，世界！",
      "spanish": "¡Hola, mundo!",
      "french": "Bonjour, le monde!"
    }
  }
  ```

### Supported Languages
- **GET** `/api/languages` - Get list of supported languages
  
  Response:
  ```json
  {
    "english": "English",
    "chinese": "中文",
    "spanish": "Español",
    ...
  }
  ```

### Grammar Analysis
- **POST** `/api/analyzeGrammar` - Analyze grammatical components of text
  
  Request:
  ```json
  {
    "text": "I love programming",
    "languageCode": "english"
  }
  ```
  
  Response:
  ```json
  {
    "originalText": "I love programming",
    "languageCode": "english",
    "components": [
      {
        "text": "I",
        "componentType": "PRONOUN",
        "color": "#FFB6C1",
        "startIndex": 0,
        "endIndex": 1,
        "features": {}
      },
      {
        "text": "love",
        "componentType": "VERB",
        "color": "#4ECDC4",
        "startIndex": 2,
        "endIndex": 6,
        "features": {}
      },
      {
        "text": "programming",
        "componentType": "NOUN",
        "color": "#FF6B6B",
        "startIndex": 7,
        "endIndex": 18,
        "features": {}
      }
    ]
  }
  ```

## Supported Languages

The service supports translation and analysis for the following languages:
- English
- Chinese (中文)
- Spanish (Español)
- French (Français)
- German (Deutsch)
- Japanese (日本語)
- Korean (한국어)
- Russian (Русский)
- Arabic (العربية)
- Portuguese (Português)
- Italian (Italiano)

## Agno Framework Features

This implementation leverages agno's capabilities:
- **Agent-based Architecture**: Uses agno.Agent for translation and analysis tasks
- **Ollama Integration**: Configured to work with local Ollama LLM service
- **Async Operations**: Supports concurrent translation requests
- **Prompt Management**: Structured prompt templates for consistent results

## Development

### Running Tests

```bash
python test_config.py
```

### Adding New Languages

1. Update `config.yaml` to add language code to `supported-languages`
2. Add language name mapping to `language-names` section
3. Restart the service

### Customizing Prompts

Edit `prompts.yaml` to modify:
- Translation instructions and behavior
- Grammar analysis rules and output format
- Validation and error messages

## Troubleshooting

### Ollama Connection Issues
- Ensure Ollama is running: `ollama serve`
- Check Ollama is accessible at configured URL
- Verify model is available: `ollama list`

### Port Already in Use
- Change port in `config.yaml` or set `SERVER_PORT` environment variable
- Check for other services using port 60900

### Translation Quality
- Try different Ollama models
- Adjust temperature settings in agent configuration
- Refine prompts in `prompts.yaml`

## Related Links

- [Main Colosseum Project](../README.md)
- [Agno Documentation](https://github.com/agno-agi/agno)
- [Ollama Documentation](https://ollama.ai)
