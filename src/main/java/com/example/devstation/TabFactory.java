package com.example.devstation;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;

public class TabFactory {

    public static Tab createFirstTab() {
        Tab tab1 = new Tab("Tab 1");
        TextField textField1 = new TextField("Tab 1 text");
        tab1.setContent(textField1);
        tab1.setClosable(false);
        return tab1;
    }

    public static Tab createSecondTab() {
        Tab tab2 = new Tab("Tab 2");
        TextField textField2 = new TextField("Tab 2 text");
        tab2.setContent(textField2);
        tab2.setClosable(false);
        return tab2;
    }

    public static Tab createSettingsTab(TabPane tabPane, UserPreferenceManager preferenceManager) {
        Tab settingsTab = new Tab("Settings");
        settingsTab.setClosable(false);

        ComboBox<String> defaultTabSelector = new ComboBox<>();
        defaultTabSelector.getItems().addAll("Tab 1", "Tab 2");
        defaultTabSelector.setOnAction(event -> {
            String selectedTab = defaultTabSelector.getSelectionModel().getSelectedItem();
            preferenceManager.saveUserPreference(selectedTab);
        });

        settingsTab.setContent(defaultTabSelector);
        return settingsTab;
    }
}
