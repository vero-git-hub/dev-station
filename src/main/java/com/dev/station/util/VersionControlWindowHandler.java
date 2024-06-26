package com.dev.station.util;

import com.dev.station.controller.monitoring.FileMonitorAppColor;
import com.dev.station.controller.monitoring.VersionControlMode;
import com.dev.station.service.FileMonitoringService;
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

    public void openVersionControlWindow(String textArea, VersionControlMode versionControlMode)  {
        try {
            // Create a new Stage instance
            Stage stage = new Stage();
            FileMonitorAppColor fileMonitorAppColor = new FileMonitorAppColor();
            fileMonitorAppColor.start(stage);

            if (monitoringService == null) {
                throw new NullPointerException("monitoringService is null");
            }
            // Adding a controller to track file changes
            monitoringService.addFileChangeListener(fileMonitorAppColor);
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
            case "color" -> VersionControlMode.COLOR;
            default -> VersionControlMode.SYMBOL;
        };
    }
}
