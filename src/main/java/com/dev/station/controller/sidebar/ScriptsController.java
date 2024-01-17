package com.dev.station.controller.sidebar;

import com.dev.station.controller.MainController;
import com.dev.station.controller.forms.AddProgramFormController;
import com.dev.station.entity.ProcessHolder;
import com.dev.station.entity.ProgramData;
import com.dev.station.manager.LanguageManager;
import com.dev.station.manager.LaunchManager;
import com.dev.station.manager.NotificationManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import org.json.JSONObject;
import org.json.JSONArray;

public class ScriptsController {
    private final Preferences prefs = MainController.prefs;
    private final LaunchManager launchManager = new LaunchManager(new NotificationManager(LanguageManager.getResourceBundle()));
    private final Map<String, ProcessHolder> processHolders = new HashMap<>();

    @FXML private VBox programsContainer;
    @FXML public Button addProgramButton;
    ResourceBundle bundle;

    @FXML private void initialize() {
        bundle = LanguageManager.getResourceBundle();
        loadSavedPrograms();
    }

    private void loadSavedPrograms() {
        programsContainer.getChildren().clear();

        String savedProgramsJson = prefs.get("savedPrograms", "[]");
        JSONArray programsArray = new JSONArray(savedProgramsJson);

        for (int i = 0; i < programsArray.length(); i++) {
            JSONObject programJson = programsArray.getJSONObject(i);
            String name = programJson.getString("name");
            String path = programJson.getString("path");
            String category = programJson.getString("category");

            ProgramData programData = new ProgramData(name, path, category);
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
        String savedProgramsJson = prefs.get("savedPrograms", "[]");
        JSONArray programsArray = new JSONArray(savedProgramsJson);

        JSONObject programJson = new JSONObject();
        programJson.put("name", programData.getProgramName());
        programJson.put("path", programData.getProgramPath());
        programJson.put("category", programData.getCategory());

        programsArray.put(programJson);

        prefs.put("savedPrograms", programsArray.toString());
    }

    private void launchProgram(ProgramData programData) {
        String path = programData.getProgramPath();
        String category = programData.getCategory();

        ProcessHolder processHolder = processHolders.computeIfAbsent(path, k -> new ProcessHolder());

        if ("EXE".equals(category)) {
            launchManager.launchApplication(path, path, processHolder);
        } else if ("JAR".equals(category)) {
            launchManager.launchJarApplication(path, path, processHolder);
        }
    }

    private String getTranslate(String key) {
        return bundle.getString(key);
    }
}