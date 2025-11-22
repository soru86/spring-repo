# Rewards Service API

A RESTful API service for calculating customer reward points based on transaction amounts. This service implements a rewards program where customers earn points based on their purchase amounts.

## Table of Contents

- [Overview](#overview)
- [Reward Points Calculation](#reward-points-calculation)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Running Tests](#running-tests)
- [Docker Deployment](#docker-deployment)
- [Project Structure](#project-structure)
- [Sample Data](#sample-data)

## Overview

The Rewards Service is a Spring Boot application that:
- Manages customer information
- Records customer transactions
- Calculates reward points based on transaction amounts
- Provides REST APIs for all operations
- Includes comprehensive unit tests with 100% code coverage
- Supports Docker containerization

## Reward Points Calculation

The reward points are calculated using the following rules:

1. **For every dollar spent over $50**: Customer receives 1 point
2. **For every dollar spent over $100**: Customer receives an additional 1 point

### Example Calculation

For a transaction of **$120**:
- Points from $50 threshold: (120 - 50) × 1 = **70 points**
- Points from $100 threshold: (120 - 100) × 1 = **20 points**
- **Total: 90 points**

### Calculation Table

| Transaction Amount | Points Calculation | Total Points |
|-------------------|-------------------|--------------|
| $45 | 0 | 0 |
| $50 | 0 | 0 |
| $75 | (75 - 50) × 1 = 25 | 25 |
| $100 | (100 - 50) × 1 = 50 | 50 |
| $120 | (120 - 50) × 1 + (120 - 100) × 1 = 70 + 20 | 90 |
| $200 | (200 - 50) × 1 + (200 - 100) × 1 = 150 + 100 | 250 |

## Technology Stack

- **Java**: 21
- **Spring Boot**: 3.2.0
- **Database**: PostgreSQL 16
- **Build Tool**: Maven 3.9+
- **Documentation**: Swagger/OpenAPI 3
- **Testing**: JUnit 5, Mockito
- **Code Coverage**: JaCoCo

## Prerequisites

- Java 21 or higher
- Maven 3.9+ or higher
- PostgreSQL 16 or higher
- Docker and Docker Compose (optional, for containerized deployment)

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd steller-it-rewards
```

### 2. Database Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE rewards_db;
CREATE USER rewards_user WITH PASSWORD 'rewards_password';
GRANT ALL PRIVILEGES ON DATABASE rewards_db TO rewards_user;
```

### 3. Configure Application

Update `src/main/resources/application.yml` if needed, or set environment variables:

```bash
export DB_USERNAME=rewards_user
export DB_PASSWORD=rewards_password
export SERVER_PORT=8080
```

### 4. Build the Application

```bash
mvn clean install
```

### 5. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 6. Verify Health

```bash
curl http://localhost:8080/api/actuator/health
```

## API Documentation

### Swagger UI

Once the application is running, access the Swagger UI at:

```
http://localhost:8080/api/swagger-ui.html
```

### API Endpoints

#### Health Check

- **GET** `/api/actuator/health` - Health check endpoint

#### Customers

- **POST** `/api/customers` - Create a new customer
- **GET** `/api/customers` - Get all customers
- **GET** `/api/customers/{id}` - Get customer by ID

#### Transactions

- **POST** `/api/transactions` - Create a new transaction
- **GET** `/api/transactions/customer/{customerId}` - Get all transactions for a customer

#### Reward Points

- **GET** `/api/rewards/customer/{customerId}` - Calculate reward points for a customer (last 3 months)

### Sample Requests

#### Create Customer

```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com"
  }'
```

#### Create Transaction

```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "amount": 120.00,
    "description": "Grocery shopping",
    "transactionDate": "2024-01-05T09:30:00"
  }'
```

#### Get Reward Points

```bash
curl http://localhost:8080/api/rewards/customer/1
```

### Sample Response

```json
{
  "customerId": 1,
  "customerName": "John Doe",
  "email": "john.doe@example.com",
  "monthlyRewards": [
    {
      "month": "January",
      "year": 2024,
      "rewardPoints": 545.00,
      "transactionCount": 5
    },
    {
      "month": "February",
      "year": 2024,
      "rewardPoints": 555.00,
      "transactionCount": 5
    },
    {
      "month": "March",
      "year": 2024,
      "rewardPoints": 640.00,
      "transactionCount": 5
    }
  ],
  "totalRewardPoints": 1740.00
}
```

## Running Tests

### Run All Tests

```bash
mvn test
```

### Run Tests with Coverage Report

```bash
mvn clean test jacoco:report
```

The coverage report will be generated at: `target/site/jacoco/index.html`

### Test Coverage

The project includes comprehensive unit tests achieving **100% code coverage** for:
- Service layer (RewardPointsCalculator, CustomerService, TransactionService, RewardPointsService)
- Controller layer (CustomerController, TransactionController, RewardPointsController)
- Exception handling (GlobalExceptionHandler)

## Docker Deployment

### Using Docker Compose (Recommended)

```bash
docker-compose up -d
```

This will start:
- PostgreSQL database on port 5432
- Rewards Service on port 8080

### Using Docker Only

#### Build Image

```bash
docker build -t rewards-service:1.0.0 .
```

#### Run Container

```bash
docker run -d \
  --name rewards-service \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/rewards_db \
  -e SPRING_DATASOURCE_USERNAME=rewards_user \
  -e SPRING_DATASOURCE_PASSWORD=rewards_password \
  rewards-service:1.0.0
```

### Verify Docker Deployment

```bash
curl http://localhost:8080/api/actuator/health
```

## Project Structure

```
steller-it-rewards/
├── src/
│   ├── main/
│   │   ├── java/com/stellerit/rewards/
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── controller/       # REST controllers
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── exception/        # Custom exceptions and handlers
│   │   │   ├── model/            # Entity models
│   │   │   ├── repository/       # JPA repositories
│   │   │   ├── service/          # Business logic
│   │   │   └── RewardsServiceApplication.java
│   │   └── resources/
│   │       ├── application.yml   # Application configuration
│   │       └── data.sql          # Seed data
│   └── test/
│       └── java/com/stellerit/rewards/
│           ├── controller/        # Controller tests
│           ├── exception/         # Exception handler tests
│           └── service/          # Service tests
├── postman/
│   └── Rewards_Service_API.postman_collection.json
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Sample Data

The application includes seed data (`src/main/resources/data.sql`) with:

- **5 Customers**: John Doe, Jane Smith, Bob Johnson, Alice Williams, Charlie Brown
- **Transactions**: Multiple transactions per customer across January, February, and March 2024

### Sample Dataset

| Customer | Month | Transactions | Total Amount | Reward Points |
|----------|-------|--------------|--------------|---------------|
| John Doe | January | 5 | $590.50 | 545.00 |
| John Doe | February | 5 | $535.00 | 555.00 |
| John Doe | March | 5 | $590.00 | 640.00 |
| Jane Smith | January | 4 | $365.00 | 365.00 |
| Jane Smith | February | 4 | $455.00 | 510.00 |
| Jane Smith | March | 4 | $445.00 | 445.00 |

## Features

- ✅ RESTful API design
- ✅ Comprehensive error handling with custom exceptions
- ✅ Request validation
- ✅ Swagger/OpenAPI documentation
- ✅ Unit tests with 100% coverage
- ✅ Docker support
- ✅ Health check endpoint
- ✅ Logging at different levels
- ✅ Database seeding with test data
- ✅ Postman collection for API testing

## Best Practices Implemented

1. **Clean Architecture**: Separation of concerns with layered architecture
2. **SOLID Principles**: Single Responsibility, Dependency Inversion
3. **RESTful Design**: Proper HTTP methods and status codes
4. **Exception Handling**: Custom exceptions with global handler
5. **Validation**: Input validation using Bean Validation
6. **Documentation**: Comprehensive API documentation with Swagger
7. **Testing**: Unit tests with high coverage
8. **Logging**: Structured logging at appropriate levels
9. **Configuration**: Externalized configuration
10. **Security**: Non-root user in Docker container

## License

This project is licensed under the Apache License 2.0.

## Contact

For questions or support, please contact the Steller IT Team.

