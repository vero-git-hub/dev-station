package com.dev.station.manager.clear;

import com.dev.station.controller.MainController;
import com.dev.station.controller.sidebar.ClearController;
import com.dev.station.controller.tab.TabController;
import com.dev.station.manager.LanguageManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Optional;
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
        String defaultTabId = prefs.get("defaultTabId", null);
        if (defaultTabId != null) {
            tabPane.getTabs().stream()
                    .filter(tab -> tab.getId().equals(defaultTabId))
                    .findFirst()
                    .ifPresent(tabPane.getSelectionModel()::select);
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

            Tab newTab = new Tab("New");
            newTab.setContent(content);

            return newTab;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveTabsToPrefs() {
        JSONArray tabsJson = new JSONArray();
        for (Tab tab : tabPane.getTabs()) {
            if (tab == addTabButton) continue;

            JSONObject tabJson = new JSONObject();
            tabJson.put("title", tab.getText());

            tabsJson.put(tabJson);
        }

        prefs.put("savedTabs", tabsJson.toString());
    }

    private void setupTabContextMenu(Tab tab) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem renameItem = new MenuItem(clearController.getTranslate("renameItem"));
        MenuItem setDefaultItem = new MenuItem(clearController.getTranslate("setDefaultItem"));
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
            prefs.put(tab.getId(), name);
        });
    }

    private void handleSetDefaultTab(Tab tab) {
        prefs.put("defaultTabId", tab.getId());
    }
}