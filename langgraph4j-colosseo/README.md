# langgraph4j-colosseo

langgraph4j implementation for the Colosseum project.

## Overview

This project demonstrates the use of langgraph4j (version 1.7.2) for building AI agent applications. It provides a multilingual translation and grammar analysis service.

## Framework Information

- **Framework**: langgraph4j
- **Version**: 1.7.2
- **Official Documentation**: https://github.com/langgraph4j/langgraph4j/tags
- **Project Type**: Java (Maven)

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Ollama running locally (default: http://localhost:11434)

## Installation

1. Clone the repository and navigate to this project:
   ```bash
   cd langgraph4j-colosseo
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

The application will start on `http://localhost:8080`.

## Project Structure

```
langgraph4j-colosseo/
├── src/
│   ├── main/
│   │   ├── java/          # Java source code
│   │   └── resources/     # Configuration files and static resources
│   └── test/              # Test files
├── pom.xml                # Maven configuration
└── README.md              # This file
```

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
- [langgraph4j Documentation](https://github.com/langgraph4j/langgraph4j/tags)