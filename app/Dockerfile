FROM openjdk:21-jdk-slim

WORKDIR /app

COPY . /app

RUN ./gradlew build

CMD ["java", "-jar", "build/libs/app-1.0-SNAPSHOT-all.jar"]
