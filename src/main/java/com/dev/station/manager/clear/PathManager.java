package com.dev.station.manager.clear;

import com.dev.station.controller.tab.TabController;
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
    NotificationManager notificationManager;

    public PathManager(Preferences prefs, NotificationManager notificationManager) {
        this.prefs = prefs;
        this.pathsList = FXCollections.observableArrayList();
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
}