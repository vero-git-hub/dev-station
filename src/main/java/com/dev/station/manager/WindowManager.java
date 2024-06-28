package com.dev.station.manager;

import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages application windows
 */
public class WindowManager {
    private static final List<Stage> openStages = new ArrayList<>();
    private static final List<Stage> monitoringWindows = new ArrayList<>(); // List of monitoring windows

    public static void addStage(Stage stage) {
        openStages.add(stage);
    }

    public static void addMonitoringWindow(Stage stage) {
        monitoringWindows.add(stage);
    }

    public static List<Stage> getMonitoringWindows() {
        return new ArrayList<>(monitoringWindows);
    }

    public static void closeAllStages() {
        for (Stage stage : openStages) {
            if (stage != null && stage.isShowing()) {
                stage.close();
            }
        }
        openStages.clear();
    }

    public static void removeStage(Stage stage) {
        openStages.remove(stage);
    }

    public static void removeMonitoringWindow(Stage stage) {
        monitoringWindows.remove(stage);
    }
}
