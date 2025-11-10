## Order Service Microservice

This project is a sample Spring Boot microservice illustrating a microservice-specific database pattern with PostgreSQL. It includes containerization (Docker) and Kubernetes manifests with horizontal pod autoscaling up to ten replicas.

### Tech Stack

- JDK 21
- Spring Boot 3 (Web, Data JPA, Validation, Actuator)
- PostgreSQL with Flyway migrations
- Docker / Docker Compose
- Kubernetes with Horizontal Pod Autoscaler

### Running Locally

1. Build the application:

   ```bash
   mvn clean package
   ```

2. Start PostgreSQL and the microservice using Docker Compose:

   ```bash
   docker-compose up --build
   ```

3. Access the service:
   - REST API: `http://localhost:8080/api/orders`
   - Actuator health: `http://localhost:8080/actuator/health`

### REST API

- `POST /api/orders` – create an order
- `GET /api/orders` – list orders
- `GET /api/orders/{id}` – fetch an order by ID

Request payload example for `POST /api/orders`:

```json
{
  "customerName": "Ada Lovelace",
  "customerEmail": "ada@example.com",
  "items": [
    { "sku": "SKU-001", "quantity": 2, "unitPrice": 199.99 },
    { "sku": "SKU-002", "quantity": 1, "unitPrice": 49.95 }
  ]
}
```

### Kubernetes Deployment

The `k8s/` directory contains manifests for:

- Dedicated PostgreSQL instance (`postgres.yaml`)
- Microservice deployment and service (`deployment.yaml`, `service.yaml`)
- Configuration and credentials (`configmap.yaml`, `secret.yaml`)
- Horizontal Pod Autoscaler (`hpa.yaml`)

Apply them in order:

```bash
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/postgres.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/hpa.yaml
```

The HPA scales the deployment between two and ten replicas based on CPU utilization (60% target).

### Configuration

Environment variables:

- `ORDERSERVICE_DB_URL` – JDBC URL to the PostgreSQL database
- `ORDERSERVICE_DB_USERNAME` – database username
- `ORDERSERVICE_DB_PASSWORD` – database password
- `SERVER_PORT` – HTTP port (defaults to 8080)

### Database Migration

Flyway migrations are stored in `src/main/resources/db/migration`. `V1__create_orders_schema.sql` creates the dedicated schema, tables, and relationships for the order service.

