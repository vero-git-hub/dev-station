package com.dev.station.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.prefs.Preferences;

public class MainController {
    public static Preferences prefs;
    @FXML private StackPane contentArea;
    @FXML private Button manuallyButton;
    @FXML private Button clearButton;
    @FXML private Button seleniumButton;

    @FXML
    public void initialize() {
        prefs = Preferences.userNodeForPackage(getClass());

        manuallyButton.setOnAction(event -> {
            loadManuallyContent();
            setActiveButton(manuallyButton);
        });

        seleniumButton.setOnAction(event -> {
            loadSeleniumContent();
            setActiveButton(seleniumButton);
        });

        clearButton.setOnAction(event -> {
            loadClearContent();
            setActiveButton(clearButton);
        });
    }

    private void loadClearContent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/sidebar/ClearLayout.fxml"));
            Node seleniumLayout = loader.load();
            contentArea.getChildren().setAll(seleniumLayout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadSeleniumContent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/sidebar/SeleniumLayout.fxml"));
            Node seleniumLayout = loader.load();
            contentArea.getChildren().setAll(seleniumLayout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadManuallyContent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/sidebar/ManuallyLayout.fxml"));
            Node programLayout = loader.load();
            contentArea.getChildren().setAll(programLayout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setActiveButton(Button activeButton) {
        manuallyButton.getStyleClass().remove("active-button");
        seleniumButton.getStyleClass().remove("active-button");
        clearButton.getStyleClass().remove("active-button");

        activeButton.getStyleClass().add("active-button");
    }

    @FXML
    private void handleImagesButtonAction() {
        try {
            Node imagesContent = FXMLLoader.load(getClass().getResource("/ui/header/ImagesLayout.fxml"));
            contentArea.getChildren().setAll(imagesContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSettingsButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/header/SettingsLayout.fxml"));
            Node settings = loader.load();
            contentArea.getChildren().setAll(settings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}