package com.dev.station.controller.header;

import com.dev.station.Localizable;
import com.dev.station.entity.DriverSettings;
import com.dev.station.entity.ImageSettings;
import com.dev.station.entity.RegistryCleaner;
import com.dev.station.entity.SeleniumSettings;
import com.dev.station.manager.LanguageManager;
import com.dev.station.manager.NotificationManager;
import com.dev.station.model.SettingsModel;
import com.dev.station.util.ValidUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

public class SettingsController implements Localizable {

    @FXML private TextField seleniumPathField;
    @FXML private TextField seleniumJARPathField;
    @FXML private TextField imagesFolderPathField;
    @FXML private TextField imageWidthField;
    @FXML private TextField imageHeightField;
    @FXML private CheckBox useOriginalSizeCheckbox;
    @FXML private TextField websiteUrl;
    @FXML private TextField driverFolderPathField;
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
    @FXML private Button cleanRegistry;
    private NotificationManager notificationManager;
    private SettingsModel settingsModel;

    public SettingsController() {
        LanguageManager.registerForUpdates(this::updateUI);
        settingsModel = new SettingsModel();
    }

    @FXML public void initialize() {
        notificationManager = new NotificationManager(LanguageManager.getResourceBundle());
        LanguageManager.registerNotificationManager(notificationManager);

        loadSavedLanguage();
        downloadUserValues();
    }

    @FXML private void saveSeleniumSettings() {
        String seleniumPath = seleniumPathField.getText();
        String seleniumJARPath = seleniumJARPathField.getText();

        boolean isValidExePath = ValidUtils.isValidPath(seleniumPath) && seleniumPath.endsWith(".exe") && new File(seleniumPath).exists();
        boolean isValidJarPath = ValidUtils.isValidPath(seleniumJARPath) && seleniumJARPath.endsWith(".jar") && new File(seleniumJARPath).exists();

        if (isValidExePath && isValidJarPath) {
            SeleniumSettings seleniumSettings = new SeleniumSettings(seleniumJARPath, seleniumPath);

            SettingsModel.saveSeleniumSettings(seleniumSettings);

            notificationManager.showInformationAlert("successUpdateSeleniumSettings");
        } else {

            if (!isValidExePath) {
                notificationManager.showErrorAlert("errorUpdateSeleniumPath");
            }
            if (!isValidJarPath) {
                notificationManager.showErrorAlert("errorUpdateSeleniumJarPath");
            }
        }
    }

    @FXML private void saveImagesSettings() {
        String path = imagesFolderPathField.getText();
        int width = Integer.parseInt(imageWidthField.getText());
        int height = Integer.parseInt(imageHeightField.getText());
        boolean keepOriginalSize = useOriginalSizeCheckbox.isSelected();

        ImageSettings settings = new ImageSettings(path, keepOriginalSize, width, height);

        settingsModel.saveImageSettingsToFile(settings);

        notificationManager.showInformationAlert("successSaveSettings");
    }

    @FXML private void saveDriverSettings() {
        String websiteUrlText = websiteUrl.getText();
        String pathFieldText = driverFolderPathField.getText();

        if (ValidUtils.isValidURL(websiteUrlText) && ValidUtils.isValidExecutablePath(pathFieldText)) {
            settingsModel.handleSaveDriverSettings(websiteUrlText, pathFieldText);
            notificationManager.showInformationAlert("successSaveSettings");
        } else {
            notificationManager.showErrorAlert("errorSaveSettings");
        }
    }

    @FXML private void cleanRegistryAction() {
        RegistryCleaner.deleteAppRegistryFolder("Software\\JavaSoft\\Prefs\\com\\dev");
    }

    private void downloadUserValues() {
        downloadSeleniumValues();
        downloadImagesValues();

        DriverSettings driverSettings = settingsModel.readDriverSettings();
        websiteUrl.setText(driverSettings.getWebsiteUrl());
        driverFolderPathField.setText(driverSettings.getPath());
    }

    private void downloadImagesValues() {
        ImageSettings imageSettings = settingsModel.loadImageSettings();
        if (imageSettings != null) {
            imagesFolderPathField.setText(imageSettings.getPath());
            imageWidthField.setText(String.valueOf(imageSettings.getWidth()));
            imageHeightField.setText(String.valueOf(imageSettings.getHeight()));
            useOriginalSizeCheckbox.setSelected(imageSettings.isKeepOriginalSize());
        } else {
            imagesFolderPathField.setText("");
            imageWidthField.setText("");
            imageHeightField.setText("");
            useOriginalSizeCheckbox.setSelected(false);
        }
    }

    private void downloadSeleniumValues() {
        SeleniumSettings seleniumSettings = SettingsModel.loadSeleniumSettings();

        if (seleniumSettings != null) {
            seleniumPathField.setText(seleniumSettings.getPathExe());
            seleniumJARPathField.setText(seleniumSettings.getPathJar());
        } else {
            seleniumPathField.setText("");
            seleniumJARPathField.setText("");
        }
    }

    @Override public void loadSavedLanguage() {
        String savedLanguage = settingsModel.loadLanguageSetting();
        Locale locale = LanguageManager.getLocale(savedLanguage);
        LanguageManager.switchLanguage(locale);
    }

    @Override public void switchLanguage(Locale newLocale) {
        LanguageManager.switchLanguage(newLocale);
        updateUI();
    }

    @Override public void updateUI() {
        ResourceBundle bundle = LanguageManager.getResourceBundle();

        this.notificationManager = new NotificationManager(bundle);
        
        driverTab.setText(bundle.getString("settingsTabDriver"));
        imagesTab.setText(bundle.getString("settingsTabImages"));

        driverSettingsAccordion.setText(bundle.getString("driverSettingsAccordion"));
        seleniumSettingsAccordion.setText(bundle.getString("seleniumSettingsAccordion"));

        websiteUrlLabel.setText(bundle.getString("websiteUrlLabel"));
        driverFolderPathFieldLabel.setText(bundle.getString("driverFolderPathFieldLabel"));
        seleniumPathFieldLabel.setText(bundle.getString("seleniumPathFieldLabel"));
        seleniumJARPathFieldLabel.setText(bundle.getString("seleniumJARPathFieldLabel"));

        useOriginalSizeCheckbox.setText(bundle.getString("useOriginalSizeCheckbox"));

        imagesFolderPathLabel.setText(bundle.getString("imagesFolderPathLabel"));
        imageWidthLabel.setText(bundle.getString("imageWidthLabel"));
        imageHeightLabel.setText(bundle.getString("imageHeightLabel"));

        cleanRegistry.setText(bundle.getString("cleanRegistry"));
    }
}