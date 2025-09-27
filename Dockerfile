# Build stage
FROM amazoncorretto:17 as builder
WORKDIR /app
ARG SERVICE_PATH
COPY ${SERVICE_PATH}/build/libs/*.jar app.jar

# Runtime stage
FROM amazoncorretto:17
WORKDIR /app
COPY --from=builder /app/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]