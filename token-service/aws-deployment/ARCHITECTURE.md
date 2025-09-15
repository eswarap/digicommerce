# Architecture Documentation

## System Overview

Serverless microservices architecture using AWS Lambda, API Gateway, and DynamoDB for token management and notifications.

## Architecture Diagram

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────────┐
│                 │    │                  │    │                     │
│   Client Apps   │───▶│   API Gateway    │───▶│   Lambda Functions  │
│                 │    │                  │    │                     │
└─────────────────┘    └──────────────────┘    └─────────────────────┘
                              │                           │
                              │                           │
                              ▼                           ▼
                       ┌─────────────┐            ┌─────────────┐
                       │   CloudWatch│            │  DynamoDB   │
                       │    Logs     │            │   Tables    │
                       └─────────────┘            └─────────────┘
```

## Components

### API Gateway
- **Purpose**: Single entry point for all services
- **Features**: CORS enabled, request/response transformation
- **Endpoints**: `/api/auth/*`, `/api/notifications/*`

### Lambda Functions

#### Token Service Lambda
- **Runtime**: Java 17
- **Handler**: `TokenHandler::handleRequest`
- **Memory**: 512MB
- **Timeout**: 30s
- **Environment**: `JWT_SECRET`

#### Notification Service Lambda
- **Runtime**: Java 17
- **Handler**: `NotificationHandler::handleRequest`
- **Memory**: 512MB
- **Timeout**: 30s

### DynamoDB Tables

#### Tokens Table
```
Primary Key: tokenId (String)
Attributes:
- username (String)
- revoked (Boolean)
```

#### Notifications Table
```
Primary Key: notificationId (String)
Attributes:
- message (String)
- recipient (String)
- timestamp (String)
- status (String)
```

## API Endpoints

### Token Service
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | Generate JWT token |
| POST | `/api/auth/validate` | Validate token |
| POST | `/api/auth/logout` | Revoke token |

### Notification Service
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/notifications/send` | Send notification |
| GET | `/api/notifications` | Get all notifications |

## Data Flow

### Token Generation
1. Client sends username to `/api/auth/login`
2. Lambda generates JWT token
3. Token stored in DynamoDB
4. Token returned to client

### Token Validation
1. Client sends token in Authorization header
2. Lambda validates JWT signature
3. Lambda checks revocation status in DynamoDB
4. Validation result returned

### Notification Flow
1. Client sends notification request
2. Lambda stores notification in DynamoDB
3. Notification ID returned to client

## Security

- JWT tokens signed with HMAC-SHA256
- Environment variable for JWT secret
- CORS configured for cross-origin requests
- DynamoDB encryption at rest (default)

## Scalability

- **Auto-scaling**: Lambda functions scale automatically
- **Concurrency**: Up to 1000 concurrent executions per region
- **DynamoDB**: On-demand billing scales with usage
- **API Gateway**: Handles up to 10,000 requests per second

## Monitoring

- CloudWatch Logs for Lambda execution logs
- CloudWatch Metrics for performance monitoring
- API Gateway access logs
- DynamoDB metrics

## Cost Optimization

- **Lambda**: Pay per request and execution time
- **DynamoDB**: Pay per request (on-demand)
- **API Gateway**: Pay per API call
- **No idle costs**: Serverless architecture eliminates idle resource costs