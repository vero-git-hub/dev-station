package com.dev.station.controller.tab;

import com.dev.station.Localizable;
import com.dev.station.controller.monitoring.FileMonitorAppColor;
import com.dev.station.logs.JsonLogger;
import com.dev.station.logs.Loggable;
import com.dev.station.manager.LanguageManager;
import com.dev.station.manager.NotificationManager;
import com.dev.station.manager.SettingsManager;
import com.dev.station.manager.TabManager;
import com.dev.station.manager.monitoring.MonitoringTabData;
import com.dev.station.manager.WindowManager;
import com.dev.station.service.FileChangeListener;
import com.dev.station.service.FileContentProvider;
import com.dev.station.service.FileMonitoringHandler;
import com.dev.station.service.FileMonitoringService;
import com.dev.station.util.*;
import com.dev.station.util.alert.AlertUtils;
import com.dev.station.util.alert.HeaderAlertUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Timer;

public class MonitoringTabController implements Localizable, FileChangeListener, Loggable {

    // Field labels
    @FXML public Label filePathLabel;
    @FXML public Label fileNameLabel;
    @FXML public Label monitoringFrequencyLabel;
    @FXML public Label versionControlModeLabel;

    // Fields
    @FXML public TextField filePath;
    @FXML public TextField fileName;
    @FXML public ComboBox<Integer> timerComboBox;
    @FXML public ComboBox<String> versionControlModeComboBox;

    // Buttons
    @FXML public ToggleButton toggleMonitoring;
    @FXML public Button openContentButton;
    @FXML public ToggleButton clearContentToggle;
    @FXML public Button versionControlButton;
    @FXML public Button viewFileContentButton;
    @FXML public Button saveSettingsButton;

    // Show content in area below buttons
    @FXML public TextArea fileContentArea;

    @FXML public VBox root;

    private NotificationManager notificationManager; // For alerts
    private FileMonitoringHandler fileMonitoringHandler;
    private UIUpdater uiUpdater;
    private FileUtils fileUtils;
    private TabManager tabManager;
    private SettingsManager settingsManager;
    private FileValidationHandler fileValidationHandler;
    private VersionControlWindowHandler versionControlWindowHandler;
    private TabDataLoader tabDataLoader;
    private ContentWindowHandler contentWindowHandler;
    private ResourceBundle bundle; // For localization

    private Tab myTab; // Current tab in user interface for saving
    private MonitoringTabData monitoringTabData; // Monitoring tab

    private Timer timer;
    // Regularly checks the last modification time of the specified file.
    // If the file has been modified (last modified time has changed), notifies listeners that changes have occurred.
    private FileMonitoringService monitoringService;
    private String fullFilePath;
    private Stage monitoringWindowStage; // Show content in other window

    public MonitoringTabController() {
        LanguageManager.registerForUpdates(this::updateUI);
        this.settingsManager = new SettingsManager();
    }

    public void setMyTab(Tab myTab) {
        this.myTab = myTab;
    }

    /**
     * Start and stop monitoring in textArea - button ON/OFF monitoring
     */
    @FXML public void handleMonitoringAction(ActionEvent actionEvent) {
        String path = filePath.getText();
        String file = fileName.getText();

        // If the file does not exist, the path to which is taken from the fields
        if (!fileValidationHandler.validateFile(path, file, bundle)) {
            return;
        }

        if (toggleMonitoring.isSelected()) {
            fileContentArea.setVisible(true);
            fileMonitoringHandler.startMonitoring(path, file, timerComboBox.getValue());
        } else {
            fileMonitoringHandler.stopMonitoring();
            closeVersionControlWindows(myTab.getId());
            fileContentArea.setVisible(false);
            closeStage(monitoringWindowStage);
        }
        uiUpdater.updateControlStates(toggleMonitoring, clearContentToggle, saveSettingsButton, filePath, fileName, new TextField(), versionControlModeComboBox);
    }

    private void closeVersionControlWindows(String tabId) {
        for (Stage stage : WindowManager.getMonitoringWindows()) {
            Object userData = stage.getUserData();
            if (userData instanceof FileMonitorAppColor) {
                FileMonitorAppColor fileMonitorAppColor = (FileMonitorAppColor) userData;
                if (fileMonitorAppColor.getTabId().equals(tabId)) {
                    fileMonitorAppColor.stopMonitoring();
                    stage.close();
                }
            }
        }
    }

    /** Open monitoring in another window */
    @FXML public void handleOpenContentAction(ActionEvent event) {
        if (!validateMonitoringState()) return;
        try {
            contentWindowHandler.openContentWindow();
        } catch (IOException e) {
            HeaderAlertUtils.showErrorAlert("", e.getMessage());
        }
    }

    /**
     * Open monitoring with Version Control.
     * Shows file contents from textArea with changes highlighted (if monitoring is active).
     */
    @FXML public void handleVersionControlAction(ActionEvent actionEvent) {
        if (!validateMonitoringState()) return;

        // from fields
        String fullPath = getFullFilePath();
        int checkInterval = timerComboBox.getValue();
        String tabId = myTab.getId();
        boolean isClearContentToggle = clearContentToggle.isSelected();

        if(fileUtils.fileExists(fullPath)){
            try {
                versionControlWindowHandler.openVersionControlWindow(fileContentArea.getText(), versionControlWindowHandler.getSelectedVersionControlMode(versionControlModeComboBox), fullPath, checkInterval, tabId, isClearContentToggle);
            } catch (Exception e) {
                HeaderAlertUtils.showErrorAlert("", e.getMessage());
            }
        }
    }

    /** Show file content in another window */
    @FXML public void handleViewFileAction(ActionEvent actionEvent) {
        fileUtils.displayFileContent(getFullFilePath(), uiUpdater);
    }

    /** Save data from fields to file */
    @FXML public void handleSaveSettingsAction(ActionEvent actionEvent) {
        if (!validateSettings()) return;
        saveSettings();
    }

    @FXML public void initialize() {
        bundle = LanguageManager.getResourceBundle();
        notificationManager = new NotificationManager(bundle);
        LanguageManager.registerNotificationManager(notificationManager);
        fileMonitoringHandler = new FileMonitoringHandler(fileContentArea, getFullFilePath());
        uiUpdater = new UIUpdater(bundle);
        fileUtils = new FileUtils();
        tabManager = new TabManager();
        fileValidationHandler = new FileValidationHandler(fileUtils, toggleMonitoring, fileContentArea);

        monitoringService = new FileMonitoringService(filePath.getText(), fileName.getText(), this);

        versionControlWindowHandler = new VersionControlWindowHandler(bundle, monitoringService, toggleMonitoring, fileContentArea);

        tabDataLoader = new TabDataLoader(uiUpdater, fileMonitoringHandler);
        contentWindowHandler = new ContentWindowHandler(uiUpdater, fileContentArea, toggleMonitoring, clearContentToggle, monitoringService, getFullFilePath());

        setMultilingual();
        loadSavedLanguage();

        uiUpdater.updateToggleButtonText(toggleMonitoring, clearContentToggle);

        toggleMonitoring.selectedProperty().addListener((observable, oldValue, newValue) -> uiUpdater.updateToggleMonitoringText(toggleMonitoring));
        clearContentToggle.selectedProperty().addListener((observable, oldValue, newValue) ->uiUpdater.updateClearContentToggleText(clearContentToggle));

        uiUpdater.setTooltips(toggleMonitoring, openContentButton, viewFileContentButton, versionControlButton, clearContentToggle, saveSettingsButton);
        uiUpdater.setComboBoxItems(versionControlModeComboBox);
        uiUpdater.setTimerComboBoxItems(timerComboBox);
    }

    public String getFullFilePath() {
        return filePath.getText() + "\\" + fileName.getText();
    }

    private boolean validateSettings() {
        String filePathValue = filePath.getText();
        String fileNameValue = fileName.getText();

        if (!fileValidationHandler.validateFile(filePathValue, fileNameValue, bundle)) {
            return false;
        }

        try {
            timerComboBox.getValue();
        } catch (NumberFormatException e) {
            HeaderAlertUtils.showErrorAlert("", getTranslate("monitoringTabController.frequencyError"));
            return false;
        }

        return true;
    }

    private void saveSettings() {
        String tabId = myTab.getId();
        String filePathValue = filePath.getText();
        String fileNameValue = fileName.getText();
        int frequency = timerComboBox.getValue();
        boolean toggleMonitoringValue = toggleMonitoring.isSelected();
        boolean clearContentToggleValue = clearContentToggle.isSelected();
        String versionControlMode = versionControlModeComboBox.getValue();

        tabManager.updateMonitoringTab(
                tabId,
                filePathValue,
                fileNameValue,
                frequency,
                toggleMonitoringValue,
                false,
                false,
                clearContentToggleValue,
                versionControlMode,
                bundle
        );
    }

    public boolean validateMonitoringState() {
        String filePathValue = filePath.getText();
        String fileNameValue = fileName.getText();

        if (!fileValidationHandler.validateFile(filePathValue, fileNameValue, bundle)) {
            return false;
        }

        if (!toggleMonitoring.isSelected()) {
            HeaderAlertUtils.showErrorAlert("", getTranslate("monitoringTabController.monitoringNotEnabled"));
            return false;
        }

        return true;
    }

    private void closeStage(Stage stage) {
        if (stage != null) {
            stage.close();
            stage = null;
        }
    }

    public String getTranslate(String key) {
        return bundle.getString(key);
    }

    private void setMultilingual() {
        bundle = LanguageManager.getResourceBundle();
        notificationManager = new NotificationManager(bundle);
        LanguageManager.registerNotificationManager(notificationManager);
    }

    /** Using in MonitoringTabManager */
    public void loadData(MonitoringTabData tabData) {
        tabDataLoader.loadData(
                tabData,
                filePath,
                fileName,
                timerComboBox,
                toggleMonitoring,
                clearContentToggle,
                versionControlModeComboBox,
                fileContentArea
        );
    }

    public void updateUI(ResourceBundle bundle) {}

    @Override public void loadSavedLanguage() {
        String savedLanguage = settingsManager.loadLanguageSetting();
        Locale locale = settingsManager.getLocale(savedLanguage);
        settingsManager.switchLanguage(locale);
    }

    @Override public void switchLanguage(Locale newLocale) {}

    @Override public void updateUI() {
        bundle = LanguageManager.getResourceBundle();
        uiUpdater.updateLabels(filePathLabel, fileNameLabel, monitoringFrequencyLabel, versionControlModeLabel, openContentButton, viewFileContentButton, versionControlButton, saveSettingsButton);
        uiUpdater.updateToggleMonitoringText(toggleMonitoring);
        uiUpdater.updateClearContentToggleText(clearContentToggle);
        uiUpdater.setComboBoxItems(versionControlModeComboBox);
        if (monitoringTabData != null) {
            uiUpdater.setSelectedVersion(versionControlModeComboBox, monitoringTabData.getVersionControlMode());
        }
    }

    @Override public void onFileChange(FileContentProvider contentProvider) {
        Platform.runLater(() -> {
            try {
                String content = contentProvider.getContent();
                fileContentArea.setText(content);

                if (clearContentToggle.isSelected()) {
                    String errorMessage = getTranslate("alert.error.setLastModified");
                    FileUtils.clearFileAndSetLastModified(fullFilePath, monitoringService, errorMessage);
                }
            } catch (IOException e) {
                HeaderAlertUtils.showErrorAlert("", e.getMessage());
            }
        });
    }

    @Override public void setLogging(String level, String message) {
        boolean isLogging = false;
        if(isLogging) {
            JsonLogger.log(level, message);
        }
    }
}
