# Order Service

Spring Boot REST API for order management.

## Features

- Create, read, update, delete orders
- Filter by customer and status
- In-memory H2 database

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/orders` | Get all orders |
| GET | `/api/orders/{id}` | Get order by ID |
| GET | `/api/orders/customer/{customerName}` | Get orders by customer |
| GET | `/api/orders/status/{status}` | Get orders by status |
| POST | `/api/orders` | Create new order |
| PUT | `/api/orders/{id}/status` | Update order status |
| DELETE | `/api/orders/{id}` | Delete order |

## Running

```bash
./gradlew bootRun
```

## Example Usage

```bash
# Create order
curl -X POST http://localhost:8082/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerName":"John Doe","productName":"Laptop","quantity":1,"price":999.99}'

# Get all orders
curl http://localhost:8082/api/orders

# Get orders by customer
curl http://localhost:8082/api/orders/customer/John%20Doe

# Update order status
curl -X PUT http://localhost:8082/api/orders/1/status \
  -H "Content-Type: text/plain" \
  -d "SHIPPED"
```