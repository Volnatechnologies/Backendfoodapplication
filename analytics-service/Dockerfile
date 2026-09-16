FROM eclipse-temurin:17-jre-alpine

# Create a non-privileged application group and user
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Copy the built JAR file into the container
COPY target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R spring:spring /app

USER spring:spring

EXPOSE 8084

ENV SPRING_PROFILES_ACTIVE=prod \
    PORT=8084

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8084/actuator/health || exit 1

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]