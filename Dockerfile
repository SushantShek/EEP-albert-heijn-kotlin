# Use the official Maven image as the base image
FROM maven:3.8.4-openjdk-17-slim AS build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and source code to the container
COPY pom.xml .
COPY src ./src

# Build the application using Maven
RUN mvn clean package

# Use the official OpenJDK image as the base image
FROM openjdk:17-slim AS runtime

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the build stage to the runtime stage
COPY --from=build /app/target/backend-assignment-sandbox-0.0.1-SNAPSHOT.jar .

# Expose the port on which your application listens
EXPOSE 8080

# Set up the H2 in-memory database
ENV SPRING_DATASOURCE_URL=jdbc:h2:mem:testdb
ENV SPRING_DATASOURCE_USERNAME=sa
ENV SPRING_DATASOURCE_PASSWORD=

# Run the application
CMD ["java", "-jar", "backend-assignment-sandbox-0.0.1-SNAPSHOT.jar"]