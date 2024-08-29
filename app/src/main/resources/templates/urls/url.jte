@import hexlet.code.dto.UrlDto
@import hexlet.code.dto.BasePage
@param UrlDto url
@param BasePage page

@template.layout.layout(
page = page,
content = @`
    <div class="container mt-4">
        <h1>Детали URL</h1>
        <p>Имя: @url.getName()</p>
        <p>Дата создания: @url.getCreatedAt()</p>
        <h2>Последняя проверка</h2>
        <p>Статус-код: @url.getStatusCode()</p>
        <p>Заголовок: @url.getTitle()</p>
        <p>H1: @url.getH1()</p>
        <p>Описание: @url.getDescription()</p>
        <a href="/urls" class="btn btn-secondary mt-4">Назад</a>
    </div>
`
)
