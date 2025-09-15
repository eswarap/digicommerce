# Manual Installation Required

The automated installation requires administrator privileges. Please install manually:

## Step 1: Install AWS CLI
1. Download from: https://awscli.amazonaws.com/AWSCLIV2.msi
2. Right-click the downloaded file → "Run as administrator"
3. Follow the installation wizard

## Step 2: Install SAM CLI
1. Download from: https://github.com/aws/aws-sam-cli/releases/latest/download/AWS_SAM_CLI_64_PY3.msi
2. Right-click the downloaded file → "Run as administrator"
3. Follow the installation wizard

## Step 3: Restart Command Prompt
Close and reopen your command prompt to refresh PATH

## Step 4: Verify Installation
```cmd
aws --version
sam --version
```

## Step 5: Configure AWS
```cmd
aws configure
```

## Step 6: Deploy
```cmd
cd aws-deployment
build-and-deploy.bat
```

The AWS CLI installer (AWSCLIV2.msi) has been downloaded to the aws-deployment folder.