module com.dev.station {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.prefs;
    requires org.jsoup;
    requires org.json;
    requires java.desktop;


    opens com.dev.station to javafx.fxml;
    exports com.dev.station;
    opens com.dev.station.controller to javafx.fxml;
    opens com.dev.station.controller.header to javafx.fxml;
    opens com.dev.station.controller.sidebar to javafx.fxml;
    opens com.dev.station.controller.forms to javafx.fxml;
    opens com.dev.station.entity to javafx.base;
    opens com.dev.station.controller.tab to javafx.fxml;
    exports com.dev.station.file;
    opens com.dev.station.file to javafx.fxml;
    opens com.dev.station.factory to javafx.base;
}