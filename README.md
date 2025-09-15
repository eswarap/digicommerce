# DigiCommerce Platform

Multi-module Gradle project containing four Spring Boot microservices.

## Services

- **orchestration-service** (port 8083) - Orchestrates calls between user and order services
- **user-service** (port 8081) - Manages user data
- **order-service** (port 8082) - Manages order data
- **token-service** (port 8090) - Handles JWT authentication

## Build & Run

```bash
# Build all services
./gradlew build

# Run specific service
./gradlew :orchestration-service:bootRun
./gradlew :user-service:bootRun
./gradlew :order-service:bootRun
./gradlew :token-service:bootRun

# Build specific service
./gradlew :orchestration-service:build
```

## Project Structure

```
digicommerce/
├── orchestration-service/
├── user-service/
├── order-service/
├── token-service/
├── build.gradle (parent)
└── settings.gradle (parent)
```