module com.smugalpaca.diagnostic_dashboard {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires com.github.oshi;
    requires org.jfree.jfreechart;


    opens com.smugalpaca.diagnostic_dashboard to javafx.fxml;
    exports com.smugalpaca.diagnostic_dashboard;
    exports com.smugalpaca.diagnostic_dashboard.SidebarApps;
    opens com.smugalpaca.diagnostic_dashboard.SidebarApps to javafx.fxml;
}