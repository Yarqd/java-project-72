# Используем официальный образ Java 21
FROM openjdk:21-jdk-slim

# Устанавливаем рабочую директорию в контейнере
WORKDIR /app

# Копируем все файлы проекта в контейнер
COPY ./app /app

# Выполняем сборку проекта
RUN ./gradlew shadowJar

# Указываем команду для запуска приложения
CMD ["java", "-jar", "build/libs/app-1.0-SNAPSHOT-all.jar"]
