## Use an appropriate base image with Java installed
#FROM adoptopenjdk/openjdk11:alpine-jre
#
## Set the working directory inside the container
#WORKDIR /app
#
## Copy the packaged JAR file into the container at the specified working directory
#COPY target/spring-boot-docker.jar /app/spring-boot-docker.jar
#
## Expose the port your Spring Boot application listens on (default is 8080)
#EXPOSE 8080
#
## Specify the command to run your Spring Boot application using CMD when the container starts
#CMD ["java", "-jar", "/app/spring-boot-docker.jar"]

## Use Java 17 base image
#FROM eclipse-temurin:17-jdk-alpine
#
## Set work directory
#WORKDIR /app
#
## Copy the built jar from target directory
#COPY target/spring-boot-docker.jar app.jar
#
## Run the Spring Boot application
#ENTRYPOINT ["java", "-jar", "app.jar"]



# Stage 1: Build the JAR using Maven
#FROM maven:3.9.6-eclipse-temurin-17 AS build
#WORKDIR /app
#COPY pom.xml .
#COPY src ./src
#RUN mvn clean package -DskipTests
#
## Stage 2: Run the JAR
#FROM eclipse-temurin:17-jdk-alpine
#WORKDIR /app
#COPY --from=build /app/target/*.jar app.jar
#ENTRYPOINT ["java", "-jar", "app.jar"]

FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the JAR
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# ✅ Set Spring profile to prod so Railway DB is used
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]







