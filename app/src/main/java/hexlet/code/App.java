package hexlet.code;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.DirectoryCodeResolver;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private static final DataSource DATA_SOURCE = DatabaseConfig.getDataSource();

    public static Javalin getApp() {
        Javalin app = Javalin.create(config -> {
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
            config.showJavalinBanner = false;
        });

        app.before(ctx -> {
            LOGGER.info("Received request: {} {}", ctx.method(), ctx.url());
        });

        Routes.configure(app);

        return app;
    }

    public static void main(String[] args) {
        initializeDatabase();
        Javalin app = getApp();
        app.start(getPort());
        LOGGER.info("Application started on port " + getPort());
    }

    static void initializeDatabase() {
        try (Connection conn = DATA_SOURCE.getConnection();
             Statement stmt = conn.createStatement();
             var resourceStream = App.class.getResourceAsStream("/schema.sql")) {

            if (resourceStream == null) {
                throw new IOException("Resource /schema.sql not found");
            }
            String sql = new String(resourceStream.readAllBytes());
            stmt.execute(sql);

        } catch (SQLException | IOException e) {
            LOGGER.error("Error initializing the database", e);
        }
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "8080");
        return Integer.parseInt(port);
    }

    private static TemplateEngine createTemplateEngine() {
        // Получаем абсолютный путь к текущей директории
        String basePath = Paths.get("").toAbsolutePath().toString();
        Path templatesPath;

        // Логируем текущую директорию
        LOGGER.info("Current base path: " + basePath);

        // Проверяем, находимся ли мы в директории app (локальная среда)
        if (basePath.endsWith("app")) {
            templatesPath = Paths.get("src/main/jte"); // Локальная среда
            LOGGER.info("Detected local environment. Using templates path: src/main/jte");
        } else {
            // Если мы не в директории app, используем полный путь с app для CI
            templatesPath = Paths.get("app/src/main/jte"); // Для GitHub Actions
            LOGGER.info("Detected CI environment. Using templates path: app/src/main/jte");
        }

        // Создаем TemplateEngine с найденным путем
        DirectoryCodeResolver codeResolver = new DirectoryCodeResolver(templatesPath);
        TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);

        // Логируем используемый путь для шаблонов
        LOGGER.info("Creating TemplateEngine with base path: " + codeResolver.getRoot());

        return templateEngine;
    }


    public static DataSource getDataSource() {
        return DATA_SOURCE;
    }

}
