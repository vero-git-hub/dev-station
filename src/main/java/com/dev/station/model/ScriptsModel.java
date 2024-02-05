package com.dev.station.model;

import com.dev.station.entity.CategoryData;
import com.dev.station.entity.ProgramData;
import com.dev.station.util.AlertUtils;
import javafx.event.ActionEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * Loading programs from json
     * @return
     */
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

    public void handleSaveCategory(ActionEvent event, String categoryName) {
        if (!categoryName.isEmpty()) {
            try {
                String jsonFilePath = JSON_FILE_PATH;

                String content = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
                JSONArray categoriesArray = new JSONArray(content);

                JSONObject newCategory = new JSONObject();
                newCategory.put("categoryId", getNextCategoryId(categoriesArray));
                newCategory.put("categoryName", categoryName);
                newCategory.put("programs", new JSONArray());

                categoriesArray.put(newCategory);

                Files.write(Paths.get(jsonFilePath), categoriesArray.toString(4).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            } catch (IOException e) {
                e.printStackTrace();
                AlertUtils.showErrorAlert("Error adding category", "");
            }
        }
    }

    private int getNextCategoryId(JSONArray categoriesArray) {
        int maxId = 0;
        for (int i = 0; i < categoriesArray.length(); i++) {
            JSONObject category = categoriesArray.getJSONObject(i);
            int categoryId = category.getInt("categoryId");
            if (categoryId > maxId) {
                maxId = categoryId;
            }
        }
        return maxId + 1; // Возвращаем следующий доступный id
    }

    /**
     * Loading category name from json
     * @return
     */
    public List<CategoryData> loadCategoryData() {
        List<CategoryData> categoryList = new ArrayList<>();

        try {
            String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
            JSONArray categoriesArray = new JSONArray(content);

            for (int i = 0; i < categoriesArray.length(); i++) {
                JSONObject categoryJson = categoriesArray.getJSONObject(i);
                String categoryName = categoryJson.getString("categoryName");
                int categoryId = categoryJson.getInt("categoryId");

                CategoryData categoryData = new CategoryData(categoryName, categoryId);
                categoryList.add(categoryData);
            }
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Loading error", "Failed to read category file");
        }

        return categoryList;
    }
}