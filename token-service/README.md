# Token Service

A Spring Boot application for JWT token management.

## Features

- Generate JWT tokens
- Validate tokens
- Revoke tokens
- In-memory H2 database

## API Endpoints

- `POST /api/auth/login` - Generate token (Body: username as string)
- `POST /api/auth/logout` - Revoke token (Header: Authorization: Bearer <token>)
- `POST /api/auth/validate` - Validate token (Header: Authorization: Bearer <token>)

## Running

```bash
./gradlew bootRun
```

## Example Usage

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login -d "testuser" -H "Content-Type: text/plain"

# Validate (replace <token> with actual token)
curl -X POST http://localhost:8080/api/auth/validate -H "Authorization: Bearer <token>"

# Logout
curl -X POST http://localhost:8080/api/auth/logout -H "Authorization: Bearer <token>"
```