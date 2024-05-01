# Use a suitable base JDK image
FROM azul/zulu-openjdk:17 AS builder

# Set the working directory
WORKDIR /app

# Copy the Gradle files first to leverage Docker cache
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle

# Copy the source code
COPY src ./src

# Build the application
RUN ./gradlew build -x test

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
