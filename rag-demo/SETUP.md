# Setup Instructions

## Prerequisites

1. **Docker and Docker Compose** installed
2. At least **8GB RAM** available
3. **Java 17** (for local development)
4. **Node.js 18+** (for local frontend development)

## Quick Start

### 1. Start All Services

```bash
docker-compose up -d
```

This starts:
- Chroma vector database (port 8000)
- Ollama service (port 11434)
- Spring Boot backend (port 8080)
- React frontend (port 3000)

### 2. Initialize Ollama Models

After services are running, pull the required models:

```bash
# Option 1: Use the provided script
./init-ollama.sh

# Option 2: Manual commands
docker exec rag-ollama ollama pull deepseek-r1
docker exec rag-ollama ollama pull nomic-embed-text
```

**Note**: This step is crucial! The application will not work without these models.

### 3. Verify Services

Check if all services are running:

```bash
docker-compose ps
```

You should see all 4 services in "Up" status.

### 4. Access the Application

- **Frontend UI**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **API Docs**: http://localhost:8080/swagger-ui.html (if enabled)

## Usage Flow

1. **Upload PDF**: Use the file upload interface to upload a PDF document
2. **Wait for Processing**: The system will:
   - Extract text from PDF
   - Split into chunks
   - Generate vector embeddings
   - Store in Chroma vector database
3. **Start Chatting**: Ask questions about the PDF content
4. **View History**: Chat history is automatically saved

## Development Mode

### Backend Only

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run
```

Backend runs on http://localhost:8080

### Frontend Only

```bash
cd frontend
npm install
npm start
```

Frontend runs on http://localhost:3000

### Full Stack (Docker)

```bash
docker-compose up --build
```

## Troubleshooting

### Models Not Found

If you see errors about missing models:

```bash
docker exec rag-ollama ollama list
docker exec rag-ollama ollama pull deepseek-r1
docker exec rag-ollama ollama pull nomic-embed-text
```

### Chroma Not Responding

Check Chroma health:

```bash
curl http://localhost:8000/api/v1/heartbeat
```

### Backend Connection Issues

Check backend logs:

```bash
docker-compose logs backend
```

### Frontend Not Loading

Check frontend logs:

```bash
docker-compose logs frontend
```

### Port Conflicts

If ports are already in use, modify `docker-compose.yml` to use different ports.

## Stopping Services

```bash
docker-compose down
```

To remove volumes (clears data):

```bash
docker-compose down -v
```

## Configuration

### Environment Variables

Edit `docker-compose.yml` to change:
- Ollama base URL
- Chroma base URL
- Port mappings

### Application Properties

- Development: `src/main/resources/application.properties`
- Production: `src/main/resources/application-prod.properties`

## Architecture

```
┌─────────────┐
│   React UI  │ (Port 3000)
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ Spring Boot │ (Port 8080)
│   Backend   │
└──┬──────┬───┘
   │      │
   ▼      ▼
┌─────┐ ┌─────────┐
│Ollama│ │ Chroma │
│(11434)│ │ (8000) │
└─────┘ └─────────┘
```

## Next Steps

1. Upload a PDF document
2. Wait for processing to complete
3. Start asking questions!
4. Check chat history in the UI

