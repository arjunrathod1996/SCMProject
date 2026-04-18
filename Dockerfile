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

# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# 1. Cache Maven dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# 2. Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
# 3. Use JRE instead of JDK for the runtime environment
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 4. Create a non-root user for better security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the built JAR from the previous stage
COPY --from=build /app/target/*.jar app.jar

# 5. Expose the port (Informational, adjust if not 8080)
EXPOSE 8080

# Use default profile (k8s will override with environment variable)
ENV SPRING_PROFILES_ACTIVE=default

ENTRYPOINT ["java", "-jar", "app.jar"]







