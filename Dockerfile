# Use a suitable base JDK image for the builder stage
FROM gradle:7.4.0-jdk11 AS builder

# Set the working directory
WORKDIR /app

# Copy the Gradle build files
COPY build.gradle.kts settings.gradle.kts /app/

# Copy the gradle wrapper files
COPY gradle /app/gradle

# Copy the source code
COPY src /app/src

# Build the application
RUN gradle build -x test

# Use a lightweight JRE image for the final image
FROM azul/zulu-openjdk:17-jre

# Set the working directory
WORKDIR /app

# Copy the JAR file built in the previous stage
COPY --from=builder /app/build/libs/RevVault-all.jar /app/app.jar

# Expose the port that the Ktor application listens on
EXPOSE 8080

# Set the entry point to run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
