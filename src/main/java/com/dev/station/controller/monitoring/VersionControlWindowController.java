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
import org.apache.commons.text.diff.CommandVisitor;
import org.apache.commons.text.diff.EditScript;
import org.apache.commons.text.diff.StringsComparator;

import java.io.IOException;
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
        StringBuilder highlightedText = new StringBuilder("<style>.added { color: green; } .removed { color: red; }</style><pre>");

        StringsComparator comp = new StringsComparator(oldContent, newContent);
        EditScript<Character> script = comp.getScript();
        script.visit(new CommandVisitor<Character>() {
            @Override
            public void visitInsertCommand(Character object) {
                highlightedText.append("<span class='added'>").append(object).append("</span>");
            }

            @Override
            public void visitDeleteCommand(Character object) {
                highlightedText.append("<span class='removed'>").append(object).append("</span>");
            }

            @Override
            public void visitKeepCommand(Character object) {
                highlightedText.append(object);
            }
        });

        highlightedText.append("</pre>");
        String textToHtml = "<html><body>" + highlightedText.toString() + "</body></html>";
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