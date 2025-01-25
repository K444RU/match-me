# 1. Build stage
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and download dependencies first (caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the project and build
COPY src ./src
RUN mvn clean package -DskipTests

# 2. Runtime stage
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/target/srv-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8000
EXPOSE 8000

# Spring Boot runs on port 8000 (as configured)
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
