# Orchestration Service

Spring Boot service that orchestrates calls to User Service and Order Service.

## Features

- Combines user data with their orders
- WebClient for HTTP communication
- Configurable service URLs

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/orchest.svc/api/v1/orchestration/user/{userId}/orders` | Get user and their orders by user ID |
| GET | `/orchest.svc/api/v1/orchestration/username/{username}/orders` | Get user and their orders by username |

## Running

```bash
./gradlew bootRun
```

## Prerequisites

Start the dependent services:
1. User Service (port 8081)
2. Order Service (port 8082)

## Example Usage

```bash
# Get user orders by ID
curl http://localhost:8083/orchest.svc/api/v1/orchestration/user/1/orders

# Get user orders by username
curl http://localhost:8083/orchest.svc/api/v1/orchestration/username/john/orders
```

## Response Format

```json
{
  "user": {
    "id": 1,
    "username": "john",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe"
  },
  "orders": [
    {
      "id": 1,
      "customerName": "John Doe",
      "productName": "Laptop",
      "quantity": 1,
      "price": 999.99,
      "status": "PENDING"
    }
  ],
  "totalOrders": 1
}
```