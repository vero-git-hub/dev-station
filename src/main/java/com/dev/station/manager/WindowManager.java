package com.dev.station.manager;

import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages application windows
 */
public class WindowManager {
    private static final List<Stage> openStages = new ArrayList<>();

    public static void addStage(Stage stage) {
        openStages.add(stage);
    }

    public static void closeAllStages() {
        for (Stage stage : openStages) {
            if (stage != null && stage.isShowing()) {
                stage.close();
            }
        }
        openStages.clear();
    }
}
