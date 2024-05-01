# Use a suitable base image with JDK 17
FROM openjdk:17-jdk AS builder

# Set the working directory
WORKDIR /app

# Copy Gradle build files
COPY build.gradle.kts settings.gradle.kts gradle.properties /app/

# Copy the Gradle wrapper files
COPY gradle /app/gradle

# Copy the Gradle wrapper executable
COPY gradlew /app/

# Make the Gradle wrapper executable
RUN chmod +x /app/gradlew

# Copy the source code
COPY src /app/src

# Build the application using Gradle wrapper
RUN ./gradlew build -x test --no-daemon

# Use a lightweight JRE image for the final image
FROM adoptopenjdk:17-jre-hotspot

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /app/build/libs/RevVault-all.jar /app/app.jar

# Expose the port that the application listens on
EXPOSE 8080

# Set the entry point to run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]