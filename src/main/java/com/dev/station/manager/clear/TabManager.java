package com.dev.station.manager.clear;

import com.dev.station.controller.MainController;
import com.dev.station.controller.sidebar.ClearController;
import com.dev.station.controller.tab.TabController;
import com.dev.station.file.JsonTabsManager;
import com.dev.station.file.TabData;
import com.dev.station.manager.LanguageManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.prefs.Preferences;

public class TabManager {
    private final Preferences prefs = MainController.prefs;
    private final ClearController clearController;
    TabPane tabPane;
    Tab addTabButton;
    Label addTabLabel;

    public TabManager(ClearController clearController, TabPane tabPane, Tab addTabButton, Label addTabLabel) {
        this.clearController = clearController;
        this.tabPane = tabPane;
        this.addTabButton = addTabButton;
        this.addTabLabel = addTabLabel;
    }

    public void setupTabPane() {
        setupTabContextMenus();
        selectDefaultTab();
        restoreTabTitles();
        setupAddTabButton();
    }

    private void setupTabContextMenus() {
        tabPane.getTabs().forEach(this::setupTabContextMenu);
    }

    private void selectDefaultTab() {
        JsonTabsManager jsonTabsManager = new JsonTabsManager();
        List<TabData> tabs = jsonTabsManager.loadTabs();

        TabData defaultTabData = tabs.stream()
                .filter(TabData::isDefault)
                .findFirst()
                .orElse(null);

        if (defaultTabData != null) {
            tabPane.getTabs().stream()
                    .filter(tab -> tab.getId().equals(defaultTabData.getId()))
                    .findFirst()
                    .ifPresent(tab -> tabPane.getSelectionModel().select(tab));
        } else {
            System.out.println("No default tab set");
        }
    }

    private void restoreTabTitles() {
        tabPane.getTabs().forEach(tab -> {
            String tabId = tab.getId();
            if (tabId != null && prefs.get(tabId, null) != null) {
                tab.setText(prefs.get(tabId, tab.getText()));
            }
        });
    }

    private void setupAddTabButton() {
        addTabLabel.setOnMouseClicked(event -> {
            Tab newTab = createNewTab();
            if (newTab != null) {
                tabPane.getTabs().add(tabPane.getTabs().size() - 1, newTab);
                tabPane.getSelectionModel().select(newTab);
            }
        });
    }

    private Tab createNewTab() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/tab/TabContent.fxml"));
            loader.setResources(LanguageManager.getResourceBundle());

            Node content = loader.load();
            TabController tabController = loader.getController();

            String tabId = UUID.randomUUID().toString();
            Tab newTab = new Tab("New tab");
            newTab.setContent(content);
            newTab.setId(tabId);
            tabController.setMyTab(newTab);
            tabController.setupTableColumns();
            newTab.getStyleClass().add("clickable");

            JsonTabsManager jsonTabsManager = new JsonTabsManager();
            List<TabData> currentTabs = jsonTabsManager.loadTabs();

            TabData newTabData = new TabData();
            newTabData.setId(tabId);
            newTabData.setName(newTab.getText());
            newTabData.setRecycleBinPath("");
            newTabData.setPaths(new ArrayList<>());

            currentTabs.add(newTabData);
            jsonTabsManager.saveTabs(currentTabs);

            setupTabContextMenu(newTab);

            return newTab;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setupTabContextMenu(Tab tab) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem renameItem = new MenuItem(clearController.getTranslate("renameItem"));
        MenuItem setDefaultItem = new MenuItem(clearController.getTranslate("setDefaultItem"));

        renameItem.getStyleClass().add("clickable");
        setDefaultItem.getStyleClass().add("clickable");

        contextMenu.getItems().addAll(renameItem, setDefaultItem);

        renameItem.setOnAction(e -> handleRenameTab(tab));
        setDefaultItem.setOnAction(e -> handleSetDefaultTab(tab));

        tab.setContextMenu(contextMenu);
    }

    private void handleRenameTab(Tab tab) {
        TextInputDialog dialog = new TextInputDialog(tab.getText());
        dialog.setTitle(clearController.getTranslate("titleRenameTab"));
        dialog.setHeaderText(clearController.getTranslate("headerTextRenameTab"));
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            tab.setText(name);

            JsonTabsManager jsonTabsManager = new JsonTabsManager();
            List<TabData> tabs = jsonTabsManager.loadTabs();

            for (TabData tabData : tabs) {
                if (tabData.getId().equals(tab.getId())) {
                    tabData.setName(name);
                    break;
                }
            }

            jsonTabsManager.saveTabs(tabs);
        });
    }

    private void handleSetDefaultTab(Tab tab) {
        JsonTabsManager jsonTabsManager = new JsonTabsManager();
        List<TabData> tabs = jsonTabsManager.loadTabs();

        tabs.forEach(t -> t.setDefault(false));

        tabs.stream()
                .filter(t -> t.getId().equals(tab.getId()))
                .findFirst()
                .ifPresent(t -> t.setDefault(true));

        jsonTabsManager.saveTabs(tabs);
    }

    public void loadTabsFromJson() {
        JsonTabsManager jsonTabsManager = new JsonTabsManager();
        List<TabData> tabs = jsonTabsManager.loadTabs();

        for (TabData tabData : tabs) {
            Tab tab = createTabFromData(tabData);
            tabPane.getTabs().add(tabPane.getTabs().size() - 1, tab);
        }
    }

    private Tab createTabFromData(TabData tabData) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/tab/TabContent.fxml"));
            loader.setResources(LanguageManager.getResourceBundle());

            Node content = loader.load();
            TabController tabController = loader.getController();

            Tab tab = new Tab(tabData.getName());
            tab.setContent(content);
            tab.setId(tabData.getId());
            tab.getStyleClass().add("clickable");

            tabController.setMyTab(tab);
            tabController.loadData(tabData);

            setupTabContextMenu(tab);

            return tab;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}