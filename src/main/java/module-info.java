module org.app.restaurant {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.dlsc.formsfx;
    requires java.sql;
    requires jbcrypt;
    requires java.desktop;
    requires mysql.connector.j;
    requires jdk.jdi;

    opens org.app.restaurant to javafx.fxml;
    exports org.app.restaurant;
    exports org.app.restaurant.Controller;
    opens org.app.restaurant.Controller to javafx.fxml;
}