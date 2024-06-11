package hexlet.code;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Url {
    private long id;
    private String name;
    private Timestamp createdAt;
}
