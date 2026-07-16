# ==========================================
# Stage 1: Build the Application
# ==========================================
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

# Copy the gradle wrapper and build files first to cache dependencies
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Copy the actual source code
COPY src ./src

# Give execution rights and build the JAR (skipping tests for speed)
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

# ==========================================
# Stage 2: Create the Production Image
# ==========================================
FROM eclipse-temurin:17-jre
WORKDIR /app

# Extract only the compiled JAR from the builder stage
COPY --from=builder /app/build/libs/*SNAPSHOT.jar app.jar

# Expose the port Spring Boot runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]