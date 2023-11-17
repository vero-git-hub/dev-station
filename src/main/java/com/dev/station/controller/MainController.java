package com.dev.station.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

import java.util.prefs.Preferences;

public class MainController {

    @FXML
    private TabPane tabPane;

    private Preferences prefs = Preferences.userNodeForPackage(MainController.class);

    @FXML
    public void initialize() {
        selectDefaultTab();
    }

    public void selectDefaultTab() {
        String defaultTab = prefs.get("defaultTab", "Program management 1");
        switch (defaultTab) {
            case "Program management 1":
                tabPane.getSelectionModel().select(0);
                break;
            case "Program management 2":
                tabPane.getSelectionModel().select(1);
                break;
        }
    }
}
