package com.dev.station.controller.monitoring;

import com.dev.station.Localizable;
import com.dev.station.manager.LanguageManager;
import com.dev.station.model.SettingsModel;
import com.dev.station.service.FileChangeListener;
import com.dev.station.service.FileContentProvider;
import com.dev.station.util.alert.AlertUtils;
import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.DeltaType;
import com.github.difflib.patch.Patch;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.commons.text.diff.CommandVisitor;
import org.apache.commons.text.diff.EditScript;
import org.apache.commons.text.diff.StringsComparator;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

public class VersionControlWindowController implements Localizable, FileChangeListener {
    @FXML public WebView versionControlWebView;
    private WebEngine webEngine;
    ResourceBundle bundle;
    SettingsModel settingsModel;
    private String previousContent = "";
    private VersionControlMode versionControlMode;

    public VersionControlWindowController() {
        LanguageManager.registerForUpdates(this::updateUI);
        settingsModel = new SettingsModel();
    }

    public void setVersionControlMode(VersionControlMode mode) {
        this.versionControlMode = mode;
    }

    @FXML private void initialize() {
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

    public void getCurrentContent(ContentCallback callback) {
        Platform.runLater(() -> {
            String content = (String) webEngine.executeScript("document.body.querySelector('pre').innerText");
            callback.onContentReceived(content);
        });
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
        switch (versionControlMode) {
            case SYMBOL:
                highlightChangesBySymbol(oldContent, newContent);
                break;
            case WORD:
                highlightChangesByWord(oldContent, newContent);
                break;
            case LINE:
                highlightChangesByLine(oldContent, newContent);
                break;
            case TOOLTIP:
                highlightChangesByLineWithTooltip(oldContent, newContent);
                break;
        }
    }

    private void highlightChangesBySymbol(String oldContent, String newContent) {
        StringBuilder highlightedText = new StringBuilder(
                "<style>"
                        + ".added { background-color: #ccffcc; } "
                        + ".removed { background-color: #ffcccc; } "
                        + "</style><pre>"
        );

        StringsComparator comp = new StringsComparator(oldContent, newContent);
        EditScript<Character> script = comp.getScript();
        script.visit(new CommandVisitor<Character>() {
            @Override
            public void visitInsertCommand(Character object) {
                highlightedText.append("<span class='added'>").append(escapeHtml(String.valueOf(object))).append("</span>");
            }

            @Override
            public void visitDeleteCommand(Character object) {
                highlightedText.append("<span class='removed'>").append(escapeHtml(String.valueOf(object))).append("</span>");
            }

            @Override
            public void visitKeepCommand(Character object) {
                highlightedText.append(escapeHtml(String.valueOf(object)));
            }
        });

        highlightedText.append("</pre>");
        String textToHtml = "<html><body>" + highlightedText.toString() + "</body></html>";
        Platform.runLater(() -> webEngine.loadContent(textToHtml));
    }

    private void highlightChangesByWord(String oldContent, String newContent) {
        try {
            String[] oldTokens = oldContent.split("(?<=\\s)|(?=\\s+)");
            String[] newTokens = newContent.split("(?<=\\s)|(?=\\s+)");

            Patch<String> patch = DiffUtils.diff(Arrays.asList(oldTokens), Arrays.asList(newTokens));
            StringBuilder wordHighlightedText = new StringBuilder("<style>.added { background-color: #ccffcc; } .removed { background-color: #ffcccc; }</style><pre>");

            int startOfChangeIndex = 0;
            for (AbstractDelta<String> delta : patch.getDeltas()) {
                while (startOfChangeIndex < delta.getSource().getPosition()) {
                    wordHighlightedText.append(oldTokens[startOfChangeIndex]);
                    startOfChangeIndex++;
                }

                if (delta.getType() == DeltaType.DELETE || delta.getType() == DeltaType.CHANGE) {
                    wordHighlightedText.append("<span class='removed'>");
                    for (String line : delta.getSource().getLines()) {
                        wordHighlightedText.append(escapeHtml(line));
                    }
                    wordHighlightedText.append("</span>");
                }

                if (delta.getType() == DeltaType.INSERT || delta.getType() == DeltaType.CHANGE) {
                    wordHighlightedText.append("<span class='added'>");
                    for (String line : delta.getTarget().getLines()) {
                        wordHighlightedText.append(escapeHtml(line));
                    }
                    wordHighlightedText.append("</span>");
                }

                startOfChangeIndex += delta.getSource().size();
            }

            while (startOfChangeIndex < oldTokens.length) {
                wordHighlightedText.append(oldTokens[startOfChangeIndex++]);
            }

            wordHighlightedText.append("</pre>");
            String wordTextToHtml = "<html><body>" + wordHighlightedText.toString() + "</body></html>";
            Platform.runLater(() -> webEngine.loadContent(wordTextToHtml));
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> webEngine.loadContent("<html><body><p>Error processing the content.</p></body></html>"));
        }
    }

    private void highlightChangesByLine(String oldContent, String newContent) {
        StringBuilder highlightedText = new StringBuilder("<style>");
        highlightedText.append(".added { background-color: #ccffcc; } ");
        highlightedText.append(".removed { background-color: #ffcccc; }</style><pre>");

        String[] oldLines = oldContent.split("\\r?\\n");
        String[] newLines = newContent.split("\\r?\\n");

        int minLength = Math.min(oldLines.length, newLines.length);
        for (int i = 0; i < minLength; i++) {
            String oldLine = oldLines[i];
            String newLine = newLines[i];

            if (oldLine.equals(newLine)) {
                highlightedText.append(escapeHtml(oldLine) + "\n");
            } else {
                if (i < oldLines.length) {
                    highlightedText.append("<span class='removed'>");
                    highlightedText.append(escapeHtml(oldLine) + "\n");
                    highlightedText.append("</span>");
                }
                if (i < newLines.length) {
                    highlightedText.append("<span class='added'>");
                    highlightedText.append(escapeHtml(newLine) + "\n");
                    highlightedText.append("</span>");
                }
            }
        }

        if (oldLines.length > newLines.length) {
            for (int i = minLength; i < oldLines.length; i++) {
                highlightedText.append("<span class='removed'>");
                highlightedText.append(escapeHtml(oldLines[i]) + "\n");
                highlightedText.append("</span>");
            }
        } else if (newLines.length > oldLines.length) {
            for (int i = minLength; i < newLines.length; i++) {
                highlightedText.append("<span class='added'>");
                highlightedText.append(escapeHtml(newLines[i]) + "\n");
                highlightedText.append("</span>");
            }
        }

        highlightedText.append("</pre>");
        String textToHtml = "<html><body>" + highlightedText.toString() + "</body></html>";
        Platform.runLater(() -> webEngine.loadContent(textToHtml));
    }

    private void highlightChangesByLineWithTooltip(String oldContent, String newContent) {
        StringBuilder highlightedText = new StringBuilder("<style>");
        highlightedText.append(".changed { background-color: #6495ED; } ");
        highlightedText.append("</style><pre>");

        String[] oldLines = oldContent.split("\\r?\\n");
        String[] newLines = newContent.split("\\r?\\n");

        int minLength = Math.min(oldLines.length, newLines.length);
        for (int i = 0; i < minLength; i++) {
            String oldLine = oldLines[i];
            String newLine = newLines[i];

            if (oldLine.equals(newLine)) {
                highlightedText.append(escapeHtml(oldLine) + "\n");
            } else {
                highlightedText.append("<span class='changed' title='").append(escapeHtml(oldLine)).append(" -> ").append(escapeHtml(newLine)).append("'>");
                highlightedText.append(escapeHtml(newLine) + "\n");
                highlightedText.append("</span>");
            }
        }

        if (oldLines.length > newLines.length) {
            for (int i = minLength; i < oldLines.length; i++) {
                highlightedText.append("<span class='removed'>");
                highlightedText.append(escapeHtml(oldLines[i]) + "\n");
                highlightedText.append("</span>");
            }
        } else if (newLines.length > oldLines.length) {
            for (int i = minLength; i < newLines.length; i++) {
                highlightedText.append("<span class='added'>");
                highlightedText.append(escapeHtml(newLines[i]) + "\n");
                highlightedText.append("</span>");
            }
        }

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
