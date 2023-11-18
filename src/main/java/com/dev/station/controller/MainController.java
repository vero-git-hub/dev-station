package com.dev.station.controller;

import com.dev.station.manager.TabSelectionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TabPane;

import java.io.IOException;
import java.util.prefs.Preferences;

public class MainController {
    @FXML
    private TabPane tabPane;
    private TabSelectionManager tabSelectionManager;

    @FXML
    public void initialize() {
        Preferences prefs = Preferences.userNodeForPackage(getClass());
        FXMLLoader programLoader = new FXMLLoader(getClass().getResource("/ui/ProgramLayout.fxml"));
        try {
            Node programNode = programLoader.load();
            tabPane.getTabs().get(0).setContent(programNode);

            ProgramController programController = programLoader.getController();
            programController.init(prefs);
        } catch (IOException e) {
            e.printStackTrace();
        }

        tabSelectionManager = new TabSelectionManager(prefs, tabPane);
        tabSelectionManager.selectDefaultTab();
    }
}