package com.dev.station.controller.sidebar;

import com.dev.station.manager.LanguageManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class ScriptsController {
    @FXML public Button addProgramButton;
    ResourceBundle bundle;

    @FXML private void initialize() {
        bundle = LanguageManager.getResourceBundle();
    }

    @FXML private void handleAddProgram() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/forms/AddProgramForm.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(getTranslate("scriptsAdditionFormTitle"));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getTranslate(String key) {
        return bundle.getString(key);
    }
}