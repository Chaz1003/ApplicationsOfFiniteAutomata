module com.example.movingsprites {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.movingsprites to javafx.fxml;
    exports com.example.movingsprites;
}