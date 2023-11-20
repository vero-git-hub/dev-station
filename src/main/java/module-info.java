module com.dev.station {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.prefs;
    requires org.jsoup;


    opens com.dev.station to javafx.fxml;
    exports com.dev.station;
    opens com.dev.station.controller to javafx.fxml;
}