package com.dev.station.controller.sidebar;

import com.dev.station.Localizable;
import com.dev.station.controller.MainController;
import com.dev.station.entity.DriverSettings;
import com.dev.station.entity.ProcessHolder;
import com.dev.station.entity.driver.FileDownloader;
import com.dev.station.entity.driver.UpdateFinder;
import com.dev.station.entity.driver.ZipExtractor;
import com.dev.station.manager.DriverManager;
import com.dev.station.manager.LanguageManager;
import com.dev.station.manager.LaunchManager;
import com.dev.station.manager.NotificationManager;
import com.dev.station.model.SettingsModel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class DriverController implements Localizable {
    private final Preferences prefs = MainController.prefs;
    private Process seleniumProcess;
    ResourceBundle bundle;
    NotificationManager notificationManager;
    DriverManager driverManager;
    LaunchManager launchManager;
    SettingsModel settingsModel;
    @FXML private ToggleButton toggleSelenium;
    @FXML private Label versionStatusLabel = new Label();
    @FXML private Button updateButton;
    @FXML private StackPane notificationPane;

    public DriverController() {
        LanguageManager.registerForUpdates(this::updateUI);
    }

    @FXML
    private void initialize() {
        bundle = LanguageManager.getResourceBundle();

        notificationManager = new NotificationManager(bundle);
        LanguageManager.registerNotificationManager(notificationManager);

        loadSavedLanguage();

        driverManager = new DriverManager(notificationManager);
        launchManager = new LaunchManager(notificationManager);
        settingsModel = new SettingsModel();

        compareDriverVersions();
    }

    @Override
    public void loadSavedLanguage() {
        String savedLanguage = settingsModel.loadLanguageSetting();
        Locale locale = LanguageManager.getLocale(savedLanguage);
        LanguageManager.switchLanguage(locale);
    }

    @Override
    public void switchLanguage(Locale newLocale) {
        LanguageManager.switchLanguage(newLocale);
        updateUI();
    }

    @Override
    public void updateUI() {
        updateButton.setText(getTranslate("updateButton"));
        toggleSelenium.setText(getTranslate("toggleSelenium"));
    }

    public void compareDriverVersions() {
        DriverSettings driverSettings = settingsModel.readDriverSettings();
        String url = driverSettings.getWebsiteUrl();
        String path = driverSettings.getPath();

        String currentVersion = driverManager.getCurrentVersion(path);
        String websiteVersion = driverManager.getWebsiteVersion(url);

        String versionStatus;
        if(currentVersion.equals(websiteVersion)) {
            versionStatus = getTranslate("versionSame");
            updateVersionStatus(versionStatus + " " + currentVersion);
        } else {
            versionStatus = getTranslate("versionVary");
            updateVersionStatus(versionStatus);
            updateButtonVisibility(true);
        }
        showTemporaryNotification(getTranslate("driverVersionsComparisonSuccess"));
    }

    private void showTemporaryNotification(String message) {
        Label notificationLabel = new Label(message);
        notificationLabel.getStyleClass().add("notification-label");

        notificationPane.getChildren().add(notificationLabel);

        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(2),
                ae -> notificationPane.getChildren().remove(notificationLabel)));
        timeline.play();
    }

    private void updateButtonVisibility(boolean isVisible) {
        updateButton.setVisible(isVisible);
    }

    private void updateVersionStatus(String message) {
        versionStatusLabel.setText(message);
    }

    @FXML
    private void handleUpdateButton() {
        UpdateFinder updateFinder = new UpdateFinder(notificationManager);
        String fileURL = updateFinder.findUpdateLink(prefs);
        String saveDir = prefs.get("driverFolderPath", "");
        String zipFilePath = null;

        try {
            zipFilePath = FileDownloader.downloadFile(fileURL, saveDir);
            notificationManager.showInformationAlert("successDownloaded");
        } catch (IOException e) {
            notificationManager.showErrorAlert("errorDownloaded");
            e.printStackTrace();
        }

        String outputDir = prefs.get("driverFolderPath", "");
        String fileNameToExtract = prefs.get("driverExeName", "");

        try {
            ZipExtractor.extractDriver(zipFilePath, outputDir, fileNameToExtract);
            notificationManager.showInformationAlert("successExtracted");
            updateVersionStatus("Driver version updated");
            updateButtonVisibility(false);
        } catch (IOException e) {
            notificationManager.showErrorAlert("errorExtracted");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleToggleSelenium() {
        if (toggleSelenium.isSelected()) {
            boolean isSeleniumRunning = false;
            boolean launchedExe = launchManager.launchApplication("seleniumPath", "C:\\Program Files\\Selenium\\selenium.exe", new ProcessHolder(seleniumProcess, isSeleniumRunning));

            if (launchedExe) {
                launchManager.launchJarApplication("seleniumJARPath", "C:\\Program Files\\Selenium\\selenium.jar", new ProcessHolder(seleniumProcess, isSeleniumRunning));
            }
        }
    }

    private String getTranslate(String key) {
        return bundle.getString(key);
    }
}