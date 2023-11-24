package com.dev.station.controller.sidebar;

import com.dev.station.controller.MainController;
import com.dev.station.entity.ProcessHolder;
import com.dev.station.entity.WebParser;
import com.dev.station.entity.driver.FileDownloader;
import com.dev.station.entity.driver.UpdateFinder;
import com.dev.station.entity.driver.ZipExtractor;
import com.dev.station.entity.driver.version.VersionExtractor;
import com.dev.station.entity.driver.version.VersionFinder;
import com.dev.station.manager.FileManager;
import com.dev.station.util.AlertUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;

import java.io.IOException;
import java.util.prefs.Preferences;

public class SeleniumController {
    private final Preferences prefs = MainController.prefs;
    private Process seleniumProcess;
    @FXML
    private ToggleButton toggleSelenium;
    @FXML
    private Label versionStatusLabel = new Label();
    @FXML
    private Button updateButton;

    @FXML
    private void initialize() {
        compareDriverVersions();
    }

    public void compareDriverVersions() {
        String currentVersion = getCurrentVersion();
        String websiteVersion = getWebsiteVersion();

        currentVersion = VersionExtractor.extractVersion(currentVersion);
        websiteVersion = VersionExtractor.extractVersion(websiteVersion);

        if(currentVersion.equals(websiteVersion)) {
            updateVersionStatus("The versions are the same! -> " + currentVersion);
        } else {
            updateVersionStatus("Versions vary!");
            updateButtonVisibility(true);
        }
    }

    private void updateButtonVisibility(boolean isVisible) {
        updateButton.setVisible(isVisible);
    }

    private String getWebsiteVersion() {
        return new WebParser().parseWebsiteForVersion(prefs);
    }

    private String getCurrentVersion() {
        VersionFinder finder = new VersionFinder();
        String version = null;
        try {
            version = finder.getVersion(prefs);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Failed to get driver version", "Check the registry and the method for getting the version.");
        }
        return version;
    }

    private void updateVersionStatus(String message) {
        versionStatusLabel.setText(message);
    }

    @FXML
    private void handleUpdateButton() {
        UpdateFinder updateFinder = new UpdateFinder();
        String fileURL = updateFinder.findUpdateLink(prefs);
        String saveDir = prefs.get("driverFolderPath", "");
        String zipFilePath = null;

        try {
            zipFilePath = FileDownloader.downloadFile(fileURL, saveDir);
            AlertUtils.showInformationAlert("Success", "File downloaded successfully: " + saveDir);
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Failed", "Error uploading file to " + saveDir);
        }

        String outputDir = prefs.get("driverFolderPath", "");
        String fileNameToExtract = prefs.get("driverExeName", "");

        try {
            ZipExtractor.extractDriver(zipFilePath, outputDir, fileNameToExtract);
            AlertUtils.showInformationAlert("Success", "Driver was successfully extracted to: " + outputDir);
            updateVersionStatus("Driver version updated");
            updateButtonVisibility(false);
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Failed", "Failed to extract file to: " + outputDir);
        }
    }

    @FXML
    private void handleToggleSelenium() {
        if (toggleSelenium.isSelected()) {
            boolean isSeleniumRunning = false;
            boolean launchedExe = FileManager.launchApplication("seleniumPath", "C:\\Program Files\\Selenium\\selenium.exe", new ProcessHolder(seleniumProcess, isSeleniumRunning));

            if (launchedExe) {
                FileManager.launchJarApplication("seleniumJARPath", "C:\\Program Files\\Selenium\\selenium.jar", new ProcessHolder(seleniumProcess, isSeleniumRunning));
            }
        }
    }
}