FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src/main ./src/main
COPY src/test ./src/test
RUN mvn -B package

FROM eclipse-temurin:21-jre
WORKDIR /work
COPY --from=build /workspace/target/quarkus-app/ /work/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/work/quarkus-run.jar"]