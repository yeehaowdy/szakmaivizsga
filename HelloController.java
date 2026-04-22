package com.example.muzeumgui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class HelloController {

    @FXML private RadioButton rbVarosok;
    @FXML private RadioButton rbTipusok;
    @FXML private ListView<String> lvBal;
    @FXML private ListView<String> lvJobb;
    @FXML private Label statusLabel;

    // A beolvasott látogatási adatok listája
    private List<Latogatas> osszesLatogatas = new ArrayList<>();

    @FXML
    public void initialize() {
        // Alapértelmezés szerint a Városok rádiógomb aktív [cite: 32]
        rbVarosok.setSelected(true);

        // Figyeljük, ha a bal oldali listában kiválasztanak valamit
        lvBal.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                frissitJobbLista(newValue);
            }
        });
    }

    @FXML
    protected void onMegnyitas() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("CSV fájl megnyitása");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV fájlok", "*.csv"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                // Fájl beolvasása UTF-8 kódolással [cite: 36]
                List<String> sorok = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
                osszesLatogatas.clear();

                // Beolvasás ciklussal, a fejlécet (0. sor) kihagyjuk
                for (int i = 1; i < sorok.size(); i++) {
                    osszesLatogatas.add(new Latogatas(sorok.get(i)));
                }

                statusLabel.setText("Adatok betöltve: " + osszesLatogatas.size() + " db");
                frissitBalLista(); // Betöltés után frissítjük a bal oldali listát

            } catch (IOException e) {
                statusLabel.setText("Hiba a fájl beolvasásakor!");
            }
        }
    }

    @FXML
    protected void frissitBalLista() {
        lvBal.getItems().clear();
        lvJobb.getItems().clear();

        // TreeSet-et használunk, hogy egyedi és rendezett elemeink legyenek
        TreeSet<String> egyediElemek = new TreeSet<>();

        if (rbVarosok.isSelected()) {
            // Ha a városok aktív, kigyűjtjük a városokat
            for (Latogatas l : osszesLatogatas) {
                egyediElemek.add(l.varos);
            }
        } else {
            // Ha a típusok aktív, kigyűjtjük a típusokat
            for (Latogatas l : osszesLatogatas) {
                egyediElemek.add(l.tipus);
            }
        }

        // Hozzáadjuk a rendezett elemeket a listához
        for (String elem : egyediElemek) {
            lvBal.getItems().add(elem);
        }
    }

    private void frissitJobbLista(String szuro) {
        lvJobb.getItems().clear();

        // Végigmegyünk a listán és keressük az egyezéseket [cite: 38, 40]
        for (Latogatas l : osszesLatogatas) {
            if (rbVarosok.isSelected() && l.varos.equals(szuro)) {
                lvJobb.getItems().add(l.nev + " (" + l.muzeum + ")");
            } else if (rbTipusok.isSelected() && l.tipus.equals(szuro)) {
                lvJobb.getItems().add(l.nev + " (" + l.muzeum + ")");
            }
        }
    }

    @FXML
    protected void onNevjegy() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Névjegy");
        alert.setHeaderText("Múzeumlátogatások GUI");
        alert.setContentText("Készítette: [Neved]\nVerzió: 1.0");
        alert.showAndWait(); // [cite: 41]
    }

    @FXML
    protected void onKilepes() {
        Platform.exit(); // [cite: 41]
    }

    // Belső osztály a CSV adatok tárolására
    private static class Latogatas {
        String nev, muzeum, varos, tipus;

        public Latogatas(String sor) {
            String[] s = sor.split(",", -1);
            this.nev = s[0].replace("\"", "");
            this.muzeum = s[3].replace("\"", "");
            this.varos = s[4].replace("\"", "");
            this.tipus = s[5].replace("\"", "");
        }
    }
}
