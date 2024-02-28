package com.dev.station;

import com.dev.station.model.SettingsModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.dev.station.manager.monitoring.TimerManager;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("ui/MainLayout.fxml"));
            Parent root = loader.load();

            stage.setTitle("DevStation");

            Scene scene = new Scene(root, 825, 600); // width, height
            stage.setScene(scene);

            SettingsModel settingsModel = new SettingsModel();
            String theme = settingsModel.loadThemeSetting();
            if ("dark".equals(theme)) {
                scene.getStylesheets().add(getClass().getResource("/styles/dark-theme.css").toExternalForm());
            } else {
                scene.getStylesheets().add(getClass().getResource("/styles/light-theme.css").toExternalForm());
            }

            stage.setOnCloseRequest(event -> TimerManager.stopAll());

            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}