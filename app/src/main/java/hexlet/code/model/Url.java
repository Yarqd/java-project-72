package hexlet.code.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public final class Url {
    private Long id;
    private String name;
    private Timestamp createdAt;

    // Конструктор без поля createdAt
    public Url(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
