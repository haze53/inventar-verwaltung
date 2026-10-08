package de.hss;

import de.hss.db.Database;
import de.hss.db.InventarRepository;
import de.hss.model.InventarItem;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class InventarRepositoryTest {

    @Test
    public void speichertUndLiestEintrag() throws IOException {
        Database database = Database.sqliteDatei(Files.createTempFile("inventar", ".db").toString());
        database.initialisieren();
        InventarRepository repository = new InventarRepository(database);

        InventarItem item = new InventarItem();
        item.setName("HDMI-Kabel");
        item.setKategorie("Technik");
        item.setAnzahl(3);
        item.setLagerort("Schrank A");

        InventarItem gespeichert = repository.speichern(item);
        assertNotNull(gespeichert.getId());

        List<InventarItem> alle = repository.alle();
        assertEquals(1, alle.size());
        assertEquals("HDMI-Kabel", alle.get(0).getName());
        assertEquals(3, alle.get(0).getAnzahl());
    }
}
