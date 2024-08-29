@import java.util.List
@import hexlet.code.dto.UrlDto
@import hexlet.code.dto.BasePage
@param List<UrlDto> urls
@param BasePage page

@template.layout.layout(
page = page,
content = @`
    <div class="container mt-4">
        <h1>Сайты</h1>
        <table class="table table-bordered table-hover mt-3">
            <thead>
            <tr>
                <th class="col-1">ID</th>
                <th>Имя</th>
                <th class="col-2">Последняя проверка</th>
                <th class="col-1">Код ответа</th>
            </tr>
            </thead>
            <tbody>
            @for (UrlDto url : urls)
                <tr>
                    <td>${url.getId()}</td>
                    <td><a href="/urls/${url.getId()}">${url.getName()}</a></td>
                    <td>${url.getLastCheckedAt() != null ? url.getLastCheckedAt() : "Не проверялось"}</td>
                    <td>${url.getStatusCode() != null ? url.getStatusCode().toString() : ""}</td>
                </tr>
            @endfor
            </tbody>
        </table>
    </div>
`
)
