package com.dev.station.manager.monitoring;

import org.json.JSONObject;

public class MonitoringTabData {

    private boolean isDefault;
    private String name;
    private String id;
    private String filePath;
    private String fileName;
    private int monitoringFrequency;
    private boolean toggleMonitoring;
    private boolean openContentButton;
    private boolean parseAsArrayToggle;
    private boolean clearContentToggle;
    private String versionControlMode;

    public MonitoringTabData() {}

    public MonitoringTabData(boolean isDefault, String name, String id, String filePath, String fileName,
                             int monitoringFrequency, boolean toggleMonitoring, boolean openContentButton,
                             boolean parseAsArrayToggle, boolean clearContentToggle,
                             String versionControlMode) {
        this.isDefault = isDefault;
        this.name = name;
        this.id = id;
        this.filePath = filePath;
        this.fileName = fileName;
        this.monitoringFrequency = monitoringFrequency;
        this.toggleMonitoring = toggleMonitoring;
        this.openContentButton = openContentButton;
        this.parseAsArrayToggle = parseAsArrayToggle;
        this.clearContentToggle = clearContentToggle;
        this.versionControlMode = versionControlMode;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getMonitoringFrequency() {
        return monitoringFrequency;
    }

    public void setMonitoringFrequency(int monitoringFrequency) {
        this.monitoringFrequency = monitoringFrequency;
    }

    public boolean isToggleMonitoring() {
        return toggleMonitoring;
    }

    public void setToggleMonitoring(boolean toggleMonitoring) {
        this.toggleMonitoring = toggleMonitoring;
    }

    public boolean isOpenContentButton() {
        return openContentButton;
    }

    public void setOpenContentButton(boolean openContentButton) {
        this.openContentButton = openContentButton;
    }

    public boolean isParseAsArrayToggle() {
        return parseAsArrayToggle;
    }

    public void setParseAsArrayToggle(boolean parseAsArrayToggle) {
        this.parseAsArrayToggle = parseAsArrayToggle;
    }

    public boolean isClearContentToggle() {
        return clearContentToggle;
    }

    public void setClearContentToggle(boolean clearContentToggle) {
        this.clearContentToggle = clearContentToggle;
    }

    public String getVersionControlMode() {
        return versionControlMode;
    }

    public void setVersionControlMode(String versionControlMode) {
        this.versionControlMode = versionControlMode;
    }

    /**
     * @param jsonObject
     * @return
     * Method for creating an object from JSON
     */
    public static MonitoringTabData fromJson(JSONObject jsonObject) {
        return new MonitoringTabData(
                jsonObject.getBoolean("isDefault"),
                jsonObject.getString("name"),
                jsonObject.getString("id"),
                jsonObject.getString("filePath"),
                jsonObject.getString("fileName"),
                jsonObject.getInt("monitoringFrequency"),
                jsonObject.getBoolean("toggleMonitoring"),
                jsonObject.getBoolean("openContentButton"),
                jsonObject.getBoolean("parseAsArrayToggle"),
                jsonObject.getBoolean("clearContentToggle"),
                jsonObject.getString("versionControlMode")
        );
    }

    /**
     * Method to convert an object to JSON
     * @return
     */
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isDefault", this.isDefault);
        jsonObject.put("name", this.name);
        jsonObject.put("id", this.id);
        jsonObject.put("filePath", this.filePath);
        jsonObject.put("fileName", this.fileName);
        jsonObject.put("monitoringFrequency", this.monitoringFrequency);
        jsonObject.put("toggleMonitoring", this.toggleMonitoring);
        jsonObject.put("openContentButton", this.openContentButton);
        jsonObject.put("parseAsArrayToggle", this.parseAsArrayToggle);
        jsonObject.put("clearContentToggle", this.clearContentToggle);
        jsonObject.put("versionControlMode", this.versionControlMode);
        return jsonObject;
    }

    @Override
    public String toString() {
        return "MonitoringTabData{" +
                "isDefault=" + isDefault +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", monitoringFrequency=" + monitoringFrequency +
                ", toggleMonitoring=" + toggleMonitoring +
                ", openContentButton=" + openContentButton +
                ", parseAsArrayToggle=" + parseAsArrayToggle +
                ", clearContentToggle=" + clearContentToggle +
                ", versionControlMode='" + versionControlMode + '\'' +
                '}';
    }
}
