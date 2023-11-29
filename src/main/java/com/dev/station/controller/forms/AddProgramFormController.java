package com.dev.station.controller.forms;

import com.dev.station.entity.ProgramData;
import com.dev.station.manager.LanguageManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ResourceBundle;
import java.util.function.Consumer;

public class AddProgramFormController {
    @FXML public Label programNameLabel;
    @FXML public Label pathToExecutableLabel;
    @FXML public Button saveButton;
    @FXML public Button cancelButton;
    public Label categoryComboBoxLabel;
    @FXML private TextField programNameField;
    @FXML private TextField programPathField;
    @FXML private ComboBox<String> categoryComboBox;
    ResourceBundle bundle;
    private Consumer<ProgramData> onSave;

    @FXML private void initialize() {
        bundle = LanguageManager.getResourceBundle();
        programNameLabel.setText(getTranslate("scriptsProgramNameLabel"));
        pathToExecutableLabel.setText(getTranslate("scriptsPathToExecutableLabel"));
        saveButton.setText(getTranslate("scriptsSaveButton"));
        cancelButton.setText(getTranslate("scriptsCancelButton"));
        categoryComboBoxLabel.setText(getTranslate("scriptsCategoryComboBoxLabel"));

        categoryComboBox.getItems().addAll("EXE", "JAR");
        categoryComboBox.setValue("EXE");
    }

    public void setOnSave(Consumer<ProgramData> onSave) {
        this.onSave = onSave;
    }

    @FXML private void handleSave(ActionEvent event) {
        String programName = programNameField.getText();
        String programPath = programPathField.getText();
        String category = categoryComboBox.getValue();

        ProgramData programData = new ProgramData(programName, programPath, category);
        if (onSave != null) {
            onSave.accept(programData);
        }
    }

    @FXML private void handleCancel(ActionEvent event) {
    }

    private String getTranslate(String key) {
        return bundle.getString(key);
    }
}