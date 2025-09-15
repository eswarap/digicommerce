# AWS Lambda Deployment

This directory contains the AWS Lambda deployment configuration for both Token Service and Notification Service.

## Prerequisites

1. Install AWS CLI and configure credentials
2. Install SAM CLI
3. Install Java 17
4. Install Gradle

## Deployment Steps

1. **Build and Deploy:**
   ```bash
   build-and-deploy.bat
   ```

2. **Manual Steps:**
   ```bash
   # Build Lambda JARs
   cd token-lambda && gradle shadowJar
   cd ../notification-lambda && gradle shadowJar
   
   # Deploy with SAM
   cd ..
   sam build
   sam deploy --guided
   ```

## API Endpoints

After deployment, you'll get an API Gateway URL. The endpoints will be:

### Token Service
- `POST /api/auth/login` - Generate token
- `POST /api/auth/validate` - Validate token  
- `POST /api/auth/logout` - Revoke token

### Notification Service
- `POST /api/notifications/send` - Send notification
- `GET /api/notifications` - Get all notifications

## Example Usage

```bash
# Get API Gateway URL from deployment output
export API_URL="https://your-api-id.execute-api.us-east-1.amazonaws.com/prod"

# Login
curl -X POST $API_URL/api/auth/login -d "testuser" -H "Content-Type: text/plain"

# Send notification
curl -X POST $API_URL/api/notifications/send \
  -H "Content-Type: application/json" \
  -d '{"message":"Hello World","recipient":"user@example.com"}'

# Get notifications
curl -X GET $API_URL/api/notifications
```

## Environment Variables

Set `JWT_SECRET` environment variable in the Lambda function configuration or update the SAM template.