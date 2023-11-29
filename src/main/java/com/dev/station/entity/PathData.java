package com.dev.station.entity;

import javafx.beans.property.SimpleStringProperty;

public class PathData {
    private final SimpleStringProperty name;
    private final SimpleStringProperty path;
    private final SimpleStringProperty exclusions;

    public PathData(String name, String path, String exclusions) {
        this.name = new SimpleStringProperty(name);
        this.path = new SimpleStringProperty(path);
        this.exclusions = new SimpleStringProperty(exclusions);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getPath() {
        return path.get();
    }

    public SimpleStringProperty pathProperty() {
        return path;
    }

    public void setPath(String path) {
        this.path.set(path);
    }

    public String getExclusions() {
        return exclusions.get();
    }

    public SimpleStringProperty exclusionsProperty() {
        return exclusions;
    }

    public void setExclusions(String exclusions) {
        this.exclusions.set(exclusions);
    }
}