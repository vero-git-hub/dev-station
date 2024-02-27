package com.dev.station.controller.tab;

import com.dev.station.Localizable;
import com.dev.station.manager.LanguageManager;
import com.dev.station.manager.NotificationManager;
import com.dev.station.manager.monitoring.MonitoringTabData;
import com.dev.station.model.SettingsModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Locale;
import java.util.ResourceBundle;

public class MonitoringTabController implements Localizable {

    @FXML public Label filePathLabel;
    @FXML public TextField filePath;
    @FXML public Label fileNameLabel;
    @FXML public TextField fileName;
    @FXML public Label monitoringFrequencyLabel;
    @FXML public TextField monitoringFrequency;
    @FXML public ToggleButton toggleMonitoring;
    @FXML public ToggleGroup monitoringToggleGroup;
    @FXML public ToggleButton openContentButton;
    @FXML public ToggleButton parseAsArrayToggle;
    @FXML public ToggleButton clearContentToggle;
    @FXML public Button saveSettingsButton;
    ResourceBundle bundle;
    private NotificationManager notificationManager;
    private Tab myTab;
    SettingsModel settingsModel;

    public MonitoringTabController() {
        LanguageManager.registerForUpdates(this::updateUI);
        settingsModel = new SettingsModel();
    }

    @FXML public void handleToggleMonitoringAction(ActionEvent actionEvent) {}

    @FXML public void handleSaveSettingsAction(ActionEvent actionEvent) {}

    public void initialize() {
        bundle = LanguageManager.getResourceBundle();

        notificationManager = new NotificationManager(bundle);
        LanguageManager.registerNotificationManager(notificationManager);

        setMultilingual();
        loadSavedLanguage();
    }

    private void setMultilingual() {
        bundle = LanguageManager.getResourceBundle();
        notificationManager = new NotificationManager(bundle);
        LanguageManager.registerNotificationManager(notificationManager);
    }

    public void updateUI(ResourceBundle bundle) {}

    public void setMyTab(Tab tab) {}

    public void loadData(MonitoringTabData tabData) {}

    public String getTranslate(String key) {
        return bundle.getString(key);
    }

    @Override public void loadSavedLanguage() {
        String savedLanguage = settingsModel.loadLanguageSetting();
        Locale locale = LanguageManager.getLocale(savedLanguage);
        LanguageManager.switchLanguage(locale);
    }

    @Override public void switchLanguage(Locale newLocale) {}

    @Override public void updateUI() {
        bundle = LanguageManager.getResourceBundle();

        filePathLabel.setText(getTranslate("monitoringTabController.filePathLabel"));
        fileNameLabel.setText(getTranslate("monitoringTabController.fileNameLabel"));
        monitoringFrequencyLabel.setText(getTranslate("monitoringTabController.monitoringFrequencyLabel"));

        toggleMonitoring.setText(getTranslate("monitoringTabController.toggleMonitoring"));
        openContentButton.setText(getTranslate("monitoringTabController.openContentButton"));
        parseAsArrayToggle.setText(getTranslate("monitoringTabController.parseAsArrayToggle"));
        clearContentToggle.setText(getTranslate("monitoringTabController.clearContentToggle"));
        saveSettingsButton.setText(getTranslate("monitoringTabController.saveSettingsButton"));
    }
}
