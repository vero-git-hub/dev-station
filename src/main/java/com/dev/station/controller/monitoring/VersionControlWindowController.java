package com.dev.station.controller.monitoring;

import com.dev.station.Localizable;
import com.dev.station.controller.monitoring.highlight.HighlightChangesByWord;
import com.dev.station.controller.monitoring.highlight.HighlightStrategy;
import com.dev.station.logs.JsonLogger;
import com.dev.station.logs.Loggable;
import com.dev.station.manager.LanguageManager;
import com.dev.station.model.SettingsModel;
import com.dev.station.service.FileChangeListener;
import com.dev.station.service.FileContentProvider;
import com.dev.station.service.FileMonitoringService;
import com.dev.station.util.FileUtils;
import com.dev.station.util.alert.AlertUtils;
import com.dev.station.util.alert.HeaderAlertUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.util.*;

import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

public class VersionControlWindowController implements Localizable, FileChangeListener, Loggable {
    @FXML private AnchorPane anchorPane;
    private StyleClassedTextArea versionControlTextArea;
    private HighlightStrategy highlightStrategy;
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

    public VersionControlWindowController() {
        LanguageManager.registerForUpdates(this::updateUI);
        settingsModel = new SettingsModel();
    }

    public void setVersionControlMode(VersionControlMode mode) {
        this.versionControlMode = mode;
    }

    public void setMonitoringService(FileMonitoringService service) {
        this.monitoringService = service;
    }

    public void setHighlightStrategy(HighlightStrategy highlightStrategy) {
        this.highlightStrategy = highlightStrategy;
    }

    @FXML private void initialize() {
        bundle = LanguageManager.getResourceBundle();

        versionControlTextArea = new StyleClassedTextArea();

        versionControlTextArea.setWrapText(true);
        versionControlTextArea.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());

        // Wrap in ScrollPane for scrolling
        StackPane stackPane = new StackPane(versionControlTextArea);
        AnchorPane.setTopAnchor(stackPane, 0.0);
        AnchorPane.setBottomAnchor(stackPane, 0.0);
        AnchorPane.setLeftAnchor(stackPane, 0.0);
        AnchorPane.setRightAnchor(stackPane, 0.0);

        anchorPane.getChildren().add(stackPane);
    }

    /**
     * Called in MonitoringTabController
     * @param content
     * @param mode
     * @param service
     */
    public void prepareToVersionControl(String content, VersionControlMode mode, FileMonitoringService service) {
        setInitialContent(content);
        setVersionControlMode(mode);
        setMonitoringService(service);
    }

    public void setInitialContent(String content) {
        Platform.runLater(() -> {
            versionControlTextArea.clear();
            versionControlTextArea.appendText(content);
        });
        previousContent = content;
        setLogging("INFO", "[VersionControlWindowController - setInitialContent] Initial HTML Content Set: " + content);
    }

    public String getTranslate(String key) {
        return bundle.getString(key);
    }

    public void getCurrentContent(ContentCallback callback) {
        Platform.runLater(() -> {
            String content = versionControlTextArea.getText();
            callback.onContentReceived(content);
        });
    }

    /**
     * Using interface for highlight changes
     * @param oldContent
     * @param newContent
     */
    private void highlightChanges(String oldContent, String newContent) {
        System.out.println("highlightChanges called"); // Для отладки
        chooseVersionControlMode();

        if (highlightStrategy != null) {
            System.out.println("Old content: " + oldContent);
            System.out.println("New content: " + newContent);

            StyleSpans<Collection<String>> highlightedTextSpans = highlightStrategy.highlightChanges(oldContent, newContent);
            System.out.println("Highlighted text spans: " + highlightedTextSpans); // Для отладки

            //Highlighting changes in text
            Platform.runLater(() -> {
                versionControlTextArea.replaceText(newContent);
                versionControlTextArea.setStyle("-fx-text-fill: black;"); // Применяем явные стили для теста
                versionControlTextArea.setStyleSpans(0, highlightedTextSpans);
            });
        } else {
            setLogging("ERROR", "[highlightChanges] HighlightStrategy is null");
        }
    }

    private void chooseVersionControlMode() {
        switch (versionControlMode) {
            case SYMBOL:
                //highlightStrategy = new HighlightChangesBySymbol();
                break;
            case WORD:
                highlightStrategy = new HighlightChangesByWord();
                break;
            case LINE:
                //processedContent = highlightChangesByLine(oldContent, newContent);
                break;
            case TOOLTIP:
                //processedContent = highlightChangesByLineWithTooltip(oldContent, newContent);
                break;
        }
    }

    public void setClearFileAfterReading(boolean clearFileAfterReading) {
        this.clearFileAfterReading = clearFileAfterReading;
    }

    public void setFilePathToClear(String filePath) {
        this.filePathToClear = filePath;
    }

    /**
     * Overriding onFileChange to handle file changes
     * @param contentProvider
     */
    @Override public void onFileChange(FileContentProvider contentProvider) {
        setLogging("INFO", "[VersionControlWindowController - onFileChange] Call onFileChange");
        Platform.runLater(() -> {
            try {
                String newContent = contentProvider.getContent().replace("\r\n", "\n").replace("\r", "\n");

                setLogging("INFO", "[onFileChange] File Changed, New Content: " + newContent);

                highlightChanges(previousContent, newContent);
                previousContent = newContent;
            } catch (Throwable e) {
                HeaderAlertUtils.showErrorAlert("", e.getMessage());
            }
        });
    }

    private void testHighlight() {
        Platform.runLater(() -> {
            StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
            spansBuilder.add(Collections.singleton("highlight"), 5); // Подсветка первых 5 символов
            versionControlTextArea.setStyleSpans(0, spansBuilder.create());
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

    @Override public void updateUI() {}

    @Override public void setLogging(String level, String message) {
        if(isLogging) {
            JsonLogger.log(level, message);
        }
    }
}