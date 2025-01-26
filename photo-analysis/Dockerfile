FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/photo-analysis-latest.jar app.jar

CMD ["java", "-jar", "app.jar"]