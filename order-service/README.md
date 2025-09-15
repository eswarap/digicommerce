# Order Service

Spring Boot REST API for order management.

## Features

- Create, read, update, delete orders
- Filter by customer and status
- In-memory H2 database

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/order.svc/api/v1/orders` | Get all orders |
| GET | `/order.svc/api/v1/orders/{id}` | Get order by ID |
| GET | `/order.svc/api/v1/orders/customer/{customerName}` | Get orders by customer |
| GET | `/order.svc/api/v1/orders/status/{status}` | Get orders by status |
| POST | `/order.svc/api/v1/orders` | Create new order |
| PUT | `/order.svc/api/v1/orders/{id}/status` | Update order status |
| DELETE | `/order.svc/api/v1/orders/{id}` | Delete order |

## Running

```bash
./gradlew bootRun
```

## Example Usage

```bash
# Create order
curl -X POST http://localhost:8082/order.svc/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{"customerName":"John Doe","productName":"Laptop","quantity":1,"price":999.99}'

# Get all orders
curl http://localhost:8082/order.svc/api/v1/orders

# Get orders by customer
curl http://localhost:8082/order.svc/api/v1/orders/customer/John%20Doe

# Update order status
curl -X PUT http://localhost:8082/order.svc/api/v1/orders/1/status \
  -H "Content-Type: text/plain" \
  -d "SHIPPED"
```