package com.dev.station.util;

import com.dev.station.controller.monitoring.FileMonitorAppColor;
import com.dev.station.controller.monitoring.VersionControlMode;
import com.dev.station.manager.WindowManager;
import com.dev.station.service.FileMonitoringService;
import com.dev.station.util.alert.AlertUtils;
import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

import java.util.ResourceBundle;

/** Control the opening of the version control window */
public class VersionControlWindowHandler {

    private final ResourceBundle bundle;
    private final FileMonitoringService monitoringService;
    private final ToggleButton toggleMonitoring;
    private final TextArea fileContentArea;

    public VersionControlWindowHandler(ResourceBundle bundle, FileMonitoringService monitoringService, ToggleButton toggleMonitoring, TextArea fileContentArea) {
        this.bundle = bundle;
        this.monitoringService = monitoringService;
        this.toggleMonitoring = toggleMonitoring;
        this.fileContentArea = fileContentArea;
    }

    public void openVersionControlWindow(String textArea, VersionControlMode versionControlMode, String file1Path, int checkInterval, String tabId, boolean isClearContentToggle)  {
        if(versionControlMode == null) { return; }

        if(versionControlMode == VersionControlMode.COLOR) {
            createAndOpenMonitoringWindow(textArea, file1Path, checkInterval, tabId, isClearContentToggle);
        } else {
            AlertUtils.showInformationAlert("Info message", "Sorry other VCS modes in development. Choose \"color\" VCS mode.");
        }
    }

    private void createAndOpenMonitoringWindow(String textArea, String file1Path, int checkInterval, String tabId, boolean isClearContentToggle) {
        try {
            // Create a new Stage instance
            Stage stage = new Stage();
            FileMonitorAppColor fileMonitorAppColor = new FileMonitorAppColor();
            fileMonitorAppColor.setInitialContent(textArea);
            fileMonitorAppColor.setFile1Path(file1Path);
            String file2Path = FileUtils.generateUniqueFilePath();
            fileMonitorAppColor.setFile2Path(file2Path);
            fileMonitorAppColor.setCheckInterval(checkInterval);
            fileMonitorAppColor.setTabId(tabId);
            fileMonitorAppColor.setClearContentToggle(isClearContentToggle);
            fileMonitorAppColor.setWindowTitle("Version Control - " + file1Path); // Set the window title
            fileMonitorAppColor.start(stage);

            if (monitoringService == null) {
                throw new NullPointerException("monitoringService is null");
            }
            // Adding a controller to track file changes
            monitoringService.addFileChangeListener(fileMonitorAppColor);

            stage.setOnCloseRequest(windowEvent -> {
                if (toggleMonitoring.isSelected()) {
                    fileContentArea.setVisible(true);
                    fileMonitorAppColor.getCurrentContent(content -> Platform.runLater(() -> fileContentArea.setText(content)));
                }
                monitoringService.removeFileChangeListener(fileMonitorAppColor);
                // Stop the timer when closing a window
                fileMonitorAppColor.stopMonitoring();
                WindowManager.removeMonitoringWindow(stage);
            });

            WindowManager.addMonitoringWindow(stage); // Adding to WindowManager
            stage.setUserData(fileMonitorAppColor); // Setting UserData for a Window
            stage.show();
            fileContentArea.setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to open version control window.", e);
        }
    }

    /**
     * Get version control mode from dropdown list
     * @return object of version control mode
     */
    public VersionControlMode getSelectedVersionControlMode(ComboBox<String> versionControlModeComboBox) {
        String selectedMode = versionControlModeComboBox.getSelectionModel().getSelectedItem();

        return switch (selectedMode) {
            case "символ", "symbol" -> VersionControlMode.SYMBOL;
            case "слово", "word" -> VersionControlMode.WORD;
            case "строка", "line" -> VersionControlMode.LINE;
            case "подсказка", "tooltip" -> VersionControlMode.TOOLTIP;
            case "цвет", "color" -> VersionControlMode.COLOR;
            default -> VersionControlMode.SYMBOL;
        };
    }
}
