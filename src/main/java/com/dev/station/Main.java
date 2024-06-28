package com.dev.station;

import com.dev.station.controller.monitoring.FileMonitorAppColor;
import com.dev.station.manager.TimerManager;
import com.dev.station.manager.WindowManager;
import com.dev.station.model.SettingsModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("ui/MainLayout.fxml"));
            Parent root = loader.load();

            stage.setTitle("DevStation");

            Scene scene = new Scene(root, 900, 600); // width, height
            stage.setScene(scene);

            SettingsModel settingsModel = new SettingsModel();
            String theme = settingsModel.loadThemeSetting();
            if ("dark".equals(theme)) {
                scene.getStylesheets().add(getClass().getResource("/styles/dark-theme.css").toExternalForm());
            } else {
                scene.getStylesheets().add(getClass().getResource("/styles/light-theme.css").toExternalForm());
            }

            WindowManager.addStage(stage);

            stage.setOnCloseRequest(event -> {
                TimerManager.stopAll();
                closeAllMonitoringWindows(); // Closing all monitoring windows
                WindowManager.closeAllStages();
            });

            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    private void closeAllMonitoringWindows() {
        List<Stage> monitoringWindows = WindowManager.getMonitoringWindows(); // Getting a list of all monitoring windows
        for (Stage window : monitoringWindows) {
            if (window.getUserData() instanceof FileMonitorAppColor) {
                ((FileMonitorAppColor) window.getUserData()).stopMonitoring();
            }
            window.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}