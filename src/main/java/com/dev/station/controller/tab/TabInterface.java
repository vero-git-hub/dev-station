package com.dev.station.controller.tab;

import com.dev.station.file.TabData;
import javafx.scene.control.Tab;

public interface TabInterface {
    void setMyTab(Tab tab);

    void loadData(TabData tabData);
}
