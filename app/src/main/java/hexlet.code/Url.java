package hexlet.code;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Url {
    private Long id;
    private String name;
    private Timestamp createdAt;
}
