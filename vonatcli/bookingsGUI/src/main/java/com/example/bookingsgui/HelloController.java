package com.example.bookingsgui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class HelloController {

    @FXML private RadioButton rbYears;
    @FXML private RadioButton rbHotels;
    @FXML private ListView<String> leftList;
    @FXML private ListView<String> rightList;

    private List<Booking> allBookings = new ArrayList<>();

    @FXML
    public void initialize() {

        rbYears.setOnAction(e -> updateLeftList());
        rbHotels.setOnAction(e -> updateLeftList());


        leftList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) updateRightList(newVal);
        });
    }

    @FXML
    protected void onOpenClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            try {
                allBookings = Files.lines(file.toPath())
                        .skip(1)
                        .map(line -> line.split(","))
                        .map(parts -> new Booking(parts))
                        .collect(Collectors.toList());
                updateLeftList();
            } catch (Exception e) {
                showError("Hiba a fájl beolvasásakor!");
            }
        }
    }

    private void updateLeftList() {
        if (allBookings.isEmpty()) return;

        ObservableList<String> items = FXCollections.observableArrayList();
        if (rbYears.isSelected()) {
            items.addAll(allBookings.stream()
                    .map(b -> b.startDate.split("-")[0])
                    .distinct().sorted().collect(Collectors.toList()));
        } else {
            items.addAll(allBookings.stream()
                    .map(b -> b.hotelName)
                    .distinct().sorted().collect(Collectors.toList()));
        }
        leftList.setItems(items);
        rightList.getItems().clear();
    }

    private void updateRightList(String selectedItem) {
        ObservableList<String> details = FXCollections.observableArrayList();
        if (rbYears.isSelected()) {
            allBookings.stream()
                    .filter(b -> b.startDate.startsWith(selectedItem))
                    .forEach(b -> details.add(b.hotelName + " (" + b.hotelCity + ")"));
        } else {
            allBookings.stream()
                    .filter(b -> b.hotelName.equals(selectedItem))
                    .forEach(b -> details.add(b.startDate + " - " + b.lastName + " " + b.firstName));
        }
        rightList.setItems(details);
    }

    @FXML
    protected void onAboutClick() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Névjegy");
        alert.setHeaderText("BookingsGUI Alkalmazás");
        alert.setContentText("Készítette: [Vizsgázó Neve]\nVerzió: 1.0");
        alert.showAndWait();
    }

    @FXML
    protected void onExitClick() {
        System.exit(0);
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    private static class Booking {
        String firstName, lastName, hotelName, hotelCity, startDate;

        public Booking(String[] p) {
            this.firstName = p[1];
            this.lastName = p[2];
            this.hotelCity = p[7];
            this.hotelName = p[8];
            this.startDate = p[13];
        }
    }
}
