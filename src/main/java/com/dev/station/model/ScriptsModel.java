package com.dev.station.model;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public List<ProgramData> loadProgramData() {
        List<ProgramData> programList = new ArrayList<>();

        try {
            String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
            JSONArray programsArray = new JSONArray(content);

            for (int i = 0; i < programsArray.length(); i++) {
                JSONObject programJson = programsArray.getJSONObject(i);
                String name = programJson.getString("name");
                String path = programJson.getString("path");
                String extension = programJson.getString("extension");

                ProgramData programData = new ProgramData(name, path, extension);
                programList.add(programData);
            }
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Loading error", "Failed to read program file");
        }

        return programList;
    }
}