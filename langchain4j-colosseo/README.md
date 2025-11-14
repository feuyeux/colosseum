# langchain4j-colosseo

langchain4j implementation for the Colosseum project.

## Overview

This project demonstrates the use of langchain4j (version 1.8.0) for building AI agent applications. It provides a multilingual translation and grammar analysis service.

## Installation

1. Clone the repository and navigate to this project:
   ```bash
   cd langchain4j-colosseo
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

The application will start on `http://localhost:50300`.

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
- [langchain4j Documentation](https://github.com/langchain4j/langchain4j/tags)