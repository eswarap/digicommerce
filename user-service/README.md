# User Service

Spring Boot REST API for user management.

## Features

- Create, read, update, delete users
- In-memory H2 database
- RESTful endpoints

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users` | Get all users |
| GET | `/api/users/{id}` | Get user by ID |
| GET | `/api/users/username/{username}` | Get user by username |
| POST | `/api/users` | Create new user |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Delete user |

## Running

```bash
./gradlew bootRun
```

## Example Usage

```bash
# Create user
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@example.com","firstName":"John","lastName":"Doe"}'

# Get all users
curl http://localhost:8081/api/users

# Get user by ID
curl http://localhost:8081/api/users/1

# Update user
curl -X PUT http://localhost:8081/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"email":"john.doe@example.com","firstName":"John","lastName":"Doe"}'

# Delete user
curl -X DELETE http://localhost:8081/api/users/1
```