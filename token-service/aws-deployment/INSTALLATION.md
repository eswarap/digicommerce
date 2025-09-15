# AWS Tools Installation Guide

## Manual Installation (Recommended)

### 1. Install AWS CLI v2
1. Download: https://awscli.amazonaws.com/AWSCLIV2.msi
2. Run the installer as Administrator
3. Restart command prompt
4. Verify: `aws --version`

### 2. Install SAM CLI
1. Download: https://github.com/aws/aws-sam-cli/releases/latest/download/AWS_SAM_CLI_64_PY3.msi
2. Run the installer as Administrator
3. Restart command prompt
4. Verify: `sam --version`

### 3. Configure AWS Credentials
```bash
aws configure
```
Enter:
- AWS Access Key ID
- AWS Secret Access Key
- Default region (e.g., us-east-1)
- Output format (json)

### 4. Test Configuration
```bash
aws sts get-caller-identity
```

## Alternative: Using Package Managers

### Chocolatey
```bash
choco install awscli
choco install aws-sam-cli
```

### Scoop
```bash
scoop install aws
scoop install aws-sam-cli
```

## Verification Commands
```bash
aws --version
sam --version
aws sts get-caller-identity
```

## Next Steps
After installation, run:
```bash
cd aws-deployment
build-and-deploy.bat
```