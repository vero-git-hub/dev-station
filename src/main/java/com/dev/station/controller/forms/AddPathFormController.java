package com.dev.station.controller.forms;

import com.dev.station.manager.LanguageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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

    @FXML private void initialize() {
        bundle = LanguageManager.getResourceBundle();
        pathNameLabel.setText(getTranslate("addPathFormNameLabel"));
        pathToDirectoryLabel.setText(getTranslate("addPathFormDirectoryLabel"));
        exclusionsLabel.setText(getTranslate("addPathFormExclusionsLabel"));
        saveButton.setText(getTranslate("saveButton"));
        cancelButton.setText(getTranslate("cancelButton"));
    }

    @FXML private void handleSave() {
        closeStage();
    }

    @FXML private void handleCancel() {
        closeStage();
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