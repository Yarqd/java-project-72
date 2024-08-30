package hexlet.code;
import hexlet.code.controllers.UrlController;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.sql.SQLException;
import static io.restassured.RestAssured.given;
import static io.restassured.config.RedirectConfig.redirectConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
class AppTest {
    private static Javalin app;
    @Mock
    private UrlRepository urlRepository;
    @InjectMocks
    private UrlController urlController;
    @BeforeAll
    static void setUp() {
        app = App.getApp().start(0);
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = app.port();
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
    void testCreateUrl() {
        Response response = given()
                .formParam("url", "http://example.com")
                .redirects().follow(false)
                .post("/urls");
        assertEquals(302, response.getStatusCode());
    }
    @Test
    void testShowListUrl() throws SQLException {
        Response response = given()
                .redirects().follow(false)
                .get("/urls");
        assertEquals(302, response.getStatusCode());
    }
    @Test
    void testListUrlAfterAddedSomeUrls() throws SQLException {
        Response response = given()
                .redirects().follow(false)
                .get("/urls");
        assertEquals(302, response.getStatusCode());
    }
    @Test
    void testShowUrl() throws SQLException {
        RestAssuredConfig config = RestAssured.config().redirect(redirectConfig().followRedirects(false));
        Response response = given()
                .config(config)
                .when()
                .get("/urls/1");
        assertEquals(302, response.getStatusCode());
    }
    @Test
    void testInvalidUrl() {
        Response response = given()
                .formParam("url", "invalid-url")
                .post("/urls");
        assertEquals(400, response.getStatusCode());
    }
    @Test
    void testCreateExistingUrl() {
        Response response = given()
                .formParam("url", "http://example.com")
                .post("/urls");
        assertEquals(302, response.getStatusCode());
    }
    @Test
    void testSaveUrlCheck() throws SQLException {
        Response response = given().post("/urls/1/checks");
        assertEquals(302, response.getStatusCode());
    }
    @Test
    void testShowUrlCheck() throws SQLException {
        RestAssuredConfig config = RestAssured.config().redirect(redirectConfig().followRedirects(false));
        Response response = given()
                .config(config)
                .when()
                .get("/urls/1");
        assertEquals(302, response.getStatusCode());
    }
}