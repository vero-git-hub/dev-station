package com.dev.station.manager.clear;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PathData {
    private String id;
    private String name;
    private String path;
    private List<String> exclusions;

    public PathData() {}

    public PathData(String name, String path, List<String> exclusions) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.path = path;
        this.exclusions = exclusions;
    }

    public PathData(String id, String name, String path, List<String> exclusions) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.exclusions = exclusions;
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
        pathJson.put("id", id);
        pathJson.put("name", name);
        pathJson.put("path", path);
        pathJson.put("exclusions", new JSONArray(exclusions));
        return pathJson;
    }

    public static PathData fromJson(JSONObject jsonObject) {
        String id = jsonObject.optString("id", UUID.randomUUID().toString());
        String name = jsonObject.getString("name");
        String path = jsonObject.getString("path");
        JSONArray exclusionsArray = jsonObject.getJSONArray("exclusions");

        List<String> exclusions = new ArrayList<>();
        for (int i = 0; i < exclusionsArray.length(); i++) {
            exclusions.add(exclusionsArray.getString(i));
        }

        return new PathData(id, name, path, exclusions);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PathData pathData = (PathData) o;

        return id.equals(pathData.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "PathData{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", exclusions=" + exclusions +
                '}';
    }
}