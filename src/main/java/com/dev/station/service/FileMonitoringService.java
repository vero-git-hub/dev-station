package com.dev.station.service;

import com.dev.station.manager.TimerManager;

import java.io.File;
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

    private Timer getTimer() {
        if (timer == null) {
            timer = new Timer("FileMonitoringServiceTimer", true);
        }
        return timer;
    }

    public void startMonitoring(int frequency) {
        stopMonitoring();

        long period = frequency * 1000L;

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                File file = new File(filePath, fileName);
                long currentModified = file.lastModified();
                if (currentModified > lastModified) {
                    lastModified = currentModified;
                    if (listener != null) {
                        FileContentProvider contentProvider = () -> new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                        listener.onFileChange(contentProvider);
                    }
                }
            }
        };

        TimerManager.addTimer(getTimer());
        getTimer().scheduleAtFixedRate(task, 0, period);
    }

    public void stopMonitoring() {
        if (timer != null) {
            TimerManager.removeTimer(timer);
            timer = null;
        }
    }

    public void updateLastModified(long newLastModified) {
        this.lastModified = newLastModified;
    }

    public void setFileChangeListener(FileChangeListener listener) {
        this.listener = listener;
    }

    public void removeFileChangeListener() {
        this.listener = null;
    }
}
