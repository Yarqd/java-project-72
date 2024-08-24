package hexlet.code;

import hexlet.code.controllers.UrlController;
import hexlet.code.model.Url;
//import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlRepository;
import hexlet.code.repository.UrlCheckRepository;
import io.javalin.Javalin;
//import io.javalin.testtools.JavalinTest;
import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
//import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.config.RedirectConfig.redirectConfig;
//import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
//import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppTest {
    private static Javalin app;
    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCheckRepository urlCheckRepository;

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
    @BeforeEach
    void initializeDatabase() throws SQLException {
        MockitoAnnotations.openMocks(this);
        // Ленивая мокировка с пробрасыванием SQLException
        Url url = new Url(1L, "http://example.com", new Timestamp(System.currentTimeMillis()));
        lenient().when(urlRepository.findById(anyLong())).thenReturn(url);
        lenient().when(urlRepository.findAll()).thenReturn(List.of(url));
    }
    @Test
    void testMainPage() {
        Response response = given().get("/");
        assertEquals(200, response.getStatusCode());
    }
    @Test
    void testCreateUrl() {
        // Выполняем POST запрос
        Response response = given()
                .formParam("url", "http://example.com")
                .redirects().follow(false)
                .post("/urls");
        // Проверка что редирект произошёл
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
        // Здесь мы не настраиваем моки, просто выполняем запрос и проверяем статус
        Response response = given()
                .formParam("url", "http://example.com")
                .post("/urls");
        // Проверяем статус ответа
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

//    @Test
//    void testCreateUrl2() throws SQLException {
//        // Подготавливаем фейковый URL
//        MockWebServer mockServer = new MockWebServer();
//        String url = mockServer.url("/").toString().replaceAll("/$", "");
//
//        // Мокируем поведение репозитория для метода existsByName
//        when(urlRepository.existsByName(url)).thenReturn(false);
//
//        // Отправляем POST запрос с фейковым URL
//        Response response = given()
//                .formParam("url", url)
//                .post("/urls");
//
//        // Проверяем статус ответа
//        assertEquals(302, response.getStatusCode());
//
//        // Проверяем, что URL был сохранен
//        Url actualUrl = urlRepository.findById(1L);
//        assertThat(actualUrl).isNotNull();
//        assertThat(actualUrl.getName()).isEqualTo(url);
//    }

}
