package com.dev.station.controller.forms;

import com.dev.station.controller.MainController;
import com.dev.station.controller.tab.TabController;
import com.dev.station.file.JsonTabsManager;
import com.dev.station.file.TabData;
import com.dev.station.file.PathData;
import com.dev.station.manager.LanguageManager;
import com.dev.station.manager.clear.PathManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

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
    private final Preferences prefs = MainController.prefs;
    private DataSavedListener dataSavedListener;
    private PathManager pathManager;
    private String tabId;
    private TabController tabController;

    public void setPathManager(PathManager pathManager) {
        this.pathManager = pathManager;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public void setTabController(TabController tabController) {
        this.tabController = tabController;
    }

    @FXML private void initialize() {
        bundle = LanguageManager.getResourceBundle();
        pathNameLabel.setText(getTranslate("addPathFormNameLabel"));
        pathToDirectoryLabel.setText(getTranslate("addPathFormDirectoryLabel"));
        exclusionsLabel.setText(getTranslate("addPathFormExclusionsLabel"));
        saveButton.setText(getTranslate("saveButton"));
        cancelButton.setText(getTranslate("cancelButton"));
    }

    @FXML
    private void handleSave() {
        String pathName = pathNameField.getText().trim();
        String directoryPath = directoryPathField.getText().trim();
        String exclusionsString = exclusionsField.getText().trim();

        if (!pathName.isEmpty() && !directoryPath.isEmpty()) {
            List<String> exclusions = Arrays.asList(exclusionsString.split("\\s*,\\s*"));
            PathData newPath = new PathData(pathName, directoryPath, exclusions);

            JsonTabsManager jsonTabsManager = new JsonTabsManager();
            List<TabData> tabs = jsonTabsManager.loadTabs();

            for (TabData tab : tabs) {
                if (tab.getId().equals(this.tabId)) {
                    tab.getPaths().add(newPath);
                    break;
                }
            }

            jsonTabsManager.saveTabs(tabs);
        }

        if (dataSavedListener != null) {
            dataSavedListener.onDataSaved();
        }

        if (tabController != null) {
            tabController.updatePathsTable();
        }

        closeStage();

        if (onSave != null) {
            onSave.run();
        }
    }


    @FXML private void handleCancel() {
        closeStage();
    }

    public void setDataSavedListener(DataSavedListener listener) {
        this.dataSavedListener = listener;
    }

    private void closeStage() {
        Stage stage = (Stage) pathNameField.getScene().getWindow();
        stage.close();
    }

    public void setOnSave(Runnable onSave) {
        this.onSave = onSave;
    }

    private String getTranslate(String key) {
        return bundle.getString(key);
    }
}