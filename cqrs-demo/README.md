# CQRS Microservices Demo

A fully functional microservices demo implementing the CQRS (Command Query Responsibility Segregation) pattern using Java, Spring Boot, Kafka, PostgreSQL, and Kubernetes.

## Architecture Overview

This project demonstrates a microservices architecture with the following components:

- **Service Registry (Eureka)**: Service discovery and registration
- **API Gateway**: Single entry point with JWT authentication and circuit breaker
- **Command Service**: Handles write operations (CQRS write side)
- **Query Service**: Handles read operations (CQRS read side)
- **Kafka**: Event streaming for asynchronous communication
- **PostgreSQL**: Separate databases for each service (microservice per database)

## Features

✅ **CQRS Pattern**: Separate command and query models
✅ **Event-Driven Architecture**: Kafka for event messaging
✅ **Service Discovery**: Eureka service registry
✅ **API Gateway**: Spring Cloud Gateway with routing
✅ **JWT Authentication**: Token-based authentication
✅ **Circuit Breaker**: Resilience4j for fault tolerance
✅ **Containerization**: Docker support
✅ **Kubernetes**: Full K8s deployment with auto-scaling (up to 10 pods)
✅ **Horizontal Scaling**: HPA configured for high load scenarios

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose
- Kubernetes cluster (for K8s deployment)
- kubectl configured (for K8s deployment)

## Project Structure

```
cqrs-demo/
├── api-gateway/          # API Gateway service
├── command-service/      # Command service (write side)
├── query-service/        # Query service (read side)
├── service-registry/     # Eureka service registry
├── common-events/        # Shared event models
├── k8s/                  # Kubernetes manifests
└── docker-compose.yml    # Docker Compose for local development
```

## Local Development Setup

### 1. Build the Project

```bash
mvn clean install
```

### 2. Start Infrastructure with Docker Compose

```bash
docker-compose up -d
```

This will start:
- PostgreSQL databases (command_db and query_db)
- Zookeeper
- Kafka
- Service Registry
- API Gateway
- Command Service
- Query Service

### 3. Access Services

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **Command Service**: http://localhost:8081
- **Query Service**: http://localhost:8082

## API Usage

### 1. Register/Login to Get JWT Token

```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "user1", "password": "password123"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "user1", "password": "password123"}'
```

Response will include a JWT token:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "user1"
}
```

### 2. Create an Order (Command)

```bash
curl -X POST http://localhost:8080/api/commands/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "customerId": "customer-123",
    "productId": "product-456",
    "quantity": 5,
    "totalAmount": 250.00
  }'
```

### 3. Query Orders

```bash
# Get all orders
curl -X GET http://localhost:8080/api/queries/orders \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Get order by ID
curl -X GET http://localhost:8080/api/queries/orders/{orderId} \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Get orders by customer
curl -X GET "http://localhost:8080/api/queries/orders?customerId=customer-123" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Get orders by status
curl -X GET "http://localhost:8080/api/queries/orders?status=PENDING" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 4. Update Order

```bash
curl -X PUT http://localhost:8080/api/commands/orders/{orderId} \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "status": "CONFIRMED",
    "totalAmount": 250.00
  }'
```

### 5. Cancel Order

```bash
curl -X DELETE http://localhost:8080/api/commands/orders/{orderId}?reason=User%20request \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Kubernetes Deployment

### Prerequisites for AWS EKS

1. AWS EKS cluster configured
2. kubectl configured to access your cluster
3. Docker images built and pushed to a container registry (ECR, Docker Hub, etc.)

### Build and Push Docker Images

```bash
# Build all services
mvn clean package

# Build Docker images
docker build -t cqrs-demo/service-registry:1.0.0 ./service-registry
docker build -t cqrs-demo/api-gateway:1.0.0 ./api-gateway
docker build -t cqrs-demo/command-service:1.0.0 ./command-service
docker build -t cqrs-demo/query-service:1.0.0 ./query-service

# Tag and push to your registry (example with AWS ECR)
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <account-id>.dkr.ecr.us-east-1.amazonaws.com

docker tag cqrs-demo/service-registry:1.0.0 <account-id>.dkr.ecr.us-east-1.amazonaws.com/cqrs-demo/service-registry:1.0.0
docker tag cqrs-demo/api-gateway:1.0.0 <account-id>.dkr.ecr.us-east-1.amazonaws.com/cqrs-demo/api-gateway:1.0.0
docker tag cqrs-demo/command-service:1.0.0 <account-id>.dkr.ecr.us-east-1.amazonaws.com/cqrs-demo/command-service:1.0.0
docker tag cqrs-demo/query-service:1.0.0 <account-id>.dkr.ecr.us-east-1.amazonaws.com/cqrs-demo/query-service:1.0.0

docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/cqrs-demo/service-registry:1.0.0
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/cqrs-demo/api-gateway:1.0.0
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/cqrs-demo/command-service:1.0.0
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/cqrs-demo/query-service:1.0.0
```

### Update Image References

Update the image references in the Kubernetes manifests (`k8s/*.yaml`) to point to your container registry.

### Deploy to Kubernetes

```bash
# Create namespace
kubectl apply -f k8s/namespace.yaml

# Deploy infrastructure
kubectl apply -f k8s/postgres-command.yaml
kubectl apply -f k8s/postgres-query.yaml
kubectl apply -f k8s/kafka.yaml

# Wait for infrastructure to be ready
kubectl wait --for=condition=ready pod -l app=postgres-command -n cqrs-demo --timeout=300s
kubectl wait --for=condition=ready pod -l app=postgres-query -n cqrs-demo --timeout=300s
kubectl wait --for=condition=ready pod -l app=kafka -n cqrs-demo --timeout=300s

# Deploy services
kubectl apply -f k8s/service-registry.yaml
kubectl apply -f k8s/api-gateway.yaml
kubectl apply -f k8s/command-service.yaml
kubectl apply -f k8s/query-service.yaml
```

### Check Deployment Status

```bash
# Check pods
kubectl get pods -n cqrs-demo

# Check services
kubectl get svc -n cqrs-demo

# Check HPA
kubectl get hpa -n cqrs-demo

# View logs
kubectl logs -f deployment/api-gateway -n cqrs-demo
```

### Access Services

Get the LoadBalancer external IP:

```bash
kubectl get svc api-gateway -n cqrs-demo
```

Access the API Gateway using the external IP.

## Auto-Scaling

The Horizontal Pod Autoscaler (HPA) is configured for:
- **API Gateway**: 2-10 pods (CPU: 70%, Memory: 80%)
- **Command Service**: 2-10 pods (CPU: 70%, Memory: 80%)
- **Query Service**: 2-10 pods (CPU: 70%, Memory: 80%)

HPA will automatically scale pods based on CPU and memory utilization.

## Circuit Breaker

Circuit breakers are configured using Resilience4j:
- **Failure Rate Threshold**: 50%
- **Sliding Window Size**: 10 requests
- **Wait Duration**: 10 seconds
- **Minimum Calls**: 5

Circuit breaker status can be monitored via actuator endpoints:
- `/actuator/health/circuitbreakers`

## Database Schema

### Command Service Database (command_db)
- `orders` table: Stores order commands

### Query Service Database (query_db)
- `order_views` table: Read-optimized view of orders

## Event Topics

Kafka topics used:
- `order-created`: Published when an order is created
- `order-updated`: Published when an order is updated
- `order-cancelled`: Published when an order is cancelled

## Monitoring

Health check endpoints:
- Service Registry: `http://localhost:8761/actuator/health`
- API Gateway: `http://localhost:8080/actuator/health`
- Command Service: `http://localhost:8081/actuator/health`
- Query Service: `http://localhost:8082/actuator/health`

## Troubleshooting

### Services not starting
1. Check if PostgreSQL and Kafka are running
2. Verify Eureka is accessible
3. Check service logs: `docker-compose logs <service-name>`

### Kafka connection issues
1. Ensure Kafka and Zookeeper are running
2. Check `KAFKA_BOOTSTRAP_SERVERS` environment variable

### Database connection issues
1. Verify PostgreSQL is running
2. Check database credentials in `application.yml`
3. Ensure database exists

## License

This is a demo project for educational purposes.

