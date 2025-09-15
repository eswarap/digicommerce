@echo off
echo Setting up AWS credentials...

echo.
echo You'll need:
echo - AWS Access Key ID
echo - AWS Secret Access Key
echo - Default region (e.g., us-east-1)
echo - Output format (json recommended)

echo.
aws configure

echo.
echo Testing AWS connection...
aws sts get-caller-identity

echo.
echo If successful, you should see your AWS account details above.