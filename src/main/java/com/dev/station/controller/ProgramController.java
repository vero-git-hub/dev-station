package com.dev.station.controller;

import com.dev.station.entity.ProcessHolder;
import com.dev.station.util.AlertUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;

import java.io.IOException;
import java.util.prefs.Preferences;

public class ProgramController {
    private Preferences prefs;
    private Process ubuntuProcess;
    private Process phpStormProcess;
    private Process seleniumProcess;
    private boolean isUbuntuRunning = false;
    private boolean isPhpStormRunning = false;
    private boolean isSeleniumRunning = false;
    @FXML
    private ToggleButton toggleUbuntu;
    @FXML
    private ToggleButton togglePhpStorm;
    @FXML
    private ToggleButton toggleSelenium;

    public void init(Preferences prefs) {
        this.prefs = prefs;
    }

    @FXML
    public void handleToggleUbuntu(ActionEvent actionEvent) {
        if (toggleUbuntu.isSelected()) {
            launchApplication("ubuntuPath", "C:\\Program Files\\Ubuntu\\ubuntu.exe", new ProcessHolder(ubuntuProcess, isUbuntuRunning));
        } else {
            closeUbuntu();
        }
    }

    @FXML
    private void handleTogglePhpStorm() {
        if (togglePhpStorm.isSelected()) {
            launchApplication("phpStormPath", "C:\\Program Files\\PhpStorm\\phpstorm.exe", new ProcessHolder(phpStormProcess, isPhpStormRunning));
        } else {
            closePhpStorm();
        }
    }

    @FXML
    private void handleToggleSelenium() {
        if (toggleSelenium.isSelected()) {
            launchApplication("seleniumPath", "C:\\Program Files\\Selenium\\selenium.exe", new ProcessHolder(seleniumProcess, isSeleniumRunning));
        } else {
            closeSelenium();
        }
    }

    private void launchApplication(String pathKey, String defaultPath, ProcessHolder processHolder) {
        try {
            if (!processHolder.isRunning) {
                String path = prefs.get(pathKey, defaultPath);
                processHolder.process = new ProcessBuilder(path).start();
                processHolder.isRunning = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Failed to start", "The specified file cannot be found.\n Check the file path in the Settings tab.");
        }
    }

    public void closeUbuntu() {}

    public void closePhpStorm() {
        // close PhpStorm
    }

    private void closeSelenium() {}
}