package com.dev.station.service;

import com.dev.station.manager.TimerManager;
import com.dev.station.util.AlertUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Timer;
import java.util.TimerTask;

public class FileMonitoringService {
    private Timer timer;
    private FileChangeListener listener;
    private long lastModified = 0;
    private String filePath;
    private String fileName;

    public FileMonitoringService(String filePath, String fileName, FileChangeListener listener) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.listener = listener;
    }

    public void startMonitoring(int frequency) {
        stopMonitoring();

        timer = new Timer("FileMonitoringServiceTimer", true);
        long period = frequency * 1000L;

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                File file = new File(filePath, fileName);
                try {
                    long currentModified = file.lastModified();
                    if (currentModified > lastModified) {
                        lastModified = currentModified;
                        String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                        if (listener != null) {
                            listener.onFileChange(content);
                        }
                    }
                } catch (IOException e) {
                    AlertUtils.showErrorAlert("", "");
                    e.printStackTrace();
                }
            }
        };

        TimerManager.addTimer(timer);
        timer.scheduleAtFixedRate(task, 0, period);
    }

    public void stopMonitoring() {
        if (timer != null) {
            TimerManager.removeTimer(timer);
            timer = null;
        }
    }
}
