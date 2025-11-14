# oxygent-colosseo

OxyGent implementation for the Colosseum project.

## Overview

This project demonstrates the use of OxyGent (version -) for building AI agent applications. It provides a multilingual translation and grammar analysis service.

## Framework Information

- **Framework**: OxyGent
- **Version**: -
- **Official Documentation**: https://github.com/jd-opensource/OxyGent/tags
- **Project Type**: Python

## Prerequisites

- Python 3.8 or higher
- pip package manager
- Ollama running locally (default: http://localhost:11434)

## Installation

1. Clone the repository and navigate to this project:
   ```bash
   cd oxygent-colosseo
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

### Option 1: 运行翻译服务示例

Start the application:

```bash
python main.py
```

The application will start on `http://localhost:60200`.

### Option 2: 使用 OxyGent 自带管理平台

OxyGent 框架自带可视化管理平台，用于管理和监控 Agent 工作流。

**启动 OxyGent 平台：**

```bash
# 安装 OxyGent
pip install oxygent

# 启动平台（默认端口 7788）
oxygent-server
```

**访问平台：**
- 管理平台：http://localhost:7788

**平台功能：**
- Agent 工作流可视化编辑
- 实时监控和调试
- 任务执行历史查看
- 配置管理

**注意：** OxyGent 平台是框架自带的独立管理系统，与本示例项目的翻译服务是分开的。

## Project Structure

```
oxygent-colosseo/
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
- [OxyGent Documentation](https://github.com/jd-opensource/OxyGent/tags)