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
    requires java.management;
    requires jdk.management;
    requires org.apache.commons.text;
    requires io.github.javadiffutils;
    requires org.fxmisc.richtext;


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
    exports com.dev.station.manager;
    opens com.dev.station.manager to javafx.fxml;
    opens com.dev.station.controller.monitoring to javafx.fxml;
    exports com.dev.station.util;
    opens com.dev.station.util to javafx.fxml;
    exports com.dev.station.util.alert;
    opens com.dev.station.util.alert to javafx.fxml;
    exports com.dev.station.controller.monitoring to javafx.graphics;

}