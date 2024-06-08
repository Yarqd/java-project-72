package hexlet.code;

import io.javalin.Javalin;

public class App {

    // Метод, который возвращает настроенное приложение Javalin
    public static Javalin getApp() {
        return Javalin.create(config -> {
            // Включаем логирование запросов для разработки
            config.requestLogger.http((ctx, executionTimeMs) -> {
                System.out.println(ctx.method() + " " + ctx.path() + " took " + executionTimeMs + " ms");
            });
        }).get("/", ctx -> ctx.result("Hello World"));
    }

    // Статический метод main() для запуска приложения
    public static void main(String[] args) {
        Javalin app = getApp(); // Получаем настроенное приложение
        app.start(getPort()); // Запускаем приложение на порту
    }

    // Метод для получения порта из переменной окружения или использование порта 7070 по умолчанию
    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.parseInt(port);
    }
}
