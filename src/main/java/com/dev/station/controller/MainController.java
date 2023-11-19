package com.dev.station.controller;

import com.dev.station.manager.TabSelectionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;
import java.util.prefs.Preferences;

public class MainController {
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab imagesTab;
    private TabSelectionManager tabSelectionManager;
    private ImagesController imagesController;

    @FXML
    public void initialize() {
        Preferences prefs = Preferences.userNodeForPackage(getClass());

        loadProgramController(prefs);

        tabSelectionManager = new TabSelectionManager(prefs, tabPane);
        tabSelectionManager.selectDefaultTab();

        loadImagesController();
    }

    private void loadProgramController(Preferences prefs) {
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