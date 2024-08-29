//package hexlet.code;
//
//import io.javalin.Javalin;
//import okhttp3.FormBody;
//import okhttp3.mockwebserver.MockResponse;
//import okhttp3.mockwebserver.MockWebServer;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.io.IOException;
//import java.util.Map;
//
//import static io.javalin.testtools.JavalinTest.test;
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//public class AppTest {
//
//    private static MockWebServer mockServer;
//    private Javalin app;
//
//    @BeforeAll
//    public static void beforeAll() throws IOException {
//        mockServer = new MockWebServer();
//        mockServer.enqueue(new MockResponse().setBody("<html>Mock Response</html>"));
//        mockServer.start();
//    }
//
//    @BeforeEach
//    public void beforeEach() {
//        app = App.getApp();
//    }
//
//    @AfterAll
//    public static void afterAll() throws IOException {
//        mockServer.shutdown();
//    }
//
//    @Test
//    public void testMainPage() {
//        test(app, (server, client) -> {
//            var response = client.get("/");
//            assert response.code() == 200;
//            String responseBody = response.body().string();
//            System.out.println(responseBody);  // Выводим его в консоль для отладки
//            assert responseBody.contains("Анализатор страниц");  // Проверяем соответствующее содержание
//        });
//    }
//
//    @Test
//    public void testCreateUrl() {
//        test(app, (server, client) -> {
//            var response = client.post("/urls")
//                    .body(Map.of("url", "http://example.com"))
//                    .asFormUrlEncoded()
//                    .execute();
//
//            assertEquals(200, response.code());
//            assertTrue(response.body().string().contains("Страница успешно добавлена"));
//        });
//    }
//
//    @Test
//    public void testShowListUrl() {
//        test(app, (server, client) -> {
//            client.post("/urls", Map.of("url", "http://example.com"));
//            var response = client.get("/urls");
//            assert response.code() == 200;
//            assert response.body().string().contains("http://example.com");
//        });
//    }
//
//    @Test
//    public void testListUrlAfterAddedSomeUrls() {
//        test(app, (server, client) -> {
//            client.post("/urls", Map.of("url", "http://example1.com"));
//            client.post("/urls", Map.of("url", "http://example2.com"));
//
//            var response = client.get("/urls");
//            assert response.code() == 200;
//            assert response.body().string().contains("http://example1.com");
//            assert response.body().string().contains("http://example2.com");
//        });
//    }
//
//    @Test
//    public void testShowUrl() {
//        test(app, (server, client) -> {
//            client.post("/urls", Map.of("url", "http://example.com"));
//            var response = client.get("/urls/1");
//            assert response.code() == 200;
//            assert response.body().string().contains("http://example.com");
//        });
//    }
//
//    @Test
//    public void testShowUrlNotFound() {
//        test(app, (server, client) -> {
//            var response = client.get("/urls/9999"); // используйте ID, который точно не существует
//            assert response.code() == 404;
//        });
//    }
//
//    @Test
//    public void testInvalidUrl() {
//        test(app, (server, client) -> {
//            var response = client.post("/urls", Map.of("url", "invalid-url"));
//            assert response.code() == 400;
//            assert response.body().string().contains("Некорректный URL");
//        });
//    }
//
//    @Test
//    public void testCreateExistingUrl() {
//        test(app, (server, client) -> {
//            client.post("/urls", Map.of("url", "http://example.com"));
//            var response = client.post("/urls", Map.of("url", "http://example.com"));
//            assert response.code() == 200 || response.code() == 302 || response.code() == 303;
//            assert response.body().string().contains("Страница уже существует");
//        });
//    }
//
//    @Test
//    public void testSaveUrlCheck() {
//        test(app, (server, client) -> {
//            client.post("/urls", Map.of("url", "http://example.com"));
//            var response = client.post("/urls/1/checks");
//            assert response.code() == 200;
//            assert response.body().string().contains("Страница успешно проверена");
//        });
//    }
//
//    @Test
//    public void testShowUrlCheck() {
//        test(app, (server, client) -> {
//            client.post("/urls", Map.of("url", "http://example.com"));
//            client.post("/urls/1/checks");
//
//            var response = client.get("/urls/1");
//            assert response.code() == 200;
//            assert response.body().string().contains("200");
//            assert response.body().string().contains("example.com");
//        });
//    }
//
//    @Test
//    public void testUrlCheck() {
//        test(app, (server, client) -> {
//            var response = client.post("/urls", Map.of("url", "http://example.com"));
//            assert response.code() == 200;
//
//            response = client.post("/urls/1/checks");
//            assert response.code() == 200;
//            assert response.body().string().contains("Страница успешно проверена");
//        });
//    }
//}
