# Build stage
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# 1) Copy only build files first (better caching)
COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .

# 2) Pre-download dependencies (cache layer)
RUN chmod +x ./gradlew && ./gradlew --no-daemon dependencies || true

# 3) Copy source
COPY src src

# 4) Build runnable jar
RUN ./gradlew bootJar -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

# Cloud Run uses PORT env var; Spring should map to it
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
