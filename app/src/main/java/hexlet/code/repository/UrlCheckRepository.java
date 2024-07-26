package hexlet.code.repository;

import hexlet.code.model.UrlCheck;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class UrlCheckRepository extends BaseRepository {

    public UrlCheckRepository(DataSource dataSource) {
        super(dataSource);
    }

    public void save(UrlCheck urlCheck) throws SQLException {
        String sql = "INSERT INTO url_checks (status_code, title, h1, description, url_id, created_at)"
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, urlCheck.getStatusCode());
            stmt.setString(2, urlCheck.getTitle());
            stmt.setString(3, urlCheck.getH1());
            stmt.setString(4, urlCheck.getDescription());
            stmt.setLong(5, urlCheck.getUrlId());
            stmt.setTimestamp(6, urlCheck.getCreatedAt());
            stmt.executeUpdate();
        }
    }

    public List<UrlCheck> findByUrlId(long urlId) throws SQLException {
        List<UrlCheck> urlChecks = new ArrayList<>();
        String sql = "SELECT id, status_code, title, h1, description, "
                + "url_id, created_at FROM url_checks WHERE url_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, urlId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UrlCheck urlCheck = new UrlCheck(
                            rs.getLong("url_id"),
                            rs.getInt("status_code"),
                            rs.getString("title"),
                            rs.getString("h1"),
                            rs.getString("description"),
                            rs.getTimestamp("created_at")
                    );
                    urlCheck.setId(rs.getLong("id"));
                    urlChecks.add(urlCheck);
                }
            }
        }
        return urlChecks;
    }

    public UrlCheck findLatestByUrlId(long urlId) throws SQLException {
        String sql = "SELECT id, status_code, title, h1, description, url_id, "
                + "created_at FROM url_checks WHERE url_id = ? ORDER BY created_at DESC LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, urlId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UrlCheck urlCheck = new UrlCheck(
                            rs.getLong("url_id"),
                            rs.getInt("status_code"),
                            rs.getString("title"),
                            rs.getString("h1"),
                            rs.getString("description"),
                            rs.getTimestamp("created_at")
                    );
                    urlCheck.setId(rs.getLong("id"));
                    return urlCheck;
                }
            }
        }
        return null;
    }
}
