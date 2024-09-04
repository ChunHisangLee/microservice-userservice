# Use a smaller OpenJDK image for better performance and smaller size
FROM openjdk:21-jdk-slim

# Maintainer information
LABEL maintainer="jack"

# Copy the Maven build artifact from the target directory to the container
COPY target/user-service-0.0.1-SNAPSHOT.jar /app/user-service.jar

# Set the working directory inside the container
WORKDIR /app

# Expose the port the application will run on
EXPOSE 8081

# Set environment variables (can be overridden in docker-compose.yml)
ENV SPRING_DATASOURCE_URL="jdbc:postgresql://db:5432/userdb" \
    SPRING_DATASOURCE_USERNAME="postgres" \
    SPRING_DATASOURCE_PASSWORD="Ab123456" \
    APP_JWT_SECRET="Xb34fJd9kPbvmJc84mDkV9b3Xb34fJd9kPbvmJc84mDkV9b3Xb34fJd9kPbvmJc84" \
    APP_JWT_EXPIRATION_MS="3600000" \
    SECURITY_AUTHENTICATION_ENABLED="false" \
    SPRING_RABBITMQ_HOST="rabbitmq" \
    SPRING_RABBITMQ_PORT="5672" \
    SPRING_RABBITMQ_USERNAME="guest" \
    SPRING_RABBITMQ_PASSWORD="guest" \
    SPRING_PROFILES_ACTIVE="docker"

# Use exec form of CMD to ensure signals are received by the JVM process
CMD ["java", "-jar", "/app/user-service.jar"]
