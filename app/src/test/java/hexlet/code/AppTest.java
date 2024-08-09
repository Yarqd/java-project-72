package hexlet.code;

import hexlet.code.controllers.UrlController;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static io.restassured.config.RedirectConfig.redirectConfig;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class AppTest {

    private static Javalin app;

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlController urlController;

    @BeforeAll
    static void setUp() {
        // Запуск Javalin приложения
        app = App.getApp().start(0);
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = app.port();
    }

    @AfterAll
    static void tearDown() {
        // Остановка приложения после тестов
        app.stop();
    }

    @BeforeEach
    void initializeDatabase() {
        // Инициализация моков перед каждым тестом
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMainPage() {
        // Тест главной страницы
        Response response = given().get("/");
        assertEquals(200, response.getStatusCode());
    }

    @Test
    void testCreateUrl() throws Exception {
        Response response = given()
                .formParam("url", "http://example.com")
                .redirects().follow(false) // Добавляем это, чтобы RestAssured не следовал за перенаправлениями
                .post("/urls");

        assertEquals(302, response.getStatusCode()); // Ожидаем статус 302 (перенаправление)
        response.then().header("Location", containsString("/urls"));
    }

    @Test
    void testShowListUrl() throws Exception {
        // Создаем тестовый URL
        Url url = new Url(1L, "http://example.com", new Timestamp(System.currentTimeMillis()));
        List<Url> urls = List.of(url);

        // Настройка mock'а для возвращения нашего списка URL'ов
        when(urlRepository.findAll()).thenReturn(urls);

        // Отправляем GET-запрос и проверяем ответ без следования за перенаправлением
        Response response = given()
                .redirects().follow(false) // Отключаем следование за перенаправлениями
                .get("/urls");

        // Проверка, что сервер вернул статус 302 (перенаправление)
        assertEquals(302, response.getStatusCode());

        // Проверяем наличие заголовка Location с ожидаемым значением
        response.then().header("Location", containsString("/urls"));

        // Вы также можете добавить проверки на наличие URL в ответе
    }

    @Test
    void testListUrlAfterAddedSomeUrls() throws Exception {
        // Добавляем несколько URL'ов для теста
        Url url1 = new Url(1L, "http://example1.com", new Timestamp(System.currentTimeMillis()));
        Url url2 = new Url(2L, "http://example2.com", new Timestamp(System.currentTimeMillis()));
        List<Url> urls = List.of(url1, url2);

        // Настройка mock'а для возвращения списка URL'ов
        when(urlRepository.findAll()).thenReturn(urls);

        // Отправляем GET-запрос и проверяем ответ без следования за перенаправлением
        Response response = given()
                .redirects().follow(false) // Отключаем следование за перенаправлениями
                .get("/urls");

        // Проверка, что сервер вернул статус 302 (перенаправление)
        assertEquals(302, response.getStatusCode());

        // Проверка наличия заголовка Location с ожидаемым значением
        response.then().header("Location", containsString("/urls"));

        // Вы также можете добавить дополнительные проверки на наличие URL в ответе, если это нужно
    }


    @Test
    void testShowUrl() {
        // Отключаем автоматическое следование за перенаправлениями
        RestAssuredConfig config = RestAssured.config().redirect(redirectConfig().followRedirects(false));

        // Выполнение запроса с отключенными перенаправлениями
        given()
                .config(config)
                .when()
                .get("/urls/1")
                .then()
                .statusCode(302); // Проверяем, что происходит перенаправление
    }


    @Test
    void testShowUrlNotFound() throws Exception {
        // Мокаем поведение репозитория, чтобы он вернул пустой Optional<Url>
        when(urlRepository.findById(anyLong())).thenAnswer(invocation -> Optional.empty());

        // Выполняем запрос к несуществующему URL и проверяем, что происходит перенаправление
        given()
                .redirects().follow(false)  // Запрещаем следование за перенаправлением
                .when()
                .get("/urls/999")
                .then()
                .statusCode(302)  // Ожидаем код 302 (перенаправление)
                .header("Location", "/urls");  // Проверяем, что произошло перенаправление на страницу со списком URL
    }

    @Test
    void testInvalidUrl() {
        // Тест для проверки создания некорректного URL
        Response response = given()
                .formParam("url", "invalid-url")
                .post("/urls");
        assertEquals(400, response.getStatusCode());
    }

    @Test
    void testCreateExistingUrl() throws SQLException {
        // Мокирование метода existsByName для проверки существующего URL
        when(urlRepository.existsByName("http://example.com")).thenReturn(true);

        // Проверка попытки создания существующего URL
        Response response = given().formParam("url", "http://example.com").post("/urls");
        assertEquals(302, response.getStatusCode());
    }

    @Test
    void testSaveUrlCheck() throws SQLException {
        // Мокирование findById для получения URL перед проверкой
        Url url = new Url();
        url.setId(1L);
        url.setName("http://example.com");
        url.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        when(urlRepository.findById(1L)).thenReturn(url);

        // Тест на проверку сохранения URL
        Response response = given().post("/urls/1/checks");
        assertEquals(302, response.getStatusCode());
    }

    @Test
    void testShowUrlCheck() {
        // Отключаем автоматическое следование за перенаправлениями
        RestAssuredConfig config = RestAssured.config().redirect(redirectConfig().followRedirects(false));

        // Выполнение запроса с отключенными перенаправлениями
        given()
                .config(config)
                .when()
                .get("/urls/1")
                .then()
                .statusCode(302); // Ожидаем код ответа 302 (перенаправление)
    }

}
