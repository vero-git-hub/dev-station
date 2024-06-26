package com.dev.station.service;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.IOException;

public class FileMonitoringHandler {
    private FileMonitoringService monitoringService;
    private final TextArea fileContentArea;
    private final String fullFilePath;

    public FileMonitoringHandler(TextArea fileContentArea, String fullFilePath) {
        this.fileContentArea = fileContentArea;
        this.fullFilePath = fullFilePath;
    }

    public FileMonitoringService getMonitoringService() {
        return monitoringService;
    }

    public void startMonitoring(String filePath, String fileName, int frequency) {
        if (monitoringService != null) {
            monitoringService.stopMonitoring();
        }

        try {
            monitoringService = new FileMonitoringService(filePath, fileName, contentProvider -> {
                Platform.runLater(() -> {
                    try {
                        String content = contentProvider.getContent();
                        fileContentArea.setText(content);

                        // Additional logic when changing a file
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            });
            monitoringService.startMonitoring(frequency);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void stopMonitoring() {
        if (monitoringService != null) {
            monitoringService.stopMonitoring();
        }
    }
}