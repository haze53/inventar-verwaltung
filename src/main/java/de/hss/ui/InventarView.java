package de.hss.ui;

import de.hss.api.ProduktSuche;
import de.hss.db.InventarRepository;
import de.hss.model.InventarItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class InventarView extends BorderPane {

    private final InventarRepository repository;
    private final ProduktSuche produktSuche;

    private final ObservableList<InventarItem> daten = FXCollections.observableArrayList();
    private final FilteredList<InventarItem> gefiltert = new FilteredList<>(daten, item -> true);
    private final TableView<InventarItem> tabelle = new TableView<>(gefiltert);

    private final TextField sucheFeld = new TextField();
    private final TextField nameFeld = new TextField();
    private final TextField kategorieFeld = new TextField();
    private final TextField anzahlFeld = new TextField();
    private final TextField lagerortFeld = new TextField();
    private final TextField barcodeFeld = new TextField();
    private final TextArea notizFeld = new TextArea();

    private InventarItem ausgewaehlt;

    public InventarView(InventarRepository repository, ProduktSuche produktSuche) {
        this.repository = repository;
        this.produktSuche = produktSuche;
        setPadding(new Insets(16));
        setTop(kopfzeile());
        setCenter(tabelleBauen());
        setRight(formular());
        tabelle.getSelectionModel().selectedItemProperty().addListener((obs, alt, neu) -> formularFuellen(neu));
        laden();
    }

    private HBox kopfzeile() {
        sucheFeld.setPromptText("Suchen nach Name, Kategorie oder Lagerort...");
        sucheFeld.textProperty().addListener((obs, alt, neu) -> sucheAktualisieren(neu));
        HBox.setHgrow(sucheFeld, Priority.ALWAYS);
        Label titel = new Label("Inventarverwaltung");
        titel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        VBox links = new VBox(8, titel, sucheFeld);
        HBox box = new HBox(links);
        box.setPadding(new Insets(0, 0, 12, 0));
        HBox.setHgrow(links, Priority.ALWAYS);
        return box;
    }

    private TableView<InventarItem> tabelleBauen() {
        tabelle.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tabelle.getColumns().addAll(
                spalte("Name", "name"),
                spalte("Kategorie", "kategorie"),
                zahlSpalte(),
                spalte("Lagerort", "lagerort"),
                spalte("Barcode", "barcode")
        );
        tabelle.setPlaceholder(new Label("Noch keine Einträge. Rechts etwas eintragen und speichern."));
        return tabelle;
    }

    private TableColumn<InventarItem, String> spalte(String titel, String eigenschaft) {
        TableColumn<InventarItem, String> column = new TableColumn<>(titel);
        column.setCellValueFactory(new PropertyValueFactory<>(eigenschaft));
        return column;
    }

    private TableColumn<InventarItem, Integer> zahlSpalte() {
        TableColumn<InventarItem, Integer> column = new TableColumn<>("Anzahl");
        column.setCellValueFactory(new PropertyValueFactory<>("anzahl"));
        column.setMaxWidth(90);
        return column;
    }

    private VBox formular() {
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        nameFeld.setPromptText("z. B. HDMI-Kabel");
        kategorieFeld.setPromptText("z. B. Technik");
        anzahlFeld.setPromptText("1");
        lagerortFeld.setPromptText("z. B. Schrank A");
        barcodeFeld.setPromptText("optional, z. B. 4006381333931");
        notizFeld.setPromptText("kurze Notiz");
        notizFeld.setPrefRowCount(3);
        notizFeld.setWrapText(true);

        int zeile = 0;
        grid.add(new Label("Name"), 0, zeile);
        grid.add(nameFeld, 1, zeile++);
        grid.add(new Label("Kategorie"), 0, zeile);
        grid.add(kategorieFeld, 1, zeile++);
        grid.add(new Label("Anzahl"), 0, zeile);
        grid.add(anzahlFeld, 1, zeile++);
        grid.add(new Label("Lagerort"), 0, zeile);
        grid.add(lagerortFeld, 1, zeile++);
        grid.add(new Label("Barcode"), 0, zeile);
        grid.add(barcodeFeld, 1, zeile++);
        grid.add(new Label("Notiz"), 0, zeile);
        grid.add(notizFeld, 1, zeile);

        Button neu = new Button("Neu");
        Button speichern = new Button("Speichern");
        Button loeschen = new Button("Löschen");
        Button barcode = new Button("Barcode suchen");
        neu.setOnAction(e -> formularLeeren());
        speichern.setOnAction(e -> speichern());
        loeschen.setOnAction(e -> loeschen());
        barcode.setOnAction(e -> barcodeSuchen());

        HBox buttons = new HBox(8, neu, speichern, loeschen);
        VBox box = new VBox(12, new Label("Eintrag"), grid, barcode, buttons);
        box.setPadding(new Insets(0, 0, 0, 16));
        box.setPrefWidth(340);
        return box;
    }

    private void sucheAktualisieren(String text) {
        String q = text == null ? "" : text.toLowerCase().trim();
        gefiltert.setPredicate(item -> {
            if (q.isEmpty()) {
                return true;
            }
            return enthaelt(item.getName(), q)
                    || enthaelt(item.getKategorie(), q)
                    || enthaelt(item.getLagerort(), q)
                    || enthaelt(item.getBarcode(), q);
        });
    }

    private boolean enthaelt(String wert, String suche) {
        return wert != null && wert.toLowerCase().contains(suche);
    }

    private void laden() {
        daten.setAll(repository.alle());
    }

    private void formularFuellen(InventarItem item) {
        ausgewaehlt = item;
        if (item == null) {
            return;
        }
        nameFeld.setText(leer(item.getName()));
        kategorieFeld.setText(leer(item.getKategorie()));
        anzahlFeld.setText(String.valueOf(item.getAnzahl()));
        lagerortFeld.setText(leer(item.getLagerort()));
        barcodeFeld.setText(leer(item.getBarcode()));
        notizFeld.setText(leer(item.getNotiz()));
    }

    private void formularLeeren() {
        tabelle.getSelectionModel().clearSelection();
        ausgewaehlt = null;
        nameFeld.clear();
        kategorieFeld.clear();
        anzahlFeld.setText("1");
        lagerortFeld.clear();
        barcodeFeld.clear();
        notizFeld.clear();
        nameFeld.requestFocus();
    }

    private void speichern() {
        String name = nameFeld.getText() == null ? "" : nameFeld.getText().trim();
        if (name.isEmpty()) {
            hinweis("Bitte einen Namen eingeben.");
            return;
        }
        int anzahl;
        try {
            anzahl = Integer.parseInt(anzahlFeld.getText().trim());
        } catch (NumberFormatException e) {
            hinweis("Anzahl muss eine ganze Zahl sein.");
            return;
        }
        InventarItem item = ausgewaehlt == null ? new InventarItem() : ausgewaehlt;
        item.setName(name);
        item.setKategorie(kategorieFeld.getText());
        item.setAnzahl(anzahl);
        item.setLagerort(lagerortFeld.getText());
        item.setBarcode(barcodeFeld.getText());
        item.setNotiz(notizFeld.getText());
        repository.speichern(item);
        laden();
        tabelle.getSelectionModel().select(item);
    }

    private void loeschen() {
        if (ausgewaehlt == null || ausgewaehlt.getId() == null) {
            hinweis("Bitte zuerst einen Eintrag in der Tabelle auswählen.");
            return;
        }
        repository.loeschen(ausgewaehlt.getId());
        formularLeeren();
        laden();
    }

    private void barcodeSuchen() {
        String barcode = barcodeFeld.getText();
        produktSuche.nachBarcode(barcode).ifPresentOrElse(info -> {
            if (nameFeld.getText() == null || nameFeld.getText().isBlank()) {
                nameFeld.setText(info.name());
            }
            if (kategorieFeld.getText() == null || kategorieFeld.getText().isBlank()) {
                kategorieFeld.setText(info.kategorie());
            }
            hinweis("Produkt gefunden: " + info.name());
        }, () -> hinweis("Kein Produkt zu diesem Barcode gefunden (Internet nötig)."));
    }

    private void hinweis(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Inventar");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    private String leer(String wert) {
        return wert == null ? "" : wert;
    }
}
