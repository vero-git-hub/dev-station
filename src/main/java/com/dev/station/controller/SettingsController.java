package com.dev.station.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.prefs.Preferences;

public class SettingsController {
    @FXML
    private ComboBox<String> startupTabComboBox;
    @FXML
    public TextField ubuntuPathField;
    @FXML
    private TextField phpStormPathField;
    @FXML
    private TextField seleniumPathField;
    private Preferences prefs = Preferences.userNodeForPackage(SettingsController.class);

    @FXML
    public void initialize() {
        String defaultTab = prefs.get("defaultTab", "Program management 1");
        startupTabComboBox.getSelectionModel().select(defaultTab);

        startupTabComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            prefs.put("defaultTab", newVal);
        });

        ubuntuPathField.setText(prefs.get("ubuntuPath", ""));
        phpStormPathField.setText(prefs.get("phpStormPath", ""));
        seleniumPathField.setText(prefs.get("seleniumPath", ""));
    }

    @FXML
    private void saveUbuntuSettings() {
        prefs.put("ubuntuPath", ubuntuPathField.getText());
        // message about successful saving or validation
    }

    @FXML
    private void savePhpStormSettings() {
        prefs.put("phpStormPath", phpStormPathField.getText());
        // message about successful saving or validation
    }

    @FXML
    private void saveSeleniumSettings() {
        prefs.put("seleniumPath", seleniumPathField.getText());
        // message about successful saving or validation
    }
}