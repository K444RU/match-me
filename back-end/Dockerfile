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

# Copy the jar file from the previous stage
COPY --from=build /app/target/*.jar /app/app.jar

# Expose port 8000
EXPOSE 8000

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
