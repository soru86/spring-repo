# Observability Reference

## Metrics
1. Ensure Prometheus is running (`docker compose up prometheus grafana` or `kubectl apply -f infra/monitoring/prometheus.yml` inside cluster).
2. Each service exposes `/actuator/prometheus`; the provided config scrapes gateway + domain services.
3. Sample PromQL:
   - `http_server_requests_seconds_count{application="order-service"}` for request volume.
   - `resilience4j_circuitbreaker_state{name="inventory"}` to monitor breaker transitions.

## Dashboards
1. Start Grafana (`localhost:3000`, default admin/admin).
2. Add Prometheus datasource (`http://prometheus:9090` in docker network or `http://prometheus.saga-demo:9090` in cluster).
3. Import dashboards: `1860` (Node Exporter) + custom JSON under `docs/grafana/order-saga-dashboard.json` (create from metrics above).

## Logging
- Logs are JSON-formatted. Pipe into ELK/Loki:
  ```
  docker run -d --name loki grafana/loki
  docker run -d --name promtail -v $(pwd)/infra/logging/promtail-config.yml:/etc/promtail/config.yml grafana/promtail
  ```
- Each log includes timestamp, level, logger, message, and optional `orderId` MDC.

## Tracing (future)
- Add OpenTelemetry instrumentation via `spring-boot-starter-actuator` + `opentelemetry-exporter-otlp`.
- Configure collector deployment in `infra/kubernetes`.

