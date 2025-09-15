# Token Service

A Spring Boot application for JWT token management.

## Features

- Generate JWT tokens
- Validate tokens
- Revoke tokens
- In-memory H2 database

## API Endpoints

- `POST /token.svc/api/v1/auth/login` - Generate token (Body: username as string)
- `POST /token.svc/api/v1/auth/logout` - Revoke token (Header: Authorization: Bearer <token>)
- `POST /token.svc/api/v1/auth/validate` - Validate token (Header: Authorization: Bearer <token>)

## Running

```bash
./gradlew bootRun
```

## Example Usage

```bash
# Login
curl -X POST http://localhost:8090/token.svc/api/v1/auth/login -d "testuser" -H "Content-Type: text/plain"

# Validate (replace <token> with actual token)
curl -X POST http://localhost:8090/token.svc/api/v1/auth/validate -H "Authorization: Bearer <token>"

# Logout
curl -X POST http://localhost:8090/token.svc/api/v1/auth/logout -H "Authorization: Bearer <token>"
```