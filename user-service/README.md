# User Service

Spring Boot REST API for user management.

## Features

- Create, read, update, delete users
- In-memory H2 database
- RESTful endpoints

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/user.svc/api/v1/users` | Get all users |
| GET | `/user.svc/api/v1/users/{id}` | Get user by ID |
| GET | `/user.svc/api/v1/users/username/{username}` | Get user by username |
| POST | `/user.svc/api/v1/users` | Create new user |
| PUT | `/user.svc/api/v1/users/{id}` | Update user |
| DELETE | `/user.svc/api/v1/users/{id}` | Delete user |

## Running

```bash
./gradlew bootRun
```

## Example Usage

```bash
# Create user
curl -X POST http://localhost:8081/user.svc/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@example.com","firstName":"John","lastName":"Doe"}'

# Get all users
curl http://localhost:8081/user.svc/api/v1/users

# Get user by ID
curl http://localhost:8081/user.svc/api/v1/users/1

# Update user
curl -X PUT http://localhost:8081/user.svc/api/v1/users/1 \
  -H "Content-Type: application/json" \
  -d '{"email":"john.doe@example.com","firstName":"John","lastName":"Doe"}'

# Delete user
curl -X DELETE http://localhost:8081/user.svc/api/v1/users/1
```