package com.dev.station.controller;

import com.dev.station.manager.TabSelectionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.prefs.Preferences;

public class MainController {
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab imagesTab;
    private TabSelectionManager tabSelectionManager;
    private ImagesController imagesController;
    Preferences prefs;
    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        prefs = Preferences.userNodeForPackage(getClass());

//        loadProgramController();
//
//        tabSelectionManager = new TabSelectionManager(prefs, tabPane);
//        tabSelectionManager.selectDefaultTab();
//
//        loadImagesController();
    }

    @FXML
    private void handleSettingsButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/SettingsLayout.fxml"));
            Node settings = loader.load();
            contentArea.getChildren().setAll(settings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadProgramController() {
        FXMLLoader programLoader = new FXMLLoader(getClass().getResource("/ui/ProgramLayout.fxml"));
        try {
            Node programNode = programLoader.load();
            tabPane.getTabs().get(0).setContent(programNode);
            ProgramController programController = programLoader.getController();
            programController.init(prefs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadImagesController() {
        FXMLLoader imagesLoader = new FXMLLoader(getClass().getResource("/ui/ImagesLayout.fxml"));
        try {
            Node imagesNode = imagesLoader.load();
            imagesController = imagesLoader.getController();

            imagesTab.setContent(imagesNode);
            imagesTab.setOnSelectionChanged(event -> {
                if (imagesTab.isSelected()) {
                    imagesController.loadImages();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}