package com.dev.station.controller.header;

import com.dev.station.controller.MainController;
import com.dev.station.util.AlertUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class SettingsController {
    private final Preferences prefs = MainController.prefs;
    @FXML private ComboBox<String> startupTabComboBox;
    @FXML private TextField phpStormPathField;
    @FXML private TextField seleniumPathField;
    @FXML private TextField seleniumJARPathField;
    @FXML private TextField fieldClearFirstFolder;
    @FXML private TextField fieldClearSecondFolder;
    @FXML private TextField firstRecycleBinFolderField;
    @FXML private TextField secondRecycleBinFolderField;
    @FXML private TextField imagesFolderPathField;
    @FXML private TextField imageWidthField;
    @FXML private TextField imageHeightField;
    @FXML private CheckBox useOriginalSizeCheckbox;
    @FXML private TextField registryKey;
    @FXML private TextField websiteUrl;
    @FXML private TextField driverFolderPathField;
    @FXML private TextField driverExeNameField;
    @FXML private Tab generalTab;
    @FXML private Tab driverTab;
    @FXML private Tab clearTab;
    @FXML private Tab imagesTab;
    @FXML private TitledPane driverSettingsAccordion;
    @FXML private TitledPane seleniumSettingsAccordion;
    @FXML public Label registryKeyLabel;
    @FXML public Label websiteUrlLabel;
    @FXML public Label driverFolderPathFieldLabel;
    @FXML public Label driverExeNameFieldLabel;
    @FXML public Label seleniumPathFieldLabel;
    @FXML public Label seleniumJARPathFieldLabel;
    @FXML public Label firstFolderLabel;
    @FXML public Label firstFolderPathLabel;
    @FXML public Label firstRecycleBinPathLabel;
    @FXML public Label secondFolderLabel;
    @FXML public Label secondFolderPathLabel;
    @FXML public Label secondRecycleBinPathLabel;
    @FXML public Button removeAllTabsButton;
    @FXML public Label imagesFolderPathLabel;
    @FXML public Label imageWidthLabel;
    @FXML public Label imageHeightLabel;

    @FXML
    public void initialize() {
        localize();

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

    private void localize() {
        Locale locale = Locale.getDefault();
        //locale = new Locale("en");
        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);

        generalTab.setText(bundle.getString("settingsTabGeneral"));
        driverTab.setText(bundle.getString("settingsTabDriver"));
        clearTab.setText(bundle.getString("settingsTabClear"));
        imagesTab.setText(bundle.getString("settingsTabImages"));

        driverSettingsAccordion.setText(bundle.getString("driverSettingsAccordion"));
        seleniumSettingsAccordion.setText(bundle.getString("seleniumSettingsAccordion"));

        registryKeyLabel.setText(bundle.getString("registryKeyLabel"));
        websiteUrlLabel.setText(bundle.getString("websiteUrlLabel"));
        driverFolderPathFieldLabel.setText(bundle.getString("driverFolderPathFieldLabel"));
        driverExeNameFieldLabel.setText(bundle.getString("driverExeNameFieldLabel"));
        seleniumPathFieldLabel.setText(bundle.getString("seleniumPathFieldLabel"));
        seleniumJARPathFieldLabel.setText(bundle.getString("seleniumJARPathFieldLabel"));

        firstFolderLabel.setText(bundle.getString("firstFolderLabel"));
        firstFolderPathLabel.setText(bundle.getString("firstFolderPathLabel"));
        firstRecycleBinPathLabel.setText(bundle.getString("firstRecycleBinPathLabel"));
        secondFolderLabel.setText(bundle.getString("secondFolderLabel"));
        secondFolderPathLabel.setText(bundle.getString("secondFolderPathLabel"));
        secondRecycleBinPathLabel.setText(bundle.getString("secondRecycleBinPathLabel"));
        removeAllTabsButton.setText(bundle.getString("removeAllTabsButton"));

        useOriginalSizeCheckbox.setText(bundle.getString("useOriginalSizeCheckbox"));

        imagesFolderPathLabel.setText(bundle.getString("imagesFolderPathLabel"));
        imageWidthLabel.setText(bundle.getString("imageWidthLabel"));
        imageHeightLabel.setText(bundle.getString("imageHeightLabel"));
    }

    @FXML
    private void savePhpStormSettings() {
        String path = phpStormPathField.getText();

        if (isValidPath(path) && path.endsWith("exe") && new File(path).exists()) {
            prefs.put("phpStormPath", path);
            AlertUtils.showInformationAlert("Success", "Path to PhpStorm updated successfully.");
        } else {
            AlertUtils.showErrorAlert("Invalid Path", "The entered path is not valid. Please enter a correct path to PhpStorm (ends with \".exe\").");
        }
    }

    @FXML
    private void saveSeleniumSettings() {
        String seleniumPath = seleniumPathField.getText();

        if (isValidPath(seleniumPath) && seleniumPath.endsWith(".exe") && new File(seleniumPath).exists()) {
            prefs.put("seleniumPath", seleniumPath);
            AlertUtils.showInformationAlert("Success", "Path to Selenium executable updated successfully.");
        } else {
            AlertUtils.showErrorAlert("Invalid Path", "The entered path is not valid. Please enter a correct path to the Selenium executable (ends with \".exe\").");
        }

        saveSeleniumJARSettings();
    }

    private void saveSeleniumJARSettings() {
        String seleniumJARPath = seleniumJARPathField.getText();

        if (isValidPath(seleniumJARPath) && seleniumJARPath.endsWith(".jar") && new File(seleniumJARPath).exists()) {
            prefs.put("seleniumJARPath", seleniumJARPath);
            AlertUtils.showInformationAlert("Success", "Path to Selenium JAR updated successfully.");
        } else {
            AlertUtils.showErrorAlert("Invalid Path", "The entered path is not valid. Please enter a correct path to the Selenium JAR (ends with \".jar\")..");
        }
    }

    @FXML
    private void saveClearTabSettings() {
        if (isValidDirectoryPath(fieldClearFirstFolder.getText()) &&
                isValidDirectoryPath(fieldClearSecondFolder.getText()) &&
                isValidDirectoryPath(firstRecycleBinFolderField.getText()) &&
                isValidDirectoryPath(secondRecycleBinFolderField.getText())) {

            prefs.put("fieldClearFirstFolder", fieldClearFirstFolder.getText());
            prefs.put("fieldClearSecondFolder", fieldClearSecondFolder.getText());
            prefs.put("firstRecycleBin", firstRecycleBinFolderField.getText());
            prefs.put("secondRecycleBin", secondRecycleBinFolderField.getText());
            AlertUtils.showInformationAlert("Success!", "Save clear tab settings.");
        } else {
            AlertUtils.showErrorAlert("Invalid Path", "One or more entered paths are invalid. Please enter correct directory paths.");
        }
    }

    private boolean isValidDirectoryPath(String path) {
        try {
            return Files.isDirectory(Paths.get(path));
        } catch (InvalidPathException | NullPointerException ex) {
            return false;
        }
    }

    @FXML
    private void saveImagesSettings() {
        prefs.put("imagesFolderPath", imagesFolderPathField.getText());
        saveImageSizeSettings();
    }

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
    private void saveDriverSettings() {
        if (isValidRegistryKey(registryKey.getText()) &&
                isValidURL(websiteUrl.getText()) &&
                isValidDirectoryPath(driverFolderPathField.getText()) &&
                isValidFileName(driverExeNameField.getText())) {

            prefs.put("registryKey", registryKey.getText());
            prefs.put("websiteUrl", websiteUrl.getText());
            prefs.put("driverFolderPath", driverFolderPathField.getText());
            prefs.put("driverExeName", driverExeNameField.getText());
            AlertUtils.showInformationAlert("Success!", "Driver settings saved successfully.");
        } else {
            AlertUtils.showErrorAlert("Invalid Input", "One or more entered values are invalid. Please enter correct information.");
        }
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

    private boolean isValidRegistryKey(String registryKey) {
        String regex = "^(HKEY_CURRENT_USER|HKEY_LOCAL_MACHINE|HKEY_CLASSES_ROOT|HKEY_USERS|HKEY_CURRENT_CONFIG)\\\\([\\w\\d\\s]+\\\\?)*$";
        return registryKey.matches(regex);
    }

    private boolean isValidFileName(String fileName) {
        String regex = "^[^<>:\"/\\\\|?*]+\\.\\w+$";
        return fileName.matches(regex);
    }

    private boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }

    private boolean isValidPath(String path) {
        try {
            Paths.get(path);
            return true;
        } catch (InvalidPathException | NullPointerException ex) {
            return false;
        }
    }
}