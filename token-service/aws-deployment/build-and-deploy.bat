@echo off
echo Building Lambda functions...

cd token-lambda
call gradle shadowJar
if %errorlevel% neq 0 (
    echo Token Lambda build failed
    exit /b 1
)

cd ..\notification-lambda
call gradle shadowJar
if %errorlevel% neq 0 (
    echo Notification Lambda build failed
    exit /b 1
)

cd ..
echo Building and deploying with SAM...
sam build
if %errorlevel% neq 0 (
    echo SAM build failed
    exit /b 1
)

sam deploy --guided