#!/bin/bash

# Script to initialize Ollama models
echo "Initializing Ollama models..."

# Pull Llama 3.2 model
echo "Pulling llama3.2 model..."
docker exec rag-ollama ollama pull llama3.2

# Pull embedding model
echo "Pulling nomic-embed-text model..."
docker exec rag-ollama ollama pull nomic-embed-text

echo "Models initialized successfully!"

