#!/bin/bash

# Deploy CQRS Demo to Kubernetes
# Usage: ./deploy-k8s.sh [namespace]

NAMESPACE=${1:-cqrs-demo}

echo "Deploying CQRS Demo to Kubernetes namespace: $NAMESPACE"

# Create namespace
kubectl apply -f k8s/namespace.yaml

# Deploy infrastructure
echo "Deploying PostgreSQL databases..."
kubectl apply -f k8s/postgres-command.yaml
kubectl apply -f k8s/postgres-query.yaml

echo "Deploying Kafka..."
kubectl apply -f k8s/kafka.yaml

# Wait for infrastructure
echo "Waiting for infrastructure to be ready..."
kubectl wait --for=condition=ready pod -l app=postgres-command -n $NAMESPACE --timeout=300s
kubectl wait --for=condition=ready pod -l app=postgres-query -n $NAMESPACE --timeout=300s
kubectl wait --for=condition=ready pod -l app=kafka -n $NAMESPACE --timeout=300s

# Deploy services
echo "Deploying Service Registry..."
kubectl apply -f k8s/service-registry.yaml

echo "Waiting for Service Registry..."
sleep 30

echo "Deploying API Gateway..."
kubectl apply -f k8s/api-gateway.yaml

echo "Deploying Command Service..."
kubectl apply -f k8s/command-service.yaml

echo "Deploying Query Service..."
kubectl apply -f k8s/query-service.yaml

echo "Deployment completed!"
echo "Check status with: kubectl get pods -n $NAMESPACE"
echo "Get API Gateway external IP: kubectl get svc api-gateway -n $NAMESPACE"

