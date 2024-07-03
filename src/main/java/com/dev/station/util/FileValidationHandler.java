package com.dev.station.util;

import com.dev.station.util.alert.AlertUtils;
import com.dev.station.util.alert.HeaderAlertUtils;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;

import java.io.File;
import java.util.ResourceBundle;

public class FileValidationHandler {
    private final FileUtils fileUtils;
    private final ToggleButton toggleMonitoring;
    private final TextArea fileContentArea;

    public FileValidationHandler(FileUtils fileUtils, ToggleButton toggleMonitoring, TextArea fileContentArea) {
        this.fileUtils = fileUtils;
        this.toggleMonitoring = toggleMonitoring;
        this.fileContentArea = fileContentArea;
    }

    public boolean validateFile(String filePath, String fileName, ResourceBundle bundle) {
        if (!fileUtils.fileExists(filePath, fileName)) {
            HeaderAlertUtils.showErrorAlert("", bundle.getString("alert.fileNotFound") + " " + filePath + File.separator + fileName);
            return false;
        }
        return true;
    }

    public void handleFileNotFound(String filePath, String fileName, ResourceBundle bundle) {
        HeaderAlertUtils.showErrorAlert("", bundle.getString("alert.fileNotFound") + " " + filePath);
        HeaderAlertUtils.showErrorAlert("", bundle.getString("alert.fileNotFound") + " " + filePath + " " + fileName);
        doIfFileNotExists();
    }

    public void doIfFileNotExists() {
        toggleMonitoring.setSelected(false);
//        fileMonitoringHandler.stopMonitoring();
        fileContentArea.setVisible(false);
//        closeStage(monitoringWindowStage);
    }
}
