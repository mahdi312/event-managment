FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY pom.xml .
COPY eureka-server/pom.xml eureka-server/pom.xml
COPY .mvn .mvn
COPY mvnw .
COPY eureka-server/src eureka-server/src

RUN --mount=type=cache,target=/root/.m2 ./mvnw dependency:go-offline -f eureka-server/pom.xml

RUN ./mvnw clean install -DskipTests -f eureka-server/pom.xml

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/eureka-server/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]