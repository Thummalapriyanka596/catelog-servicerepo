# Use Java 17 slim image
FROM openjdk:17-jdk-slim

# Create /tmp directory (used by Spring Boot)
VOLUME /tmp

# Build argument: location of the built JAR
ARG JAR_FILE=target/catalog-service-0.0.1-SNAPSHOT.jar

# Copy JAR to container and rename
COPY ${JAR_FILE} app.jar

# Run the app when the container starts
ENTRYPOINT ["java", "-jar", "/app.jar"]
