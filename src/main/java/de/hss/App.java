package de.hss;

import de.hss.api.ProduktSuche;
import de.hss.db.Database;
import de.hss.db.InventarRepository;
import de.hss.ui.InventarView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        Database database = Database.sqliteDatei("inventar.db");
        database.initialisieren();

        InventarView view = new InventarView(
                new InventarRepository(database),
                new ProduktSuche()
        );

        stage.setTitle("Inventarverwaltung");
        stage.setScene(new Scene(view, 980, 520));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
