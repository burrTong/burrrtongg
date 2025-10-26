# Stage 1: Build the application
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN mkdir -p /app/uploads # Create uploads directory
RUN chmod -R 755 /app/uploads # Ensure permissions for uploaded files
RUN gradle clean build -x test

# Stage 2: Create the runtime image
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
COPY --from=build /app/src/main/resources/ ./src/main/resources/
ENTRYPOINT ["java","-jar","app.jar"]