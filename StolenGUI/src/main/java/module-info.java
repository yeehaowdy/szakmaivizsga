module com.example.stolengui {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.stolengui to javafx.fxml;
    exports com.example.stolengui;
}