package hexlet.code.model;

import java.sql.Timestamp;

/**.
 * Модель URL
 */
public final class Url {

    private long id;
    private String name;
    private Timestamp createdAt;

    public Url() {
    }

    public Url(long id, String name, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
