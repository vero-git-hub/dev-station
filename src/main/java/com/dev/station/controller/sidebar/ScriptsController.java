package com.dev.station.controller.sidebar;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ScriptsController {
    @FXML public Button addProgramButton;

    @FXML
    private void handleAddProgram() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/forms/AddProgramForm.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add Program");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}