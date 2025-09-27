# Fix AWS Permissions for Secrets Manager

## Temporary Workaround: Use Local Secret

Since you don't have IAM permissions, temporarily disable AWS Secrets Manager:

```yaml
# In token-service/src/main/resources/application.yml
aws:
  region: us-east-1
  enabled: false  # Disable AWS integration

jwt:
  secret: mySecretKeyForJWTTokenGeneration123456789012
```

## Option 1: AWS Console (Recommended)

Since your user lacks IAM permissions, use AWS Console:

1. **Login to AWS Console** as root user or IAM admin
2. **Go to IAM > Users > admin**
3. **Click "Add permissions" > "Attach policies directly"**
4. **Search for "SecretsManagerReadWrite"** and attach it
5. **Or create custom policy with this JSON:**
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "secretsmanager:GetSecretValue",
                "secretsmanager:DescribeSecret"
            ],
            "Resource": "arn:aws:secretsmanager:us-east-1:137974098890:secret:jwt-secret*"
        }
    ]
}
```

## Option 2: CLI (Requires Admin Access)

## Create IAM Policy

```bash
# Create policy document
cat > secretsmanager-policy.json << EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "secretsmanager:GetSecretValue",
                "secretsmanager:DescribeSecret"
            ],
            "Resource": "arn:aws:secretsmanager:us-east-1:137974098890:secret:jwt-secret*"
        }
    ]
}
EOF

# Create the policy
aws iam create-policy \
    --policy-name SecretsManagerJWTAccess \
    --policy-document file://secretsmanager-policy.json

# Attach policy to admin user
aws iam attach-user-policy \
    --user-name admin \
    --policy-arn arn:aws:iam::137974098890:policy/SecretsManagerJWTAccess
```

## Option 3: Root User CLI

```bash
# Login as root user or user with IAM admin permissions
# Then add inline policy
aws iam put-user-policy \
    --user-name admin \
    --policy-name SecretsManagerAccess \
    --policy-document file://policy.json

# Where policy.json contains:
cat > policy.json << EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "secretsmanager:GetSecretValue",
                "secretsmanager:DescribeSecret"
            ],
            "Resource": "arn:aws:secretsmanager:us-east-1:137974098890:secret:jwt-secret*"
        }
    ]
}
EOF
```

## Option 4: Contact AWS Administrator

Ask your AWS administrator to:
1. **Attach SecretsManagerReadWrite policy** to your user
2. **Or grant these specific permissions:**
   - `secretsmanager:GetSecretValue`
   - `secretsmanager:DescribeSecret`

## Verify Permissions

```bash
# Test the permission
aws secretsmanager get-secret-value \
    --secret-id jwt-secret \
    --region us-east-1

# List user policies
aws iam list-attached-user-policies --user-name admin
aws iam list-user-policies --user-name admin
```