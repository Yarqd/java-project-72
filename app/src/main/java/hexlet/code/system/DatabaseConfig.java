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

        // Проверка на случай отсутствия переменных окружения (используем H2 как запасной вариант)
        if (dbUrl == null || dbUsername == null || dbPassword == null) {
            dbUrl = DEFAULT_H2_URL;
            dbUsername = DEFAULT_H2_USERNAME;
            dbPassword = DEFAULT_H2_PASSWORD;
        }

        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);

        return new HikariDataSource(config);
    }
}
