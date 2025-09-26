# Stage 1: Build the application
FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy only the files needed for building to leverage Docker cache
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:17-jre-jammy

# Set working directory
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Copy the application properties
COPY src/main/resources/application-prod.properties /app/config/

# Environment variables (can be overridden at runtime)
ENV SPRING_PROFILES_ACTIVE=prod \
    SPRING_CONFIG_LOCATION=file:/app/config/application-prod.properties \
    TZ=UTC

# Expose the port the app runs on
EXPOSE 8080

# Set the entrypoint
ENTRYPOINT ["java", "-jar", "app.jar"]
