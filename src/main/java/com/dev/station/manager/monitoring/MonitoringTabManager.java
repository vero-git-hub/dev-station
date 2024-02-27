package com.dev.station.manager.monitoring;

import com.dev.station.controller.sidebar.MonitoringController;
import com.dev.station.controller.tab.MonitoringTabController;
import com.dev.station.manager.LanguageManager;
import com.dev.station.util.AlertUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MonitoringTabManager {
    private MonitoringController controller;
    private TabPane tabPane;
    private Tab addTabButton;
    private Label addTabLabel;
    private String screenType;

    public MonitoringTabManager(MonitoringController controller, TabPane tabPane, Tab addTabButton, Label addTabLabel, String screenType) {
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
        MonitoringJsonTabsManager monitoringJsonTabsManager = new MonitoringJsonTabsManager();

        List<MonitoringTabData> tabs = monitoringJsonTabsManager.loadMonitoringTabs(1, screenType);

        MonitoringTabData defaultTabData = tabs.stream()
                .filter(MonitoringTabData::isDefault)
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dev/station/ui/tab/MonitoringTabContent.fxml"));
            loader.setResources(LanguageManager.getResourceBundle());

            Node content = loader.load();
            MonitoringTabController tabController = loader.getController();

            String tabId = UUID.randomUUID().toString();
            Tab newTab = new Tab("New tab");
            newTab.setContent(content);
            newTab.setId(tabId);
            tabController.setMyTab(newTab);
            //tabController.setupTableColumns();
            newTab.getStyleClass().add("clickable");

            MonitoringJsonTabsManager monitoringJsonTabsManager = new MonitoringJsonTabsManager();
            List<MonitoringTabData> currentTabs = monitoringJsonTabsManager.loadMonitoringTabs(1, screenType);

            MonitoringTabData tabData = new MonitoringTabData();
            tabData.setDefault(false);
            tabData.setName("New Monitoring Tab");
            tabData.setId(UUID.randomUUID().toString());
            tabData.setFilePath("/path/to/file");
            tabData.setFileName("monitoring_file.txt");
            tabData.setMonitoringFrequency(true);
            tabData.setToggleMonitoring(true);
            tabData.setOpenContentButton(true);
            tabData.setParseAsArrayToggle(false);
            tabData.setClearContentToggle(true);

            currentTabs.add(tabData);
            monitoringJsonTabsManager.saveMonitoringTabs(1, screenType, currentTabs);

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
            MonitoringJsonTabsManager monitoringJsonTabsManager = new MonitoringJsonTabsManager();
            List<MonitoringTabData> tabs = monitoringJsonTabsManager.loadMonitoringTabs(1, screenType);

            boolean isRemoved = tabs.removeIf(t -> t.getId().equals(tab.getId()));
            if(isRemoved) {
                monitoringJsonTabsManager.saveMonitoringTabs(1, screenType, tabs);

                boolean isTabPaneRemoved = tabPane.getTabs().remove(tab);

                AlertUtils.showSuccessAlert("", controller.getTranslate("monitoringTabManager.deletionSuccess"));
            }

        } else {
            AlertUtils.showInformationAlert("", controller.getTranslate("alerts.deletionCancelled"));
        }
    }

    private void handleRenameTab(Tab tab) {
        TextInputDialog dialog = new TextInputDialog(tab.getText());
        dialog.setTitle(controller.getTranslate("titleRenameTab"));
        dialog.setHeaderText(controller.getTranslate("headerTextRenameTab"));
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            tab.setText(name);

            MonitoringJsonTabsManager jsonTabsManager = new MonitoringJsonTabsManager();
            List<MonitoringTabData> tabs = jsonTabsManager.loadMonitoringTabs(1, screenType);

            for (MonitoringTabData tabData : tabs) {
                if (tabData.getId().equals(tab.getId())) {
                    tabData.setName(name);
                    break;
                }
            }

            jsonTabsManager.saveMonitoringTabs(1, screenType, tabs);
        });
    }

    private void handleSetDefaultTab(Tab tab) {
        MonitoringJsonTabsManager jsonTabsManager = new MonitoringJsonTabsManager();
        List<MonitoringTabData> tabs = jsonTabsManager.loadMonitoringTabs(1, screenType);

        tabs.forEach(t -> t.setDefault(false));

        tabs.stream()
                .filter(t -> t.getId().equals(tab.getId()))
                .findFirst()
                .ifPresent(t -> t.setDefault(true));

        jsonTabsManager.saveMonitoringTabs(1, screenType, tabs);
    }

    public void loadTabsFromJson() {
        MonitoringJsonTabsManager jsonTabsManager = new MonitoringJsonTabsManager();
        List<MonitoringTabData> tabs = jsonTabsManager.loadMonitoringTabs(1, screenType);

        for (MonitoringTabData tabData : tabs) {
            Tab tab = createTabFromData(tabData);
            tabPane.getTabs().add(tabPane.getTabs().size() - 1, tab);
        }
    }

    private Tab createTabFromData(MonitoringTabData tabData) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dev/station/ui/tab/MonitoringTabContent.fxml"));
            loader.setResources(LanguageManager.getResourceBundle());

            Node content = loader.load();
            MonitoringTabController tabController = loader.getController();

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
