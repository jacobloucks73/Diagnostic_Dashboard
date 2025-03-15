module com.smugalpaca.diagnostic_dashboard {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.smugalpaca.diagnostic_dashboard to javafx.fxml;
    exports com.smugalpaca.diagnostic_dashboard;
}