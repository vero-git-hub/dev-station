package com.dev.station.controller.sidebar;

import com.dev.station.Localizable;
import com.dev.station.controller.tab.ClearTabController;
import com.dev.station.manager.LanguageManager;
import com.dev.station.manager.NotificationManager;
import com.dev.station.manager.clear.TabClearManager;
import com.dev.station.model.SettingsModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.util.Locale;
import java.util.ResourceBundle;

public class ClearController implements Localizable {

    @FXML private TabPane tabPane;
    @FXML private Tab addTabButton;
    @FXML private Label addTabLabel;
    private NotificationManager notificationManager;
    private TabClearManager tabClearManager;
    private ResourceBundle bundle;
    private SettingsModel settingsModel;

    public ClearController() {
        LanguageManager.registerForUpdates(this::updateUI);
        settingsModel = new SettingsModel();
    }

    @FXML
    private void initialize() {
        setMultilingual();
        loadSavedLanguage();
        definitionManagers();

        tabClearManager.loadTabsFromJson();
        setupTabPane();
    }

    private void setMultilingual() {
        bundle = LanguageManager.getResourceBundle();
        notificationManager = new NotificationManager(bundle);
        LanguageManager.registerNotificationManager(notificationManager);
    }

    private void definitionManagers() {
        tabClearManager = new TabClearManager(this, tabPane, addTabButton, addTabLabel, "Clear");
    }

    private void setupTabPane() {
        tabClearManager.setupTabPane();
    }

    public String getTranslate(String key) {
        return bundle.getString(key);
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
            if (tab.getContent() != null && tab.getContent().getUserData() instanceof ClearTabController) {
                ClearTabController clearTabController = (ClearTabController) tab.getContent().getUserData();
                clearTabController.updateUI(LanguageManager.getResourceBundle());
            }
        }
    }
}