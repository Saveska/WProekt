# Using an official OpenJDK 16 runtime as a parent image
FROM adoptopenjdk:16-jre-hotspot

# Setting the working directory to /app
WORKDIR /app

# Copying the application JAR file into the container
COPY target/mindmappr.jar /app/mindmappr.jar

# Exposing the port that the Spring Boot app runs on
EXPOSE 8080

# Installing PostgreSQL client (for database migration or other purposes)
RUN apt-get update && apt-get install -y postgresql-client

# Defining the command to run the application
CMD ["java", "-jar", "mindmappr.jar"]
