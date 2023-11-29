package com.dev.station.entity;

public class ProgramData {
    private String programName;
    private String programPath;
    private String category;

    public ProgramData(String programName, String programPath, String category) {
        this.programName = programName;
        this.programPath = programPath;
        this.category = category;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getProgramPath() {
        return programPath;
    }

    public void setProgramPath(String programPath) {
        this.programPath = programPath;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}