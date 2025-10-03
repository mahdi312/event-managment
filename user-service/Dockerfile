FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY pom.xml .
COPY user-service/pom.xml user-service/pom.xml
COPY .mvn .mvn
COPY mvnw .
COPY user-service/src user-service/src

RUN --mount=type=cache,target=/root/.m2 ./mvnw dependency:go-offline -f user-service/pom.xml

RUN ./mvnw clean install -DskipTests -f user-service/pom.xml

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/user-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]