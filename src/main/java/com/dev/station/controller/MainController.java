package com.dev.station.controller;

import com.dev.station.entity.VersionFinder;
import com.dev.station.entity.WebParser;
import com.dev.station.manager.TabSelectionManager;
import com.dev.station.util.AlertUtils;
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
    Preferences prefs;

    @FXML
    public void initialize() {
        prefs = Preferences.userNodeForPackage(getClass());

        loadProgramController();

        tabSelectionManager = new TabSelectionManager(prefs, tabPane);
        tabSelectionManager.selectDefaultTab();

        loadImagesController();

        compareDriverVersions();
    }

    private void compareDriverVersions() {
        String currentVersion = getCurrentVersion();
        String websiteVersion = getWebsiteVersion();

        System.out.println("currentVersion " + currentVersion);
        System.out.println("websiteVersion " + websiteVersion);
    }

    private String getWebsiteVersion() {
        return new WebParser().parseWebsiteForVersion(prefs);
    }

    private String getCurrentVersion() {
        VersionFinder finder = new VersionFinder();
        String version = null;
        try {
            version = finder.getVersion(prefs);
        } catch (IOException | InterruptedException e) {
            AlertUtils.showErrorAlert("Failed to get driver version", "Check the registry and the method for getting the version.");
            e.printStackTrace();
        }
        return version;
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