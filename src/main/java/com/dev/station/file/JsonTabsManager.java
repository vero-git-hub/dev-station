package com.dev.station.file;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonTabsManager {
    private static final String FILE_PATH = "tabs_config.json";

    public List<TabData> loadTabs(int userId, String screenType) {
        List<TabData> tabs = new ArrayList<>();
        try {
            File file = new File(FILE_PATH);
            if (file.exists()) {
                String content = new String(Files.readAllBytes(file.toPath()));
                JSONArray configArray = new JSONArray(content);

                for (int i = 0; i < configArray.length(); i++) {
                    JSONObject configObject = configArray.getJSONObject(i);
                    if (configObject.getInt("userId") == userId && configObject.getString("screenType").equals(screenType)) {
                        JSONArray tabsArray = configObject.getJSONArray("tabs");
                        for (int j = 0; j < tabsArray.length(); j++) {
                            tabs.add(TabData.fromJson(tabsArray.getJSONObject(j)));
                        }
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tabs;
    }

    public boolean saveTabs(int userId, String screenType, List<TabData> tabs) {
        try {
            File file = new File(FILE_PATH);
            JSONArray configArray = file.exists() ? new JSONArray(new String(Files.readAllBytes(file.toPath()))) : new JSONArray();

            boolean found = false;
            for (int i = 0; i < configArray.length(); i++) {
                JSONObject configObject = configArray.getJSONObject(i);
                if (configObject.getInt("userId") == userId && configObject.getString("screenType").equals(screenType)) {
                    JSONArray tabsArray = new JSONArray();
                    for (TabData tab : tabs) {
                        tabsArray.put(tab.toJson());
                    }
                    configObject.put("tabs", tabsArray);
                    found = true;
                    break;
                }
            }

            if (!found) {
                JSONObject newConfigObject = new JSONObject();
                newConfigObject.put("userId", userId);
                newConfigObject.put("screenType", screenType);
                JSONArray tabsArray = new JSONArray();
                for (TabData tab : tabs) {
                    tabsArray.put(tab.toJson());
                }
                newConfigObject.put("tabs", tabsArray);
                configArray.put(newConfigObject);
            }

            String jsonString = configArray.toString(4);
            Files.write(Paths.get(FILE_PATH), jsonString.getBytes());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}