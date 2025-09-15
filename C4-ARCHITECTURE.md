# DigiCommerce Architecture (C4 Model)

## Level 1: System Context

```
                    ┌─────────────────────┐
                    │     Customer        │
                    │   (Person)          │
                    └─────────┬───────────┘
                              │ Uses
                              ▼
                    ┌─────────────────────┐
                    │   DigiCommerce      │
                    │    Platform         │
                    │  (Software System)  │
                    │                     │
                    │ Manages users and   │
                    │ processes orders    │
                    └─────────────────────┘
```

**Purpose**: E-commerce platform for managing users and processing orders
**Primary Users**: Customers who create accounts and place orders
**Key Capabilities**: User management, order processing, authentication

## Level 2: Container Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                    DigiCommerce Platform                        │
│                                                                 │
│  ┌─────────────────┐    ┌─────────────────┐                   │
│  │ Orchestration   │◄──►│   User Service  │                   │
│  │   Service       │    │  [Spring Boot]  │                   │
│  │ [Spring Boot]   │    │   Port: 8081    │                   │
│  │  Port: 8083     │    └─────────┬───────┘                   │
│  └─────────┬───────┘              │                           │
│            │                      │                           │
│            ▼                      ▼                           │
│  ┌─────────────────┐    ┌─────────────────┐                   │
│  │  Order Service  │    │  Token Service  │                   │
│  │ [Spring Boot]   │◄──►│ [Spring Boot]   │                   │
│  │  Port: 8082     │    │  Port: 8090     │                   │
│  └─────────────────┘    └─────────────────┘                   │
└─────────────────────────────────────────────────────────────────┘
                              ▲
                              │ HTTPS/REST
                    ┌─────────────────────┐
                    │     Customer        │
                    │   (Web Client)      │
                    └─────────────────────┘
```

### Container Responsibilities

**Orchestration Service** (Port 8083)
- Technology: Spring Boot, WebFlux
- Purpose: API composition and business logic coordination
- Responsibilities: Aggregate user and order data, orchestrate cross-service operations

**User Service** (Port 8081)
- Technology: Spring Boot, JPA, H2
- Purpose: User management and profile data
- Responsibilities: User CRUD operations, profile management

**Order Service** (Port 8082)
- Technology: Spring Boot, JPA, H2
- Purpose: Order processing and management
- Responsibilities: Order lifecycle, status tracking, customer order history

**Token Service** (Port 8090)
- Technology: Spring Boot, Spring Security, JWT, H2
- Purpose: Authentication and authorization
- Responsibilities: JWT token generation, validation, revocation

## Level 3: Component Diagram - Orchestration Service

```
┌─────────────────────────────────────────────────────────────────┐
│                  Orchestration Service                          │
│                                                                 │
│  ┌─────────────────┐    ┌─────────────────┐                   │
│  │ REST Controller │    │ Service Layer   │                   │
│  │                 │───►│                 │                   │
│  │ - UserOrders    │    │ - Orchestration │                   │
│  │   Controller    │    │   Service       │                   │
│  └─────────────────┘    └─────────┬───────┘                   │
│                                   │                           │
│                                   ▼                           │
│                         ┌─────────────────┐                   │
│                         │ HTTP Clients    │                   │
│                         │                 │                   │
│                         │ - UserClient    │                   │
│                         │ - OrderClient   │                   │
│                         └─────────────────┘                   │
└─────────────────────────────────────────────────────────────────┘
```

## Level 4: Code Structure

### Orchestration Service Classes
```
org.woven.digicommerce.orchestsvc/
├── controller/
│   └── OrchestrationController.java
├── service/
│   └── OrchestrationService.java
├── client/
│   ├── UserServiceClient.java
│   └── OrderServiceClient.java
├── dto/
│   ├── UserOrderResponse.java
│   ├── User.java
│   └── Order.java
└── Application.java
```

## Communication Patterns

### Inter-Container Communication
```
Customer ──HTTP/REST──► Orchestration Service
                              │
                              ├──HTTP/REST──► User Service
                              │                     │
                              └──HTTP/REST──► Order Service
                                                    │
                              ┌─────────────────────┘
                              ▼
                        Token Service ◄──HTTP/REST── All Services
```

### Authentication Flow
1. **Login**: Customer → Token Service → JWT Token
2. **Request**: Customer → Orchestration Service (with JWT)
3. **Validation**: Services → Token Service (validate JWT)
4. **Response**: Aggregated data → Customer

## Technology Decisions

### Platform
- **Java 17** - LTS version with modern language features
- **Spring Boot 3.2.0** - Production-ready framework
- **Gradle Multi-Module** - Build automation and dependency management

### Communication
- **HTTP REST** - Synchronous service communication
- **JSON** - Data exchange format
- **Spring WebFlux** - Reactive HTTP client for non-blocking calls

### Data Storage
- **H2 Database** - In-memory for development/testing
- **Spring Data JPA** - ORM and repository pattern
- **Database per Service** - Microservices data isolation

### Security
- **JWT Tokens** - Stateless authentication
- **Spring Security** - Security framework
- **Bearer Token Authentication** - Standard HTTP auth

### Testing Strategy
- **JUnit 5** - Unit testing framework
- **Cucumber** - BDD acceptance testing
- **Spring Boot Test** - Integration testing

## Data Model

### User Service Domain
```
User Entity:
├── id: Long (Primary Key)
├── username: String (Unique)
├── email: String
├── firstName: String
└── lastName: String
```

### Order Service Domain
```
Order Entity:
├── id: Long (Primary Key)
├── userId: Long (Foreign Key)
├── customerName: String
├── productName: String
├── quantity: Integer
├── price: BigDecimal
└── status: OrderStatus (Enum)
```

### Token Service Domain
```
Token Entity:
├── id: Long (Primary Key)
├── username: String
├── tokenHash: String
├── createdAt: LocalDateTime
├── expiresAt: LocalDateTime
└── revoked: Boolean
```

## Security Architecture

### Trust Boundaries
```
┌─────────────────────────────────────────────────────────────────┐
│                    Trusted Internal Network                     │
│                                                                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐        │
│  │    User     │    │   Order     │    │    Token    │        │
│  │   Service   │    │  Service    │    │   Service   │        │
│  └─────────────┘    └─────────────┘    └─────────────┘        │
│         ▲                   ▲                   ▲              │
│         │                   │                   │              │
└─────────┼───────────────────┼───────────────────┼──────────────┘
          │                   │                   │
          └───────────────────┼───────────────────┘
                              │
                    ┌─────────────────┐
                    │ Orchestration   │
                    │    Service      │
                    └─────────────────┘
                              ▲
                              │ HTTPS + JWT
                    ┌─────────────────┐
                    │   External      │
                    │   Client        │
                    └─────────────────┘
```

### Security Controls
- **JWT Authentication** - Stateless token-based auth
- **Service-to-Service Validation** - All services validate tokens
- **Token Revocation** - Centralized token management
- **HTTPS Communication** - Encrypted data in transit

## Deployment View

### Development Environment
```
┌─────────────────────────────────────────────────────────────────┐
│                      Local Machine                              │
│                                                                 │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐            │
│  │   :8081     │  │   :8082     │  │   :8083     │            │
│  │    User     │  │   Order     │  │Orchestration│            │
│  │   Service   │  │  Service    │  │   Service   │            │
│  └─────────────┘  └─────────────┘  └─────────────┘            │
│                                                                 │
│                    ┌─────────────┐                             │
│                    │   :8090     │                             │
│                    │   Token     │                             │
│                    │  Service    │                             │
│                    └─────────────┘                             │
└─────────────────────────────────────────────────────────────────┘
```

### Production Deployment
```
┌─────────────────────────────────────────────────────────────────┐
│                    Kubernetes Cluster                           │
│                                                                 │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐            │
│  │    Pod      │  │    Pod      │  │    Pod      │            │
│  │ User Service│  │Order Service│  │Orchestration│            │
│  │   + DB      │  │   + DB      │  │   Service   │            │
│  └─────────────┘  └─────────────┘  └─────────────┘            │
│                                                                 │
│                    ┌─────────────┐                             │
│                    │    Pod      │                             │
│                    │Token Service│                             │
│                    │   + DB      │                             │
│                    └─────────────┘                             │
└─────────────────────────────────────────────────────────────────┘
```

## Quality Attributes

### Scalability
- **Horizontal Scaling**: Stateless services enable multiple instances
- **Database Isolation**: Each service owns its data
- **Load Distribution**: Services can be scaled independently

### Reliability
- **Service Isolation**: Failure in one service doesn't cascade
- **Graceful Degradation**: Services handle downstream failures
- **Health Monitoring**: Spring Boot Actuator endpoints

### Security
- **Defense in Depth**: Multiple security layers
- **Principle of Least Privilege**: Services only access required resources
- **Secure by Default**: JWT tokens with expiration

### Maintainability
- **Single Responsibility**: Each service has focused purpose
- **Loose Coupling**: Services communicate via well-defined APIs
- **Technology Consistency**: Uniform Spring Boot stack

## Build & Deployment Strategy

### Source Code Organization
```
digicommerce/
├── build.gradle (parent)
├── settings.gradle
├── C4-ARCHITECTURE.md
├── README.md
├── orchestration-service/
│   ├── src/main/java/
│   ├── src/test/java/
│   └── build.gradle
├── user-service/
├── order-service/
└── token-service/
```

### Continuous Integration
```bash
# Build all services
./gradlew build

# Run tests
./gradlew test

# Package applications
./gradlew bootJar
```

## Future Considerations

### Evolution Path
1. **Message Queues**: Async communication for better resilience
2. **API Gateway**: Centralized routing and cross-cutting concerns
3. **Service Mesh**: Advanced traffic management and observability
4. **Event Sourcing**: Audit trail and temporal queries
5. **CQRS**: Separate read/write models for performance