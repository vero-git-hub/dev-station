package com.dev.station.controller.forms;

import com.dev.station.controller.tab.ClearTabController;
import com.dev.station.manager.clear.JsonTabsManager;
import com.dev.station.manager.clear.PathData;
import com.dev.station.manager.clear.TabData;
import com.dev.station.manager.LanguageManager;

import com.dev.station.util.alert.AlertUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class AddPathFormController {
    @FXML public Label pathNameLabel;
    @FXML public Label pathToDirectoryLabel;
    @FXML public Label exclusionsLabel;
    @FXML public Button saveButton;
    @FXML public Button cancelButton;
    @FXML private TextField pathNameField;
    @FXML private TextField directoryPathField;
    @FXML private TextField exclusionsField;
    private Runnable onSave;
    ResourceBundle bundle;
    private DataSavedListener dataSavedListener;
    private String tabId;
    private ClearTabController clearTabController;
    private boolean isEditMode = false;
    private PathData currentPathData;

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public void setTabController(ClearTabController clearTabController) {
        this.clearTabController = clearTabController;
    }

    @FXML private void initialize() {
        bundle = LanguageManager.getResourceBundle();
        pathNameLabel.setText(getTranslate("addPathFormNameLabel"));
        pathToDirectoryLabel.setText(getTranslate("addPathFormDirectoryLabel"));
        exclusionsLabel.setText(getTranslate("addPathFormExclusionsLabel"));
        saveButton.setText(getTranslate("saveButton"));
        cancelButton.setText(getTranslate("cancelButton"));
    }

    @FXML private void handleSave() {
        String pathName = pathNameField.getText().trim();
        String directoryPath = directoryPathField.getText().trim();
        String exclusionsString = exclusionsField.getText().trim();

        if (pathName.isEmpty() || directoryPath.isEmpty()) {
            AlertUtils.showErrorAlert("Empty fields", "Please fill fields.");
            return;
        }

        List<String> exclusions = Arrays.asList(exclusionsString.split("\\s*,\\s*"));

        JsonTabsManager jsonTabsManager = new JsonTabsManager();
        List<TabData> tabs = jsonTabsManager.loadTabs(1, "Clear");

        if (isEditMode && currentPathData != null) {
            currentPathData.setName(pathName);
            currentPathData.setPath(directoryPath);
            currentPathData.setExclusions(exclusions);

            for (TabData tab : tabs) {
                if (tab.getId().equals(this.tabId)) {
                    List<PathData> paths = tab.getPaths();

                    int index = paths.indexOf(currentPathData);
                    if (index != -1) {
                        paths.set(index, currentPathData);
                    }
                    break;
                }
            }
        } else {
            PathData newPathData = new PathData(pathName, directoryPath, exclusions);

            tabs.stream()
                    .filter(tab -> tab.getId().equals(this.tabId))
                    .findFirst()
                    .map(tab -> tab.getPaths().add(newPathData))
                    .isPresent();
        }

        boolean saveResult = jsonTabsManager.saveTabs(1, "Clear", tabs);

        if (dataSavedListener != null) {
            dataSavedListener.onDataSaved();
        }

        if (clearTabController != null) {
            clearTabController.updatePathsTable();
        }

        closeStage();

        if (onSave != null) {
            onSave.run();
        }

        if (saveResult) {
            AlertUtils.showSuccessAlert("Saving successful", "Path saved successfully");
        } else {
            AlertUtils.showErrorAlert("Saving failed", "Error saving path");
        }
    }

    @FXML private void handleCancel() {
        closeStage();
    }

    public void setEditMode(boolean isEditMode, PathData pathData) {
        this.isEditMode = isEditMode;
        this.currentPathData = pathData;

        if (isEditMode && pathData != null) {
            pathNameField.setText(pathData.getName());
            directoryPathField.setText(pathData.getPath());
            String exclusionsString = String.join(", ", pathData.getExclusions());
            exclusionsField.setText(exclusionsString);
        }
    }

    public void setCurrentPathData(PathData pathData) {
        this.currentPathData = pathData;
    }

    private void closeStage() {
        Stage stage = (Stage) pathNameField.getScene().getWindow();
        stage.close();
    }

    private String getTranslate(String key) {
        return bundle.getString(key);
    }
}