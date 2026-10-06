package de.hss;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        Label label = new Label("Hallo Inventarverwaltung!");
        stage.setScene(new Scene(new StackPane(label), 400, 300));
        stage.setTitle("Inventar");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}