package hexlet.code.controllers;

import hexlet.code.DatabaseConfig;
import hexlet.code.dto.BasePage;
import hexlet.code.dto.UrlCheckDto;
import hexlet.code.dto.UrlDto;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlController.class);
    private static final UrlRepository URL_REPOSITORY = new UrlRepository(DatabaseConfig.getDataSource());
    private static final UrlCheckRepository URL_CHECK_REPOSITORY = new UrlCheckRepository(
            DatabaseConfig.getDataSource());
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void addUrl(Context ctx) {
        String inputUrl = ctx.formParam("url");

        try {
            java.net.URL url = new java.net.URL(inputUrl);
            String domainUrl = url.getProtocol() + "://" + url.getHost() + (url.getPort() == -1 ? "" : ":"
                    + url.getPort());

            try {
                if (URL_REPOSITORY.existsByName(domainUrl)) {
                    ctx.sessionAttribute("flash", "Страница уже существует");
                    ctx.sessionAttribute("flashType", "error");
                    ctx.redirect("/urls");
                    return;
                }

                Url newUrl = new Url();
                newUrl.setName(domainUrl);
                newUrl.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
                URL_REPOSITORY.save(newUrl);

                ctx.sessionAttribute("flash", "Страница успешно добавлена");
                ctx.sessionAttribute("flashType", "success");
                ctx.redirect("/urls");
            } catch (SQLException e) {
                LOGGER.error("Ошибка при добавлении URL", e);
                ctx.sessionAttribute("flash", "Ошибка при добавлении URL: " + e.getMessage());
                ctx.sessionAttribute("flashType", "error");
                ctx.redirect("/urls");
            }
        } catch (java.net.MalformedURLException e) {
            LOGGER.error("Некорректный URL", e);
            ctx.status(400).result("Некорректный URL");
        }
    }

    public static void listUrls(Context ctx) {
        try {
            List<Url> urls = URL_REPOSITORY.findAll();
            List<UrlDto> urlsWithChecks = urls.stream().map(url -> {
                try {
                    UrlCheck latestCheck = URL_CHECK_REPOSITORY.findLatestByUrlId(url.getId());
                    return new UrlDto(
                            url.getId(),
                            url.getName(),
                            latestCheck != null ? DATE_FORMAT.format(latestCheck.getCreatedAt()) : null,
                            latestCheck != null ? latestCheck.getStatusCode() : null
                    );
                } catch (SQLException e) {
                    LOGGER.error("Ошибка при получении проверки для URL", e);
                    return new UrlDto(
                            url.getId(),
                            url.getName(),
                            null,
                            null
                    );
                }
            }).collect(Collectors.toList());

            BasePage page = new BasePage(ctx.sessionAttribute("flash"), ctx.sessionAttribute("flashType"));
            ctx.render("urls/urls.jte", model("page", page, "urls", urlsWithChecks));
        } catch (SQLException e) {
            LOGGER.error("Ошибка при получении URL", e);
            ctx.sessionAttribute("flash", "Ошибка при получении URL: " + e.getMessage());
            ctx.sessionAttribute("flashType", "error");
            ctx.redirect("/urls");
        }
    }

    public static void showUrl(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        try {
            Url url = URL_REPOSITORY.findById(id);
            if (url == null) {
                throw new NotFoundResponse("URL не найден");
            }
            List<UrlCheck> checks = URL_CHECK_REPOSITORY.findByUrlId(id);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            List<UrlCheckDto> formattedChecks = checks.stream().map(check -> new UrlCheckDto(
                    check.getId(),
                    check.getStatusCode(),
                    check.getTitle(),
                    check.getH1(),
                    check.getDescription(),
                    check.getUrlId(),
                    check.getCreatedAt().toLocalDateTime().format(formatter)
            )).collect(Collectors.toList());

            UrlDto urlDto = new UrlDto(
                    url.getId(),
                    url.getName(),
                    checks.isEmpty() ? "Не проверялось" : checks.get(0).getCreatedAt().
                            toLocalDateTime().format(formatter),
                    checks.isEmpty() ? null : checks.get(0).getStatusCode()
            );

            BasePage page = new BasePage(ctx.sessionAttribute("flash"),
                    ctx.sessionAttribute("flashType"));
            ctx.render("urls/show.jte", model("page", page,
                    "url", urlDto, "checks", formattedChecks));
        } catch (SQLException e) {
            LOGGER.error("Ошибка при получении URL", e);
            ctx.sessionAttribute("flash", "Ошибка при получении URL: " + e.getMessage());
            ctx.sessionAttribute("flashType", "error");
            ctx.redirect("/urls");
        }
    }
}
