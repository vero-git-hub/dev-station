package com.dev.station.manager;

import com.dev.station.controller.MainController;
import com.dev.station.entity.ProcessHolder;
import com.dev.station.util.AlertUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

public class LaunchManager {

    private static final Preferences prefs = MainController.prefs;

    public static boolean launchApplication(String pathKey, String defaultPath, ProcessHolder processHolder) {
        try {
            if (!processHolder.isRunning) {
                String path = prefs.get(pathKey, defaultPath);
                processHolder.process = new ProcessBuilder(path).start();
                processHolder.isRunning = true;
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Failed to start", "The specified file cannot be found.\n Check the file path in the Settings tab.");
        }
        return false;
    }

    public static void launchJarApplication(String pathKey, String defaultPath, ProcessHolder processHolder) {
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
            AlertUtils.showErrorAlert("Failed to start", "The specified JAR file cannot be found or failed to start.\nCheck the file path in the Settings tab.");
        }
    }
}
