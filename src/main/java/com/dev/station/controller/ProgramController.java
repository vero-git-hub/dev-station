package com.dev.station.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;

import java.io.IOException;
import java.util.prefs.Preferences;

public class ProgramController {
    private Preferences prefs;
    private Process phpStormProcess;
    private Process ubuntuProcess;
    private boolean isPhpStormRunning = false;
    private boolean isUbuntuRunning = false;
    @FXML
    private ToggleButton togglePhpStorm;
    @FXML
    private ToggleButton toggleUbuntu;

    public void init(Preferences prefs) {
        this.prefs = prefs;
    }

    @FXML
    private void handleTogglePhpStorm() {
        if (togglePhpStorm.isSelected()) {
            launchPhpStorm();
        } else {
            closePhpStorm();
        }
    }

    @FXML
    public void handleToggleUbuntu(ActionEvent actionEvent) {
        if (toggleUbuntu.isSelected()) {
            launchUbuntu();
        } else {
            closeUbuntu();
        }
    }

    public void launchPhpStorm() {
        try {
            if (!isPhpStormRunning) {
                String pathToPhpStorm = prefs.get("phpStormPath", "C:\\Program Files\\PhpStorm\\phpstorm.exe");
                phpStormProcess = new ProcessBuilder(pathToPhpStorm).start();
                isPhpStormRunning = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closePhpStorm() {
        // close PhpStorm
    }

    public boolean isPhpStormRunning() {
        return isPhpStormRunning;
    }

    public void launchUbuntu() {
        try {
            if (!isUbuntuRunning) {
                String path = prefs.get("ubuntuPath", "C:\\Program Files\\Ubuntu\\ubuntu.exe");
                ubuntuProcess = new ProcessBuilder(path).start();
                isUbuntuRunning = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeUbuntu() {
    }
}