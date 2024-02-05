package com.dev.station.entity;

public class ProgramData {
    private int id;
    private String programName;
    private String programPath;
    private String programExtension;
    private String description;
    private String action;
    private int categoryId;

    public ProgramData(int id, String programName, String programPath, String programExtension, String description, String action, int categoryId) {
        this.id = id;
        this.programName = programName;
        this.programPath = programPath;
        this.programExtension = programExtension;
        this.description = description;
        this.action = action;
        this.categoryId = categoryId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getProgramExtension() {
        return programExtension;
    }

    public void setProgramExtension(String programExtension) {
        this.programExtension = programExtension;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}