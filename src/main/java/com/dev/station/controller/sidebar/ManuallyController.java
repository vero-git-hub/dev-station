package com.dev.station.controller.sidebar;

import com.dev.station.entity.ProcessHolder;
import com.dev.station.manager.FileManager;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;

public class ManuallyController {
    private Process phpStormProcess;
    @FXML
    private ToggleButton togglePhpStorm;

    @FXML
    private void handleTogglePhpStorm() {
        if (togglePhpStorm.isSelected()) {
            boolean isPhpStormRunning = false;
            FileManager.launchApplication("phpStormPath", "C:\\Program Files\\PhpStorm\\phpstorm.exe", new ProcessHolder(phpStormProcess, isPhpStormRunning));
        }
    }
}