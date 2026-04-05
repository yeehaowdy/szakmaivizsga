package com.example.certigui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class HelloController {
    @FXML private RadioButton rbExams, rbPeople;
    @FXML private ListView<String> lv_left, lv_right;

    private List<ExamAttempt> attempts = new ArrayList<>();

    @FXML
    public void initialize() {
        // Módváltás figyelése
        rbExams.setOnAction(e -> updateLeftList());
        rbPeople.setOnAction(e -> updateLeftList());

        // Bal oldali lista kattintás figyelése
        lv_left.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) updateRightList(newVal);
        });
    }

    @FXML
    private void handleMenuOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                attempts = Files.lines(file.toPath())
                        .skip(1) // Fejléc kihagyása
                        .map(line -> new ExamAttempt(line.split(",")))
                        .collect(Collectors.toList());
                updateLeftList();
            } catch (Exception e) {
                showError("Hiba a fájl beolvasásakor!");
            }
        }
    }

    private void updateLeftList() {
        lv_left.getItems().clear();
        lv_right.getItems().clear();

        if (rbExams.isSelected()) {
            Set<String> examNames = attempts.stream()
                    .map(ExamAttempt::getCertificationName)
                    .collect(Collectors.toCollection(TreeSet::new));
            lv_left.getItems().addAll(examNames);
        } else {
            // Nagykorúak (2026-ban legalább 18 évesek) ABC sorrendben
            Set<String> adultNames = attempts.stream()
                    .filter(a -> a.getAge(2026) >= 18)
                    .map(ExamAttempt::getFullName)
                    .collect(Collectors.toCollection(TreeSet::new));
            lv_left.getItems().addAll(adultNames);
        }
    }

    private void updateRightList(String selected) {
        lv_right.getItems().clear();
        if (rbExams.isSelected()) {
            // Sikeres vizsgázók az adott vizsgán
            List<String> passedPeople = attempts.stream()
                    .filter(a -> a.getCertificationName().equals(selected) && a.isPassed())
                    .map(ExamAttempt::getFullName)
                    .distinct()
                    .collect(Collectors.toList());
            lv_right.getItems().addAll(passedPeople);
        } else {
            // Adott személy egyedi vizsgái
            List<String> personCerts = attempts.stream()
                    .filter(a -> a.getFullName().equals(selected))
                    .map(ExamAttempt::getCertificationName)
                    .distinct()
                    .collect(Collectors.toList());
            lv_right.getItems().addAll(personCerts);
        }
    }

    @FXML private void handleExit() { Platform.exit(); }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Névjegy");
        alert.setHeaderText("GetCertifiedGUI");
        alert.setContentText("Szakmai vizsga tanúsítványok kezelő szoftvere.");
        alert.showAndWait();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.showAndWait();
    }
}
