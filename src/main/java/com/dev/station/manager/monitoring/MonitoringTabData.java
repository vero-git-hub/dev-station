package com.dev.station.manager.monitoring;

import org.json.JSONObject;

public class MonitoringTabData {

    private boolean isDefault;
    private String name;
    private String id;
    private String filePath;
    private String fileName;
    private boolean monitoringFrequency;
    private boolean toggleMonitoring;
    private boolean openContentButton;
    private boolean parseAsArrayToggle;
    private boolean clearContentToggle;

    public MonitoringTabData() {}

    public MonitoringTabData(boolean isDefault, String name, String id, String filePath, String fileName,
                             boolean monitoringFrequency, boolean toggleMonitoring, boolean openContentButton,
                             boolean parseAsArrayToggle, boolean clearContentToggle) {
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

    public boolean isMonitoringFrequency() {
        return monitoringFrequency;
    }

    public void setMonitoringFrequency(boolean monitoringFrequency) {
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

    /**
     * Method for creating an object from JSON
     * @param jsonObject
     * @return
     */
    public static MonitoringTabData fromJson(JSONObject jsonObject) {
        return new MonitoringTabData(
                jsonObject.getBoolean("isDefault"),
                jsonObject.getString("name"),
                jsonObject.getString("id"),
                jsonObject.getString("filePath"),
                jsonObject.getString("fileName"),
                jsonObject.getBoolean("monitoringFrequency"),
                jsonObject.getBoolean("toggleMonitoring"),
                jsonObject.getBoolean("openContentButton"),
                jsonObject.getBoolean("parseAsArrayToggle"),
                jsonObject.getBoolean("clearContentToggle")
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
        return jsonObject;
    }
}
