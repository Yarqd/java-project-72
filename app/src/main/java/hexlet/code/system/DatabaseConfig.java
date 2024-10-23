package hexlet.code.system;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

public class DatabaseConfig {

    private static final String H2_URL = "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1";
    private static final String H2_USERNAME = "sa";
    private static final String H2_PASSWORD = "";

    private static final String POSTGRES_URL = "jdbc:postgresql://dpg-csck1dij1k6c739buutg-a.frankfurt-postgres.render.com:5432/dbname_w2y3";
    private static final String POSTGRES_USERNAME = "dbname_w2y3_user";
    private static final String POSTGRES_PASSWORD = "YWXdq9IHOpaAK7SjA0TF7JgmZNnabXbD";

    public static DataSource getDataSource() {
        HikariConfig config = new HikariConfig();

        // Прямое использование PostgreSQL
        String dbUrl = POSTGRES_URL;
        config.setJdbcUrl(dbUrl);
        config.setUsername(POSTGRES_USERNAME);
        config.setPassword(POSTGRES_PASSWORD);

        return new HikariDataSource(config);
    }
}
