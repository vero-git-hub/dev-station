package com.dev.station.controller.forms;

import com.dev.station.entity.ProgramData;
import com.dev.station.manager.LanguageManager;
import com.dev.station.util.AlertUtils;
import com.dev.station.util.FileUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ResourceBundle;
import java.util.function.Consumer;

public class AddProgramFormController {
    @FXML public Label programNameLabel;
    @FXML public Label pathToExecutableLabel;
    @FXML public Button saveButton;
    @FXML public Button cancelButton;
    @FXML private TextField programNameField;
    @FXML private TextField programPathField;
    ResourceBundle bundle;
    private Consumer<ProgramData> onSave;

    @FXML private void initialize() {
        bundle = LanguageManager.getResourceBundle();
        programNameLabel.setText(getTranslate("scriptsProgramNameLabel"));
        pathToExecutableLabel.setText(getTranslate("scriptsPathToExecutableLabel"));
        saveButton.setText(getTranslate("saveButton"));
        cancelButton.setText(getTranslate("cancelButton"));
    }

    public void setOnSave(Consumer<ProgramData> onSave) {
        this.onSave = onSave;
    }

    @FXML private void handleSave(ActionEvent event) {
//        String programName = programNameField.getText().trim();
//        String programPath = programPathField.getText().trim();
//
//        if(!programName.isEmpty() && !programPath.isEmpty()) {
//            String fileExtension = FileUtils.getFileExtension(programPath);
//
//            if(fileExtension.equalsIgnoreCase("exe") || fileExtension.equalsIgnoreCase("jar")) {
//                ProgramData programData = new ProgramData(programName, programPath, fileExtension);
//                if (onSave != null) {
//                    onSave.accept(programData);
//                    closeStage(event);
//                }
//            } else {
//                AlertUtils.showErrorAlert("Error file extension", "Only the extension exe or jar is allowed.");
//            }
//        }
    }

    private void closeStage(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML private void handleCancel(ActionEvent event) {
        closeStage(event);
    }

    private String getTranslate(String key) {
        return bundle.getString(key);
    }
}