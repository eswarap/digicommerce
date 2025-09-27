# Build stage
FROM public.ecr.aws/docker/library/openjdk:17-jdk-slim as builder
WORKDIR /app
ARG SERVICE_PATH
COPY ${SERVICE_PATH}/build/libs/*.jar app.jar

# Runtime stage
FROM public.ecr.aws/docker/library/openjdk:17-jre-slim
WORKDIR /app
COPY --from=builder /app/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]