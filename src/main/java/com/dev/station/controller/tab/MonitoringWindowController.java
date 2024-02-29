package com.dev.station.controller.tab;

import com.dev.station.service.FileChangeListener;
import com.dev.station.service.FileMonitoringService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

/**
 * Manage monitoring logic in a new window
 */
public class MonitoringWindowController implements FileChangeListener {

    @FXML private TextArea monitoringTextArea;
    private FileMonitoringService monitoringService;

    public void initData(String filePath, String fileName, int frequency) {
        if (monitoringService != null) {
            monitoringService.stopMonitoring();
        }
        monitoringService = new FileMonitoringService(filePath, fileName, this);
        monitoringService.startMonitoring(frequency);
    }

    public void shutdown() {
        if (monitoringService != null) {
            monitoringService.stopMonitoring();
        }
    }

    @Override
    public void onFileChange(String content) {
        Platform.runLater(() -> monitoringTextArea.setText(content));
    }
}