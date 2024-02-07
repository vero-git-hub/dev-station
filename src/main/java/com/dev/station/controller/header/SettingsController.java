package com.dev.station.controller.header;

import com.dev.station.Localizable;
import com.dev.station.controller.MainController;
import com.dev.station.entity.DriverSettings;
import com.dev.station.manager.LanguageManager;
import com.dev.station.manager.NotificationManager;
import com.dev.station.model.ScriptsModel;
import com.dev.station.model.SettingsModel;
import com.dev.station.util.ValidUtils;
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
import java.util.prefs.Preferences;

public class SettingsController implements Localizable {
    private final Preferences prefs = MainController.prefs;
    private NotificationManager notificationManager;
    private SettingsModel settingsModel;

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
    @FXML private TextField websiteUrl;
    @FXML private TextField driverFolderPathField;
    @FXML private Tab generalTab;
    @FXML private Tab driverTab;
    @FXML private Tab imagesTab;
    @FXML private TitledPane driverSettingsAccordion;
    @FXML private TitledPane seleniumSettingsAccordion;
    @FXML public Label websiteUrlLabel;
    @FXML public Label driverFolderPathFieldLabel;
    @FXML public Label seleniumPathFieldLabel;
    @FXML public Label seleniumJARPathFieldLabel;
    @FXML public Label imagesFolderPathLabel;
    @FXML public Label imageWidthLabel;
    @FXML public Label imageHeightLabel;
    @FXML
    private ComboBox<String> languageComboBox;

    public SettingsController() {
        LanguageManager.registerForUpdates(this::updateUI);
        settingsModel = new SettingsModel();
    }

    @FXML
    public void initialize() {
        notificationManager = new NotificationManager(LanguageManager.getResourceBundle());
        LanguageManager.registerNotificationManager(notificationManager);

        loadSavedLanguage();
        downloadUserValues();
    }

    private void downloadUserValues() {
        seleniumPathField.setText(prefs.get("seleniumPath", ""));
        seleniumJARPathField.setText(prefs.get("seleniumJARPath", ""));

//        fieldClearFirstFolder.setText(prefs.get("fieldClearFirstFolder", ""));
//        fieldClearSecondFolder.setText(prefs.get("fieldClearSecondFolder", ""));
//        firstRecycleBinFolderField.setText(prefs.get("firstRecycleBin", ""));
//        secondRecycleBinFolderField.setText(prefs.get("secondRecycleBin", ""));

        imagesFolderPathField.setText(prefs.get("imagesFolderPath", ""));

        imageWidthField.setText(prefs.get("imageWidthField", ""));
        imageHeightField.setText(prefs.get("imageHeightField", ""));
        useOriginalSizeCheckbox.setSelected(prefs.getBoolean("useOriginalSizeCheckbox", false));

        DriverSettings driverSettings = settingsModel.readDriverSettings();
        websiteUrl.setText(driverSettings.getWebsiteUrl());
        driverFolderPathField.setText(driverSettings.getPath());
    }

    @Override
    public void loadSavedLanguage() {
        String savedLanguage = prefs.get("selectedLanguage", "English");
        languageComboBox.setValue(savedLanguage);

        languageComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals(oldVal)) {
                Locale newLocale = LanguageManager.getLocale(newVal);
                switchLanguage(newLocale);
            }
        });

        Locale initialLocale = LanguageManager.getLocale(savedLanguage);
        switchLanguage(initialLocale);
    }

    @Override
    public void switchLanguage(Locale newLocale) {
        String newLanguage;
        if (newLocale.equals(Locale.ENGLISH)) {
            newLanguage = "English";
        } else if (newLocale.equals(new Locale("ru", "RU"))) {
            newLanguage = "Русский";
        } else {
            newLanguage = "English";
        }

        prefs.put("selectedLanguage", newLanguage);

        LanguageManager.switchLanguage(newLocale);
        updateUI();
    }

    @Override
    public void updateUI() {
        ResourceBundle bundle = LanguageManager.getResourceBundle();

        this.notificationManager = new NotificationManager(bundle);

        generalTab.setText(bundle.getString("settingsTabGeneral"));
        driverTab.setText(bundle.getString("settingsTabDriver"));
        //clearTab.setText(bundle.getString("settingsTabClear"));
        imagesTab.setText(bundle.getString("settingsTabImages"));

        driverSettingsAccordion.setText(bundle.getString("driverSettingsAccordion"));
        seleniumSettingsAccordion.setText(bundle.getString("seleniumSettingsAccordion"));

        websiteUrlLabel.setText(bundle.getString("websiteUrlLabel"));
        driverFolderPathFieldLabel.setText(bundle.getString("driverFolderPathFieldLabel"));
        seleniumPathFieldLabel.setText(bundle.getString("seleniumPathFieldLabel"));
        seleniumJARPathFieldLabel.setText(bundle.getString("seleniumJARPathFieldLabel"));

        //firstFolderLabel.setText(bundle.getString("firstFolderLabel"));
        //firstFolderPathLabel.setText(bundle.getString("firstFolderPathLabel"));
//        firstRecycleBinPathLabel.setText(bundle.getString("firstRecycleBinPathLabel"));
//        secondFolderLabel.setText(bundle.getString("secondFolderLabel"));
//        secondFolderPathLabel.setText(bundle.getString("secondFolderPathLabel"));
//        secondRecycleBinPathLabel.setText(bundle.getString("secondRecycleBinPathLabel"));
//        removeAllTabsButton.setText(bundle.getString("removeAllTabsButton"));

        useOriginalSizeCheckbox.setText(bundle.getString("useOriginalSizeCheckbox"));

        imagesFolderPathLabel.setText(bundle.getString("imagesFolderPathLabel"));
        imageWidthLabel.setText(bundle.getString("imageWidthLabel"));
        imageHeightLabel.setText(bundle.getString("imageHeightLabel"));
    }

    @FXML
    private void saveSeleniumSettings() {
        String seleniumPath = seleniumPathField.getText();

        if (ValidUtils.isValidPath(seleniumPath) && seleniumPath.endsWith(".exe") && new File(seleniumPath).exists()) {
            prefs.put("seleniumPath", seleniumPath);
            notificationManager.showInformationAlert("successUpdateSeleniumPath");
        } else {
            notificationManager.showErrorAlert("errorUpdateSeleniumPath");
        }

        saveSeleniumJARSettings();
    }

    private void saveSeleniumJARSettings() {
        String seleniumJARPath = seleniumJARPathField.getText();

        if (ValidUtils.isValidPath(seleniumJARPath) && seleniumJARPath.endsWith(".jar") && new File(seleniumJARPath).exists()) {
            prefs.put("seleniumJARPath", seleniumJARPath);
            notificationManager.showInformationAlert("successUpdateSeleniumJarPath");
        } else {
            notificationManager.showErrorAlert("errorUpdateSeleniumJarPath");
        }
    }

    @FXML
    private void saveClearTabSettings() {
        if (ValidUtils.isValidDirectoryPath(fieldClearFirstFolder.getText()) &&
                ValidUtils.isValidDirectoryPath(fieldClearSecondFolder.getText()) &&
                ValidUtils.isValidDirectoryPath(firstRecycleBinFolderField.getText()) &&
                ValidUtils.isValidDirectoryPath(secondRecycleBinFolderField.getText())) {

            prefs.put("fieldClearFirstFolder", fieldClearFirstFolder.getText());
            prefs.put("fieldClearSecondFolder", fieldClearSecondFolder.getText());
            prefs.put("firstRecycleBin", firstRecycleBinFolderField.getText());
            prefs.put("secondRecycleBin", secondRecycleBinFolderField.getText());

            notificationManager.showInformationAlert("successSaveSettings");
        } else {
            notificationManager.showErrorAlert("errorSaveSettings");
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

        notificationManager.showInformationAlert("successSaveSettings");
    }

    @FXML
    private void saveDriverSettings() {
        String websiteUrlText = websiteUrl.getText();
        String pathFieldText = driverFolderPathField.getText();

        if (ValidUtils.isValidURL(websiteUrlText) && ValidUtils.isValidExecutablePath(pathFieldText)) {
            settingsModel.handleSaveDriverSettings(websiteUrlText, pathFieldText);

            notificationManager.showInformationAlert("successSaveSettings");
        } else {
            notificationManager.showErrorAlert("errorSaveSettings");
        }
    }

    @FXML
    private void removeAllTabsSettings() {
        int tabCount = prefs.getInt("tabCount", 0);
        for (int i = 1; i <= tabCount; i++) {
            prefs.remove("uniqueTabId" + i);
        }
        prefs.remove("tabCount");
    }

    public DriverSettings readDriverSettings() {
        return settingsModel.readDriverSettings();
    }
}