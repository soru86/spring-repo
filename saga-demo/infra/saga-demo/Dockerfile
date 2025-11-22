ARG SERVICE=order-service

FROM maven:3.9.6-eclipse-temurin-21 AS build
ARG SERVICE
WORKDIR /workspace
COPY ../.. /workspace
RUN mvn -pl ${SERVICE} -am package -DskipTests

FROM eclipse-temurin:21-jre
ARG SERVICE=order-service
ENV SERVICE_NAME=${SERVICE}
WORKDIR /opt/app
COPY --from=build /workspace/${SERVICE}/target/${SERVICE}-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/opt/app/app.jar"]

