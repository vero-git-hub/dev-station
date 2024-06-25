package com.dev.station.manager.clear;

import com.dev.station.controller.sidebar.ClearController;
import com.dev.station.controller.tab.ClearTabController;
import com.dev.station.manager.LanguageManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TabClearManager {
    private ClearController controller;
    private TabPane tabPane;
    private Tab addTabButton;
    private Label addTabLabel;
    private String screenType;

    public TabClearManager(ClearController controller, TabPane tabPane, Tab addTabButton, Label addTabLabel, String screenType) {
        this.controller = controller;
        this.tabPane = tabPane;
        this.addTabButton = addTabButton;
        this.addTabLabel = addTabLabel;
        this.screenType = screenType;
    }

    public void setupTabPane() {
        setupTabContextMenus();
        selectDefaultTab();
        setupAddTabButton();
    }

    private void setupTabContextMenus() {
        tabPane.getTabs().forEach(this::setupTabContextMenu);
    }

    private void selectDefaultTab() {
        JsonTabsManager jsonTabsManager = new JsonTabsManager();
        List<TabData> tabs = jsonTabsManager.loadTabs(1, screenType);

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dev/station/ui/tab/TabContent.fxml"));
            loader.setResources(LanguageManager.getResourceBundle());

            Node content = loader.load();
            ClearTabController clearTabController = loader.getController();

            String tabId = UUID.randomUUID().toString();
            Tab newTab = new Tab("New tab");
            newTab.setContent(content);
            newTab.setId(tabId);
            clearTabController.setMyTab(newTab);
            clearTabController.setupTableColumns();
            newTab.getStyleClass().add("clickable");

            JsonTabsManager jsonTabsManager = new JsonTabsManager();
            List<TabData> currentTabs = jsonTabsManager.loadTabs(1, screenType);

            TabData newTabData = new TabData();
            newTabData.setId(tabId);
            newTabData.setName(newTab.getText());
            newTabData.setRecycleBinPath("");
            newTabData.setPaths(new ArrayList<>());

            currentTabs.add(newTabData);
            jsonTabsManager.saveTabs(1, screenType, currentTabs);

            setupTabContextMenu(newTab);

            return newTab;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setupTabContextMenu(Tab tab) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem renameItem = new MenuItem(controller.getTranslate("renameItem"));
        MenuItem setDefaultItem = new MenuItem(controller.getTranslate("setDefaultItem"));
        MenuItem deleteItem = new MenuItem(controller.getTranslate("deleteItem"));

        renameItem.getStyleClass().add("clickable");
        setDefaultItem.getStyleClass().add("clickable");
        deleteItem.getStyleClass().add("clickable");

        contextMenu.getItems().addAll(renameItem, setDefaultItem, deleteItem);

        renameItem.setOnAction(e -> handleRenameTab(tab));
        setDefaultItem.setOnAction(e -> handleSetDefaultTab(tab));
        deleteItem.setOnAction(e -> handleDeleteTab(tab));

        tab.setContextMenu(contextMenu);
    }

    private void handleDeleteTab(Tab tab) {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle(controller.getTranslate("titleDeleteTab"));
        confirmationDialog.setHeaderText(controller.getTranslate("headerTextDeleteTab"));
        confirmationDialog.setContentText(controller.getTranslate("contentTextDeleteTab"));

        Optional<ButtonType> result = confirmationDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            JsonTabsManager jsonTabsManager = new JsonTabsManager();
            List<TabData> tabs = jsonTabsManager.loadTabs(1, screenType);

            tabs.removeIf(t -> t.getId().equals(tab.getId()));

            jsonTabsManager.saveTabs(1, screenType, tabs);

            tabPane.getTabs().remove(tab);
        }
    }

    private void handleRenameTab(Tab tab) {
        TextInputDialog dialog = new TextInputDialog(tab.getText());
        dialog.setTitle(controller.getTranslate("titleRenameTab"));
        dialog.setHeaderText(controller.getTranslate("headerTextRenameTab"));
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            tab.setText(name);

            JsonTabsManager jsonTabsManager = new JsonTabsManager();
            List<TabData> tabs = jsonTabsManager.loadTabs(1, screenType);

            for (TabData tabData : tabs) {
                if (tabData.getId().equals(tab.getId())) {
                    tabData.setName(name);
                    break;
                }
            }

            jsonTabsManager.saveTabs(1, screenType, tabs);
        });
    }

    private void handleSetDefaultTab(Tab tab) {
        JsonTabsManager jsonTabsManager = new JsonTabsManager();
        List<TabData> tabs = jsonTabsManager.loadTabs(1, screenType);

        tabs.forEach(t -> t.setDefault(false));

        tabs.stream()
                .filter(t -> t.getId().equals(tab.getId()))
                .findFirst()
                .ifPresent(t -> t.setDefault(true));

        jsonTabsManager.saveTabs(1, screenType, tabs);
    }

    public void loadTabsFromJson() {
        JsonTabsManager jsonTabsManager = new JsonTabsManager();
        List<TabData> tabs = jsonTabsManager.loadTabs(1, screenType);

        for (TabData tabData : tabs) {
            Tab tab = createTabFromData(tabData);
            tabPane.getTabs().add(tabPane.getTabs().size() - 1, tab);
        }
    }

    private Tab createTabFromData(TabData tabData) {
        String resource = "/com/dev/station/ui/tab/TabContent.fxml";
        String resourceForMonitoring = "/com/dev/station/ui/tab/MonitoringTabContent.fxml";

        if("Monitoring".equals(screenType)) {
            resource = resourceForMonitoring;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            loader.setResources(LanguageManager.getResourceBundle());

            Node content = loader.load();
            ClearTabController tabController = loader.getController();

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