# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw
# Pre-fetch dependencies (cached layer, speeds up rebuilds)
RUN ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Render injects $PORT at runtime — Spring Boot must bind to it explicitly
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8080}"]