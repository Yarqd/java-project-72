package hexlet.code;

import hexlet.code.dto.UrlDto;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class AppTest {
    private static Javalin app;
    private static DataSource dataSource;
    private static UrlRepository urlRepository;
    private static UrlCheckRepository urlCheckRepository;

    @BeforeAll
    static void setUp() throws SQLException {
        app = App.getApp().start(0);
        dataSource = App.DATA_SOURCE; // Используем базу данных H2
        urlRepository = new UrlRepository(dataSource);
        urlCheckRepository = new UrlCheckRepository(dataSource);

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = app.port();

        initializeDatabase();
    }

    @BeforeEach
    void resetDatabase() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            String databaseProductName = connection.getMetaData().getDatabaseProductName();

            if ("H2".equals(databaseProductName)) {
                statement.execute("SET REFERENTIAL_INTEGRITY FALSE");
            }

            statement.execute("DROP TABLE IF EXISTS url_checks");
            statement.execute("DROP TABLE IF EXISTS urls");

            if ("H2".equals(databaseProductName)) {
                statement.execute("SET REFERENTIAL_INTEGRITY TRUE");
            }

            initializeDatabase();
        }
    }

    @AfterAll
    static void tearDown() {
        app.stop();
    }

    @Test
    void testMainPage() {
        Response response = given().get("/");
        assertEquals(200, response.getStatusCode());
    }

    @Test
    void testCreateUrl() throws SQLException {
        Response response = given()
                .formParam("url", "http://example.com")
                .post("/urls");
        assertEquals(302, response.getStatusCode());

        Url url = urlRepository.findById(1L);
        assertNotNull(url);
        assertEquals("http://example.com", url.getName());
    }

    @Test
    void testShowListUrl() {
        Response response = given().get("/urls");
        assertEquals(200, response.getStatusCode());
    }

    @Test
    void testListUrlAfterAddedSomeUrls() throws SQLException {
        urlRepository.save(new Url(null, "http://example1.com", null));
        urlRepository.save(new Url(null, "http://example2.com", null));

        Response response = given().get("/urls");
        assertEquals(200, response.getStatusCode());
        String responseBody = response.getBody().asString();

        assertTrue(responseBody.contains("http://example1.com"));
        assertTrue(responseBody.contains("http://example2.com"));
    }

    @Test
    void testShowUrl() throws SQLException {
        Url newUrl = new Url(null, "http://example.com", null);
        urlRepository.save(newUrl);

        Response response = given().get("/urls/" + newUrl.getId());
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().asString().contains("http://example.com"));
    }

    @Test
    void testShowUrlNotFound() {
        Response response = given().get("/urls/999");
        assertEquals(404, response.getStatusCode());
    }

    @Test
    void testInvalidUrl() {
        Response response = given()
                .formParam("url", "invalid-url")
                .post("/urls");
        assertEquals(400, response.getStatusCode());
    }

    @Test
    void testCreateExistingUrl() throws SQLException {
        Url existingUrl = new Url(null, "http://example.com", null);
        urlRepository.save(existingUrl);

        Response response = given()
                .formParam("url", "http://example.com")
                .post("/urls");
        assertEquals(302, response.getStatusCode());

        List<UrlDto> urls = urlRepository.findAllWithLatestChecks();
        assertEquals(1, urls.size());
    }

    @Test
    void testSaveUrlCheck() throws SQLException {
        Url newUrl = new Url(null, "http://example.com", null);
        urlRepository.save(newUrl);

        Response response = given().post("/urls/" + newUrl.getId() + "/checks");
        assertEquals(302, response.getStatusCode());

        List<UrlCheck> checks = urlCheckRepository.findByUrlId(newUrl.getId());
        assertFalse(checks.isEmpty());
    }

    @Test
    void testShowUrlCheck() throws SQLException {
        Url newUrl = new Url(null, "http://example.com", null);
        urlRepository.save(newUrl);
        Response checkResponse = given().post("/urls/" + newUrl.getId() + "/checks");
        assertEquals(302, checkResponse.getStatusCode());

        Response response = given().get("/urls/" + newUrl.getId() + "/checks");
        assertEquals(404, response.getStatusCode());
    }

    private static void initializeDatabase() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            // Чтение SQL-скрипта из файла schema.sql
            String schemaSql = new String(Files.readAllBytes(Paths.get("src/test/resources/fixtures/schema.sql")),
                    StandardCharsets.UTF_8);

            // Выполнение скрипта для инициализации базы данных
            stmt.execute(schemaSql);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize the database schema", e);
        }
    }
}
