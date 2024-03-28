package com.dev.station.controller.monitoring;

import com.dev.station.Localizable;
import com.dev.station.logs.JsonLogger;
import com.dev.station.logs.Loggable;
import com.dev.station.manager.LanguageManager;
import com.dev.station.model.SettingsModel;
import com.dev.station.service.FileChangeListener;
import com.dev.station.service.FileContentProvider;
import com.dev.station.service.FileMonitoringService;
import com.dev.station.util.FileUtils;
import com.dev.station.util.alert.AlertUtils;
import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.DeltaType;
import com.github.difflib.patch.Patch;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.commons.text.diff.CommandVisitor;
import org.apache.commons.text.diff.EditScript;
import org.apache.commons.text.diff.StringsComparator;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class VersionControlWindowController implements Localizable, FileChangeListener, Loggable {
    @FXML public WebView versionControlWebView;
    private WebEngine webEngine;
    ResourceBundle bundle;
    SettingsModel settingsModel;
    private String previousContent = "";
    private VersionControlMode versionControlMode;
    private boolean clearFileAfterReading = false;
    private String filePathToClear;
    private FileMonitoringService monitoringService;
    private boolean isLogging = true; //for developer logs
    @FXML public TextArea versionControlTextArea;

    public VersionControlWindowController() {
        LanguageManager.registerForUpdates(this::updateUI);
        settingsModel = new SettingsModel();
    }

    public void setVersionControlMode(VersionControlMode mode) {
        this.versionControlMode = mode;
    }

    public void setMonitoringService(FileMonitoringService service) {
        this.monitoringService = service;
//         Add the current controller to the list of file change listeners
        this.monitoringService.addFileChangeListener(this);
    }

    @FXML private void initialize() {
        bundle = LanguageManager.getResourceBundle();
        //webEngine = versionControlWebView.getEngine();
    }

    public void setInitialContent(String content) {
        previousContent = content;
        //String htmlContent = String.format("%s", escapeHtml(content));
        setLogging("INFO", "Initial HTML Content Set: " + content);
        //Platform.runLater(() -> webEngine.loadContent(htmlContent));
        Platform.runLater(() -> versionControlTextArea.setText(content));
    }

    private String escapeHtml(String string) {
        String escapedText = string.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
//                .replace("\n", "<br>")
//                .replace(" ", "&nbsp;");
        setLogging("INFO", "Escaped Text: " + escapedText);
        return escapedText;
    }

    public String getTranslate(String key) {
        return bundle.getString(key);
    }

    public void getCurrentContent(ContentCallback callback) {
        Platform.runLater(() -> {
            //String content = (String) webEngine.executeScript("document.body.querySelector('pre').innerText");
            String content = versionControlTextArea.getText();
            callback.onContentReceived(content);
        });
    }

    private void highlightChanges(String oldContent, String newContent) {
        oldContent = oldContent.replace("\r\n", "\n").replace("\r", "\n");
        newContent = newContent.replace("\r\n", "\n").replace("\r", "\n");
        setLogging("INFO", "Highlighting Changes:");
        setLogging("INFO", "Old Content: " + oldContent);
        setLogging("INFO", "New Content: " + newContent);
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

    /**
     * Identifying differences & changes by words
     * @param oldContent
     * @param newContent
     */
    private void highlightChangesByWord(String oldContent, String newContent) {
        try {
            // Initializing styles and text preprocessing for HTML.
            StringBuilder wordHighlightedText = new StringBuilder();

            // Get differences between old and new content.
            List<AbstractDelta<String>> deltas = computeDeltas(oldContent, newContent);

            // Building an HTML string with highlighting of changes.
            buildHighlightedHtml(wordHighlightedText, deltas, oldContent);

            // HTML completion and display.
            displayHtmlContent(wordHighlightedText.toString());
        } catch (Exception e) {
            displayErrorMessage();
        }
    }

    private String initialHtmlStyles() {
        return "<style>.added { background-color: #ccffcc; } .removed { background-color: #ffcccc; }</style><pre>";
    }

    private List<AbstractDelta<String>> computeDeltas(String oldContent, String newContent) {
        String[] oldTokens = oldContent.split("(?<=\\s)|(?=\\s+)");
        String[] newTokens = newContent.split("(?<=\\s)|(?=\\s+)");
        return DiffUtils.diff(Arrays.asList(oldTokens), Arrays.asList(newTokens)).getDeltas();
    }

//    private void buildHighlightedHtml(StringBuilder wordHighlightedText, List<AbstractDelta<String>> deltas, String oldContent) {
//        String[] oldTokens = oldContent.split("(?<=\\s)|(?=\\s+)");
//        int startOfChangeIndex = 0;
//        for (AbstractDelta<String> delta : deltas) {
//            // Logging before adding tokens before the change begins.
//            setLogging("INFO", "Adding unchanged tokens before change at position: " + startOfChangeIndex);
//
//            // Adding tokens before the change begins.
//            while (startOfChangeIndex < delta.getSource().getPosition()) {
//                wordHighlightedText.append(oldTokens[startOfChangeIndex++]);
//            }
//
//            // Logging the processing of changes.
//            setLogging("INFO", "Processing change: " + delta);
//
//            // Processing changes.
//            highlightDelta(wordHighlightedText, delta);
//
//            // Move to next change.
//            startOfChangeIndex += delta.getSource().size();
//        }
//
//        // Adding remaining tokens.
//        setLogging("INFO", "Adding remaining tokens after last change.");
//
//        // Adding remaining tokens.
//        for (int i = startOfChangeIndex; i < oldTokens.length; i++) {
//            wordHighlightedText.append(oldTokens[i]);
//        }
//
//        wordHighlightedText.append("</pre>");
//
//        // Logging the final HTML content.
//        setLogging("INFO", "Final HTML Content Prepared: " + wordHighlightedText.toString());
//    }

    private void buildHighlightedHtml(StringBuilder highlightedText, List<AbstractDelta<String>> deltas, String oldContent) {
        int startOfChangeIndex = 0;
        String[] oldTokens = oldContent.split("(?<=\\s)|(?=\\s+)");

        for (AbstractDelta<String> delta : deltas) {
            // Add tokens before the change starts
            while (startOfChangeIndex < delta.getSource().getPosition()) {
                highlightedText.append(oldTokens[startOfChangeIndex++]);
            }

            // Processing changes
            if (delta.getType() == DeltaType.DELETE) {
                highlightedText.append("[Deleted: ").append(String.join("", delta.getSource().getLines())).append("]");
            } else if (delta.getType() == DeltaType.INSERT) {
                highlightedText.append("[Added: ").append(String.join("", delta.getTarget().getLines())).append("]");
            } else if (delta.getType() == DeltaType.CHANGE) {
                highlightedText.append("[Changed from: ").append(String.join("", delta.getSource().getLines())).append(" на: ").append(String.join("", delta.getTarget().getLines())).append("]");
            }

            startOfChangeIndex += delta.getSource().size();
        }

        // Add the remaining tokens
        for (int i = startOfChangeIndex; i < oldTokens.length; i++) {
            highlightedText.append(oldTokens[i]);
        }
    }

    private void highlightDelta(StringBuilder wordHighlightedText, AbstractDelta<String> delta) {
        String sourceText = escapeHtml(String.join("", delta.getSource().getLines()));
        String targetText = escapeHtml(String.join("", delta.getTarget().getLines()));

        if (delta.getType() == DeltaType.DELETE || delta.getType() == DeltaType.CHANGE) {
            setLogging("INFO", "Deleted/Changed Source: " + sourceText);
            wordHighlightedText.append("<span class='removed'>").append(escapeHtml(String.join("", delta.getSource().getLines()))).append("</span>");
        }
        if (delta.getType() == DeltaType.INSERT || delta.getType() == DeltaType.CHANGE) {
            setLogging("INFO", "Inserted/Changed Target: " + targetText);
            wordHighlightedText.append("<span class='added'>").append(escapeHtml(String.join("", delta.getTarget().getLines()))).append("</span>");
        }
    }

    private void displayHtmlContent(String htmlContent) {
        setLogging("INFO", "Final HTML Content: " + htmlContent);
        //Platform.runLater(() -> webEngine.loadContent("<html><body>" + htmlContent + "</body></html>"));
        Platform.runLater(() -> versionControlTextArea.setText(htmlContent));
    }

    private void displayErrorMessage() {
        Platform.runLater(() -> webEngine.loadContent("<html><body><p>Error processing the content.</p></body></html>"));
    }

//    ====================================================

    /**
     * For highlight changes by line
     * @param oldContent
     * @param newContent
     */
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

    public void setClearFileAfterReading(boolean clearFileAfterReading) {
        this.clearFileAfterReading = clearFileAfterReading;
    }

    public void setFilePathToClear(String filePath) {
        this.filePathToClear = filePath;
    }

    /**
     * @param contentProvider
     * Overriding onFileChange to handle file changes
     */
    @Override public void onFileChange(FileContentProvider contentProvider) {
        Platform.runLater(() -> {
            try {
                String newContent = contentProvider.getContent().replace("\r\n", "\n").replace("\r", "\n");
                setLogging("INFO", "File Changed, New Content: " + newContent);
                // Call a method to highlight changes
                highlightChanges(previousContent, newContent);
                previousContent = newContent;

                // If clear button enable
                if (clearFileAfterReading) {
                    FileUtils.clearFileAndSetLastModified(filePathToClear, monitoringService, getTranslate("alert.error.setLastModified"));
                }
            } catch (IOException e) {
                AlertUtils.showErrorAlert("", e.getMessage());
            }
        });
    }

    @Override public void loadSavedLanguage() {
        String savedLanguage = settingsModel.loadLanguageSetting();
        Locale locale = LanguageManager.getLocale(savedLanguage);
        LanguageManager.switchLanguage(locale);
    }

    @Override public void switchLanguage(Locale newLocale) {
        bundle = LanguageManager.getResourceBundle();
    }

    @Override public void updateUI() {

    }

    @Override
    public void setLogging(String level, String message) {
        if(isLogging) {
            JsonLogger.log(level, message);
        }
    }
}
