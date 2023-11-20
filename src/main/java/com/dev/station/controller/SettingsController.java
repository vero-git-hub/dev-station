package com.dev.station.controller;

import com.dev.station.util.AlertUtils;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
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
    @FXML
    private TextField seleniumJARPathField;
    @FXML
    private TextField variableFolderPathField;
    @FXML
    private TextField recycleBinFolderPathField;
    @FXML
    private TextField imagesFolderPathField;
    @FXML
    private TextField imageWidthField;
    @FXML
    private TextField imageHeightField;
    @FXML
    private CheckBox useOriginalSizeCheckbox;
    @FXML
    private TextField registryKey;
    @FXML
    private TextField websiteUrl;
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
        seleniumJARPathField.setText(prefs.get("seleniumJARPath", ""));
        variableFolderPathField.setText(prefs.get("variableFolderPath", ""));
        recycleBinFolderPathField.setText(prefs.get("recycleBinFolderPath", ""));
        imagesFolderPathField.setText(prefs.get("imagesFolderPath", ""));

        imageWidthField.setText(prefs.get("imageWidthField", ""));
        imageHeightField.setText(prefs.get("imageHeightField", ""));
        useOriginalSizeCheckbox.setSelected(prefs.getBoolean("useOriginalSizeCheckbox", false));

        registryKey.setText(prefs.get("registryKey", ""));
        websiteUrl.setText(prefs.get("websiteUrl", ""));
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

    @FXML
    private void saveSeleniumJARSettings() {
        prefs.put("seleniumJARPath", seleniumJARPathField.getText());
        // message about successful saving or validation
    }

    @FXML
    private void saveVariableFolderSettings() {
        prefs.put("variableFolderPath", variableFolderPathField.getText());
    }

    @FXML
    private void saveRecycleBinFolderSettings() {
        prefs.put("recycleBinFolderPath", recycleBinFolderPathField.getText());
    }

    @FXML
    private void saveImagesFolderSettings() {
        prefs.put("imagesFolderPath", imagesFolderPathField.getText());
    }

    @FXML
    private void saveImageSizeSettings() {
        String imageWidth = imageWidthField.getText();
        String imageHeight = imageHeightField.getText();
        boolean useOriginalSize = useOriginalSizeCheckbox.isSelected();

        prefs.put("imageWidthField", imageWidth);
        prefs.put("imageHeightField", imageHeight);
        prefs.putBoolean("useOriginalSizeCheckbox", useOriginalSize);

        AlertUtils.showInformationAlert("Settings Updated", "Image settings updated successfully.");
    }

    @FXML
    private void saveRegistryKeySettings() {
        prefs.put("registryKey", registryKey.getText());
    }

    @FXML
    private void saveWebsiteUrlSettings() {
        prefs.put("websiteUrl", websiteUrl.getText());
    }
}