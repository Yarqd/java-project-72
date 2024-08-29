@import hexlet.code.dto.UrlDto
@import hexlet.code.dto.UrlCheckDto
@import hexlet.code.dto.BasePage
@import hexlet.code.Routes
@import java.util.List
@param UrlDto url
@param List<UrlCheckDto> checks
@param BasePage page

@template.layout.layout(page = page,
content = @`
    <section>
        <div class="container-lg mt-5">
            <h1>Сайт: ${url.getName()}</h1>

            <table class="table table-bordered table-hover mt-3">
                <tbody>
                <tr>
                    <td>ID</td>
                    <td>${url.getId()}</td>
                </tr>
                <tr>
                    <td>Имя</td>
                    <td>${url.getName()}</td>
                </tr>
                <tr>
                    <td>Дата создания</td>
                    <td>${url.getLastCheckedAt() != null ? url.getLastCheckedAt() : "Не проверялось"}</td>
                </tr>
                </tbody>
            </table>

            <h2 class="mt-5">Проверки</h2>
            <form method="post" action="${Routes.urlChecksPath(url.getId())}">
                <button type="submit" class="btn btn-primary">Запустить проверку</button>
            </form>

            <table class="table table-bordered table-hover mt-3">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Код ответа</th>
                    <th>title</th>
                    <th>h1</th>
                    <th>description</th>
                    <th>Дата проверки</th>
                </tr>
                </thead>

                <tbody>
                @for (UrlCheckDto check : checks)
                    <tr>
                        <td>${check.getId()}</td>
                        <td>${check.getStatusCode()}</td>
                        <td>${check.getTitle()}</td>
                        <td>${check.getH1()}</td>
                        <td>${check.getDescription()}</td>
                        <td>${check.getCreatedAt()}</td>
                    </tr>
                @endfor
                </tbody>
            </table>
        </div>
    </section>
`)
