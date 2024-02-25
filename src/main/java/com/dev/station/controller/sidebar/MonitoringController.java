package com.dev.station.controller.sidebar;

import com.dev.station.Localizable;
import com.dev.station.controller.TabControllerInterface;
import com.dev.station.controller.tab.TabController;
import com.dev.station.file.PathData;
import com.dev.station.file.TabData;
import com.dev.station.manager.LanguageManager;
import com.dev.station.manager.NotificationManager;
import com.dev.station.manager.clear.TabManager;
import com.dev.station.model.SettingsModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Locale;
import java.util.ResourceBundle;

public class MonitoringController implements Localizable, TabControllerInterface {
    @FXML private TabPane tabPane;
    @FXML private Tab addTabButton;
    @FXML private Label addTabLabel;
    private NotificationManager notificationManager;
    private TabManager tabManager;
    private ResourceBundle bundle;
    private SettingsModel settingsModel;

    public MonitoringController() {
        LanguageManager.registerForUpdates(this::updateUI);
        settingsModel = new SettingsModel();
    }

    @FXML private void initialize() {
        setMultilingual();
        loadSavedLanguage();
        definitionManagers();

        tabManager.loadTabsFromJson();
        setupTabPane();
    }

    private void setMultilingual() {
        bundle = LanguageManager.getResourceBundle();
        notificationManager = new NotificationManager(bundle);
        LanguageManager.registerNotificationManager(notificationManager);
    }

    private void definitionManagers() {
        tabManager = new TabManager(this, tabPane, addTabButton, addTabLabel);
    }

    private void setupTabPane() {
        tabManager.setupTabPane();
    }

    @Override
    public void loadSavedLanguage() {
        String savedLanguage = settingsModel.loadLanguageSetting();
        Locale locale = LanguageManager.getLocale(savedLanguage);
        LanguageManager.switchLanguage(locale);
    }

    @Override
    public void switchLanguage(Locale newLocale) {
        LanguageManager.switchLanguage(newLocale);
        updateUI();
    }

    @Override
    public void updateUI() {
        ResourceBundle bundle = LanguageManager.getResourceBundle();
        notificationManager.updateResourceBundle(bundle);

        for (Tab tab : tabPane.getTabs()) {
            if (tab.getContent() != null && tab.getContent().getUserData() instanceof TabController) {
                TabController tabController = (TabController) tab.getContent().getUserData();
                tabController.updateUI(LanguageManager.getResourceBundle());
            }
        }
    }

    @Override
    public String getTranslate(String key) {
        return bundle.getString(key);
    }

    @Override
    public void setupTableColumns() {
        // Logic for setting up table columns for MonitoringController
    }

    @Override
    public void loadData(TabData tabData) {
        // Logic for loading data into MonitoringController if necessary
    }
}