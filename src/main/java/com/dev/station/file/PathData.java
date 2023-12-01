package com.dev.station.file;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PathData {
    private String name;
    private String path;
    private List<String> exclusions;

    public PathData() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getExclusions() {
        return exclusions;
    }

    public void setExclusions(List<String> exclusions) {
        this.exclusions = exclusions;
    }

    public JSONObject toJson() {
        JSONObject pathJson = new JSONObject();
        pathJson.put("name", name);
        pathJson.put("path", path);
        pathJson.put("exclusions", new JSONArray(exclusions));
        return pathJson;
    }

    public static PathData fromJson(JSONObject jsonObject) {
        PathData path = new PathData();
        path.setName(jsonObject.getString("name"));
        path.setPath(jsonObject.getString("path"));
        JSONArray exclusionsArray = jsonObject.getJSONArray("exclusions");

        List<String> exclusions = new ArrayList<>();
        for (int i = 0; i < exclusionsArray.length(); i++) {
            exclusions.add(exclusionsArray.getString(i));
        }
        path.setExclusions(exclusions);

        return path;
    }
}