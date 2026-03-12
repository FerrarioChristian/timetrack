# ---------------------------------------------------
# Stage 1: Build stage
# ---------------------------------------------------
FROM eclipse-temurin:23-jdk AS builder
WORKDIR /build

# Copy Maven wrapper and pom.xml first to take advantage of Docker layer caching
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy project source code and compile jar
COPY src ./src
RUN ./mvnw clean package -DskipTests

# ---------------------------------------------------
# Stage 2: Minimal runtime image
# ---------------------------------------------------
FROM eclipse-temurin:23-jre
WORKDIR /app

# Run as non-root user for security best practices
RUN addgroup --system timetrack && adduser --system --ingroup timetrack timetrack
USER timetrack:timetrack

# Copy the built jar from builder stage
COPY --from=builder /build/target/timetrack-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
