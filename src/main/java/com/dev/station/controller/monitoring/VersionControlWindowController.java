package com.dev.station.controller.monitoring;

import com.dev.station.Localizable;
import com.dev.station.manager.LanguageManager;
import com.dev.station.model.SettingsModel;
import com.dev.station.service.FileChangeListener;
import com.dev.station.service.FileContentProvider;
import com.dev.station.util.AlertUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class VersionControlWindowController implements Localizable, FileChangeListener {
    @FXML public TextArea versionControlTextArea;
    ResourceBundle bundle;
    SettingsModel settingsModel;
    private String previousContent = "";

    public VersionControlWindowController() {
        LanguageManager.registerForUpdates(this::updateUI);
        settingsModel = new SettingsModel();
    }

    public void setInitialContent(String content) {
        previousContent = content;
        Platform.runLater(() -> versionControlTextArea.setText(content));
    }

    public String getTranslate(String key) {
        return bundle.getString(key);
    }

    public String getCurrentContent() {
        return versionControlTextArea.getText();
    }

    @Override
    public void onFileChange(FileContentProvider contentProvider) {
        Platform.runLater(() -> {
            try {
                String newContent = contentProvider.getContent();
                highlightChanges(previousContent, newContent);
                previousContent = newContent;
            } catch (IOException e) {
                AlertUtils.showErrorAlert("", e.getMessage());
            }
        });
    }

    private void highlightChanges(String oldContent, String newContent) {
        List<String> oldLines = Arrays.asList(oldContent.split("\n"));
        List<String> newLines = Arrays.asList(newContent.split("\n"));

        StringBuilder highlightedText = new StringBuilder();

        for (int i = 0; i < newLines.size(); i++) {
            if (i < oldLines.size() && !newLines.get(i).equals(oldLines.get(i))) {
                highlightedText.append("*").append(newLines.get(i)).append("\n");
            } else if (i >= oldLines.size()) {
                highlightedText.append("*").append(newLines.get(i)).append("\n");
            } else {
                highlightedText.append(newLines.get(i)).append("\n");
            }
        }

        versionControlTextArea.setText(highlightedText.toString());
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
