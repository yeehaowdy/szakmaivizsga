package com.example.stolengui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class HelloController {
    @FXML private RadioButton rb1, rb2; // rb1: Eszközök, rb2: Személyek
    @FXML private ListView<Object> lv_left;
    @FXML private ListView<String> lv_right;

    private List<Asset> allAssets = new ArrayList<>();
    private final ToggleGroup group = new ToggleGroup();

    @FXML
    public void initialize() {
        // Rádiógombok konfigurálása
        rb1.setText("Eszközök");
        rb2.setText("Személyek");
        rb1.setToggleGroup(group);
        rb2.setToggleGroup(group);
        rb1.setSelected(true);

        // Váltás a módok között
        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> updateLeftList());

        // Kattintás a bal oldali listán
        lv_left.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) updateRightList(newValue);
        });
    }

    @FXML
    private void handleMenuOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV fájlok", "*.csv"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                List<String> lines = Files.readAllLines(file.toPath());
                allAssets.clear();
                // Első sor (fejléc) kihagyása [cite: 72]
                for (int i = 1; i < lines.size(); i++) {
                    allAssets.add(new Asset(lines.get(i).split(",", -1)));
                }
                updateLeftList();
            } catch (Exception e) {
                showError("Hiba a fájl beolvasásakor!");
            }
        }
    }

    private void updateLeftList() {
        lv_left.getItems().clear();
        lv_right.getItems().clear();

        if (allAssets.isEmpty()) return;

        if (rb1.isSelected()) {
            // Eszközök nevei növekvőben [cite: 83, 84]
            List<Asset> sortedAssets = allAssets.stream()
                    .sorted(Comparator.comparing(a -> a.name))
                    .collect(Collectors.toList());
            lv_left.getItems().addAll(sortedAssets);
        } else {
            // Személyek nevei ABC sorrendben
            Set<String> names = allAssets.stream()
                    .map(a -> a.ownerName)
                    .collect(Collectors.toCollection(TreeSet::new));
            lv_left.getItems().addAll(names);
        }
    }

    private void updateRightList(Object selected) {
        lv_right.getItems().clear();
        if (rb1.isSelected()) {
            // Kiválasztott eszköz tolvajának adatai
            Asset a = (Asset) selected;
            lv_right.getItems().add("Tolvaj: " + a.thiefName);
            lv_right.getItems().add("Státusz: " + (a.isStolen ? "Lopott" : "Saját birtokban"));
        } else {
            // Kiválasztott személytől ellopott eszközök
            String owner = (String) selected;
            allAssets.stream()
                    .filter(a -> a.ownerName.equals(owner) && a.isStolen)
                    .forEach(a -> lv_right.getItems().add(a.name + " (" + (a.price == null ? "N/A" : a.price) + " Ft)"));
        }
    }

    @FXML private void handleExit() { Platform.exit(); }

    @FXML private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Névjegy");
        alert.setHeaderText("StolenGUI Alkalmazás");
        alert.setContentText("Készítette: [Vizsgázó Neve]\nVerzió: 1.0");
        alert.showAndWait();
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}