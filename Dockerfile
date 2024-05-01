FROM azul/zulu-openjdk:21-latest as builder
WORKDIR /src
COPY . .
RUN ./gradlew build

FROM azul/zulu-openjdk:21-latest
WORKDIR /app
COPY --from=builder /src/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]