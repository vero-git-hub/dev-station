package com.dev.station.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;

import java.io.IOException;
import java.util.prefs.Preferences;

public class MainController {

    @FXML
    private TabPane tabPane;
    @FXML
    private ToggleButton togglePhpStorm;

    private Preferences prefs = Preferences.userNodeForPackage(MainController.class);

    @FXML
    public void initialize() {
        selectDefaultTab();
    }

    @FXML
    private void handleTogglePhpStorm() {
        if (togglePhpStorm.isSelected()) {
            launchPhpStorm();
        } else {
            // logic to turn off PhpStorm
        }
    }

    private void launchPhpStorm() {
        try {
            String pathToPhpStorm = prefs.get("phpStormPath", "C:\\Program Files");

            Runtime.getRuntime().exec(pathToPhpStorm);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
