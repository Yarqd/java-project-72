package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.javalin.Javalin;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

public class App {
    public static void main(String[] args) {
        String databaseUrl = System.getenv().getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(databaseUrl);

        if (databaseUrl.contains("h2")) {
            config.setDriverClassName("org.h2.Driver");
            config.setUsername("sa");
            config.setPassword("");
        } else {
            config.setDriverClassName("org.postgresql.Driver");
            config.setUsername(System.getenv().getOrDefault("DB_USERNAME", "postgres"));
            config.setPassword(System.getenv().getOrDefault("DB_PASSWORD", "password"));
        }

        HikariDataSource dataSource = new HikariDataSource(config);

        if (databaseUrl.contains("h2")) {
            initializeDatabase(dataSource);
        }

        Javalin app = getApp(dataSource);
        app.start(getPort());
    }

    public static Javalin getApp(DataSource dataSource) {
        Javalin app = Javalin.create(config -> {
            config.requestLogger.http((ctx, executionTimeMs) -> {
                System.out.println(ctx.method() + " " + ctx.path() + " took " + executionTimeMs + " ms");
            });
        });

        UrlRepository urlRepository = new UrlRepository(dataSource);

        app.post("/urls", ctx -> {
            String urlName = ctx.formParam("url");
            Url url = new Url();
            url.setName(urlName);
            url.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            urlRepository.addUrl(url);
            ctx.result("URL добавлен: " + urlName);
        });

        app.get("/urls", ctx -> {
            List<Url> urls = urlRepository.getAllUrls();
            ctx.json(urls);
        });

        app.get("/", ctx -> ctx.result("Hello World"));
        return app;
    }

    private static void initializeDatabase(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS urls ("
                    + "id IDENTITY PRIMARY KEY, "
                    + "name VARCHAR(255) NOT NULL, "
                    + "created_at TIMESTAMP NOT NULL"
                    + ")";
            statement.executeUpdate(sql);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.parseInt(port);
    }
}
