package com.dev.station.controller;

import com.dev.station.file.TabData;

public interface TabControllerInterface {
    String getTranslate(String key);
    void setupTableColumns();
    void loadData(TabData tabData);
}
