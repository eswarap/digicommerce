# DigiCommerce Platform

Multi-module Gradle project containing four Spring Boot microservices with automated AWS deployment via GitHub Actions.

## Services

- **orchestration-service** (port 8083) - Orchestrates calls between user and order services
- **user-service** (port 8081) - Manages user data
- **order-service** (port 8082) - Manages order data
- **token-service** (port 8090) - Handles JWT authentication

## AWS Deployment

### Prerequisites
1. AWS CLI configured with appropriate permissions
2. GitHub repository with the following secrets:
   - `AWS_ACCESS_KEY_ID`
   - `AWS_SECRET_ACCESS_KEY`

### Deploy Infrastructure
```bash
# Deploy AWS infrastructure (ECR, ECS, ALB, etc.)
./aws-deployment/deploy.sh
```

### Automated CI/CD
Push to `main` branch triggers GitHub Actions workflow that:
1. Builds JAR files with Gradle
2. Builds and pushes Docker images to ECR
3. Updates ECS services with new images

## Local Development

```bash
# Build all services
./gradlew build

# Run specific service
./gradlew :orchestration-service:bootRun
./gradlew :user-service:bootRun
./gradlew :order-service:bootRun
./gradlew :token-service:bootRun
```

## Project Structure

```
digicommerce/
├── .github/workflows/
│   └── deploy.yml              # GitHub Actions CI/CD
├── aws-deployment/
│   ├── deploy.sh              # Infrastructure deployment
│   ├── infrastructure.yaml    # AWS resources
│   └── services.yaml          # ECS services
├── orchestration-service/
├── user-service/
├── order-service/
├── token-service/
├── Dockerfile                 # Multi-service Docker build
├── build.gradle              # Parent build config
└── settings.gradle           # Project settings
```

## API Endpoints (via ALB)

After deployment, services are available at:
- User Service: `http://<ALB-URL>/user.svc/api/v1/users`
- Order Service: `http://<ALB-URL>/order.svc/api/v1/orders`
- Token Service: `http://<ALB-URL>/token.svc/api/v1/auth/login`
- Orchestration Service: `http://<ALB-URL>/orchest.svc/api/v1/orchestration/user/1/orders`