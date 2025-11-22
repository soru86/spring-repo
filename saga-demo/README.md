## Saga Demo Platform

End-to-end microservice reference implementing an order saga with Spring Boot 3 / Java 21, Kafka, PostgreSQL, Eureka discovery, Spring Cloud Gateway, Resilience4j circuit breakers, JWT security, Docker, Kubernetes (with Istio service mesh), Jenkins + Ansible CI/CD, and observability (Prometheus + Grafana + structured logging).

### Architecture Overview
- **Gateway** (`api-gateway`) terminates JWT auth, routes traffic via Eureka and applies circuit breakers/fallbacks.
- **Auth Service** issues HMAC JWTs backed by PostgreSQL users.
- **Order Service** persists orders, publishes saga events to Kafka.
- **Inventory & Payment Services** encapsulate downstream responsibilities and expose compensating endpoints.
- **Saga Orchestrator** listens to Kafka, coordinates the saga (reserve inventory → capture payment → confirm order, otherwise rollback).
- **Discovery Service** (Eureka) supplies service registry for gateway + services.
- **Kafka/Zookeeper** transport domain events.
- **Postgres** stores auth, order, inventory, payment data (seeded via `infra/postgres/seed-data.sql`).
- **Observability stack**: Spring Actuator + Micrometer → Prometheus + Grafana (docker/k8s manifests included), JSON logs for ELK/Loki.
- **Service Mesh**: optional Istio `Gateway`/`VirtualService` enabling mTLS, traffic shaping, zero-trust networking.

### Saga Flow
1. Client obtains JWT from `auth-service`.
2. `POST /orders` via gateway stores order (`PENDING`) and emits Kafka `order-events`.
3. `saga-orchestrator` consumes the event, calls `inventory-service` (`/reserve`) under a Resilience4j circuit breaker.
4. If inventory succeeds, orchestrator charges payment; on success it PATCHes `order-service` to `COMPLETED`.
5. Any failure triggers compensating actions (`inventory/release`, `payment/refund`) and marks the order `REJECTED`.

### Modules
| Module | Purpose |
| --- | --- |
| `common-library` | Shared DTOs, SagaEvent, JWT helpers, API response objects. |
| `discovery-service` | Eureka server on `8761`. |
| `api-gateway` | Spring Cloud Gateway + JWT resource server, fallback endpoint, Prometheus metrics. |
| `auth-service` | Username/password login → JWT issuance (default `demo/demo123`). |
| `order-service` | JPA entities, Kafka producer, Swagger docs, status patch endpoint. |
| `inventory-service` | Stock table + reserve/release APIs. |
| `payment-service` | Payment capture/refund simulation. |
| `saga-orchestrator` | Kafka listener + WebClient orchestrator with circuit breaker. |

### Tech Choices & Requirements Mapping
- **Java 21 / Spring Boot 3.2** across all modules.
- **Saga pattern**: orchestrated via Kafka + coordinator microservice.
- **Kafka**: `order-events` topic powering saga orchestration.
- **PostgreSQL**: persistence for all services; seed script provided.
- **Service discovery**: Netflix Eureka.
- **Circuit breaker**: Resilience4j decorating orchestrator calls.
- **API Gateway**: Spring Cloud Gateway with JWT auth + fallback endpoint.
- **Service mesh**: Istio manifests (`infra/kubernetes/istio/virtual-service.yml`).
- **JWT auth**: HMAC secret stored in `saga.security.jwt.secret` (Base64) shared across services.
- **Docker & Kubernetes**: Multi-stage Dockerfile with ARG-driven builds, `infra/docker/docker-compose.yml`, and `infra/kubernetes/stack.yml` (HPAs scale up to 10 pods).
- **Logging & Monitoring**: JSON console logs + Prometheus scrape config + Grafana container; ready for ELK/Loki ingestion.
- **CI/CD**: `Jenkinsfile` builds/tests, produces/pushes Docker images, then runs `ansible/deploy.yml` against Kubernetes.
- **Swagger/OpenAPI**: Springdoc on every service (`/swagger-ui.html`, `/v3/api-docs`).
- **Postman collection**: `postman/SagaDemo.postman_collection.json`.

### Getting Started
1. **Prereqs**: Maven 3.9+, JDK 21, Docker, docker-compose, kubectl, helm/istioctl (optional), Jenkins & Ansible for pipeline, Postman for testing.
2. **Build**: `mvn clean package` (root).
3. **Local run (IDE)**: start `discovery-service`, then `api-gateway`, `auth-service`, `order-service`, `inventory-service`, `payment-service`, `saga-orchestrator`. Provide `postgres`, `kafka` via Docker (see below).

### Docker Compose
```
cd infra/docker
docker compose up --build
```
- Spins up Postgres (with seed data), Kafka, Zookeeper, all microservices, Prometheus (`9090`), Grafana (`3000`).
- Use `docker compose logs -f order-service` for structured logs.

### Kubernetes Deployment
```
kubectl apply -f infra/kubernetes/stack.yml
kubectl apply -f infra/kubernetes/istio/virtual-service.yml   # optional mesh
```
- Replace `your-registry/<service>` with pushed images from Jenkins.
- HPAs set `maxReplicas: 10` to satisfy horizontal scaling requirement.
- Expose Grafana/Prometheus via Istio or `kubectl port-forward`.

### Service Mesh (Istio)
- Install Istio (`istioctl install --set profile=demo`).
- Label namespace: `kubectl label namespace saga-demo istio-injection=enabled`.
- Apply manifests under `infra/kubernetes/istio`.
- Traffic hits Istio Ingress → VirtualService routes `/api` → API gateway.

### Security
- JWT secret configured via Base64 env `JWT_SECRET_BASE64`.
- Auth service seeds `demo/demo123` with roles `ROLE_USER`, `ROLE_ADMIN`.
- Internal calls (orchestrator → services) use bearer token `saga.internal-token`.
- Resource servers verify tokens via shared HMAC (OAuth2 resource server configuration).

### Logging & Monitoring
- JSON console logs with trace/span fields ready for ELK/Loki.
- Prometheus scrapes `/actuator/prometheus` via `infra/monitoring/prometheus.yml`.
- Grafana dashboard import instructions in `docs/observability.md` (placeholder).
- Extend logging by shipping containers logs to Loki via promtail/Fluent Bit.

### CI/CD (Jenkins + Ansible)
- `Jenkinsfile` stages: checkout → tests → parallel module build → Docker build/push → Ansible deploy.
- Credentials needed: `saga-registry` (docker username/password) and `kubeconfig`.
- `ansible/deploy.yml` copies manifests to target hosts and applies them via kubectl.

### Swagger & Postman
- Access `http://localhost:PORT/swagger-ui.html` for each service.
- Import `postman/SagaDemo.postman_collection.json` → run token + order requests.

### Seed Data
- `infra/postgres/seed-data.sql` adds sample inventory SKUs and creates logical DBs.
- Run automatically via Docker compose; for manual execution: `psql -f infra/postgres/seed-data.sql`.

### Testing the Saga
1. `POST auth-service/auth/token` with default credentials.
2. `POST api-gateway/orders` (include JWT). Response returns `PENDING` order.
3. Monitor orchestrator logs; order transitions to `COMPLETED`.
4. Force failure: reduce inventory quantity, place large-order → orchestrator triggers rollback, order becomes `REJECTED`.

### Next Steps
- Add proper Helm charts & GitOps integration.
- Extend observability with tracing (OpenTelemetry collector).
- Harden JWT secret storage (Hashicorp Vault/KMS) and rotate tokens.

