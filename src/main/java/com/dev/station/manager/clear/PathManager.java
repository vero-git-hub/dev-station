package com.dev.station.manager.clear;

import com.dev.station.controller.sidebar.ClearController;
import com.dev.station.entity.PathData;
import com.dev.station.manager.NotificationManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.prefs.Preferences;

public class PathManager {
    private final Preferences prefs;
    private final ObservableList<PathData> pathsList;
    ClearController clearController;
    NotificationManager notificationManager;

    public PathManager(Preferences prefs, ClearController clearController, NotificationManager notificationManager) {
        this.prefs = prefs;
        this.pathsList = FXCollections.observableArrayList();
        this.clearController = clearController;
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

    public void savePath(PathData pathData) {
        JSONObject pathJson = new JSONObject();
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
