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
        DirectoryCodeResolver codeResolver = new DirectoryCodeResolver(Paths.get("src/main/jte"));
        TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
        LOGGER.info("Creating TemplateEngine with base path: " + codeResolver.getRoot());
        return templateEngine;
    }

    public static DataSource getDataSource() {
        return DATA_SOURCE;
    }

}
