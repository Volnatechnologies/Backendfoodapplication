FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8088

ENTRYPOINT ["java", "-Duser.timezone=UTC", "-jar", "app.jar"]