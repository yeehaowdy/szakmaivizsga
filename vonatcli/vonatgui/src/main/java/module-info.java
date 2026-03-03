module com.example.vonatgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.vonatgui to javafx.fxml;
    exports com.example.vonatgui;
}