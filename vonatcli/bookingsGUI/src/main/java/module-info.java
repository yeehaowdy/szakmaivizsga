module com.example.bookingsgui {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.bookingsgui to javafx.fxml;
    exports com.example.bookingsgui;
}