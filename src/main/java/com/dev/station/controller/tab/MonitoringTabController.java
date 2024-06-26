package com.dev.station.controller.tab;

import com.dev.station.Localizable;
import com.dev.station.logs.JsonLogger;
import com.dev.station.logs.Loggable;
import com.dev.station.manager.*;
import com.dev.station.manager.monitoring.MonitoringTabData;
import com.dev.station.service.FileChangeListener;
import com.dev.station.service.FileContentProvider;
import com.dev.station.service.FileMonitoringHandler;
import com.dev.station.service.FileMonitoringService;
import com.dev.station.util.*;
import com.dev.station.util.alert.AlertUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
    @FXML public TextField monitoringFrequency;
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
    //private FileHandler fileHandler;
    private FileUtils fileUtils;
    private TabManager tabManager;
    private SettingsManager settingsManager;
    private FileValidationHandler fileValidationHandler;
    private VersionControlWindowHandler versionControlWindowHandler;
    private TabDataLoader tabDataLoader;
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
     * Start and stop monitoring in textArea - кнопка ВКЛ мониторинг
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
            fileMonitoringHandler.startMonitoring(path, file, Integer.parseInt(monitoringFrequency.getText()));
        } else {
            fileMonitoringHandler.stopMonitoring();
            fileContentArea.setVisible(false);
            closeStage(monitoringWindowStage);
        }
    }

    @FXML public void handleOpenContentAction(ActionEvent event) {
        if (!validateMonitoringState()) return;
        try {
            openContentWindow();
        } catch (IOException e) {
            AlertUtils.showErrorAlert("", e.getMessage());
        }
    }

    /**
     * Shows file contents from textArea with changes highlighted (if monitoring is active).
     */
    @FXML public void handleVersionControlAction(ActionEvent actionEvent) {
        if (!validateMonitoringState()) return;
        try {
            versionControlWindowHandler.openVersionControlWindow(fileContentArea.getText(), versionControlWindowHandler.getSelectedVersionControlMode(versionControlModeComboBox));
        } catch (Exception e) {
            AlertUtils.showErrorAlert("", e.getMessage());
        }
    }

    /**
     * Shows file contents from textArea in new window (if monitoring is active)
     */
    private void openContentWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dev/station/ui/tab/MonitoringWindow.fxml"));
        Parent root = loader.load();

        MonitoringWindowController controller = loader.getController();
        controller.setInitialContent(fileContentArea.getText());

        controller.setClearFileAfterReading(clearContentToggle.isSelected());
        controller.setFilePathToClear(fullFilePath);
        controller.setMonitoringService(monitoringService);

        monitoringService.addFileChangeListener(controller);

        monitoringWindowStage = uiUpdater.createStage(root, "monitoringTabController.handleOpenContentButtonAction.stage");
        monitoringWindowStage.setOnCloseRequest(windowEvent -> {
            if (toggleMonitoring.isSelected()) {
                fileContentArea.setVisible(true);
                Platform.runLater(() -> fileContentArea.setText(controller.getCurrentContent()));
            }
        });

        WindowManager.addStage(monitoringWindowStage);
        monitoringWindowStage.show();
        fileContentArea.setVisible(false);
    }

    @FXML public void handleViewFileAction(ActionEvent actionEvent) {
        fileUtils.displayFileContent(getFullFilePath(), uiUpdater);
    }

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
        versionControlWindowHandler = new VersionControlWindowHandler(bundle, fileMonitoringHandler.getMonitoringService(), toggleMonitoring, fileContentArea);
        tabDataLoader = new TabDataLoader(uiUpdater, fileMonitoringHandler);

        setMultilingual();
        loadSavedLanguage();

        uiUpdater.updateToggleButtonText(toggleMonitoring, clearContentToggle);

        toggleMonitoring.selectedProperty().addListener((observable, oldValue, newValue) -> uiUpdater.updateToggleMonitoringText(toggleMonitoring));
        clearContentToggle.selectedProperty().addListener((observable, oldValue, newValue) ->uiUpdater.updateClearContentToggleText(clearContentToggle));

        uiUpdater.setTooltips(toggleMonitoring, openContentButton, viewFileContentButton, versionControlButton, clearContentToggle, saveSettingsButton);
        uiUpdater.setComboBoxItems(versionControlModeComboBox);
    }

    private String getFullFilePath() {
        return filePath.getText() + "\\" + fileName.getText();
    }

    private boolean validateSettings() {
        String filePathValue = filePath.getText();
        String fileNameValue = fileName.getText();

        if (!fileValidationHandler.validateFile(filePathValue, fileNameValue, bundle)) {
            return false;
        }

        try {
            Integer.parseInt(monitoringFrequency.getText());
        } catch (NumberFormatException e) {
            AlertUtils.showErrorAlert("", getTranslate("monitoringTabController.frequencyError"));
            return false;
        }

        return true;
    }

    private void saveSettings() {
        String tabId = myTab.getId();
        String filePathValue = filePath.getText();
        String fileNameValue = fileName.getText();
        int frequency = Integer.parseInt(monitoringFrequency.getText());
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
            AlertUtils.showErrorAlert("", getTranslate("monitoringTabController.monitoringNotEnabled"));
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

    /**
     * Using in MonitoringTabManager
     */
    public void loadData(MonitoringTabData tabData) {
        tabDataLoader.loadData(tabData, filePath, fileName, monitoringFrequency, toggleMonitoring, clearContentToggle, versionControlModeComboBox, fileContentArea);
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
                AlertUtils.showErrorAlert("", e.getMessage());
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
