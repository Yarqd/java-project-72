package hexlet.code.repository;

import hexlet.code.model.Url;
import hexlet.code.dto.UrlDto;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public final class UrlRepository extends BaseRepository {

    public UrlRepository(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * Сохраняет URL в базе данных.
     *
     * @param url объект Url для сохранения.
     * @throws SQLException если возникает ошибка при доступе к базе данных.
     */
    public void save(Url url) throws SQLException {
        String sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, url.getName());
            stmt.setTimestamp(2, url.getCreatedAt());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    url.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    /**
     * Возвращает список всех URL из базы данных с их последними проверками.
     *
     * @return список объектов UrlDto.
     * @throws SQLException если возникает ошибка при доступе к базе данных.
     */
    public List<UrlDto> findAllWithLatestChecks() throws SQLException {
        List<UrlDto> urls = new ArrayList<>();
        String sql = "SELECT u.id, u.name, uc.created_at, uc.status_code "
                + "FROM urls u "
                + "LEFT JOIN (SELECT DISTINCT ON (url_id) * FROM url_checks ORDER BY url_id DESC, id DESC) uc "
                + "ON u.id = uc.url_id "
                + "ORDER BY u.id ASC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                UrlDto urlDto = new UrlDto(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toString() : null,
                        rs.getObject("status_code", Integer.class)
                );
                urls.add(urlDto);
            }
        }
        return urls;
    }

    /**
     * Ищет URL по его идентификатору.
     *
     * @param id идентификатор URL.
     * @return объект Url или null, если URL не найден.
     * @throws SQLException если возникает ошибка при доступе к базе данных.
     */
    public Url findById(long id) throws SQLException {
        String sql = "SELECT id, name FROM urls WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Url url = new Url();
                    url.setId(rs.getLong("id"));
                    url.setName(rs.getString("name"));
                    return url;
                }
            }
        }
        return null;
    }

    /**
     * Проверяет, существует ли URL с заданным именем в базе данных.
     *
     * @param name имя URL.
     * @return true, если URL существует, иначе false.
     * @throws SQLException если возникает ошибка при доступе к базе данных.
     */
    public boolean existsByName(String name) throws SQLException {
        String sql = "SELECT COUNT(*) FROM urls WHERE name = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Возвращает имя URL по его идентификатору.
     *
     * @param id идентификатор URL.
     * @return имя URL или null, если URL не найден.
     * @throws SQLException если возникает ошибка при доступе к базе данных.
     */
    public String getUrlById(long id) throws SQLException {
        String sql = "SELECT name FROM urls WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        }
        return null;
    }
}
