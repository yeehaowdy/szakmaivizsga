module com.example.propgui {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.propgui to javafx.fxml;
    exports com.example.propgui;
}