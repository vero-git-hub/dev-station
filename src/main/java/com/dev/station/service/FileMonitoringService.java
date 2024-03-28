package com.dev.station.service;

import com.dev.station.manager.TimerManager;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FileMonitoringService {
    private Timer timer;
    private long lastModified = 0;
    private String filePath;
    private String fileName;
    private List<FileChangeListener> listeners = new ArrayList<>();

    public FileMonitoringService(String filePath, String fileName, FileChangeListener initialListener) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.listeners.add(initialListener); // Add an initial listener to the list
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
                    FileContentProvider contentProvider = () -> new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                    notifyFileChange(contentProvider); // Notify all listeners
                }
            }
        };

        TimerManager.addTimer(getTimer());
        getTimer().scheduleAtFixedRate(task, 0, period);
    }

    public void stopMonitoring() {
        if (timer != null) {
            TimerManager.removeTimer(timer);
            timer.cancel(); // Explicitly stopping the timer
            timer = null;
        }
    }

    public void updateLastModified(long newLastModified) {
        this.lastModified = newLastModified;
    }

    /**
     * @param listener
     * Add a listener
     */
    public void addFileChangeListener(FileChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * @param listener
     * Remove listener
     */
    public void removeFileChangeListener(FileChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * @param contentProvider
     * Notifying listeners about changes
     */
    protected void notifyFileChange(FileContentProvider contentProvider) {
        for (FileChangeListener listener : listeners) {
            if (listener != null) {
                listener.onFileChange(contentProvider);
            }
        }
    }
}
