module com.example.madarakgui {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.madarakgui to javafx.fxml;
    exports com.example.madarakgui;
}