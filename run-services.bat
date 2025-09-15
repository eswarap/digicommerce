@echo off
echo Starting all services...

echo Starting User Service on port 8081...
start "User Service" cmd /k "cd /d C:\Users\ES20128974\IdeaProjects\microservices-platform && .\gradlew.bat :user-service:bootRun"

timeout /t 10

echo Starting Order Service on port 8082...
start "Order Service" cmd /k "cd /d C:\Users\ES20128974\IdeaProjects\microservices-platform && .\gradlew.bat :order-service:bootRun"

timeout /t 10

echo Starting Orchestration Service on port 8083...
start "Orchestration Service" cmd /k "cd /d C:\Users\ES20128974\IdeaProjects\microservices-platform && .\gradlew.bat :orchestration-service:bootRun"

echo All services started in separate windows.