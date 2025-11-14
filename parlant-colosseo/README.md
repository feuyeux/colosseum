# parlant-colosseo

parlant implementation for the Colosseum project.

## Overview

This project demonstrates the use of parlant (version 3.0.3) for building AI agent applications. It provides a multilingual translation and grammar analysis service.

## Installation

1. Clone the repository and navigate to this project:
   ```bash
   cd parlant-colosseo
   ```

2. Create a virtual environment (required to avoid dependency conflicts):
   ```bash
   python3 -m venv .venv
   source .venv/bin/activate  # On Windows: .venv\Scripts\activate
   ```

3. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```

## Running the Application

### Option 1: Using the start script (recommended)
```bash
./start.sh
```

### Option 2: Manual activation
```bash
source .venv/bin/activate  # On Windows: .venv\Scripts\activate
python main.py
```

**Note**: This project uses a dedicated virtual environment to isolate its dependencies (especially `parlant==3.0.3`) from other projects.

The application will start on `http://localhost:60100`.

### 访问 Web 界面

启动后可通过浏览器访问：
- **翻译服务界面：** http://localhost:60100
- **Parlant 管理界面：** http://localhost:60100/admin （如果 Parlant 框架配置了管理界面）

**注意：** Parlant 框架本身可能提供管理界面，具体可用性取决于框架版本和配置。

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

## Related Links

- [Main Colosseum Project](../../README.md)
- [parlant Documentation](https://github.com/emcie-co/parlant/tags)