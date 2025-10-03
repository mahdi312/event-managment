FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY pom.xml .
COPY event-service/pom.xml event-service/pom.xml
COPY .mvn .mvn
COPY mvnw .
COPY event-service/src event-service/src

RUN --mount=type=cache,target=/root/.m2 ./mvnw dependency:go-offline -f event-service/pom.xml

RUN ./mvnw clean install -DskipTests -f event-service/pom.xml

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/event-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]