package com.dev.station.entity;

/**
 * Return driver data
 */
public class DriverSettings {
    private String websiteUrl;
    private String path;

    public DriverSettings(String websiteUrl, String path) {
        this.websiteUrl = websiteUrl;
        this.path = path;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public String getPath() {
        return path;
    }
}
