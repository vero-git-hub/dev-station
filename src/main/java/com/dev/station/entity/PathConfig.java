package com.dev.station.entity;

import java.util.Set;

public class PathConfig {
    private String path;
    private Set<String> exclusions;

    public PathConfig() {

    }

    public PathConfig(String path, Set<String> exclusions) {
        this.path = path;
        this.exclusions = exclusions;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Set<String> getExclusions() {
        return exclusions;
    }

    public void setExclusions(Set<String> exclusions) {
        this.exclusions = exclusions;
    }
}
