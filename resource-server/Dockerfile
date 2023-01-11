FROM maven:3.8.4-openjdk-17-slim AS build

COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -DskipTests

FROM openjdk:17-alpine

ARG JAR_FILE=target/*.jar
COPY --from=build /home/app/target/*.jar app.jar

RUN ls

ENTRYPOINT ["java","-jar","/app.jar"]
