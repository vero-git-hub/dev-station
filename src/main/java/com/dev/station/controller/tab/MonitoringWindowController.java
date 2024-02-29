package com.dev.station.controller.tab;

import com.dev.station.manager.TimerManager;
import com.dev.station.util.AlertUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Manage monitoring logic in a new window
 */
public class MonitoringWindowController {

    @FXML private TextArea monitoringTextArea;

    private Timer timer;
    private long lastModified = 0;

    public void initData(String filePath, String fileName, int frequency) {
        startMonitoring(filePath, fileName, frequency);
    }

    private void startMonitoring(String filePath, String fileName, int frequency) {
        stopMonitoring();

        timer = new Timer();
        TimerManager.addTimer(timer);

        long period = frequency * 1000L;

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                File file = new File(filePath, fileName);
                try {
                    long currentModified = file.lastModified();
                    if (currentModified > lastModified) {
                        lastModified = currentModified;
                        String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                        Platform.runLater(() -> monitoringTextArea.setText(content));
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> {
                        AlertUtils.showErrorAlert("", e.getMessage());
                    });
                }
            }
        }, 0, period);
    }

    private void stopMonitoring() {
        if (timer != null) {
            TimerManager.removeTimer(timer);
            timer.cancel();
            timer = null;
        }
    }

    public void shutdown() {
        stopMonitoring();
    }
}