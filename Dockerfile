# ==========================================
# STAGE 1: Build
# ==========================================
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy only pom.xml first to leverage Docker layer caching for dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Now copy the source and build
COPY src ./src
RUN mvn clean package -DskipTests -B


# ==========================================
# STAGE 2: Run
# ==========================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy only the built jar from the previous stage (keeps final image small)
COPY --from=build /app/target/*.jar app.jar

# Default values — override these at `docker run` / docker-compose / K8s time.
# Never bake a real frontend domain in here; inject it per environment.
ENV APP_CORS_ALLOWED_ORIGINS=""
ENV JWT_SECRET=""
ENV SPRING_PROFILES_ACTIVE="prod"

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]