package com.dev.station.manager;

import javafx.scene.control.TabPane;
import java.util.prefs.Preferences;

public class TabSelectionManager {
    private Preferences prefs;
    private TabPane tabPane;

    public TabSelectionManager(Preferences prefs, TabPane tabPane) {
        this.prefs = prefs;
        this.tabPane = tabPane;
    }

    public void selectDefaultTab() {
        String defaultTab = prefs.get("defaultTab", "Program management 1");
        switch (defaultTab) {
            case "Program management 1":
                tabPane.getSelectionModel().select(0);
                break;
            case "Program management 2":
                tabPane.getSelectionModel().select(1);
                break;
        }
    }
}