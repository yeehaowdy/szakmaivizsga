package com.example.vonatgui;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import javax.print.DocFlavor;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @FXML
    public ComboBox<String> cbx_category;
    public ListView<String> lv_right;
    public ListView<String> lv_left;
    public MenuItem menuOpen;
    public MenuBar menubar;
    @FXML
    private Label welcomeText;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cbx_category.setItems(FXCollections.observableArrayList("A", "B", "C"));
        cbx_category.setValue("C");

        if(lv_left==null) lv_left = new ListView<>();
        if(lv_right==null) lv_right = new ListView<>();

    }

    public void handleMenuOpen(ActionEvent actionEvent) {
        //open file
        FileChooser chooser = new FileChooser();
        chooser.showOpenDialog(menubar.getScene().getWindow());
    }
}
