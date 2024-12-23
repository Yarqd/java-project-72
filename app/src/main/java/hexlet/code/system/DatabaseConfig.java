package hexlet.code.system;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DatabaseConfig {

    private static final String DEFAULT_H2_URL = "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1";
    private static final String DEFAULT_H2_USERNAME = "sa";
    private static final String DEFAULT_H2_PASSWORD = "";

    public static DataSource getDataSource() {
        HikariConfig config = new HikariConfig();

        // Получение значений из переменных окружения
        String dbUrl = System.getenv("JDBC_DATABASE_URL");
        String dbUsername = System.getenv("DB_USERNAME");
        String dbPassword = System.getenv("DB_PASSWORD");

        if (dbUrl == null || dbUsername == null || dbPassword == null) {
            // Используем H2 как запасной вариант
            System.out.println("Environment variables for the database are not set. Using H2 in-memory database.");
            dbUrl = DEFAULT_H2_URL;
            dbUsername = DEFAULT_H2_USERNAME;
            dbPassword = DEFAULT_H2_PASSWORD;
        } else {
            System.out.println("Connecting to PostgreSQL database with URL: " + dbUrl);
        }

        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);

        // Настройка пула соединений HikariCP для оптимальной работы
        config.setDriverClassName("org.postgresql.Driver");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        return new HikariDataSource(config);
    }
}
