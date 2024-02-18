package com.dev.station.entity;

public class ImageSettings {
    private String path;
    private boolean keepOriginalSize;
    private int width;
    private int height;

    public ImageSettings(String path, boolean keepOriginalSize, int width, int height) {
        this.path = path;
        this.keepOriginalSize = keepOriginalSize;
        this.width = width;
        this.height = height;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isKeepOriginalSize() {
        return keepOriginalSize;
    }

    public void setKeepOriginalSize(boolean keepOriginalSize) {
        this.keepOriginalSize = keepOriginalSize;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
