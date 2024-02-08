package com.dev.station;

import com.dev.station.model.SettingsModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("ui/MainLayout.fxml"));
            Parent root = loader.load();

            stage.setTitle( "DevStation" );

            SettingsModel settingsModel = new SettingsModel();
            String theme = settingsModel.loadThemeSetting();
            Scene scene = new Scene(root, 800, 600);
            if ("dark".equals(theme)) {
                scene.getStylesheets().add(getClass().getResource("/styles/dark-theme.css").toExternalForm());
            } else {
                scene.getStylesheets().add(getClass().getResource("/styles/light-theme.css").toExternalForm());
            }
            stage.setScene(scene);
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}