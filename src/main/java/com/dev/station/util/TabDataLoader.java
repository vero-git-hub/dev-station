package com.dev.station.util;

import com.dev.station.manager.monitoring.MonitoringTabData;
import com.dev.station.service.FileMonitoringHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

public class TabDataLoader {
    private final UIUpdater uiUpdater;
    private final FileMonitoringHandler fileMonitoringHandler;

    public TabDataLoader(UIUpdater uiUpdater, FileMonitoringHandler fileMonitoringHandler) {
        this.uiUpdater = uiUpdater;
        this.fileMonitoringHandler = fileMonitoringHandler;
    }

    /**
     * Loading user values
     */
    public void loadData(MonitoringTabData tabData, TextField filePath, TextField fileName, TextField monitoringFrequency,
                         ToggleButton toggleMonitoring, ToggleButton clearContentToggle, ComboBox<String> versionControlModeComboBox,
                         TextArea fileContentArea) {
        //setMonitoringTabData(tabData);

        filePath.setText(tabData.getFilePath());
        fileName.setText(tabData.getFileName());
        monitoringFrequency.setText(String.valueOf(tabData.getMonitoringFrequency()));
        toggleMonitoring.setSelected(tabData.isToggleMonitoring());
        clearContentToggle.setSelected(tabData.isClearContentToggle());

        // Set the selected version control mode
        uiUpdater.setSelectedVersion(versionControlModeComboBox, tabData.getVersionControlMode());

        // Start or stop monitoring
        if (toggleMonitoring.isSelected()) {
            fileContentArea.setVisible(true);
            fileMonitoringHandler.startMonitoring(tabData.getFilePath(), tabData.getFileName(), tabData.getMonitoringFrequency());
        } else {
            fileContentArea.setVisible(false);
            fileMonitoringHandler.stopMonitoring();
        }
    }
}
