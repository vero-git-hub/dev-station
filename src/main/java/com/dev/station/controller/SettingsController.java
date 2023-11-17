package com.dev.station.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import java.util.prefs.Preferences;

public class SettingsController {

    @FXML
    private ComboBox<String> startupTabComboBox;

    private Preferences prefs = Preferences.userNodeForPackage(SettingsController.class);

    @FXML
    public void initialize() {
        String defaultTab = prefs.get("defaultTab", "Program management 1");
        startupTabComboBox.getSelectionModel().select(defaultTab);

        startupTabComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            prefs.put("defaultTab", newVal);
        });
    }
}
