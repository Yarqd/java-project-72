package hexlet.code;

import io.javalin.Javalin;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import io.restassured.parsing.Parser;

class AppTest {

    private static Javalin app;

    @BeforeAll
    static void setUp() {
        RestAssured.registerParser("text/plain", Parser.JSON);
        app = App.getApp().start(0);
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = app.port();
    }

    @AfterAll
    static void tearDown() {
        app.stop();
    }

    @BeforeEach
    void initializeDatabase() throws SQLException {
        try (Connection connection = App.getDataSource().getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DROP ALL OBJECTS");
            statement.execute("RUNSCRIPT FROM 'classpath:schema.sql'");
        }
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
                .post("/urls");
        assertEquals(302, response.getStatusCode());
        response.then().header("Location", containsString("/urls"));
    }

    @Test
    void testShowListUrl() {
        // Сначала добавляем URL
        given().formParam("url", "http://example.com").post("/urls");

        // Затем проверяем, что список URL возвращается корректно
        Response response = given().get("/urls");
        assertEquals(200, response.getStatusCode());
        response.then().body(containsString("http://example.com"));
    }

    @Test
    void testListUrlAfterAddedSomeUrls() {
        given().formParam("url", "http://example1.com").post("/urls");
        given().formParam("url", "http://example2.com").post("/urls");

        Response response = given().get("/urls");
        assertEquals(200, response.getStatusCode());
        response.then().body(containsString("http://example1.com"))
                .body(containsString("http://example2.com"));
    }

    @Test
    void testShowUrl() {
        given().formParam("url", "http://example.com").post("/urls");
        Response response = given().get("/urls/1");
        assertEquals(200, response.getStatusCode());
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
    void testCreateExistingUrl() {
        given().formParam("url", "http://example.com").post("/urls");
        Response response = given().formParam("url", "http://example.com").post("/urls");
        assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 302 || response.
                getStatusCode() == 303);
    }

    @Test
    void testSaveUrlCheck() {
        given().formParam("url", "http://example.com").post("/urls");
        Response response = given().post("/urls/1/checks");
        assertEquals(302, response.getStatusCode());
    }

    @Test
    void testShowUrlCheck() {
        given().formParam("url", "http://example.com").post("/urls");
        given().post("/urls/1/checks");

        Response response = given().get("/urls/1");
        assertEquals(200, response.getStatusCode());
        response.then().body(containsString("200")); // Проверяем, что код ответа есть в ответе
    }
}
