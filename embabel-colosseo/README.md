# embabel-colosseo

Embabel implementation for the Colosseum project.

## Overview

This project demonstrates the use of Embabel (version 0.2.0) for building AI agent applications. It provides a multilingual translation and grammar analysis service.

## Installation

1. Clone the repository and navigate to this project:
   ```bash
   cd embabel-colosseo
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

## Running the Application

Start the application using Maven:

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:50200`.

## API Endpoints

### Health Check
- **GET** `/actuator/health` - Check application health status

### Translation Service
- **POST** `/api/translate` - Translate text between languages
  ```json
  {
    "text": "Hello, world!",
    "sourceLanguage": "ENGLISH",
    "targetLanguage": "CHINESE"
  }
  ```

### Grammar Analysis
- **POST** `/api/analyze` - Analyze grammar structure
  ```json
  {
    "text": "I love programming",
    "language": "ENGLISH"
  }
  ```

## Configuration

Configuration is managed through `src/main/resources/application.yaml`:

- `ollama.base-url`: Ollama service URL (default: http://localhost:11434)
- `ollama.model`: LLM model to use (default: qwen2.5:7b)
- `server.port`: Application port (default: 8080)

## Related Links

- [Main Colosseum Project](../../README.md)
- [Embabel Documentation](https://github.com/embabel/embabel-agent/tags)