package com.dev.station.controller.monitoring;

import com.dev.station.Localizable;
import com.dev.station.manager.LanguageManager;
import com.dev.station.model.SettingsModel;
import com.dev.station.service.FileChangeListener;
import com.dev.station.service.FileContentProvider;
import com.dev.station.util.AlertUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class VersionControlWindowController implements Localizable, FileChangeListener {
    @FXML public WebView versionControlWebView;
    private WebEngine webEngine;
    ResourceBundle bundle;
    SettingsModel settingsModel;
    private String previousContent = "";

    public VersionControlWindowController() {
        LanguageManager.registerForUpdates(this::updateUI);
        settingsModel = new SettingsModel();
    }

    @FXML
    private void initialize() {
        webEngine = versionControlWebView.getEngine();
    }

    public void setInitialContent(String content) {
        previousContent = content;
        String htmlContent = String.format("<html><body><pre>%s</pre></body></html>", escapeHtml(content));
        Platform.runLater(() -> webEngine.loadContent(htmlContent));
    }

    private String escapeHtml(String string) {
        return string.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br>")
                .replace(" ", "&nbsp;");
    }


    public String getTranslate(String key) {
        return bundle.getString(key);
    }

    public String getCurrentContent() {
        final String[] content = new String[1];
        Platform.runLater(() -> {
            content[0] = (String) webEngine.executeScript("document.body.querySelector('pre').innerText");
        });
        return content[0];
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

        String textToHtml = "<html><body>" + highlightedText + "</body></html>";
        Platform.runLater(() -> webEngine.loadContent(textToHtml));
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
