
FROM eclipse-temurin:17-jdk

WORKDIR /app
COPY target/*.jar app.jar
RUN chown -R 1001:0 /app
ENTRYPOINT ["java","-jar","app.jar"]
