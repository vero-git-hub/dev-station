package com.dev.station.entity;

public class ProgramData {
    private String programName;
    private String programPath;
    private String programExtension;

    public ProgramData(String programName, String programPath, String programExtension) {
        this.programName = programName;
        this.programPath = programPath;
        this.programExtension = programExtension;
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
}