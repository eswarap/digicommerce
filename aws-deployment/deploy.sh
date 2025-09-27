#!/bin/bash
set -e

echo "DigiCommerce AWS Deployment Script"
echo "=================================="

# Change to project root directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
cd "$PROJECT_ROOT"

echo "Working directory: $(pwd)"

# Validate service directories exist
for service in user-service order-service token-service orchestration-service; do
    if [ ! -d "$service" ]; then
        echo "ERROR: Service directory '$service' not found in $(pwd)"
        echo "Please run this script from the digicommerce project root or ensure all service directories exist"
        exit 1
    fi
done

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo "ERROR: AWS CLI is not installed or not in PATH"
    echo "Please install AWS CLI first"
    exit 1
fi



# Set variables
REGION=us-east-1
INFRASTRUCTURE_STACK=digicommerce-infrastructure
SERVICES_STACK=digicommerce-services

# Get AWS Account ID
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
echo "Using AWS Account: $ACCOUNT_ID"
echo "Using Region: $REGION"

echo
echo "Step 2: Deploying infrastructure..."
echo "Using infrastructure template: $PROJECT_ROOT/aws-deployment/infrastructure.yaml"
if ! aws cloudformation deploy \
    --template-file aws-deployment/infrastructure.yaml \
    --stack-name $INFRASTRUCTURE_STACK \
    --region $REGION \
    --capabilities CAPABILITY_IAM 2>/dev/null; then
    
    echo "Stack deployment failed, attempting to delete and recreate..."
    aws cloudformation delete-stack --stack-name $INFRASTRUCTURE_STACK --region $REGION
    aws cloudformation wait stack-delete-complete --stack-name $INFRASTRUCTURE_STACK --region $REGION
    
    if ! aws cloudformation create-stack \
        --template-body file://aws-deployment/infrastructure.yaml \
        --stack-name $INFRASTRUCTURE_STACK \
        --region $REGION \
        --capabilities CAPABILITY_IAM; then
        echo "ERROR: Infrastructure deployment failed"
        exit 1
    fi
fi

echo
echo "Step 3: Getting ECR repository URIs..."

# Wait for stack to be complete
aws cloudformation wait stack-create-complete --stack-name $INFRASTRUCTURE_STACK --region $REGION 2>/dev/null || \
aws cloudformation wait stack-update-complete --stack-name $INFRASTRUCTURE_STACK --region $REGION 2>/dev/null

USER_REPO=$(aws cloudformation describe-stacks --stack-name $INFRASTRUCTURE_STACK --query "Stacks[0].Outputs[?OutputKey=='UserServiceRepository'].OutputValue" --output text --region $REGION)
ORDER_REPO=$(aws cloudformation describe-stacks --stack-name $INFRASTRUCTURE_STACK --query "Stacks[0].Outputs[?OutputKey=='OrderServiceRepository'].OutputValue" --output text --region $REGION)
TOKEN_REPO=$(aws cloudformation describe-stacks --stack-name $INFRASTRUCTURE_STACK --query "Stacks[0].Outputs[?OutputKey=='TokenServiceRepository'].OutputValue" --output text --region $REGION)
ORCHESTRATION_REPO=$(aws cloudformation describe-stacks --stack-name $INFRASTRUCTURE_STACK --query "Stacks[0].Outputs[?OutputKey=='OrchestrationServiceRepository'].OutputValue" --output text --region $REGION)

# Validate repository URIs
if [[ "$USER_REPO" == "None" || -z "$USER_REPO" ]]; then
    echo "ERROR: UserServiceRepository output not found in stack $INFRASTRUCTURE_STACK"
    exit 1
fi
if [[ "$ORDER_REPO" == "None" || -z "$ORDER_REPO" ]]; then
    echo "ERROR: OrderServiceRepository output not found in stack $INFRASTRUCTURE_STACK"
    exit 1
fi
if [[ "$TOKEN_REPO" == "None" || -z "$TOKEN_REPO" ]]; then
    echo "ERROR: TokenServiceRepository output not found in stack $INFRASTRUCTURE_STACK"
    exit 1
fi
if [[ "$ORCHESTRATION_REPO" == "None" || -z "$ORCHESTRATION_REPO" ]]; then
    echo "ERROR: OrchestrationServiceRepository output not found in stack $INFRASTRUCTURE_STACK"
    exit 1
fi

echo "Repository URIs:"
echo "- User: $USER_REPO"
echo "- Order: $ORDER_REPO"
echo "- Token: $TOKEN_REPO"
echo "- Orchestration: $ORCHESTRATION_REPO"

echo
echo "Step 4: Deploying ECS services..."
if ! aws cloudformation deploy \
    --template-file aws-deployment/services.yaml \
    --stack-name $SERVICES_STACK \
    --parameter-overrides InfrastructureStackName=$INFRASTRUCTURE_STACK \
    --region $REGION \
    --capabilities CAPABILITY_IAM; then
    echo "ERROR: Services deployment failed"
    exit 1
fi

echo
echo "Step 5: GitHub Actions setup complete"
echo "Push to GitHub main branch to trigger automated build and deployment"
echo "GitHub Actions will:"
echo "  1. Build JAR files with Gradle"
echo "  2. Build and push Docker images to ECR"
echo "  3. Update ECS services with new images"
echo
echo "Required GitHub Secrets:"
echo "  - AWS_ACCESS_KEY_ID"
echo "  - AWS_SECRET_ACCESS_KEY"

echo
echo "Step 6: Getting ALB URL..."
ALB_ARN=$(aws cloudformation describe-stacks --stack-name $INFRASTRUCTURE_STACK --query "Stacks[0].Outputs[?OutputKey=='ALBArn'].OutputValue" --output text --region $REGION)
ALB_URL=$(aws elbv2 describe-load-balancers --load-balancer-arns $ALB_ARN --query "LoadBalancers[0].DNSName" --output text --region $REGION)

echo
echo "========================================"
echo "Deployment completed successfully!"
echo "========================================"
echo
echo "Application Load Balancer URL: http://$ALB_URL"
echo
echo "Service endpoints:"
echo "- User Service: http://$ALB_URL/user.svc/api/v1/users"
echo "- Order Service: http://$ALB_URL/order.svc/api/v1/orders"
echo "- Token Service: http://$ALB_URL/token.svc/api/v1/auth/login"
echo "- Orchestration Service: http://$ALB_URL/orchest.svc/api/v1/orchestration/user/1/orders"
echo
echo "Note: It may take a few minutes for the services to become healthy."