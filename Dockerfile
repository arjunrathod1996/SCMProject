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

# Use Java 17 base image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the jar (adjust name if needed)
COPY target/scmproject-0.0.1-SNAPSHOT.jar app.jar

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]






