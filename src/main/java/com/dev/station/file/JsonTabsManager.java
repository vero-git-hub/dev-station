package com.dev.station.file;

import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonTabsManager {
    private static final String FILE_PATH = "tabs_config.json";

    public List<TabData> loadTabs() {
        try {
            File file = new File(FILE_PATH);
            if (file.exists()) {
                String content = new String(Files.readAllBytes(file.toPath()));
                JSONArray tabsArray = new JSONArray(content);

                List<TabData> tabs = new ArrayList<>();
                for (int i = 0; i < tabsArray.length(); i++) {
                    tabs.add(TabData.fromJson(tabsArray.getJSONObject(i)));
                }
                return tabs;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void saveTabs(List<TabData> tabs) {
        try {
            JSONArray tabsArray = new JSONArray();
            for (TabData tab : tabs) {
                tabsArray.put(tab.toJson());
            }
            String jsonString = tabsArray.toString(4);
            Files.write(Paths.get(FILE_PATH), jsonString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}