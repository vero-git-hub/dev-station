package com.dev.station.controller.forms;

import com.dev.station.controller.MainController;
import com.dev.station.entity.PathData;
import com.dev.station.manager.LanguageManager;
import com.dev.station.manager.clear.PathManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

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

    public void setPathManager(PathManager pathManager) {
        this.pathManager = pathManager;
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
        pathManager.savePath(pathNameField, directoryPathField, exclusionsField);

        if (dataSavedListener != null) {
            dataSavedListener.onDataSaved();
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