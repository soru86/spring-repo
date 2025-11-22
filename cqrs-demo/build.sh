#!/bin/bash

echo "Building CQRS Demo Microservices..."

# Build all modules
mvn clean package -DskipTests

echo "Building Docker images..."

# Build Docker images
docker build -t cqrs-demo/service-registry:1.0.0 ./service-registry
docker build -t cqrs-demo/api-gateway:1.0.0 ./api-gateway
docker build -t cqrs-demo/command-service:1.0.0 ./command-service
docker build -t cqrs-demo/query-service:1.0.0 ./query-service

echo "Build completed successfully!"
echo "To start services, run: docker-compose up -d"

