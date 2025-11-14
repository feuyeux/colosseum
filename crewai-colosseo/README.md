# crewai-colosseo

crewAI implementation for the Colosseum project.

## Overview

This project demonstrates the use of crewAI (version 1.4.1) for building AI agent applications. It provides a multilingual translation and grammar analysis service.

## Framework Information

- **Framework**: crewAI
- **Version**: 1.4.1
- **Official Documentation**: https://github.com/crewAIInc/crewAI/tags
- **Project Type**: Python

## Prerequisites

- Python 3.8 or higher
- pip package manager
- Ollama running locally (default: http://localhost:11434)

## Installation

1. Clone the repository and navigate to this project:
   ```bash
   cd crewai-colosseo
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

The application will start on `http://localhost:60700`.

### 访问 Web 界面

启动后可通过浏览器访问：
- **翻译服务界面：** http://localhost:60700

### 使用 CrewAI+ 平台（可选）

CrewAI 提供企业版云平台 CrewAI+，用于可视化构建和管理 Agent 团队。

**访问地址：** https://app.crewai.com

**功能：** 可视化工作流编辑器、团队协作、监控面板、部署管理

**注意：** 需要在 CrewAI 官网注册账号。

## Project Structure

```
crewai-colosseo/
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
- [crewAI Documentation](https://github.com/crewAIInc/crewAI/tags)