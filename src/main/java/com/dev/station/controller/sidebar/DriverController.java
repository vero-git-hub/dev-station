package com.dev.station.controller.sidebar;

import com.dev.station.Localizable;
import com.dev.station.entity.DriverSettings;
import com.dev.station.entity.ProcessHolder;
import com.dev.station.entity.SeleniumSettings;
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
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class DriverController implements Localizable {
    @FXML private ToggleButton toggleSelenium;
    @FXML private Label versionStatusLabel = new Label();
    @FXML private Button updateButton;
    @FXML private StackPane notificationPane;
    private Process seleniumProcess;
    ResourceBundle bundle;
    NotificationManager notificationManager;
    DriverManager driverManager;
    LaunchManager launchManager;
    SettingsModel settingsModel;
    private boolean isUpdateAvailable = false;

    public DriverController() {
        LanguageManager.registerForUpdates(this::updateUI);
        settingsModel = new SettingsModel();
    }

    @FXML private void initialize() {
        bundle = LanguageManager.getResourceBundle();

        notificationManager = new NotificationManager(bundle);
        LanguageManager.registerNotificationManager(notificationManager);

        loadSavedLanguage();

        driverManager = new DriverManager(notificationManager);
        launchManager = new LaunchManager(notificationManager);

        compareDriverVersions();
    }

    @FXML private void handleUpdateButton() {
        UpdateFinder updateFinder = new UpdateFinder(notificationManager);
        String fileURL = updateFinder.findUpdateLink();

        DriverSettings driverSettings = settingsModel.readDriverSettings();
        String fullPath = driverSettings.getPath();
        int lastIndex = fullPath.lastIndexOf("\\");
        String saveDir = fullPath.substring(0, lastIndex);

        String zipFilePath = null;

        try {
            zipFilePath = FileDownloader.downloadFile(fileURL, saveDir);
            notificationManager.showInformationAlert("successDownloaded");
        } catch (IOException e) {
            notificationManager.showErrorAlert("errorDownloaded");
            e.printStackTrace();
        }

        String fileNameToExtract = fullPath.substring(lastIndex + 1);

        try {
            ZipExtractor.extractDriver(zipFilePath, saveDir, fileNameToExtract);
            notificationManager.showInformationAlert("successExtracted");
            updateVersionStatus(getTranslate("updateVersionStatus"));
            updateButtonVisibility(false);
        } catch (IOException e) {
            notificationManager.showDetailsErrorAlert(e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML private void handleToggleSelenium() {
        if (toggleSelenium.isSelected()) {
            SeleniumSettings seleniumSettings = SettingsModel.loadSeleniumSettings();

            if (seleniumSettings != null) {
                boolean isSeleniumRunning = false;
                boolean launchedExe = launchManager.launchApplication(seleniumSettings.getPathExe(), new ProcessHolder(seleniumProcess, isSeleniumRunning));

                if (launchedExe) {
                    launchManager.launchJarApplication(seleniumSettings.getPathJar(), new ProcessHolder(seleniumProcess, isSeleniumRunning));
                }
            } else {
                notificationManager.showErrorAlert("Selenium settings not found.");
            }
        }
    }

    private void setTooltips() {
        Tooltip.install(updateButton, new Tooltip(getTranslate("toggleDriverUpdateHint")));
    }

    public void compareDriverVersions() {
        DriverSettings driverSettings = settingsModel.readDriverSettings();
        String url = driverSettings.getWebsiteUrl();
        String path = driverSettings.getPath();

        String currentVersion = driverManager.getCurrentVersion(path);
        String websiteVersion = driverManager.getWebsiteVersion(url);

        if(currentVersion.equals(websiteVersion)) {
            isUpdateAvailable = false;
            updateVersionStatus(getTranslate("versionSame") + " " + currentVersion);
        } else {
            isUpdateAvailable = true;
            updateVersionStatus(getTranslate("versionVary"));
        }
        updateButtonVisibility(isUpdateAvailable);
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

    private String getTranslate(String key) {
        return bundle.getString(key);
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
        bundle = LanguageManager.getResourceBundle();

        if (isUpdateAvailable) {
            versionStatusLabel.setText(getTranslate("versionVary"));
        } else {
            versionStatusLabel.setText(getTranslate("versionSame"));
        }
        updateButtonVisibility(isUpdateAvailable);

        toggleSelenium.setText(getTranslate("toggleSelenium"));
        setTooltips();
    }
}