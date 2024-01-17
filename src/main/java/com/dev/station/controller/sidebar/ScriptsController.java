package com.dev.station.controller.sidebar;

import com.dev.station.controller.MainController;
import com.dev.station.controller.forms.AddProgramFormController;
import com.dev.station.entity.ProcessHolder;
import com.dev.station.entity.ProgramData;
import com.dev.station.manager.LanguageManager;
import com.dev.station.manager.LaunchManager;
import com.dev.station.manager.NotificationManager;
import com.dev.station.model.ScriptsModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class ScriptsController {
    private final Preferences prefs = MainController.prefs;
    private final LaunchManager launchManager = new LaunchManager(new NotificationManager(LanguageManager.getResourceBundle()));
    private final Map<String, ProcessHolder> processHolders = new HashMap<>();

    @FXML private VBox programsContainer;
    @FXML public Button addProgramButton;
    ResourceBundle bundle;
    private ScriptsModel scriptsModel = new ScriptsModel();

    @FXML private void initialize() {
        bundle = LanguageManager.getResourceBundle();
        loadSavedPrograms();
    }

    private void loadSavedPrograms() {
        programsContainer.getChildren().clear();

        List<ProgramData> programDataList = scriptsModel.loadProgramData();

        for (ProgramData programData : programDataList) {
            addProgramButton(programData);
        }
    }

    @FXML private void handleAddProgram() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dev/station/ui/forms/AddProgramForm.fxml"));
            loader.setResources(bundle);
            Parent root = loader.load();

            AddProgramFormController addProgramFormController = loader.getController();
            addProgramFormController.setOnSave(programData -> {
                addProgramButton(programData);
                saveProgramData(programData);
            });

            Stage stage = new Stage();
            stage.setTitle(getTranslate("scriptsAdditionFormTitle"));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addProgramButton(ProgramData programData) {
        Button programButton = new Button(programData.getProgramName());
        programButton.setOnAction(e -> launchProgram(programData));
        programsContainer.getChildren().add(programButton);
    }

    private void saveProgramData(ProgramData programData) {
        scriptsModel.saveProgramData(programData);
    }

    private void launchProgram(ProgramData programData) {
        String path = programData.getProgramPath();
        String fileExtension = programData.getProgramExtension();

        ProcessHolder processHolder = processHolders.computeIfAbsent(path, k -> new ProcessHolder());

        if ("exe".equalsIgnoreCase(fileExtension)) {
            launchManager.launchApplication(path, path, processHolder);
        } else if ("jar".equalsIgnoreCase(fileExtension)) {
            launchManager.launchJarApplication(path, path, processHolder);
        }
    }

    private String getTranslate(String key) {
        return bundle.getString(key);
    }
}