# ag2-colosseo

ag2 implementation for the Colosseum project.

## Overview

This project demonstrates the use of ag2 (version 0.10.0) for building AI agent applications. It provides a multilingual translation and grammar analysis service.

## Framework Information

- **Framework**: ag2
- **Version**: 0.10.0
- **Official Documentation**: https://github.com/ag2ai/ag2/tags
- **Project Type**: Python

## Prerequisites

- Python 3.8 or higher
- pip package manager
- Ollama running locally (default: http://localhost:11434)

## Installation

1. Clone the repository and navigate to this project:
   ```bash
   cd ag2-colosseo
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

The application will start on `http://localhost:60600`.

### 访问 Web 界面

启动后可通过浏览器访问：
- **翻译服务界面：** http://localhost:60600

### 使用 AG2 Studio（可选）

AG2 框架提供可视化开发平台 AG2 Studio，用于构建和管理 Agent 工作流。

**启动 AG2 Studio：**
```bash
pip install ag2[autogenstudio]
ag2studio ui --port 8081
```

**访问地址：** http://localhost:8081

**功能：** 可视化 Agent 工作流构建、实时调试、对话历史管理、配置管理

## Project Structure

```
ag2-colosseo/
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
- [ag2 Documentation](https://github.com/ag2ai/ag2/tags)