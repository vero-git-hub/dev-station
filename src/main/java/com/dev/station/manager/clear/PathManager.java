package com.dev.station.manager.clear;

import com.dev.station.controller.tab.TabController;
import com.dev.station.entity.PathData;
import com.dev.station.manager.NotificationManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;
import java.util.prefs.Preferences;

public class PathManager {
    private final Preferences prefs;
    private final ObservableList<PathData> pathsList;
    NotificationManager notificationManager;
    private TabController tabController;

    public PathManager(Preferences prefs, TabController tabController, NotificationManager notificationManager) {
        this.prefs = prefs;
        this.pathsList = FXCollections.observableArrayList();
        this.tabController = tabController;
        this.notificationManager = notificationManager;
    }

    public void loadPaths() {
        String savedPathsJson = prefs.get("savedPaths", "[]");
        JSONArray pathsArray = new JSONArray(savedPathsJson);

        pathsList.clear();

        for (int i = 0; i < pathsArray.length(); i++) {
            JSONObject pathJson = pathsArray.getJSONObject(i);
            String name = pathJson.getString("name");
            String path = pathJson.getString("path");
            String exclusions = pathJson.getString("exclusions");

            PathData pathData = new PathData(name, path, exclusions);
            pathsList.add(pathData);
        }
    }

    public void loadPaths(String tabId) {
        String savedPathsJson = prefs.get("savedPaths", "[]");
        JSONArray pathsArray = new JSONArray(savedPathsJson);

        pathsList.clear();

        for (int i = 0; i < pathsArray.length(); i++) {
            JSONObject pathJson = pathsArray.getJSONObject(i);
            String currentTabId = pathJson.optString("tabId", "");

            if (currentTabId.equals(tabId)) {
                String name = pathJson.getString("name");
                String path = pathJson.getString("path");
                String exclusions = pathJson.getString("exclusions");

                PathData pathData = new PathData(name, path, exclusions);
                pathsList.add(pathData);
            }
        }
    }

    public void savePath(TextField pathNameField, TextField directoryPathField, TextField exclusionsField) {
        String pathName = pathNameField.getText().trim();
        String directoryPath = directoryPathField.getText().trim();
        String exclusions = exclusionsField.getText().trim();

        if (pathName.isEmpty() || directoryPath.isEmpty()) {
            return;
        }

        String pathId = UUID.randomUUID().toString();

        PathData pathData = new PathData(pathName, directoryPath, exclusions);

        JSONObject pathJson = new JSONObject();
        pathJson.put("id", pathId);
        pathJson.put("name", pathData.getName());
        pathJson.put("path", pathData.getPath());
        pathJson.put("exclusions", pathData.getExclusions());

        String savedPathsJson = prefs.get("savedPaths", "[]");
        JSONArray pathsArray = new JSONArray(savedPathsJson);

        pathsArray.put(pathJson);

        prefs.put("savedPaths", pathsArray.toString());
    }

    public ObservableList<PathData> getPathsList() {
        return pathsList;
    }
}