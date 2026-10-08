package de.hss.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private final String url;

    public Database(String url) {
        this.url = url;
    }

    public static Database sqliteDatei(String dateiPfad) {
        return new Database("jdbc:sqlite:" + dateiPfad);
    }

    public Connection verbinden() throws SQLException {
        return DriverManager.getConnection(url);
    }

    public void initialisieren() {
        String sql = """
                CREATE TABLE IF NOT EXISTS inventar (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    kategorie TEXT,
                    anzahl INTEGER NOT NULL DEFAULT 1,
                    lagerort TEXT,
                    barcode TEXT,
                    notiz TEXT
                )
                """;
        try (Connection connection = verbinden();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new IllegalStateException("Datenbank konnte nicht vorbereitet werden.", e);
        }
    }
}
