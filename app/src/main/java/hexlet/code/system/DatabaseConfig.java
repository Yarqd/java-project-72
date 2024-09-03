package hexlet.code.system;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

public class DatabaseConfig {

    private static final String H2_URL = "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1";
    private static final String H2_USERNAME = "sa";
    private static final String H2_PASSWORD = "";

    public static DataSource getDataSource() {
        HikariConfig config = new HikariConfig();
        String dbUrl = System.getenv("JDBC_DATABASE_URL");
        if (dbUrl == null || dbUrl.isEmpty()) {
            dbUrl = H2_URL;
            config.setUsername(H2_USERNAME);
            config.setPassword(H2_PASSWORD);
        } else {
            System.out.println("Using JDBC_DATABASE_URL: " + dbUrl);
            config.setUsername(System.getenv("DB_USER"));
            config.setPassword(System.getenv("DB_PASSWORD"));
        }

        config.setJdbcUrl(dbUrl);
        return new HikariDataSource(config);
    }
}
