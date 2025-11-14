# spring-ai-colosseo

Spring AI implementation for the Colosseum project.

## Overview

Multilingual translation and grammar analysis service built with Spring Boot and Spring AI.

## Build & Run (JDK 21 + UTF-8)

1. Navigate to the module:
   ```bash
   cd spring-ai-colosseo
   ```

2. Build (using Maven Central):
   ```bash
   mvn -DskipTests clean package
   ```

3. Start the application:
   ```bash
   java -jar target/spring-ai-colosseo-1.0.0-SNAPSHOT.jar
   ```

4. Service runs at `http://localhost:50100`.

## API Endpoints

- **GET** `/health` — simple health check
- **POST** `/echo` — echo request body (UTF-8 verification)
- **POST** `/api/translateText` — translate into multiple target languages
  ```json
  {
    "sourceText": "你好，世界！",
    "targetLanguages": ["ENGLISH","JAPANESE"]
  }
  ```
- **POST** `/api/analyzeGrammar` — grammar analysis for a language
  ```json
  {
    "text": "こんにちは世界",
    "languageCode": "JAPANESE"
  }
  ```

## Configuration

`src/main/resources/application.yaml`:

- `spring.ai.ollama.base-url`: `http://localhost:11434`
- `spring.ai.ollama.chat.options.model`: `qwen2.5:latest`
- `server.port`: `8081`
- Encoding:
  - `spring.messages.encoding: UTF-8`
  - `server.servlet.encoding.charset: UTF-8`
  - `server.servlet.encoding.enabled: true`
  - `server.servlet.encoding.force: true`
  - `logging.charset.console: UTF-8`
  - `logging.charset.file: UTF-8`

## Related Links

- [Spring AI Documentation](https://github.com/spring-projects/spring-ai)