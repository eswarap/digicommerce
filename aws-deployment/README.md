# AWS Deployment Guide

This guide explains how to deploy the DigiCommerce platform to AWS using ECS Fargate.

## Architecture

- **ECS Fargate**: Serverless container platform
- **Application Load Balancer**: Routes traffic to services
- **ECR**: Container registry for Docker images
- **CloudWatch**: Logging and monitoring
- **VPC**: Isolated network environment

## Prerequisites

1. **AWS CLI** installed and configured
2. **Docker** installed
3. **AWS Account** with appropriate permissions

## Quick Deployment

Run the automated deployment script:

```bash
aws-deployment\deploy.bat
```

## Manual Deployment Steps

### 1. Configure AWS CLI

```bash
aws configure
# Enter your AWS Access Key ID, Secret Access Key, region (us-east-1), and output format (json)
```

### 2. Deploy Infrastructure

```bash
aws cloudformation deploy \
    --template-file aws-deployment\infrastructure.yaml \
    --stack-name digicommerce-infrastructure \
    --region us-east-1 \
    --capabilities CAPABILITY_IAM
```

### 3. Build Services

```bash
gradlew build
```

### 4. Build and Push Docker Images

```bash
# Get ECR login
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <account-id>.dkr.ecr.us-east-1.amazonaws.com

# Build and push each service
docker build -t <account-id>.dkr.ecr.us-east-1.amazonaws.com/digicommerce/user-service:latest user-service
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/digicommerce/user-service:latest

docker build -t <account-id>.dkr.ecr.us-east-1.amazonaws.com/digicommerce/order-service:latest order-service
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/digicommerce/order-service:latest

docker build -t <account-id>.dkr.ecr.us-east-1.amazonaws.com/digicommerce/token-service:latest token-service
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/digicommerce/token-service:latest

docker build -t <account-id>.dkr.ecr.us-east-1.amazonaws.com/digicommerce/orchestration-service:latest orchestration-service
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/digicommerce/orchestration-service:latest
```

### 5. Deploy Services

```bash
aws cloudformation deploy \
    --template-file aws-deployment\services.yaml \
    --stack-name digicommerce-services \
    --parameter-overrides InfrastructureStackName=digicommerce-infrastructure \
    --region us-east-1 \
    --capabilities CAPABILITY_IAM
```

## Access Your Application

After deployment, get the ALB URL:

```bash
aws cloudformation describe-stacks \
    --stack-name digicommerce-infrastructure \
    --query "Stacks[0].Outputs[?OutputKey=='ALBArn'].OutputValue" \
    --output text
```

Then get the DNS name:

```bash
aws elbv2 describe-load-balancers \
    --load-balancer-arns <alb-arn> \
    --query "LoadBalancers[0].DNSName" \
    --output text
```

## Service Endpoints

- User Service: `http://<alb-url>/user.svc/api/v1/users`
- Order Service: `http://<alb-url>/order.svc/api/v1/orders`
- Token Service: `http://<alb-url>/token.svc/api/v1/auth/login`
- Orchestration Service: `http://<alb-url>/orchest.svc/api/v1/orchestration/user/1/orders`

## Cleanup

To remove all resources:

```bash
aws cloudformation delete-stack --stack-name digicommerce-services --region us-east-1
aws cloudformation delete-stack --stack-name digicommerce-infrastructure --region us-east-1
```

## Cost Optimization

- Services run on Fargate with minimal resources (256 CPU, 512 MB memory)
- ALB and ECS services scale based on demand
- CloudWatch logs retained for 7 days
- Use AWS Free Tier eligible resources where possible

## Monitoring

- CloudWatch logs: `/ecs/<service-name>`
- ECS service metrics in CloudWatch
- ALB access logs (optional, can be enabled)

## Security

- Services run in private subnets (if using NAT Gateway)
- Security groups restrict access between components
- IAM roles follow least privilege principle