# DigiCommerce Architecture

## Overview

DigiCommerce is a microservices-based e-commerce platform built with Spring Boot. The system follows a distributed architecture pattern with service-to-service communication via HTTP REST APIs.

## System Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Client/UI     │    │   Load Balancer │    │   API Gateway   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
         ┌───────────────────────┼───────────────────────┐
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ Orchestration   │◄──►│   User Service  │    │  Order Service  │
│   Service       │    │    (Port 8081)  │    │   (Port 8082)   │
│  (Port 8083)    │    └─────────────────┘    └─────────────────┘
└─────────────────┘             │                       │
         │                      │                       │
         │              ┌─────────────────┐             │
         └─────────────►│  Token Service  │◄────────────┘
                        │   (Port 8090)   │
                        └─────────────────┘
```

## Services

### Token Service (Port 8090)
- **Purpose**: JWT authentication and authorization
- **Database**: H2 in-memory
- **Context Path**: `/token.svc/api/v1`
- **Key Features**:
  - Generate JWT tokens
  - Validate tokens
  - Revoke tokens

### User Service (Port 8081)
- **Purpose**: User management and profile data
- **Database**: H2 in-memory
- **Context Path**: `/user.svc/api/v1`
- **Dependencies**: Token Service
- **Key Features**:
  - CRUD operations for users
  - User authentication
  - Profile management

### Order Service (Port 8082)
- **Purpose**: Order processing and management
- **Database**: H2 in-memory
- **Context Path**: `/order.svc/api/v1`
- **Dependencies**: Token Service
- **Key Features**:
  - Order creation and tracking
  - Status management
  - Customer order history

### Orchestration Service (Port 8083)
- **Purpose**: Business logic coordination
- **Database**: None (stateless)
- **Context Path**: `/orchest.svc/api/v1`
- **Dependencies**: User Service, Order Service
- **Key Features**:
  - Aggregate user and order data
  - Cross-service business operations
  - API composition

## Communication Patterns

### Service-to-Service Communication
- **Protocol**: HTTP REST
- **Format**: JSON
- **Client**: Spring WebClient (reactive)
- **Authentication**: JWT Bearer tokens

### Data Flow
1. Client requests → Orchestration Service
2. Orchestration Service → User/Order Services
3. User/Order Services → Token Service (validation)
4. Response aggregation → Client

## Technology Stack

### Core Framework
- **Spring Boot 3.2.0**
- **Java 17**
- **Gradle** (multi-module)

### Dependencies
- **Spring Web** - REST API endpoints
- **Spring WebFlux** - Reactive HTTP client
- **Spring Data JPA** - Database operations
- **Spring Security** - Authentication (Token Service)
- **H2 Database** - In-memory storage
- **JWT (JJWT)** - Token management
- **Lombok** - Code generation

### Testing
- **JUnit 5** - Unit testing
- **Cucumber** - BDD testing
- **Spring Boot Test** - Integration testing

## Database Design

### User Service Schema
```sql
users (
  id BIGINT PRIMARY KEY,
  username VARCHAR(50) UNIQUE,
  email VARCHAR(100),
  first_name VARCHAR(50),
  last_name VARCHAR(50)
)
```

### Order Service Schema
```sql
orders (
  id BIGINT PRIMARY KEY,
  user_id BIGINT,
  customer_name VARCHAR(100),
  product_name VARCHAR(100),
  quantity INTEGER,
  price DECIMAL(10,2),
  status VARCHAR(20)
)
```

### Token Service Schema
```sql
tokens (
  id BIGINT PRIMARY KEY,
  username VARCHAR(50),
  token_hash VARCHAR(255),
  created_at TIMESTAMP,
  expires_at TIMESTAMP,
  revoked BOOLEAN
)
```

## Security Model

### Authentication Flow
1. Client → Token Service (login)
2. Token Service → JWT token
3. Client → Services (with Bearer token)
4. Services → Token Service (validation)

### Authorization
- **Stateless JWT tokens**
- **Service-level validation**
- **Token revocation support**

## Deployment Architecture

### Local Development
```
localhost:8090 - Token Service
localhost:8081 - User Service  
localhost:8082 - Order Service
localhost:8083 - Orchestration Service
```

### Production Considerations
- **Container orchestration** (Docker/Kubernetes)
- **Service discovery** (Eureka/Consul)
- **Load balancing** (NGINX/HAProxy)
- **External databases** (PostgreSQL/MySQL)
- **Monitoring** (Prometheus/Grafana)
- **Logging** (ELK Stack)

## Build & Deployment

### Multi-Module Gradle Structure
```
digicommerce/
├── build.gradle (parent)
├── settings.gradle
├── orchestration-service/
├── user-service/
├── order-service/
└── token-service/
```

### Build Commands
```bash
# Build all services
./gradlew build

# Run specific service
./gradlew :service-name:bootRun

# Run all services
./gradlew runAllServices
```

## Scalability Considerations

### Horizontal Scaling
- **Stateless services** enable easy scaling
- **Database per service** pattern
- **Load balancer distribution**

### Performance Optimization
- **Reactive programming** (WebFlux)
- **Connection pooling** (HikariCP)
- **Caching strategies** (Redis)
- **Async communication** (Message queues)

## Monitoring & Observability

### Health Checks
- Spring Boot Actuator endpoints
- Service dependency health
- Database connectivity

### Metrics
- Request/response times
- Error rates
- Resource utilization
- Business metrics

### Logging
- Structured logging (JSON)
- Correlation IDs
- Centralized log aggregation