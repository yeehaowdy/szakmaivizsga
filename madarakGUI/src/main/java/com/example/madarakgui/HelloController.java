package com.example.madarakgui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;

public class HelloController {

    @FXML private MenuItem menuMegnyitas;
    @FXML private MenuItem menuKilepes;
    @FXML private MenuItem menuNevjegy;

    @FXML private TextField txtMin;
    @FXML private TextField txtMax;
    @FXML private Button btnSzures;
    @FXML private Button btnMind;
    @FXML private ListView<Madar> lstMadarak;

    private final ObservableList<Madar> mindenMadar = FXCollections.observableArrayList();
    private int minSuly;
    private int maxSuly;

    @FXML
    private void initialize() {
        txtMin.setDisable(true);
        txtMax.setDisable(true);
        btnSzures.setDisable(true);
        btnMind.setDisable(true);

        menuMegnyitas.setOnAction(this::handleMegnyitas);
        menuKilepes.setOnAction(e -> ((Stage) lstMadarak.getScene().getWindow()).close());
        menuNevjegy.setOnAction(e -> showNevjegy());
        btnSzures.setOnAction(e -> szures());
        btnMind.setOnAction(e -> mind());
    }

    private void handleMegnyitas(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("madarak.csv megnyitása");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV fájl", "*.csv"));
        File file = fc.showOpenDialog(lstMadarak.getScene().getWindow());
        if (file == null) return;

        mindenMadar.clear();
        try (BufferedReader br = new BufferedReader(
                new FileReader(file, StandardCharsets.UTF_8))) {
            String sor = br.readLine();
            while ((sor = br.readLine()) != null) {
                if (sor.isBlank()) continue;
                String[] m = sor.split(";");
                String magyar = m[0].trim();
                String latin = m[1].trim();
                int suly = Integer.parseInt(m[2].trim());
                mindenMadar.add(new Madar(magyar, latin, suly));
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Hiba a fájl beolvasásakor:\n" + e.getMessage()).showAndWait();
            return;
        }


        mindenMadar.sort((a, b) -> Integer.compare(a.getSuly(), b.getSuly()));
        lstMadarak.setItems(FXCollections.observableArrayList(mindenMadar));


        minSuly = mindenMadar.stream().mapToInt(Madar::getSuly).min().orElse(0);
        maxSuly = mindenMadar.stream().mapToInt(Madar::getSuly).max().orElse(0);
        txtMin.setText(String.valueOf(minSuly));
        txtMax.setText(String.valueOf(maxSuly));

        txtMin.setDisable(false);
        txtMax.setDisable(false);
        btnSzures.setDisable(false);
        btnMind.setDisable(false);
    }

    private void szures() {
        try {
            int min = Integer.parseInt(txtMin.getText().trim());
            int max = Integer.parseInt(txtMax.getText().trim());
            ObservableList<Madar> szurt = FXCollections.observableArrayList();
            for (Madar m : mindenMadar) {
                if (m.getSuly() >= min && m.getSuly() <= max) {
                    szurt.add(m);
                }
            }
            lstMadarak.setItems(szurt);
        } catch (NumberFormatException ex) {
            new Alert(Alert.AlertType.WARNING, "Érvénytelen szám a szűrési mezőkben.").showAndWait();
        }
    }

    private void mind() {
        txtMin.setText(String.valueOf(minSuly));
        txtMax.setText(String.valueOf(maxSuly));
        lstMadarak.setItems(FXCollections.observableArrayList(mindenMadar));
    }

    private void showNevjegy() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Névjegy");
        a.setHeaderText(null);
        a.setContentText("Madarak v1.0.0\n(C) Kandó");
        a.showAndWait();
    }
}

