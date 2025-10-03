FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY pom.xml .
COPY notification-service/pom.xml notification-service/pom.xml
COPY .mvn .mvn
COPY mvnw .
COPY notification-service/src notification-service/src

RUN --mount=type=cache,target=/root/.m2 ./mvnw dependency:go-offline -f notification-service/pom.xml

RUN ./mvnw clean install -DskipTests -f notification-service/pom.xml

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/notification-service/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]