package com.dev.station.model;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;

import com.dev.station.entity.ProgramData;
import com.dev.station.util.AlertUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class ScriptsModel {
    private static final String JSON_FILE_PATH = "programs.json";

    public void saveProgramData(ProgramData programData) {
        JSONArray programsArray;

        try {
            String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
            programsArray = new JSONArray(content);
        } catch (IOException e) {
            programsArray = new JSONArray();
        }

        JSONObject programJson = new JSONObject();
        programJson.put("name", programData.getProgramName());
        programJson.put("path", programData.getProgramPath());
        programJson.put("extension", programData.getProgramExtension());

        programsArray.put(programJson);

        try {
            Files.write(Paths.get(JSON_FILE_PATH), programsArray.toString(4).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Error", "Save error. Contact the developer.");
        }
    }
}