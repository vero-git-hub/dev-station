package com.dev.station.controller.sidebar;

import com.dev.station.Localizable;
import com.dev.station.controller.tab.MonitoringTabController;
import com.dev.station.manager.LanguageManager;
import com.dev.station.manager.NotificationManager;
import com.dev.station.manager.monitoring.MonitoringTabManager;
import com.dev.station.model.SettingsModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.util.Locale;
import java.util.ResourceBundle;

public class MonitoringController implements Localizable {
    @FXML private TabPane tabPane;
    @FXML private Tab addTabButton;
    @FXML private Label addTabLabel;
    private NotificationManager notificationManager;
    private MonitoringTabManager tabManager;
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
        tabManager = new MonitoringTabManager(this, tabPane, addTabButton, addTabLabel, "Monitoring");
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
            if (tab.getContent() != null && tab.getContent().getUserData() instanceof MonitoringTabController) {
                MonitoringTabController monitoringTabController = (MonitoringTabController) tab.getContent().getUserData();
                monitoringTabController.updateUI(LanguageManager.getResourceBundle());
            }
        }
    }

    public String getTranslate(String key) {
        return bundle.getString(key);
    }
}