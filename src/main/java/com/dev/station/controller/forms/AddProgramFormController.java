package com.dev.station.controller.forms;

import com.dev.station.manager.LanguageManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ResourceBundle;

public class AddProgramFormController {
    @FXML public Label programNameLabel;
    @FXML public Label pathToExecutableLabel;
    @FXML public Button saveButton;
    @FXML public Button cancelButton;
    @FXML private TextField programNameField;
    @FXML private TextField programPathField;
    ResourceBundle bundle;

    @FXML private void initialize() {
        bundle = LanguageManager.getResourceBundle();
        programNameLabel.setText(getTranslate("scriptsProgramNameLabel"));
        pathToExecutableLabel.setText(getTranslate("scriptsPathToExecutableLabel"));
        saveButton.setText(getTranslate("scriptsSaveButton"));
        cancelButton.setText(getTranslate("scriptsCancelButton"));
    }

    @FXML private void handleSave(ActionEvent event) {
    }

    @FXML private void handleCancel(ActionEvent event) {
    }

    private String getTranslate(String key) {
        return bundle.getString(key);
    }
}