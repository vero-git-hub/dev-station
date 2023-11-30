package com.dev.station.manager.clear;

import com.dev.station.controller.MainController;
import com.dev.station.controller.sidebar.ClearController;
import javafx.scene.control.*;

import java.util.Optional;
import java.util.prefs.Preferences;

public class TabManager {
    private final Preferences prefs = MainController.prefs;
    private final ClearController clearController;

    public TabManager(ClearController clearController) {
        this.clearController = clearController;
    }

    public void setupTabPane(TabPane tabPane) {
        tabPane.getTabs().forEach(this::setupTabContextMenu);

        String defaultTabId = prefs.get("defaultTabId", null);
        if (defaultTabId != null) {
            tabPane.getTabs().stream()
                    .filter(tab -> tab.getId().equals(defaultTabId))
                    .findFirst()
                    .ifPresent(tabPane.getSelectionModel()::select);
        }

        tabPane.getTabs().forEach(tab -> {
            String tabId = tab.getId();
            if (tabId != null && prefs.get(tabId, null) != null) {
                tab.setText(prefs.get(tabId, tab.getText()));
            }
        });
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