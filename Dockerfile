FROM openjdk:21-jdk-slim

WORKDIR /app

COPY app/gradlew /app/gradlew
COPY app/gradle /app/gradle
RUN chmod +x ./gradlew

COPY app /app

RUN ./gradlew shadowJar

CMD ["java", "-jar", "build/libs/app-1.0-SNAPSHOT-all.jar"]
