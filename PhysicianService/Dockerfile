FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/psoft25_1221392_1211686_1220806_1211104-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]