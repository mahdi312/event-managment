FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY pom.xml .
COPY ticketing-service/pom.xml ticketing-service/pom.xml
COPY .mvn .mvn
COPY mvnw .
COPY ticketing-service/src ticketing-service/src

RUN --mount=type=cache,target=/root/.m2 ./mvnw dependency:go-offline -f ticketing-service/pom.xml

RUN ./mvnw clean install -DskipTests -f ticketing-service/pom.xml

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/ticketing-service/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]