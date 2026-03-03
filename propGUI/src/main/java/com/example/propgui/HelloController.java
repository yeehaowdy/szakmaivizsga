package com.example.propgui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class HelloController {

    @FXML private RadioButton rb1, rb2;
    @FXML private ListView<String> lv_left;
    @FXML private ListView<String> lv_right;

    private List<Plakat> mindenPlakat = new ArrayList<>();
    private ToggleGroup csoport = new ToggleGroup();

    @FXML
    public void initialize() {
        // Rádiógombok konfigurálása
        rb1.setText("Párt");
        rb2.setText("Dátum");
        rb1.setToggleGroup(csoport);
        rb2.setToggleGroup(csoport);
        rb1.setSelected(true); // Induláskor a Párt aktív

        // Figyeljük a rádiógomb váltást és a bal oldali lista kattintását
        csoport.selectedToggleProperty().addListener((obs, old, val) -> frissitBalLista());
        lv_left.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> frissitJobbLista(val));
    }

    @FXML
    private void handleMenuOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV fájlok", "*.csv"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                mindenPlakat = Files.lines(file.toPath())
                        .skip(1)
                        .map(Plakat::new)
                        .collect(Collectors.toList());
                frissitBalLista(); // Betöltés után azonnal jelenítsük meg
            } catch (Exception e) {
                hibaUzenet("Hiba a fájl beolvasásakor!");
            }
        }
    }

    private void frissitBalLista() {
        if (mindenPlakat.isEmpty()) return;
        lv_right.getItems().clear();

        if (rb1.isSelected()) {
            // Pártok szerint növekvőben
            List<String> partok = mindenPlakat.stream()
                    .map(Plakat::getPart)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
            lv_left.getItems().setAll(partok);
        } else {
            // Dátumok szerint csökkenőben
            List<String> datumok = mindenPlakat.stream()
                    .map(p -> p.getDatum().toString())
                    .distinct()
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList());
            lv_left.getItems().setAll(datumok);
        }
    }

    private void frissitJobbLista(String kivalasztott) {
        if (kivalasztott == null) return;

        if (rb1.isSelected()) {
            // Ha párt van kiválasztva -> plakát adatai
            List<String> adatok = mindenPlakat.stream()
                    .filter(p -> p.getPart().equals(kivalasztott))
                    .map(Plakat::toString)
                    .collect(Collectors.toList());
            lv_right.getItems().setAll(adatok);
        } else {
            // Ha dátum van kiválasztva -> aznapi szövegek
            List<String> szovegek = mindenPlakat.stream()
                    .filter(p -> p.getDatum().toString().equals(kivalasztott))
                    .map(p -> p.getPart() + ": " + p.getDatum()) // A minta szerinti adatok
                    .collect(Collectors.toList());
            lv_right.getItems().setAll(szovegek);
        }
    }

    @FXML private void handleExit() { Platform.exit(); } // Kilépés menüpont

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Névjegy");
        alert.setHeaderText("PropGUI Alkalmazás");
        alert.setContentText("Készítette: [Vizsgázó Neve]\nVerzió: 1.0");
        alert.showAndWait(); // Súgó / Névjegy
    }

    private void hibaUzenet(String uzenet) {
        Alert alert = new Alert(Alert.AlertType.ERROR, uzenet);
        alert.showAndWait();
    }
}
