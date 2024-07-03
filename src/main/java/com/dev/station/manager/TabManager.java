package com.dev.station.manager;

import com.dev.station.manager.monitoring.MonitoringJsonTabsManager;
import com.dev.station.manager.monitoring.MonitoringTabData;
import com.dev.station.util.alert.AlertUtils;
import com.dev.station.util.alert.HeaderAlertUtils;

import java.util.List;
import java.util.ResourceBundle;

public class TabManager {

    public void updateMonitoringTab(String tabId, String filePath, String fileName, int monitoringFrequency, boolean toggleMonitoring, boolean openContentButton, boolean parseAsArrayToggle, boolean clearContentToggle, String versionControlMode, ResourceBundle bundle) {
        MonitoringJsonTabsManager jsonTabsManager = new MonitoringJsonTabsManager();
        List<MonitoringTabData> tabs = jsonTabsManager.loadMonitoringTabs(1, "Monitoring");

        boolean isTabExists = false;

        for (MonitoringTabData tab : tabs) {
            if (tab.getId().equals(tabId)) {
                updateTabData(tab, filePath, fileName, monitoringFrequency, toggleMonitoring, openContentButton, parseAsArrayToggle, clearContentToggle, versionControlMode);
                isTabExists = true;
                break;
            }
        }

        if(!isTabExists) {
            HeaderAlertUtils.showErrorAlert("", "No update tab found.");
        } else {
            boolean success = jsonTabsManager.saveMonitoringTabs(1, "Monitoring", tabs);
            if (success) {
                HeaderAlertUtils.showSuccessAlert("", bundle.getString("alerts.successSaving"));
            } else {
                HeaderAlertUtils.showErrorAlert("", bundle.getString("alerts.errorSaving"));
            }
        }
    }

    private void updateTabData(MonitoringTabData tab, String filePath, String fileName, int monitoringFrequency, boolean toggleMonitoring, boolean openContentButton, boolean parseAsArrayToggle, boolean clearContentToggle, String versionControlMode) {
        tab.setFilePath(filePath);
        tab.setFileName(fileName);
        tab.setMonitoringFrequency(monitoringFrequency);
        tab.setToggleMonitoring(toggleMonitoring);
        tab.setOpenContentButton(openContentButton);
        tab.setParseAsArrayToggle(parseAsArrayToggle);
        tab.setClearContentToggle(clearContentToggle);
        tab.setVersionControlMode(versionControlMode);
    }
}