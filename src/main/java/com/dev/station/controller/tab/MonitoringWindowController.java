package com.dev.station.controller.tab;

import com.dev.station.Localizable;
import com.dev.station.manager.LanguageManager;
import com.dev.station.model.SettingsModel;
import com.dev.station.service.FileChangeListener;
import com.dev.station.service.FileContentProvider;
import com.dev.station.service.FileMonitoringService;
import com.dev.station.util.alert.AlertUtils;
import com.dev.station.util.FileUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Display file content in a new window
 */
public class MonitoringWindowController implements Localizable, FileChangeListener {

    @FXML private TextArea monitoringTextArea;
    ResourceBundle bundle;
    SettingsModel settingsModel;
    private boolean clearFileAfterReading = false;
    private String filePathToClear;
    private FileMonitoringService monitoringService;

    public MonitoringWindowController() {
        LanguageManager.registerForUpdates(this::updateUI);
        settingsModel = new SettingsModel();
    }

    public void setInitialContent(String content) {
        Platform.runLater(() -> monitoringTextArea.setText(content));
    }

    public String getCurrentContent() {
        return monitoringTextArea.getText();
    }

    public void setClearFileAfterReading(boolean clearFileAfterReading) {
        this.clearFileAfterReading = clearFileAfterReading;
    }

    public void setFilePathToClear(String filePath) {
        this.filePathToClear = filePath;
    }

    public void setMonitoringService(FileMonitoringService service) {
        this.monitoringService = service;
    }

    public String getTranslate(String key) {
        return bundle.getString(key);
    }

    @Override public void onFileChange(FileContentProvider contentProvider) {
        Platform.runLater(() -> {
            try {
                String content = contentProvider.getContent();
                monitoringTextArea.setText(content);

                if (clearFileAfterReading) {
                    String errorMessage = getTranslate("alert.error.setLastModified");
                    FileUtils.clearFileAndSetLastModified(filePathToClear, monitoringService, errorMessage);
                }
            } catch (IOException e) {
                AlertUtils.showErrorAlert("", e.getMessage());
            }
        });
    }

    @Override
    public void loadSavedLanguage() {
        String savedLanguage = settingsModel.loadLanguageSetting();
        Locale locale = LanguageManager.getLocale(savedLanguage);
        LanguageManager.switchLanguage(locale);
    }

    @Override
    public void switchLanguage(Locale newLocale) {
        bundle = LanguageManager.getResourceBundle();
    }

    @Override
    public void updateUI() {

    }
}