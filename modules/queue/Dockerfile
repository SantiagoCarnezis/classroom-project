FROM amazoncorretto:21
WORKDIR /app
COPY queue/target/queue.jar app.jar
CMD ["java", "-jar", "app.jar"]