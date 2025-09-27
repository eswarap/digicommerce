# AWS Setup for DigiCommerce

## 1. Configure AWS CLI

```bash
# Install AWS CLI (if not already installed)
# Download from: https://aws.amazon.com/cli/

# Configure AWS credentials
aws configure
# Enter:
# - AWS Access Key ID
# - AWS Secret Access Key  
# - Default region: us-east-1
# - Default output format: json
```

## 2. Create JWT Secret in AWS Secrets Manager

```bash
# Create the JWT secret
aws secretsmanager create-secret \
    --name "jwt-secret" \
    --description "JWT signing secret for DigiCommerce token service" \
    --secret-string "mySecretKeyForJWTTokenGeneration123456789012" \
    --region us-east-1

# Verify the secret was created
aws secretsmanager describe-secret \
    --secret-id "jwt-secret" \
    --region us-east-1

# Test retrieving the secret
aws secretsmanager get-secret-value \
    --secret-id "jwt-secret" \
    --region us-east-1
```

## 3. IAM Permissions

Ensure your AWS user/role has the following permissions:

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "secretsmanager:GetSecretValue"
            ],
            "Resource": "arn:aws:secretsmanager:us-east-1:*:secret:jwt-secret*"
        }
    ]
}
```

## 4. Environment Variables (Alternative)

Instead of `aws configure`, you can set environment variables:

```bash
export AWS_ACCESS_KEY_ID=your-access-key
export AWS_SECRET_ACCESS_KEY=your-secret-key
export AWS_DEFAULT_REGION=us-east-1
```

## 5. Verify Setup

```bash
# Test AWS CLI access
aws sts get-caller-identity

# Test secret retrieval
aws secretsmanager get-secret-value --secret-id jwt-secret --region us-east-1
```