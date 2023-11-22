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
    @FXML private Button programManagement1Button;
    @FXML private Button programManagement2Button;
    @FXML private Button seleniumButton;

    @FXML
    public void initialize() {
        prefs = Preferences.userNodeForPackage(getClass());

        manuallyButton.setOnAction(event -> {
            loadProgramLayout();
            setActiveButton(manuallyButton);
        });

        programManagement1Button.setOnAction(event -> {
            loadProgramManagement1Content();
            setActiveButton(programManagement1Button);
        });

        programManagement2Button.setOnAction(event -> {
            loadProgramManagement2Content();
            setActiveButton(programManagement2Button);
        });

        seleniumButton.setOnAction(event -> {
            loadSeleniumContent();
            setActiveButton(seleniumButton);
        });
    }

    private void loadSeleniumContent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/SeleniumLayout.fxml"));
            Node seleniumLayout = loader.load();
            contentArea.getChildren().setAll(seleniumLayout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadProgramManagement2Content() {
    }

    private void loadProgramManagement1Content() {
    }

    private void setActiveButton(Button activeButton) {
        manuallyButton.getStyleClass().remove("active-button");
        programManagement1Button.getStyleClass().remove("active-button");
        programManagement2Button.getStyleClass().remove("active-button");
        seleniumButton.getStyleClass().remove("active-button");

        activeButton.getStyleClass().add("active-button");
    }

    private void loadProgramLayout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/ManuallyLayout.fxml"));
            Node programLayout = loader.load();
            contentArea.getChildren().setAll(programLayout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleImagesButtonAction() {
        try {
            Node imagesContent = FXMLLoader.load(getClass().getResource("/ui/ImagesLayout.fxml"));
            contentArea.getChildren().setAll(imagesContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSettingsButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/SettingsLayout.fxml"));
            Node settings = loader.load();
            contentArea.getChildren().setAll(settings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}