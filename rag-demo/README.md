# RAG Demo - Interactive Chatbot

A Retrieval Augmented Generation (RAG) application built with Spring Boot, LangChain4j, and React.js. This application allows you to upload PDF documents, process them into vector embeddings, and chat with an AI model (Deepseek R1) that uses the document context to answer questions.

## Features

- 📄 **PDF Upload**: Upload PDF documents through a web interface
- 🔍 **Vector Embeddings**: Automatic chunking and embedding generation using LangChain4j
- 💾 **Vector Database**: Chroma vector database for storing and retrieving embeddings
- 🤖 **AI Chatbot**: Interactive chat interface powered by Deepseek R1 LLM via Ollama
- 💬 **Chat History**: Persistent chat history maintained in the database
- 🐳 **Dockerized**: Complete Docker Compose setup for easy deployment
- 🔒 **Offline Mode**: All processing happens locally - no data sent to remote servers

## Tech Stack

### Backend
- **Java 17** with **Spring Boot 3.2.0**
- **LangChain4j 0.29.1** for RAG functionality
- **H2 Database** (development) / **PostgreSQL** (production) for chat history
- **Chroma** vector database for embeddings
- **Ollama** for running Deepseek R1 LLM locally

### Frontend
- **React.js 18** with modern UI
- **Axios** for API communication
- **Nginx** for serving static files in production

## Prerequisites

- Docker and Docker Compose installed
- At least 8GB RAM available for Docker containers
- Ollama with Deepseek R1 model installed locally (or will be pulled automatically)

## Quick Start

### 1. Clone the Repository

```bash
git clone <repository-url>
cd rag-demo
```

### 2. Start Services with Docker Compose

```bash
docker-compose up -d
```

This will start:
- **Chroma** vector database (port 8000)
- **Ollama** service (port 11434)
- **Spring Boot** backend (port 8080)
- **React** frontend (port 3000)

### 3. Pull Required Ollama Models

Before using the application, you need to pull the required models:

```bash
# Pull Deepseek R1 model
docker exec rag-ollama ollama pull deepseek-r1

# Pull embedding model
docker exec rag-ollama ollama pull nomic-embed-text
```

### 4. Access the Application

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **Chroma API**: http://localhost:8000
- **Ollama API**: http://localhost:11434

## Usage

1. **Upload a PDF**: Click on the file upload area and select a PDF document
2. **Wait for Processing**: The system will process the PDF, create chunks, and generate embeddings
3. **Start Chatting**: Once processing is complete, you can ask questions about the PDF content
4. **View History**: Your chat history is automatically saved and displayed

## Development Setup

### Backend Development

```bash
# Build the project
mvn clean install

# Run Spring Boot application
mvn spring-boot:run
```

The backend will run on `http://localhost:8080` with H2 database.

### Frontend Development

```bash
cd frontend
npm install
npm start
```

The frontend will run on `http://localhost:3000` and proxy API requests to the backend.

## Configuration

### Environment Variables

You can configure the application using environment variables or `application.properties`:

- `OLLAMA_BASE_URL`: Ollama service URL (default: `http://ollama:11434`)
- `CHROMA_BASE_URL`: Chroma service URL (default: `http://chroma:8000`)
- `OLLAMA_CHAT_MODEL`: Chat model name (default: `deepseek-r1`)
- `OLLAMA_EMBEDDING_MODEL`: Embedding model name (default: `nomic-embed-text`)

### Application Properties

Edit `src/main/resources/application.properties` for local development or `application-prod.properties` for Docker deployment.

## API Endpoints

### Upload PDF
```
POST /api/upload/pdf
Content-Type: multipart/form-data
Body: file (PDF file)
```

### Create Chat Session
```
POST /api/chat/session
Response: { "sessionId": "uuid" }
```

### Send Message
```
POST /api/chat/message
Body: {
  "message": "Your question",
  "sessionId": "session-uuid"
}
Response: {
  "response": "AI response",
  "sessionId": "session-uuid"
}
```

### Get Chat History
```
GET /api/chat/history/{sessionId}
Response: [ChatMessage objects]
```

## Project Structure

```
rag-demo/
├── src/
│   └── main/
│       ├── java/com/ragdemo/
│       │   ├── config/          # Configuration classes
│       │   ├── controller/       # REST controllers
│       │   ├── dto/              # Data transfer objects
│       │   ├── entity/           # JPA entities
│       │   ├── repository/       # JPA repositories
│       │   └── service/          # Business logic
│       └── resources/
│           └── application.properties
├── frontend/
│   ├── src/
│   │   ├── components/          # React components
│   │   └── App.js               # Main app component
│   └── package.json
├── docker-compose.yml
├── Dockerfile
└── pom.xml
```

## Troubleshooting

### Ollama Model Not Found
If you get errors about missing models, ensure you've pulled them:
```bash
docker exec rag-ollama ollama pull deepseek-r1
docker exec rag-ollama ollama pull nomic-embed-text
```

### Chroma Connection Issues
Check if Chroma is healthy:
```bash
docker-compose ps
curl http://localhost:8000/api/v1/heartbeat
```

### Backend Not Starting
Check logs:
```bash
docker-compose logs backend
```

### Frontend Not Connecting to Backend
Ensure the backend is running and check CORS settings. The backend has `@CrossOrigin(origins = "*")` enabled.

## Notes

- The application processes PDFs in chunks of 300 tokens with 50 token overlap
- Vector embeddings are stored persistently in Chroma
- Chat history is stored in H2 database (development) or PostgreSQL (production)
- All processing happens locally - no external API calls are made

## License

This project is provided as-is for demonstration purposes.

