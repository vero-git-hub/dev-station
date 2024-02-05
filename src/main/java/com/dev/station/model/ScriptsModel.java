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

    public void saveProgramData(ProgramData programData, int categoryId) {
        JSONArray rootArray;

        try {
            String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
            rootArray = new JSONArray(content);
        } catch (IOException e) {
            rootArray = new JSONArray();
        }

        JSONObject categoryJson = null;
        for (int i = 0; i < rootArray.length(); i++) {
            JSONObject currentCategory = rootArray.getJSONObject(i);
            if (currentCategory.getInt("categoryId") == categoryId) {
                categoryJson = currentCategory;
                break;
            }
        }

        if (categoryJson == null) {
            AlertUtils.showErrorAlert("Error", "Category not found.");
            return;
        }

        JSONArray programsArray = categoryJson.getJSONArray("programs");
        int indexToUpdate = -1;
        for (int i = 0; i < programsArray.length(); i++) {
            JSONObject existingProgram = programsArray.getJSONObject(i);
            if (existingProgram.getInt("id") == programData.getId()) {
                indexToUpdate = i;
                break;
            }
        }

        JSONObject programJson = new JSONObject();
        programJson.put("name", programData.getProgramName());
        programJson.put("path", programData.getProgramPath());
        programJson.put("extension", programData.getProgramExtension());
        programJson.put("description", programData.getDescription());
        programJson.put("action", programData.getAction());
        programJson.put("id", programData.getId());

        if (indexToUpdate >= 0) {
            programsArray.put(indexToUpdate, programJson);
        } else {
            programJson.put("id", generateUniqueId(programsArray));
            programsArray.put(programJson);
        }

        try {
            Files.write(Paths.get(JSON_FILE_PATH), rootArray.toString(4).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Error", "Save error. Contact the developer.");
        }
    }

    /**
     * Generating a unique ID for a new program
     * @param programsArray
     * @return
     */
    private int generateUniqueId(JSONArray programsArray) {
        int id = 0;
        for (int i = 0; i < programsArray.length(); i++) {
            JSONObject program = programsArray.getJSONObject(i);
            if (program.getInt("id") > id) {
                id = program.getInt("id");
            }
        }
        return id + 1;
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
        return maxId + 1;
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

                List<ProgramData> programList = new ArrayList<>();
                JSONArray programsArray = categoryJson.getJSONArray("programs");
                for (int j = 0; j < programsArray.length(); j++) {
                    JSONObject programJson = programsArray.getJSONObject(j);
                    int id = programJson.getInt("id");
                    String programName = programJson.getString("name");
                    String programPath = programJson.getString("path");
                    String programExtension = programJson.getString("extension");
                    String description = programJson.optString("description", "");
                    String action = programJson.optString("action", "");

                    ProgramData program = new ProgramData(id, programName, programPath, programExtension, description, action, categoryId);
                    programList.add(program);
                }


                CategoryData categoryData = new CategoryData(categoryName, categoryId, programList);
                categoryList.add(categoryData);
            }
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Loading error", "Failed to read category file");
        }

        return categoryList;
    }
}