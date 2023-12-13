FROM azul/zulu-openjdk:17-latest
WORKDIR /src
VOLUME /tmp
COPY build/libs/RevVault-all.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]