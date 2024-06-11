package hexlet.code;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UrlRepository extends BaseRepository {

    public UrlRepository(DataSource dataSource) {
        super(dataSource);
    }

    public void addUrl(Url url) throws Exception {
        String sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, url.getName());
            statement.setTimestamp(2, url.getCreatedAt());
            statement.executeUpdate();
        }
    }

    public List<Url> getAllUrls() throws Exception {
        List<Url> urls = new ArrayList<>();
        String sql = "SELECT * FROM urls";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Url url = new Url();
                url.setId(resultSet.getLong("id"));
                url.setName(resultSet.getString("name"));
                url.setCreatedAt(resultSet.getTimestamp("created_at"));
                urls.add(url);
            }
        }
        return urls;
    }
}
