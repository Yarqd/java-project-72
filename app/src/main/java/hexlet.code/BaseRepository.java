package hexlet.code;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.sql.DataSource;

@Getter
@Setter
public class BaseRepository {
    private DataSource dataSource;

    public BaseRepository(@NonNull DataSource ds) {
        this.dataSource = ds;
    }
}
