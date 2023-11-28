package com.dev.station.manager;

import com.dev.station.controller.MainController;
import com.dev.station.entity.ProcessHolder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

public class LaunchManager {
    private static final Preferences prefs = MainController.prefs;
    private final NotificationManager notificationManager;

    public LaunchManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public boolean launchApplication(String pathKey, String defaultPath, ProcessHolder processHolder) {
        try {
            if (!processHolder.isRunning) {
                String path = prefs.get(pathKey, defaultPath);
                processHolder.process = new ProcessBuilder(path).start();
                processHolder.isRunning = true;
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            notificationManager.showErrorAlert("launchApplication");
        }
        return false;
    }

    public void launchJarApplication(String pathKey, String defaultPath, ProcessHolder processHolder) {
        try {
            if (!processHolder.isRunning) {
                String jarPath = prefs.get(pathKey, defaultPath);
                ProcessBuilder pb = new ProcessBuilder("java", "-jar", jarPath);
                processHolder.process = pb.start();

                boolean isFinished = processHolder.process.waitFor(2, TimeUnit.SECONDS);
                if (isFinished && processHolder.process.exitValue() != 0) {
                    throw new IOException("Error running jar: process terminated with exit code " + processHolder.process.exitValue());
                }
                processHolder.isRunning = true;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            notificationManager.showErrorAlert("launchJarApplication");
        }
    }
}