package hexlet.code;

import io.javalin.Javalin;
import hexlet.code.controllers.UrlCheckController;
import hexlet.code.controllers.UrlController;
import hexlet.code.dto.BasePage;
import hexlet.code.repository.UrlRepository;
import hexlet.code.repository.UrlCheckRepository;

import static io.javalin.rendering.template.TemplateUtil.model;

public final class Routes {

    public static void configure(Javalin app) {
        // Создаем репозитории с передачей DataSource
        var dataSource = DatabaseConfig.getDataSource();
        UrlRepository urlRepository = new UrlRepository(dataSource);
        UrlCheckRepository urlCheckRepository = new UrlCheckRepository(dataSource);

        // Создаем контроллеры с передачей соответствующих репозиториев
        UrlController urlController = new UrlController(urlRepository, urlCheckRepository);
        UrlCheckController urlCheckController = new UrlCheckController(urlCheckRepository, urlRepository);

        app.get("/", ctx -> {
            String flashMessage = ctx.sessionAttribute("flash");
            String flashType = ctx.sessionAttribute("flashType");
            BasePage page = new BasePage(flashMessage, flashType);
            ctx.render("index.jte", model("page", page));
        });

        app.post("/", ctx -> {
            ctx.sessionAttribute("flash", null);
            ctx.sessionAttribute("flashType", null);
            ctx.status(204); // No Content
        });

        app.post("/urls", urlController::addUrl);
        app.get("/urls", urlController::listUrls);
        app.get("/urls/{id}", urlController::showUrl);
        app.post("/urls/{id}/checks", urlCheckController::checkUrl);
    }

    public static String rootPath() {
        return "/";
    }

    public static String urlsPath() {
        return "/urls";
    }

    public static String urlPath(long id) {
        return "/urls/" + id;
    }

    public static String urlChecksPath(long urlId) {
        return "/urls/" + urlId + "/checks";
    }
}
