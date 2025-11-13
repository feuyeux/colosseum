#!/bin/bash

# Savant Startup Script

echo "🌍 Starting Savant..."
echo ""

# Check if Ollama is running
if ! curl -s http://localhost:11434/api/tags > /dev/null 2>&1; then
    echo "⚠️  Warning: Ollama service is not running or not accessible at http://localhost:11434"
    echo "   Please start Ollama before running Savant."
    echo ""
    read -p "Do you want to continue anyway? (y/n) " -n 1 -r
    echo ""
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# Check if qwen2.5:latest model is available
if ! ollama list | grep -q "qwen2.5:latest"; then
    echo "⚠️  Warning: qwen2.5:latest model not found"
    echo "   Pulling the model now..."
    ollama pull qwen2.5:latest
fi

echo ""
echo "Starting Spring Boot application..."
echo "Access the web interface at: http://localhost:8080/index.html"
echo ""

# Start the application
mvn spring-boot:run
