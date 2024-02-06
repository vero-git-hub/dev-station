package com.dev.station.manager;

import com.dev.station.controller.MainController;
import com.dev.station.entity.ProcessHolder;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    /**
     * Launch exe script in the Scripts tab
     * @param path
     */
    public void launchApplication(String path) {
        try {
            if (path != null && !path.isEmpty()) {
                ProcessBuilder pb = new ProcessBuilder(path);
                pb.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not start the application: " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }

    /**
     * Launch jar script in the Scripts tab
     * @param pathToJar
     */
    public void launchJarApplication(String pathToJar) {
        try {
            if (pathToJar != null && !pathToJar.isEmpty()) {

                List<String> command = new ArrayList<>();
                command.add("java");
                command.add("-jar");
                command.add(pathToJar);

                ProcessBuilder pb = new ProcessBuilder(command);
                pb.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not start the JAR application: " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }

}