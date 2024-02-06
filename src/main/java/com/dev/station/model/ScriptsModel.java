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

    public void saveProgramData(ProgramData programData, int oldCategoryId) {
        JSONArray rootArray;
        int newCategoryId = programData.getCategoryId();

        try {
            String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
            rootArray = new JSONArray(content);

            if (oldCategoryId != newCategoryId) {
                removeProgramFromOldCategory(programData.getId(), oldCategoryId);

                content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
                rootArray = new JSONArray(content);
            }

        } catch (IOException e) {
            rootArray = new JSONArray();
        }

        JSONObject categoryJson = null;
        for (int i = 0; i < rootArray.length(); i++) {
            JSONObject currentCategory = rootArray.getJSONObject(i);

            if (currentCategory.getInt("categoryId") == newCategoryId) {
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
            int existingProgramId = existingProgram.optInt("id", -1);
            if (existingProgramId != -1 && existingProgramId == programData.getId()) {
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
            int currentId = program.optInt("id", 0);
            if (currentId > id) {
                id = currentId;
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

                    int id = programJson.optInt("id", -1);
                    String programName = programJson.optString("name", "Unnamed");
                    String programPath = programJson.optString("path", "");
                    String programExtension = programJson.optString("extension", "");
                    String description = programJson.optString("description", "");
                    String action = programJson.optString("action", "run");

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

    /**
     * Deleting program from json
     * @param programId
     * @param categoryId
     */
    public void deleteProgram(int programId, int categoryId) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
            JSONArray categoriesArray = new JSONArray(content);

            for (int i = 0; i < categoriesArray.length(); i++) {
                JSONObject category = categoriesArray.getJSONObject(i);
                if (category.getInt("categoryId") == categoryId) {
                    JSONArray programsArray = category.getJSONArray("programs");
                    for (int j = 0; j < programsArray.length(); j++) {
                        JSONObject program = programsArray.getJSONObject(j);
                        if (program.getInt("id") == programId) {
                            programsArray.remove(j);
                            break;
                        }
                    }
                    break;
                }
            }

            Files.write(Paths.get(JSON_FILE_PATH), categoriesArray.toString(4).getBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Error", "Delete error. Contact the developer.");
        }
    }

    /**
     * Rename category
     * @param categoryId
     * @param newName
     */
    public void renameCategory(int categoryId, String newName) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
            JSONArray categoriesArray = new JSONArray(content);

            for (int i = 0; i < categoriesArray.length(); i++) {
                JSONObject category = categoriesArray.getJSONObject(i);
                if (category.getInt("categoryId") == categoryId) {
                    category.put("categoryName", newName);
                    break;
                }
            }

            Files.write(Paths.get(JSON_FILE_PATH), categoriesArray.toString(4).getBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Error", "Error saving changes.");
        }
    }

    public void checkAndDeleteCategory(CategoryData category) {
        JSONArray categoriesArray;
        try {
            String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
            categoriesArray = new JSONArray(content);

            for (int i = 0; i < categoriesArray.length(); i++) {
                JSONObject currentCategory = categoriesArray.getJSONObject(i);
                if (currentCategory.getInt("categoryId") == category.getId()) {
                    JSONArray programsArray = currentCategory.getJSONArray("programs");
                    if (programsArray.length() > 0) {
                        AlertUtils.showErrorAlert("Error", "First, remove all programs in the category.");
                        return;
                    } else {
                        categoriesArray.remove(i);
                        break;
                    }
                }
            }

            Files.write(Paths.get(JSON_FILE_PATH), categoriesArray.toString(4).getBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Error", "Error when deleting a category.");
        }
    }

    public void removeProgramFromOldCategory(int programId, int oldCategoryId) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
            JSONArray categoriesArray = new JSONArray(content);

            for (int i = 0; i < categoriesArray.length(); i++) {
                JSONObject category = categoriesArray.getJSONObject(i);
                if (category.getInt("categoryId") == oldCategoryId) {
                    JSONArray programsArray = category.getJSONArray("programs");
                    for (int j = 0; j < programsArray.length(); j++) {
                        JSONObject program = programsArray.getJSONObject(j);
                        if (program.getInt("id") == programId) {
                            programsArray.remove(j);
                            break;
                        }
                    }
                    break;
                }
            }

            Files.write(Paths.get(JSON_FILE_PATH), categoriesArray.toString(4).getBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}