@echo off
echo Installing AWS CLI and SAM CLI...

echo.
echo 1. Installing AWS CLI v2...
curl "https://awscli.amazonaws.com/AWSCLIV2.msi" -o "AWSCLIV2.msi"
msiexec /i AWSCLIV2.msi /quiet
del AWSCLIV2.msi

echo.
echo 2. Installing SAM CLI...
curl -L "https://github.com/aws/aws-sam-cli/releases/latest/download/AWS_SAM_CLI_64_PY3.msi" -o "AWS_SAM_CLI.msi"
msiexec /i AWS_SAM_CLI.msi /quiet
del AWS_SAM_CLI.msi

echo.
echo Installation complete. Please restart your command prompt.
echo Run 'aws --version' and 'sam --version' to verify installation.
echo.
echo Next steps:
echo 1. Configure AWS credentials: aws configure
echo 2. Test SAM: sam --version