module com.example.bookgui {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.bookgui to javafx.fxml;
    exports com.example.bookgui;
}