# Use a smaller base image
FROM azul/zulu-openjdk:21-jre as builder
WORKDIR /src
COPY . .
RUN ./gradlew build && \
    rm -rf /src/.gradle /src/build/tmp /src/build/cache

# Final image
FROM azul/zulu-openjdk:21-jre
WORKDIR /app
COPY --from=builder /src/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]