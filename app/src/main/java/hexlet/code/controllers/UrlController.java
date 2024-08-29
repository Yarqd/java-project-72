package hexlet.code.controllers;

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

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static io.javalin.rendering.template.TemplateUtil.model;

public final class UrlController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlController.class);
    private final UrlRepository urlRepository;
    private final UrlCheckRepository urlCheckRepository;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public UrlController(UrlRepository urlRepository, UrlCheckRepository urlCheckRepository) {
        this.urlRepository = urlRepository;
        this.urlCheckRepository = urlCheckRepository;
    }

    public void addUrl(Context ctx) {
        String inputUrl = ctx.formParam("url");
        LOGGER.info("Input URL: " + inputUrl);

        try {
            URL url = new URL(inputUrl);
            String domainUrl = url.getProtocol() + "://" + url.getHost()
                    + (url.getPort() == -1 ? "" : ":" + url.getPort());

            if (urlRepository.existsByName(domainUrl)) {
                ctx.sessionAttribute("flash", "Страница уже существует");
                ctx.sessionAttribute("flashType", "error");
                ctx.redirect("/urls");
                return;
            }

            Url newUrl = new Url();
            newUrl.setName(domainUrl);
            newUrl.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
            urlRepository.save(newUrl);

            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.sessionAttribute("flashType", "success");
            ctx.redirect("/urls");
        } catch (MalformedURLException e) {
            LOGGER.error("Некорректный URL: " + inputUrl, e);
            ctx.status(400).result("Некорректный URL");
        } catch (SQLException e) {
            LOGGER.error("Ошибка при добавлении URL", e);
            ctx.sessionAttribute("flash", "Ошибка при добавлении URL: " + e.getMessage());
            ctx.sessionAttribute("flashType", "error");
            ctx.redirect("/urls");
        }
    }


    public void listUrls(Context ctx) {
        try {
            List<UrlDto> urlsWithChecks = urlRepository.findAllWithLatestChecks();
            BasePage page = new BasePage(ctx.sessionAttribute("flash"), ctx.sessionAttribute("flashType"));
            ctx.render("urls/urls.jte", model("page", page, "urls", urlsWithChecks));
        } catch (SQLException e) {
            LOGGER.error("Ошибка при получении URL", e);
            ctx.sessionAttribute("flash", "Ошибка при получении URL: " + e.getMessage());
            ctx.sessionAttribute("flashType", "error");
            ctx.redirect("/urls");
        }
    }


    public void showUrl(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        try {
            Url url = urlRepository.findById(id);
            if (url == null) {
                throw new NotFoundResponse("URL не найден");
            }
            List<UrlCheck> checks = urlCheckRepository.findByUrlId(id);
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

            BasePage page = new BasePage(ctx.sessionAttribute("flash"), ctx.sessionAttribute("flashType"));
            ctx.render("urls/show.jte", model("page", page, "url", urlDto, "checks", formattedChecks));
        } catch (SQLException e) {
            LOGGER.error("Ошибка при получении URL", e);
            ctx.sessionAttribute("flash", "Ошибка при получении URL: " + e.getMessage());
            ctx.sessionAttribute("flashType", "error");
            ctx.redirect("/urls");
        }
    }
}
