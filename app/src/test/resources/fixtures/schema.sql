CREATE TABLE IF NOT EXISTS urls (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS url_checks (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    status_code INT,
    title VARCHAR(255),
    h1 VARCHAR(255),
    description TEXT,
    url_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (url_id) REFERENCES urls(id) ON DELETE CASCADE
);
