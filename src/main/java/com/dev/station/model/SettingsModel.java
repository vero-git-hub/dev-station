package com.dev.station.model;

import com.dev.station.controller.header.SettingsController;
import com.dev.station.entity.DriverSettings;
import com.dev.station.entity.ImageSettings;
import com.dev.station.entity.SeleniumSettings;
import com.dev.station.util.alert.AlertUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SettingsModel {
    private static final String JSON_FILE_PATH = "ds_settings.json";
    SettingsController settingsController;
    // Cache for developer mode status
    private static Boolean developerModeCached = null;

    public SettingsModel() {
    }

    public SettingsModel(SettingsController settingsController) {
        this.settingsController = settingsController;
    }

    public void handleSaveDriverSettings(String websiteUrlText, String pathFieldText) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
            JSONArray settingsArray = new JSONArray(content);

            for (int i = 0; i < settingsArray.length(); i++) {
                JSONObject userSettings = settingsArray.getJSONObject(i);
                if (userSettings.getInt("id") == 1) {
                    JSONObject driverSettings = userSettings.getJSONObject("driverSettings");
                    driverSettings.put("url", websiteUrlText);
                    driverSettings.put("path", pathFieldText);

                    settingsArray.put(i, userSettings);
                    break;
                }
            }

            Files.write(Paths.get(JSON_FILE_PATH), settingsArray.toString(4).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DriverSettings readDriverSettings() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
            JSONArray settingsArray = new JSONArray(content);

            for (int i = 0; i < settingsArray.length(); i++) {
                JSONObject userSettings = settingsArray.getJSONObject(i);
                if (userSettings.getInt("id") == 1) {
                    JSONObject driverSettings = userSettings.getJSONObject("driverSettings");
                    String websiteUrl = driverSettings.getString("url");
                    String path = driverSettings.getString("path");

                    return new DriverSettings(websiteUrl, path);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveThemeSetting(String theme) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
            JSONArray settingsArray = new JSONArray(content);

            for (int i = 0; i < settingsArray.length(); i++) {
                JSONObject userSettings = settingsArray.getJSONObject(i);
                if (userSettings.getInt("id") == 1) {
                    userSettings.put("theme", theme);

                    settingsArray.put(i, userSettings);
                    break;
                }
            }

            Files.write(Paths.get(JSON_FILE_PATH), settingsArray.toString(4).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String loadThemeSetting() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
            JSONArray settingsArray = new JSONArray(content);

            for (int i = 0; i < settingsArray.length(); i++) {
                JSONObject userSettings = settingsArray.getJSONObject(i);
                if (userSettings.getInt("id") == 1) {
                    return userSettings.getString("theme");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "light";
    }

    public void saveLanguageSetting(String language) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
            JSONArray settingsArray = new JSONArray(content);

            for (int i = 0; i < settingsArray.length(); i++) {
                JSONObject userSettings = settingsArray.getJSONObject(i);
                if (userSettings.getInt("id") == 1) {
                    userSettings.put("language", language);

                    settingsArray.put(i, userSettings);
                    break;
                }
            }

            Files.write(Paths.get(JSON_FILE_PATH), settingsArray.toString(4).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String loadLanguageSetting() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
            JSONArray settingsArray = new JSONArray(content);

            for (int i = 0; i < settingsArray.length(); i++) {
                JSONObject userSettings = settingsArray.getJSONObject(i);
                if (userSettings.getInt("id") == 1) {
                    return userSettings.getString("language");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "EN";
    }

    public void saveImageSettingsToFile(ImageSettings imageSettings) {
        try {
            JSONArray settingsArray;
            try {
                String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
                settingsArray = new JSONArray(content);
            } catch (Exception e) {
                settingsArray = new JSONArray();
            }

            JSONObject resizeOptions = new JSONObject();
            resizeOptions.put("keepOriginalSize", imageSettings.isKeepOriginalSize());
            resizeOptions.put("width", imageSettings.getWidth());
            resizeOptions.put("height", imageSettings.getHeight());

            JSONObject imageSettingsJson = new JSONObject();
            imageSettingsJson.put("path", imageSettings.getPath());
            imageSettingsJson.put("resizeOptions", resizeOptions);

            boolean found = false;
            for (int i = 0; i < settingsArray.length(); i++) {
                JSONObject settings = settingsArray.getJSONObject(i);
                if (settings.has("imageSettings")) {
                    settings.put("imageSettings", imageSettingsJson);
                    found = true;
                    break;
                }
            }
            if (!found) {
                JSONObject newSettings = new JSONObject();
                newSettings.put("imageSettings", imageSettingsJson);
                settingsArray.put(newSettings);
            }

            Files.write(Paths.get(JSON_FILE_PATH), settingsArray.toString(4).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ImageSettings loadImageSettings() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
            JSONArray settingsArray = new JSONArray(content);

            for (int i = 0; i < settingsArray.length(); i++) {
                JSONObject settings = settingsArray.getJSONObject(i);
                if (settings.has("imageSettings")) {
                    JSONObject imageSettingsJson = settings.getJSONObject("imageSettings");
                    JSONObject resizeOptions = imageSettingsJson.getJSONObject("resizeOptions");

                    String path = imageSettingsJson.getString("path");
                    boolean keepOriginalSize = resizeOptions.getBoolean("keepOriginalSize");
                    int width = resizeOptions.getInt("width");
                    int height = resizeOptions.getInt("height");

                    return new ImageSettings(path, keepOriginalSize, width, height);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveSeleniumSettings(SeleniumSettings seleniumSettings) {
        try {
            JSONArray settingsArray;
            try {
                String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
                settingsArray = new JSONArray(content);
            } catch (Exception e) {
                settingsArray = new JSONArray();
            }

            JSONObject seleniumSettingsJson = new JSONObject();
            seleniumSettingsJson.put("pathJar", seleniumSettings.getPathJar());
            seleniumSettingsJson.put("pathExe", seleniumSettings.getPathExe());

            boolean found = false;
            for (int i = 0; i < settingsArray.length(); i++) {
                JSONObject settings = settingsArray.getJSONObject(i);
                if (settings.has("seleniumSettings")) {
                    settings.put("seleniumSettings", seleniumSettingsJson);
                    found = true;
                    break;
                }
            }

            if (!found) {
                JSONObject newSettings = new JSONObject();
                newSettings.put("seleniumSettings", seleniumSettingsJson);
                settingsArray.put(newSettings);
            }

            Files.write(Paths.get(JSON_FILE_PATH), settingsArray.toString(4).getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SeleniumSettings loadSeleniumSettings() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
            JSONArray settingsArray = new JSONArray(content);

            for (int i = 0; i < settingsArray.length(); i++) {
                JSONObject settings = settingsArray.getJSONObject(i);
                if (settings.has("seleniumSettings")) {
                    JSONObject seleniumSettingsJson = settings.getJSONObject("seleniumSettings");
                    String pathJar = seleniumSettingsJson.optString("pathJar", "");
                    String pathExe = seleniumSettingsJson.optString("pathExe", "");
                    return new SeleniumSettings(pathJar, pathExe);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Saving developer mode settings and updating the cache
     * @param isEnabled The new state of developer mode to be saved
     */
    public void saveDeveloperModeSetting(boolean isEnabled) {
        try {
            JSONArray settingsArray;
            // Load an existing JSON file
            try {
                String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
                settingsArray = new JSONArray(content);
            } catch (Exception e) {
                settingsArray = new JSONArray();
            }

            boolean found = false;
            for (int i = 0; i < settingsArray.length(); i++) {
                JSONObject settings = settingsArray.getJSONObject(i);
                if (settings.getInt("id") == 1) {
                    // Update developer mode settings
                    settings.put("developerMode", isEnabled);
                    found = true;
                    break;
                }
            }

            if (!found) {
                // If settings for id=1 are not found, create a new settings object
                JSONObject newSettings = new JSONObject();
                newSettings.put("id", 1);
                newSettings.put("developerMode", isEnabled);
                settingsArray.put(newSettings);
            }

            // Save updated JSON back to file
            Files.write(Paths.get(JSON_FILE_PATH), settingsArray.toString(4).getBytes(StandardCharsets.UTF_8));

            // Update the cache with the new value
            developerModeCached = isEnabled;

            AlertUtils.showSuccessAlert("", settingsController.getTranslate("successSaveSettings"));
        } catch (IOException e) {
            AlertUtils.showErrorAlert("", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loading the developer mode state and caching its value.
     *
     * @return true if developer mode is enabled, false otherwise.
     */
    public static boolean loadDeveloperModeSetting() {
        if (developerModeCached == null) {
            try {
                // Load the contents of the JSON file
                String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
                JSONArray settingsArray = new JSONArray(content);

                // Search for settings for user with id=1
                for (int i = 0; i < settingsArray.length(); i++) {
                    JSONObject settings = settingsArray.getJSONObject(i);
                    if (settings.getInt("id") == 1) {
                        // Return the developer mode state if the setting is found
                        return settings.optBoolean("developerMode", false); // Return false by default if the key is missing
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                // Set default value in case of error
                developerModeCached = false;
            }
        }

        // In case of any errors or if settings for id=1 are not found, return false
        return developerModeCached;
    }

}