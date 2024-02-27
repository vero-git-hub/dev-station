module com.dev.station {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
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
    exports com.dev.station.manager.monitoring;
    opens com.dev.station.manager.monitoring to javafx.fxml;
    exports com.dev.station.manager.clear;
    opens com.dev.station.manager.clear to javafx.fxml;
}