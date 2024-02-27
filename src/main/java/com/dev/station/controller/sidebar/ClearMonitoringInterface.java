package com.dev.station.controller.sidebar;

import com.dev.station.file.TabData;

public interface ClearMonitoringInterface {
    String getTranslate(String key);
    void setupTableColumns();
    void loadData(TabData tabData);
}
