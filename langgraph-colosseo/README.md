# langgraph-colosseo

langgraph implementation for the Colosseum project.

## Overview

This project demonstrates the use of langgraph (version 1.0.3) for building AI agent applications. It provides a multilingual translation and grammar analysis service.

## Framework Information

- **Framework**: langgraph
- **Version**: 1.0.3
- **Official Documentation**: https://github.com/langchain-ai/langgraph/tags
- **Project Type**: Python

## Prerequisites

- Python 3.8 or higher
- pip package manager
- Ollama running locally (default: http://localhost:11434)

## Installation

1. Clone the repository and navigate to this project:
   ```bash
   cd langgraph-colosseo
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

The application will start on `http://localhost:60400`.

### 访问 Web 界面

启动后可通过浏览器访问：
- **翻译服务界面：** http://localhost:60400

### 使用 LangGraph Studio（可选）

LangGraph 框架提供可视化调试工具 LangGraph Studio（需要 LangSmith 账号）。

**启动 LangGraph Studio：**
```bash
pip install langgraph-cli
export LANGCHAIN_API_KEY="your-api-key"
export LANGCHAIN_TRACING_V2=true
langgraph dev
```

**访问地址：** http://localhost:8123

**功能：** 图结构可视化、状态检查、步骤追踪、交互式调试

**注意：** 需要在 https://smith.langchain.com/ 注册并获取 API Key。

## Project Structure

```
langgraph-colosseo/
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
- [langgraph Documentation](https://github.com/langchain-ai/langgraph/tags)