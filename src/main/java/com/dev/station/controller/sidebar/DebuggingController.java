package com.dev.station.controller.sidebar;

import com.sun.management.OperatingSystemMXBean;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class DebuggingController {

    @FXML private Label memoryUsageLabel;
    @FXML private Label cpuUsageLabel;
    private Timeline timeline;

    public void initialize() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            updateResourceUsage();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        Platform.runLater(() -> {
            if (memoryUsageLabel.getScene() != null && memoryUsageLabel.getScene().getWindow() != null) {
                memoryUsageLabel.getScene().getWindow().setOnCloseRequest(event -> {
                    if (timeline != null) {
                        timeline.stop();
                    }
                });
            }
        });
    }

    private void updateResourceUsage() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        long usedMemoryMB = heapMemoryUsage.getUsed() / 1024 / 1024;

        OperatingSystemMXBean osMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double cpuLoad = osMXBean.getProcessCpuLoad() * 100;

        memoryUsageLabel.setText("Memory usage: " + usedMemoryMB + " MB");
        cpuUsageLabel.setText(String.format("CPU load: %.2f%%", cpuLoad));
    }

    public void stopMonitoring() {
        if (timeline != null) {
            timeline.stop();
        }
    }
}