package com.dev.station.controller.tab;

import com.dev.station.Localizable;
import com.dev.station.controller.monitoring.VersionControlMode;
import com.dev.station.controller.monitoring.VersionControlWindowController;
import com.dev.station.logs.JsonLogger;
import com.dev.station.logs.Loggable;
import com.dev.station.manager.LanguageManager;
import com.dev.station.manager.NotificationManager;
import com.dev.station.manager.WindowManager;
import com.dev.station.manager.monitoring.MonitoringJsonTabsManager;
import com.dev.station.manager.monitoring.MonitoringTabData;
import com.dev.station.model.SettingsModel;
import com.dev.station.service.FileChangeListener;
import com.dev.station.service.FileContentProvider;
import com.dev.station.service.FileMonitoringService;
import com.dev.station.util.alert.AlertUtils;
import com.dev.station.util.FileUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

public class MonitoringTabController implements Localizable, FileChangeListener, Loggable {

    @FXML public Label filePathLabel;
    @FXML public TextField filePath;
    @FXML public Label fileNameLabel;
    @FXML public TextField fileName;
    @FXML public Label monitoringFrequencyLabel;
    @FXML public TextField monitoringFrequency;
    @FXML public ToggleButton toggleMonitoring;
    @FXML public ToggleGroup monitoringToggleGroup;
    @FXML public Button openContentButton;
    @FXML public ToggleButton clearContentToggle;
    @FXML public Button saveSettingsButton;
    @FXML public TextArea fileContentArea;
    @FXML public Button viewFileContentButton;
    @FXML public Button versionControlButton;
    @FXML public Label versionControlModeLabel;
    @FXML public ComboBox versionControlModeComboBox;
    ResourceBundle bundle;
    private NotificationManager notificationManager;
    private Tab myTab;
    SettingsModel settingsModel;
    private Timer timer;
    FileMonitoringService monitoringService;
    String fullFilePath;
    private Stage monitoringWindowStage;
    private Stage versionControlWindowStage;
    VersionControlMode mode;
    private String selectedVersionControlMode;
    MonitoringTabData tabData;
    private boolean isLogging = true;

    public MonitoringTabController() {
        LanguageManager.registerForUpdates(this::updateUI);
        settingsModel = new SettingsModel();
    }

    public Tab getMyTab() {
        return myTab;
    }

    public void setMyTab(Tab myTab) {
        this.myTab = myTab;
    }

    public MonitoringTabData getTabData() {
        return tabData;
    }

    public void setTabData(MonitoringTabData tabData) {
        this.tabData = tabData;
    }

    /**
     * @param actionEvent
     * Start and stop monitoring in textArea
     */
    @FXML public void handleMonitoringAction(ActionEvent actionEvent) {
        String filePathValue = filePath.getText();
        String fileNameValue = fileName.getText();

        if (!fileExists(filePathValue, fileNameValue)) {
            AlertUtils.showErrorAlert("", getTranslate("alert.fileNotFound") + " " + filePathValue + File.separator + fileNameValue);
            doIfFileNotExists();

            return;
        }

        if (toggleMonitoring.isSelected()) {
            fileContentArea.setVisible(true);
            startMonitoring();
        } else {
            stopMonitoring();
            fileContentArea.setVisible(false);
            if (monitoringWindowStage != null) {
                monitoringWindowStage.close();
                monitoringWindowStage = null;
            }
        }
    }

    /**
     * @param event
     * Shows file contents from textArea in new window (if monitoring is active)
     */
    @FXML public void handleOpenContentAction(ActionEvent event) {
        String filePathValue = filePath.getText();
        String fileNameValue = fileName.getText();

        if (!fileExists(filePathValue, fileNameValue)) {
            AlertUtils.showErrorAlert("", getTranslate("alert.fileNotFound") + " " + filePathValue + File.separator + fileNameValue);
            doIfFileNotExists();
            return;
        }

        if (!toggleMonitoring.isSelected()) {
            AlertUtils.showErrorAlert("", getTranslate("monitoringTabController.monitoringNotEnabled"));
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dev/station/ui/tab/MonitoringWindow.fxml"));
            Parent root = loader.load();

            MonitoringWindowController controller = loader.getController();
            controller.setInitialContent(fileContentArea.getText());

            controller.setClearFileAfterReading(clearContentToggle.isSelected());
            controller.setFilePathToClear(filePath.getText() + "/" + fileName.getText());
            controller.setMonitoringService(monitoringService);

            monitoringService.addFileChangeListener(controller);

            Scene scene = new Scene(root, 825, 600);
            monitoringWindowStage = new Stage();
            monitoringWindowStage.setTitle(getTranslate("monitoringTabController.handleOpenContentButtonAction.stage"));
            monitoringWindowStage.setScene(scene);

            monitoringWindowStage.setOnCloseRequest(windowEvent -> {
                if (toggleMonitoring.isSelected()) {
                    fileContentArea.setVisible(true);
                    Platform.runLater(() -> fileContentArea.setText(controller.getCurrentContent()));
                }
            });

            WindowManager.addStage(monitoringWindowStage);
            monitoringWindowStage.show();
            fileContentArea.setVisible(false);
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("", e.getMessage());
        }
    }

    /**
     * @param actionEvent
     * Shows file contents from textArea with changes highlighted (if monitoring is active).
     * Don't use with clear content button.
     */
    @FXML public void handleVersionControlAction(ActionEvent actionEvent) {
        String filePathValue = filePath.getText();
        String fileNameValue = fileName.getText();

        if (!fileExists(filePathValue, fileNameValue)) {
            AlertUtils.showErrorAlert("", getTranslate("alert.fileNotFound") + " " + filePathValue + File.separator + fileNameValue);
            doIfFileNotExists();
            return;
        }

        if (!toggleMonitoring.isSelected()) {
            AlertUtils.showErrorAlert("", getTranslate("monitoringTabController.monitoringNotEnabled"));
            return;
        }

        setLogging("INFO", "1. Let's start loading FXML.");

        try {
            setLogging("INFO", "2. Create FXMLLoader.");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dev/station/ui/monitoring/VersionControlWindow.fxml"));

            setLogging("INFO", "3. Loading FXML.");
            Parent root = loader.load();

            setLogging("INFO", "4. FXML loaded successfully.");
            VersionControlWindowController controller = loader.getController();
            controller.setInitialContent(fileContentArea.getText());

            boolean isClearEnable = clearContentToggle.isSelected();
            // Set clearContentToggle value
            controller.setClearFileAfterReading(isClearEnable);
            if(isClearEnable){
                // Set file for cleaning
                //controller.setFilePathToClear(filePathValue + "/" + fileNameValue);
            }

            setLogging("INFO", "5. Setting up version control mode.");
            // Set version control mode
            controller.setVersionControlMode(getSelectedVersionControlMode());
            // Set monitoring service for using same monitoring action
            controller.setMonitoringService(monitoringService);

            setLogging("INFO", "6. Setting up a file change listener.");
            monitoringService.addFileChangeListener(controller);

            setLogging("INFO", "7. Displaying the version control window.");
            Scene scene = new Scene(root, 825, 600);
            versionControlWindowStage = new Stage();
            versionControlWindowStage.setTitle(getTranslate("versionControlWindowController.title"));
            versionControlWindowStage.setScene(scene);

            versionControlWindowStage.setOnCloseRequest(windowEvent -> {
                // If monitoring is enabled, save the current contents of the file in the main UI
                if (toggleMonitoring.isSelected()) {
                    fileContentArea.setVisible(true);
                    controller.getCurrentContent(content -> Platform.runLater(() -> fileContentArea.setText(content)));
                }
                // Remove the controller from the list of file change listeners in FileMonitoringService
                monitoringService.removeFileChangeListener(controller);
            });

            WindowManager.addStage(versionControlWindowStage);

            setLogging("INFO", "8. The version control window has been successfully created. Displaying...");
            versionControlWindowStage.show();
            fileContentArea.setVisible(false);

            setLogging("INFO", "9. The window is successfully opened for: " + filePathValue + " -> " + fileNameValue);
        } catch (Throwable e) {
            AlertUtils.showErrorAlert("", e.getMessage());
            e.printStackTrace();
        }
    }

    private void doIfFileNotExists() {
        toggleMonitoring.setSelected(false);

        stopMonitoring();
        fileContentArea.setVisible(false);
        if (monitoringWindowStage != null) {
            monitoringWindowStage.close();
            monitoringWindowStage = null;
        }
    }

    private VersionControlMode getSelectedVersionControlMode() {
        String selectedMode = versionControlModeComboBox.getSelectionModel().getSelectedItem().toString();
        switch (selectedMode) {
            case "символ":
            case "symbol":
                mode = VersionControlMode.SYMBOL;
                break;
            case "слово":
            case "word":
                mode = VersionControlMode.WORD;
                break;
            case "строка":
            case "line":
                mode = VersionControlMode.LINE;
                break;
            case "подсказка":
            case "tooltip":
                mode = VersionControlMode.TOOLTIP;
                break;
            default:
                mode = VersionControlMode.SYMBOL;
        }
        return mode;
    }

    @FXML public void handleViewFileAction(ActionEvent actionEvent) {
        fullFilePath = filePath.getText() + "\\" + fileName.getText();

        try {
            File file = new File(fullFilePath);

            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            StringBuilder content = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append("\n");
            }

            bufferedReader.close();

            Stage stage = new Stage();
            VBox root = new VBox();
            TextArea textArea = new TextArea();
            textArea.setText(content.toString());
            textArea.setEditable(false);

            VBox.setVgrow(textArea, Priority.ALWAYS);

            root.getChildren().add(textArea);

            Scene scene = new Scene(root, 825, 600);
            stage.setScene(scene);
            stage.setTitle(getTranslate("monitoringTabController.handleViewFileAction.stage"));

            WindowManager.addStage(stage);
            stage.show();
        } catch (FileNotFoundException e) {
            AlertUtils.showErrorAlert("", getTranslate("alert.fileNotFound") + " " + fullFilePath);
        } catch (IOException e) {
            AlertUtils.showErrorAlert("", getTranslate("alert.fileErrorRead") + " " + fullFilePath);
        } catch (Exception e) {
            AlertUtils.showErrorAlert("", getTranslate("alert.fileErrorUnknown") + " " + e.getMessage());
        }

    }

    private void startMonitoring() {
        if (monitoringService != null) {
            monitoringService.stopMonitoring();
        }

        String filePathValue = filePath.getText();
        String fileNameValue = fileName.getText();
        int frequency;
        try {
            frequency = Integer.parseInt(monitoringFrequency.getText());
        } catch (NumberFormatException e) {
            AlertUtils.showErrorAlert("", getTranslate("monitoringTabController.frequencyError"));
            return;
        }

        monitoringService = new FileMonitoringService(filePathValue, fileNameValue, this);
        monitoringService.startMonitoring(frequency);
    }

    private void stopMonitoring() {
        if (monitoringService != null) {
            monitoringService.stopMonitoring();
        }
    }

    @FXML public void handleSaveSettingsAction(ActionEvent actionEvent) {
        String filePathValue = filePath.getText();
        String fileNameValue = fileName.getText();
        int frequency;
        try {
            frequency = Integer.parseInt(monitoringFrequency.getText());
        } catch (NumberFormatException e) {
            AlertUtils.showErrorAlert("", getTranslate("monitoringTabController.frequencyError"));
            return;
        }

        boolean toggleMonitoringValue = toggleMonitoring.isSelected();
        boolean clearContentToggleValue = clearContentToggle.isSelected();

        String tabIdToUpdate = myTab.getId();

        String versionControlMode = versionControlModeComboBox.getValue().toString();

        if (!fileExists(filePathValue, fileNameValue)) {
            AlertUtils.showErrorAlert("", getTranslate("alert.fileNotFound") + " " + filePathValue + File.separator + fileNameValue);
            return;
        }

        updateMonitoringTab(tabIdToUpdate, filePathValue, fileNameValue, frequency, toggleMonitoringValue, false, false, clearContentToggleValue, versionControlMode);
    }

    private boolean fileExists(String filePath, String fileName) {
        String fullFilePath = filePath + File.separator + fileName;
        File file = new File(fullFilePath);
        return file.exists();
    }

    @FXML public void initialize() {
        bundle = LanguageManager.getResourceBundle();

        notificationManager = new NotificationManager(bundle);
        LanguageManager.registerNotificationManager(notificationManager);

        setMultilingual();
        loadSavedLanguage();

        updateToggleButtonText();

        toggleMonitoring.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updateToggleMonitoring();
        });

        clearContentToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updateClearContentToggle();
        });

        setTooltips();
        setComboBox();
    }

    private void setComboBox() {
        ObservableList<String> versionControlModes = FXCollections.observableArrayList(
                getTranslate("monitoringTabController.versionControlModeComboBox.symbol"),
                getTranslate("monitoringTabController.versionControlModeComboBox.word"),
                getTranslate("monitoringTabController.versionControlModeComboBox.line"),
                getTranslate("monitoringTabController.versionControlModeComboBox.tooltip")
        );
        versionControlModeComboBox.setItems(versionControlModes);
        versionControlModeComboBox.setOnMouseEntered(event -> versionControlModeComboBox.getScene().setCursor(Cursor.HAND));
        versionControlModeComboBox.setOnMouseExited(event -> versionControlModeComboBox.getScene().setCursor(Cursor.DEFAULT));
    }

    private void updateMonitoringTab(String tabId, String filePath, String fileName, int monitoringFrequency, boolean toggleMonitoring, boolean openContentButton, boolean parseAsArrayToggle, boolean clearContentToggle, String versionControlMode) {
        MonitoringJsonTabsManager jsonTabsManager = new MonitoringJsonTabsManager();
        List<MonitoringTabData> tabs = jsonTabsManager.loadMonitoringTabs(1, "Monitoring");

        boolean isTabExists = false;

        for (MonitoringTabData tab : tabs) {
            if (tab.getId().equals(getMyTab().getId())) {
                tab.setFilePath(filePath);
                tab.setFileName(fileName);
                tab.setMonitoringFrequency(monitoringFrequency);
                tab.setToggleMonitoring(toggleMonitoring);
                tab.setOpenContentButton(openContentButton);
                tab.setParseAsArrayToggle(parseAsArrayToggle);
                tab.setClearContentToggle(clearContentToggle);
                tab.setVersionControlMode(versionControlMode);

                isTabExists = true;
                break;
            }
        }

        if(!isTabExists) {
            AlertUtils.showErrorAlert("", "No update tab found.");
        } else {
            boolean success = jsonTabsManager.saveMonitoringTabs(1, "Monitoring", tabs);
            if (success) {
                AlertUtils.showSuccessAlert("",getTranslate("alerts.successSaving"));
            } else {
                AlertUtils.showErrorAlert("", getTranslate("alerts.errorSaving"));
            }
        }
    }

    private void setMultilingual() {
        bundle = LanguageManager.getResourceBundle();
        notificationManager = new NotificationManager(bundle);
        LanguageManager.registerNotificationManager(notificationManager);
    }

    public void updateUI(ResourceBundle bundle) {}

    /**
     * @param tabData
     * Loading user values
     */
    public void loadData(MonitoringTabData tabData) {
        setTabData(tabData);

        filePath.setText(tabData.getFilePath());
        fileName.setText(tabData.getFileName());
        monitoringFrequency.setText(String.valueOf(tabData.getMonitoringFrequency()));
        toggleMonitoring.setSelected(tabData.isToggleMonitoring());
        clearContentToggle.setSelected(tabData.isClearContentToggle());

        // Code to set the selected version control mode
        setSelectedVersion(tabData.getVersionControlMode());

        // Code to start or stop monitoring
        if (toggleMonitoring.isSelected()) {
            fileContentArea.setVisible(true);
            startMonitoring();
        } else {
            fileContentArea.setVisible(false);
            if (timer != null) {
                stopMonitoring();
            }
        }
    }

    private void setSelectedVersion(String versionControlMode) {
        if (versionControlMode != null && !versionControlMode.isEmpty()) {
            String translatedMode = "";
            switch (versionControlMode) {
                case "символ":
                case "symbol":
                    translatedMode = getTranslate("monitoringTabController.versionControlModeComboBox.symbol");
                    break;
                case "слово":
                case "word":
                    translatedMode = getTranslate("monitoringTabController.versionControlModeComboBox.word");
                    break;
                case "строка":
                case "line":
                    translatedMode = getTranslate("monitoringTabController.versionControlModeComboBox.line");
                    break;
                case "подсказка":
                case "tooltip":
                    translatedMode = getTranslate("monitoringTabController.versionControlModeComboBox.tooltip");
                    break;
                default:
                    translatedMode = getTranslate("monitoringTabController.versionControlModeComboBox.symbol");
            }

            versionControlModeComboBox.getSelectionModel().select(translatedMode);
        }
    }

    public String getTranslate(String key) {
        return bundle.getString(key);
    }

    private void setTooltips() {
        Tooltip.install(toggleMonitoring, new Tooltip(getTranslate("monitoringTabController.toggleMonitoring.tooltip")));
        Tooltip.install(openContentButton, new Tooltip(getTranslate("monitoringTabController.openContentButton.tooltip")));
        Tooltip.install(viewFileContentButton, new Tooltip(getTranslate("monitoringTabController.viewFileContentButton.tooltip")));
        Tooltip.install(versionControlButton, new Tooltip(getTranslate("monitoringTabController.versionControlButton.tooltip")));
        Tooltip.install(clearContentToggle, new Tooltip(getTranslate("monitoringTabController.clearContentToggle.tooltip")));
        Tooltip.install(saveSettingsButton, new Tooltip(getTranslate("monitoringTabController.saveSettingsButton.tooltip")));
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
        versionControlModeLabel.setText(getTranslate("monitoringTabController.versionControlModeLabel"));

        updateToggleButtonText();

        openContentButton.setText(getTranslate("monitoringTabController.openContentButton"));
        viewFileContentButton.setText(getTranslate("monitoringTabController.viewFileContentButton"));
        versionControlButton.setText(getTranslate("monitoringTabController.versionControlButton"));

        saveSettingsButton.setText(getTranslate("monitoringTabController.saveSettingsButton"));
        setTooltips();

        ObservableList<String> versionControlModes = FXCollections.observableArrayList(
                getTranslate("monitoringTabController.versionControlModeComboBox.symbol"),
                getTranslate("monitoringTabController.versionControlModeComboBox.word"),
                getTranslate("monitoringTabController.versionControlModeComboBox.line"),
                getTranslate("monitoringTabController.versionControlModeComboBox.tooltip")
        );
        versionControlModeComboBox.setItems(versionControlModes);
        if(getTabData()!= null){
            setSelectedVersion(getTabData().getVersionControlMode());
        }
    }

    private void updateToggleButtonText() {
        updateToggleMonitoring();
        updateClearContentToggle();
    }

    private void updateToggleMonitoring() {
        if (toggleMonitoring.isSelected()) {
            toggleMonitoring.setText(getTranslate("monitoringTabController.toggleMonitoring.on"));
        } else {
            toggleMonitoring.setText(getTranslate("monitoringTabController.toggleMonitoring.off"));
        }
    }

    private void updateClearContentToggle() {
        if(clearContentToggle.isSelected()) {
            clearContentToggle.setText(getTranslate("monitoringTabController.clearContentToggle.on"));
        } else {
            clearContentToggle.setText(getTranslate("monitoringTabController.clearContentToggle.off"));
        }
    }

    @Override public void onFileChange(FileContentProvider contentProvider) {
        Platform.runLater(() -> {
            try {
                String content = contentProvider.getContent();
                fileContentArea.setText(content);

                if (clearContentToggle.isSelected()) {
                    String fullFilePath = filePath.getText() + "/" + fileName.getText();
                    String errorMessage = getTranslate("alert.error.setLastModified");
                    FileUtils.clearFileAndSetLastModified(fullFilePath, monitoringService, errorMessage);
                }
            } catch (IOException e) {
                AlertUtils.showErrorAlert("", e.getMessage());
            }
        });
    }

    @Override public void setLogging(String level, String message) {
        if(isLogging) {
            JsonLogger.log(level, message);
        }
    }
}
