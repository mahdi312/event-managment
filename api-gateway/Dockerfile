FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY pom.xml .
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY .mvn .mvn
COPY mvnw .
COPY api-gateway/src api-gateway/src

RUN --mount=type=cache,target=/root/.m2 ./mvnw dependency:go-offline -f api-gateway/pom.xml

RUN ./mvnw clean install -DskipTests -f api-gateway/pom.xml

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/api-gateway/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]