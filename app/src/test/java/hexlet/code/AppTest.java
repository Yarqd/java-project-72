//package hexlet.code;
//
//import hexlet.code.controllers.UrlController;
//import hexlet.code.model.Url;
//import hexlet.code.repository.UrlRepository;
//import io.javalin.Javalin;
//import io.restassured.RestAssured;
//import okhttp3.mockwebserver.MockWebServer;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.MockitoAnnotations;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static io.restassured.RestAssured.given;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.lenient;
//import java.sql.SQLException;
//
//@ExtendWith(MockitoExtension.class)
//class AppTest {
//
//    private static Javalin app;
//    private static MockWebServer mockServer;
//
//    @Mock
//    private UrlRepository urlRepository;
//
//    @InjectMocks
//    private UrlController urlController;
//
//    private List<Url> savedUrls;
//
//    @BeforeAll
//    static void setUp() throws Exception {
//        mockServer = new MockWebServer();
//        mockServer.start();
//
//        // Создаем замокированный контроллер, который будет использоваться в приложении
//        app = Javalin.create(config -> {
//            // Указываем ваши контроллеры
//            config.addStaticFiles("/public");
//        }).routes(() -> {
//            new Routes(urlController); // urlController - это ваш замокированный контроллер
//        }).start(0);
//
//        RestAssured.baseURI = "http://localhost";
//        RestAssured.port = app.port();
//    }
//
//
//    @AfterAll
//    static void tearDown() throws Exception {
//        mockServer.shutdown();
//        app.stop();
//    }
//
//    @BeforeEach
//    void initializeDatabase() throws SQLException {
//        MockitoAnnotations.openMocks(this);
//        savedUrls = new ArrayList<>();
//
//        // Мокируем метод save
//        lenient().doAnswer(invocation -> {
//            Url url = invocation.getArgument(0);
//            url.setId((long) (savedUrls.size() + 1));
//            savedUrls.add(url);
//            return null;
//        }).when(urlRepository).save(any(Url.class));
//
//        // Мокируем метод findAll
//        lenient().when(urlRepository.findAll()).thenAnswer(invocation -> new ArrayList<>(savedUrls));
//    }
//
//    @Test
//    void testStore() throws SQLException {
//        String url = mockServer.url("/").toString().replaceAll("/$", "");
//
//        assertThat(given().formParam("url", url).post("/urls").getStatusCode()).isEqualTo(302);
//
//        Url actualUrl = urlRepository.findAll().stream()
//                .filter(u -> u.getName().equals(url))
//                .findFirst()
//                .orElse(null);
//
//        System.out.println("Сохраненные URL-ы: " + savedUrls);
//
//        assertThat(actualUrl).isNotNull();
//        assertThat(actualUrl.getName()).isEqualTo(url);
//    }
//}
