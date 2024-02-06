package com.dev.station.model;

import com.dev.station.entity.DriverSettings;
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class SettingsModel {
    private static final String JSON_FILE_PATH = "settings.json";

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

}
