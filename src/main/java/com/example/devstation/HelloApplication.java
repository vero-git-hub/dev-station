package com.example.devstation;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

public class HelloApplication extends Application {

    private final int SCENE_WIDTH = 600;
    private final int SCENE_HEIGHT = 400;

    @Override
    public void start(Stage stage) {
        TabPane tabPane = new TabPane();
        UserPreferenceManager preferenceManager = new UserPreferenceManager();

        tabPane.getTabs().addAll(
                TabFactory.createFirstTab(),
                TabFactory.createSecondTab(),
                TabFactory.createSettingsTab(tabPane, preferenceManager)
        );

        String userPreferredTab = preferenceManager.loadUserPreference();
        if ("Tab 1".equals(userPreferredTab)) {
            tabPane.getSelectionModel().select(0);
        } else if ("Tab 2".equals(userPreferredTab)) {
            tabPane.getSelectionModel().select(1);
        }

        Scene scene = new Scene(tabPane, SCENE_WIDTH, SCENE_HEIGHT);
        scene.getStylesheets().addAll(BootstrapFX.bootstrapFXStylesheet());

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}