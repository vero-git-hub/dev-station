package com.dev.station.util;

import com.dev.station.controller.monitoring.FileMonitorAppColor;
import com.dev.station.controller.monitoring.VersionControlMode;
import com.dev.station.service.FileMonitoringService;
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

    public void openVersionControlWindow(String textArea, VersionControlMode versionControlMode, String fullPath)  {
        try {
            // Create a new Stage instance
            Stage stage = new Stage();
            FileMonitorAppColor fileMonitorAppColor = new FileMonitorAppColor();
            fileMonitorAppColor.setInitialContent(textArea);
            fileMonitorAppColor.setFile1Path(fullPath);
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
            });

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
