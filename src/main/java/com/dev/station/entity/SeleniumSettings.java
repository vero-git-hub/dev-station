package com.dev.station.entity;

public class SeleniumSettings {
    private String pathJar;
    private String pathExe;

    public SeleniumSettings(String pathJar, String pathExe) {
        this.pathJar = pathJar;
        this.pathExe = pathExe;
    }

    public String getPathJar() {
        return pathJar;
    }

    public void setPathJar(String pathJar) {
        this.pathJar = pathJar;
    }

    public String getPathExe() {
        return pathExe;
    }

    public void setPathExe(String pathExe) {
        this.pathExe = pathExe;
    }
}
