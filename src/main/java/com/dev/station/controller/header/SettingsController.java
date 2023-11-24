package com.dev.station.controller.header;

import com.dev.station.controller.MainController;
import com.dev.station.util.AlertUtils;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class SettingsController {
    private final Preferences prefs = MainController.prefs;
    @FXML
    private ComboBox<String> startupTabComboBox;
    @FXML
    private TextField phpStormPathField;
    @FXML
    private TextField seleniumPathField;
    @FXML
    private TextField seleniumJARPathField;
    @FXML
    private TextField fieldClearFirstFolder;
    @FXML
    private TextField fieldClearSecondFolder;
    @FXML
    private TextField firstRecycleBinFolderField;
    @FXML
    private TextField secondRecycleBinFolderField;
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
    @FXML
    private TextField driverFolderPathField;
    @FXML
    private TextField driverExeNameField;

    @FXML
    public void initialize() {
        phpStormPathField.setText(prefs.get("phpStormPath", ""));
        seleniumPathField.setText(prefs.get("seleniumPath", ""));
        seleniumJARPathField.setText(prefs.get("seleniumJARPath", ""));

        fieldClearFirstFolder.setText(prefs.get("fieldClearFirstFolder", ""));
        fieldClearSecondFolder.setText(prefs.get("fieldClearSecondFolder", ""));
        firstRecycleBinFolderField.setText(prefs.get("firstRecycleBin", ""));
        secondRecycleBinFolderField.setText(prefs.get("secondRecycleBin", ""));

        imagesFolderPathField.setText(prefs.get("imagesFolderPath", ""));

        imageWidthField.setText(prefs.get("imageWidthField", ""));
        imageHeightField.setText(prefs.get("imageHeightField", ""));
        useOriginalSizeCheckbox.setSelected(prefs.getBoolean("useOriginalSizeCheckbox", false));

        registryKey.setText(prefs.get("registryKey", ""));
        websiteUrl.setText(prefs.get("websiteUrl", ""));
        driverFolderPathField.setText(prefs.get("driverFolderPath", ""));
        driverExeNameField.setText(prefs.get("driverExeName", ""));
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
    private void saveClearTabSettings() {
        prefs.put("fieldClearFirstFolder", fieldClearFirstFolder.getText());
        prefs.put("fieldClearSecondFolder", fieldClearSecondFolder.getText());
        prefs.put("firstRecycleBin", firstRecycleBinFolderField.getText());
        prefs.put("secondRecycleBin", secondRecycleBinFolderField.getText());
        AlertUtils.showInformationAlert("Success!", "Save clear tab settings.");
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

    @FXML
    private void saveDriverFolderSettings() {
        prefs.put("driverFolderPath", driverFolderPathField.getText());
    }

    @FXML
    private void saveDriverExeNameSettings() {
        prefs.put("driverExeName", driverExeNameField.getText());
    }

    @FXML
    private void removeAllTabsSettings() {
        try {
            System.out.println("Before deletion:");
            String[] keysBefore = prefs.keys();
            for (String key : keysBefore) {
                System.out.println(key + ": " + prefs.get(key, "default"));
            }

            int tabCount = prefs.getInt("tabCount", 0);
            for (int i = 1; i <= tabCount; i++) {
                prefs.remove("uniqueTabId" + i);
            }
            prefs.remove("tabCount");

            System.out.println("After deletion:");
            String[] keysAfter = prefs.keys();
            for (String key : keysAfter) {
                System.out.println(key + ": " + prefs.get(key, "default"));
            }
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
    }
}