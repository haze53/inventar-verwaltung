package de.hss.db;

import de.hss.model.InventarItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InventarRepository {

    private final Database database;

    public InventarRepository(Database database) {
        this.database = database;
    }

    public List<InventarItem> alle() {
        List<InventarItem> items = new ArrayList<>();
        String sql = """
                SELECT id, name, kategorie, anzahl, lagerort, barcode, notiz
                FROM inventar
                ORDER BY name
                """;
        try (Connection connection = database.verbinden();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                items.add(ausResultSet(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Inventar konnte nicht gelesen werden.", e);
        }
        return items;
    }

    public InventarItem speichern(InventarItem item) {
        if (item.getId() == null) {
            return einfuegen(item);
        }
        return aktualisieren(item);
    }

    public void löschen(int id) {
        String sql = "DELETE FROM inventar WHERE id = ?";
        try (Connection connection = database.verbinden();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Eintrag konnte nicht gelöscht werden.", e);
        }
    }

    private InventarItem einfuegen(InventarItem item) {
        String sql = """
                INSERT INTO inventar (name, kategorie, anzahl, lagerort, barcode, notiz)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = database.verbinden();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            werteSetzen(statement, item);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    item.setId(keys.getInt(1));
                }
            }
            return item;
        } catch (SQLException e) {
            throw new IllegalStateException("Eintrag konnte nicht gespeichert werden.", e);
        }
    }

    private InventarItem aktualisieren(InventarItem item) {
        String sql = """
                UPDATE inventar
                SET name = ?, kategorie = ?, anzahl = ?, lagerort = ?, barcode = ?, notiz = ?
                WHERE id = ?
                """;
        try (Connection connection = database.verbinden();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            werteSetzen(statement, item);
            statement.setInt(7, item.getId());
            statement.executeUpdate();
            return item;
        } catch (SQLException e) {
            throw new IllegalStateException("Eintrag konnte nicht aktualisiert werden.", e);
        }
    }

    private void werteSetzen(PreparedStatement statement, InventarItem item) throws SQLException {
        statement.setString(1, item.getName());
        statement.setString(2, leerAlsNull(item.getKategorie()));
        statement.setInt(3, item.getAnzahl());
        statement.setString(4, leerAlsNull(item.getLagerort()));
        statement.setString(5, leerAlsNull(item.getBarcode()));
        statement.setString(6, leerAlsNull(item.getNotiz()));
    }

    private InventarItem ausResultSet(ResultSet rs) throws SQLException {
        return new InventarItem(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("kategorie"),
                rs.getInt("anzahl"),
                rs.getString("lagerort"),
                rs.getString("barcode"),
                rs.getString("notiz")
        );
    }

    private String leerAlsNull(String wert) {
        if (wert == null || wert.isBlank()) {
            return null;
        }
        return wert.trim();
    }
}
