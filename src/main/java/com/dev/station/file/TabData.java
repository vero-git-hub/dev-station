package com.dev.station.file;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TabData {
    private String id;
    private String name;
    private String recycleBinPath;
    private List<PathData> paths;

    public TabData() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRecycleBinPath() {
        return recycleBinPath;
    }

    public void setRecycleBinPath(String recycleBinPath) {
        this.recycleBinPath = recycleBinPath;
    }

    public List<PathData> getPaths() {
        return paths;
    }

    public void setPaths(List<PathData> paths) {
        this.paths = paths;
    }

    public JSONObject toJson() {
        JSONObject tabJson = new JSONObject();
        tabJson.put("id", id);
        tabJson.put("name", name);
        tabJson.put("recycleBinPath", recycleBinPath);

        JSONArray pathsArray = new JSONArray();
        for (PathData path : paths) {
            pathsArray.put(path.toJson());
        }
        tabJson.put("paths", pathsArray);

        return tabJson;
    }

    public static TabData fromJson(JSONObject jsonObject) {
        TabData tab = new TabData();
        tab.setId(jsonObject.getString("id"));
        tab.setName(jsonObject.getString("name"));
        tab.setRecycleBinPath(jsonObject.getString("recycleBinPath"));

        JSONArray pathsArray = jsonObject.getJSONArray("paths");
        List<PathData> paths = new ArrayList<>();
        for (int i = 0; i < pathsArray.length(); i++) {
            paths.add(PathData.fromJson(pathsArray.getJSONObject(i)));
        }
        tab.setPaths(paths);

        return tab;
    }
}