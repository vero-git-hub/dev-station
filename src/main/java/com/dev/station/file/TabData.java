package com.dev.station.file;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TabData {
    private String id;
    private String name;
    private String recycleBinPath;
    private List<PathData> paths;
    private boolean isDefault;

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

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public JSONObject toJson() {
        Map<String, Object> orderedJsonMap = new LinkedHashMap<>();
        orderedJsonMap.put("id", id);
        orderedJsonMap.put("name", name);
        orderedJsonMap.put("recycleBinPath", recycleBinPath);

        JSONArray pathsArray = new JSONArray();
        for (PathData path : paths) {
            pathsArray.put(path.toJson());
        }
        orderedJsonMap.put("paths", pathsArray);
        orderedJsonMap.put("isDefault", isDefault);

        return new JSONObject(orderedJsonMap);
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
        tab.setDefault(jsonObject.optBoolean("isDefault", false));

        return tab;
    }
}