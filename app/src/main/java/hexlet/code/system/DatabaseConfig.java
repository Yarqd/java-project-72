package hexlet.code.system;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DatabaseConfig {

    private static final String DEFAULT_H2_URL = "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1";
    private static final String DEFAULT_H2_USERNAME = "sa";
    private static final String DEFAULT_H2_PASSWORD = "";

    private static final String DEFAULT_DB_URL = "jdbc:postgresql://"
            + "dpg-cud1hftumphs73des6eg-a.frankfurt-postgres.render.com/db_rgxt";
    private static final String DEFAULT_DB_USERNAME = "db_rgxt_user";
    private static final String DEFAULT_DB_PASSWORD = "1C5MiauLdmbeq3x5pyau21VOWAHEhenL";

    public static DataSource getDataSource() {
        HikariConfig config = new HikariConfig();

        // Читаем переменные окружения
        String dbUrl = System.getenv("JDBC_DATABASE_URL");
        String dbUsername = System.getenv("DB_USERNAME");
        String dbPassword = System.getenv("DB_PASSWORD");

        if (dbUrl == null || dbUsername == null || dbPassword == null) {
            System.out.println("Переменные окружения не установлены. Используем H2.");
            dbUrl = DEFAULT_H2_URL;
            dbUsername = DEFAULT_H2_USERNAME;
            dbPassword = DEFAULT_H2_PASSWORD;
            config.setDriverClassName("org.h2.Driver");
        } else {
            System.out.println("Подключаемся к PostgreSQL: " + dbUrl);
            config.setDriverClassName("org.postgresql.Driver");
        }

        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);

        // Оптимизация HikariCP
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        return new HikariDataSource(config);
    }
}
