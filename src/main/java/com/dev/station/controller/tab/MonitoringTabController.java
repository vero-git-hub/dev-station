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
import com.dev.station.util.FileUtils;
import com.dev.station.util.alert.AlertUtils;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Timer;

import static com.dev.station.util.FileUtils.fileExists;

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
    private Tab myTab; // Current tab in user interface for saving
    private final SettingsModel settingsModel;
    private Timer timer;
    // Regularly checks the last modification time of the specified file.
    // If the file has been modified (last modified time has changed), notifies listeners that changes have occurred.
    private FileMonitoringService monitoringService;
    private String fullFilePath;
    private Stage monitoringWindowStage; // Show content in other window
    private MonitoringTabData monitoringTabData; // Monitoring tab
    private ResourceBundle bundle; // For localization

    public MonitoringTabController() {
        LanguageManager.registerForUpdates(this::updateUI);
        this.settingsModel = new SettingsModel();
    }

    // Getting tab
    public Tab getMyTab() {
        return myTab;
    }

    public void setMyTab(Tab myTab) {
        this.myTab = myTab;
    }

    public MonitoringTabData getMonitoringTabData() {
        return monitoringTabData;
    }

    public void setMonitoringTabData(MonitoringTabData monitoringTabData) {
        this.monitoringTabData = monitoringTabData;
    }

    /**
     * Start and stop monitoring in textArea - кнопка ВКЛ мониторинг
     */
    @FXML public void handleMonitoringAction(ActionEvent actionEvent) {
        String path = filePath.getText();
        String file = fileName.getText();

        // If the file does not exist, the path to which is taken from the fields
        if (!fileExists(path, file)) {
            showAlertFileNotFound(path, file);
            //doIfFileNotExists();
            return;
        }

        if (toggleMonitoring.isSelected()) {
            fileContentArea.setVisible(true);
            startMonitoring();
        } else {
            stopMonitoring();
            fileContentArea.setVisible(false);
            closeStage(monitoringWindowStage);
        }
    }

    @FXML public void handleOpenContentAction(ActionEvent event) {
        if (!validateMonitoringState()) return;

        try {
            openContentWindow();
        } catch (IOException e) {
            handleException(e);
        }
    }

    /**
     * Shows file contents from textArea with changes highlighted (if monitoring is active).
     */
    @FXML public void handleVersionControlAction(ActionEvent actionEvent) {
        if (!validateMonitoringState()) return;

        try {
            openVersionControlWindow();
        } catch (Exception e) {
            handleException(e);
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

        monitoringWindowStage = createStage(root, "monitoringTabController.handleOpenContentButtonAction.stage");
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

    private Stage createStage(Parent root, String titleKey) {
        Stage stage = new Stage();
        Scene scene = new Scene(root, 825, 600);
        stage.setTitle(getTranslate(titleKey));
        stage.setScene(scene);
        return stage;
    }

    @FXML public void handleViewFileAction(ActionEvent actionEvent) {
        fullFilePath = filePath.getText() + "\\" + fileName.getText();
        displayFileContent();
    }

    @FXML
    public void handleSaveSettingsAction(ActionEvent actionEvent) {
        if (!validateSettings()) return;
        saveSettings();
    }

    @FXML public void initialize() {
        bundle = LanguageManager.getResourceBundle();

        notificationManager = new NotificationManager(bundle);
        LanguageManager.registerNotificationManager(notificationManager);

        setMultilingual();
        loadSavedLanguage();

        updateToggleButtonText();

        toggleMonitoring.selectedProperty().addListener((observable, oldValue, newValue) -> updateToggleMonitoringText());
        clearContentToggle.selectedProperty().addListener((observable, oldValue, newValue) ->updateClearContentToggleText());

        setTooltips();
        setComboBox();
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

    private void setComboBox() {
        ObservableList<String> versionControlModes = FXCollections.observableArrayList(
                getTranslate("monitoringTabController.versionControlModeComboBox.color")

        );
        versionControlModeComboBox.setItems(versionControlModes);
        versionControlModeComboBox.setOnMouseEntered(event -> versionControlModeComboBox.getScene().setCursor(Cursor.HAND));
        versionControlModeComboBox.setOnMouseExited(event -> versionControlModeComboBox.getScene().setCursor(Cursor.DEFAULT));
    }

    private boolean validateSettings() {
        String filePathValue = filePath.getText();
        String fileNameValue = fileName.getText();

        if (!fileExists(filePathValue, fileNameValue)) {
            AlertUtils.showErrorAlert("", getTranslate("alert.fileNotFound") + " " + filePathValue + File.separator + fileNameValue);
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
        String filePathValue = filePath.getText();
        String fileNameValue = fileName.getText();
        int frequency = Integer.parseInt(monitoringFrequency.getText());
        boolean toggleMonitoringValue = toggleMonitoring.isSelected();
        boolean clearContentToggleValue = clearContentToggle.isSelected();
        String versionControlMode = versionControlModeComboBox.getValue(); // .toString();

        updateMonitoringTab(myTab.getId(), filePathValue, fileNameValue, frequency, toggleMonitoringValue, false, false, clearContentToggleValue, versionControlMode);
    }

    public boolean validateMonitoringState() {
        String filePathValue = filePath.getText();
        String fileNameValue = fileName.getText();

        if (!fileExists(filePathValue, fileNameValue)) {
            showAlertFileNotFound(filePathValue, fileNameValue);
            return false;
        }

        if (!toggleMonitoring.isSelected()) {
            AlertUtils.showErrorAlert("", getTranslate("monitoringTabController.monitoringNotEnabled"));
            return false;
        }

        return true;
    }

    private void showAlertFileNotFound(String filePathValue, String fileNameValue) {
        AlertUtils.showErrorAlert("", getTranslate("alert.fileNotFound") + " " + filePathValue);
        AlertUtils.showErrorAlert("", getTranslate("alert.fileNotFound") + " " + filePathValue + " " + fileNameValue);
        doIfFileNotExists();
    }

    private void doIfFileNotExists() {
        toggleMonitoring.setSelected(false);
        stopMonitoring();
        fileContentArea.setVisible(false);
        closeStage(monitoringWindowStage);
    }

    private void closeStage(Stage stage) {
        if (stage != null) {
            stage.close();
            stage = null;
        }
    }
    private void displayFileContent() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fullFilePath))) {
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append("\n");
            }

            Stage stage = createContentDisplayStage(content.toString());
            WindowManager.addStage(stage);
            stage.show();
        } catch (IOException e) {
            handleException(e);
        }
    }

    private Stage createContentDisplayStage(String content) {
        Stage stage = new Stage();
        VBox root = new VBox();
        TextArea textArea = new TextArea();
        textArea.setText(content);
        textArea.setEditable(false);

        VBox.setVgrow(textArea, Priority.ALWAYS);
        root.getChildren().add(textArea);

        Scene scene = new Scene(root, 825, 600);
        stage.setScene(scene);
        stage.setTitle(getTranslate("monitoringTabController.handleViewFileAction.stage"));

        return stage;
    }

    private void handleException(Exception e) {
        AlertUtils.showErrorAlert("", e.getMessage());
        e.printStackTrace();
    }

    private void startMonitoring() {
        if (monitoringService != null) {
            monitoringService.stopMonitoring();
        }

        try {
            int frequency = Integer.parseInt(monitoringFrequency.getText());
            monitoringService = new FileMonitoringService(filePath.getText(), fileName.getText(), this);
            monitoringService.startMonitoring(frequency);
        } catch (NumberFormatException e) {
            AlertUtils.showErrorAlert("", getTranslate("monitoringTabController.frequencyError"));
        }
    }

    private void stopMonitoring() {
        if (monitoringService != null) {
            monitoringService.stopMonitoring();
        }
    }

    /**
     * Get version control mode from dropdown list
     * @return object of version control mode
     */
    private VersionControlMode getSelectedVersionControlMode() {
        String selectedMode = versionControlModeComboBox.getSelectionModel().getSelectedItem();

        return switch (selectedMode) {
            case "символ", "symbol" -> VersionControlMode.SYMBOL;
            case "слово", "word" -> VersionControlMode.WORD;
            case "строка", "line" -> VersionControlMode.LINE;
            case "подсказка", "tooltip" -> VersionControlMode.TOOLTIP;
            case "color" -> VersionControlMode.COLOR;
            default -> VersionControlMode.SYMBOL;
        };
    }

    private void updateMonitoringTab(String tabId, String filePath, String fileName, int monitoringFrequency, boolean toggleMonitoring, boolean openContentButton, boolean parseAsArrayToggle, boolean clearContentToggle, String versionControlMode) {
        MonitoringJsonTabsManager jsonTabsManager = new MonitoringJsonTabsManager();
        List<MonitoringTabData> tabs = jsonTabsManager.loadMonitoringTabs(1, "Monitoring");

        boolean isTabExists = false;

        for (MonitoringTabData tab : tabs) {
            if (tab.getId().equals(tabId)) {
                updateTabData(tab, filePath, fileName, monitoringFrequency, toggleMonitoring, openContentButton, parseAsArrayToggle, clearContentToggle, versionControlMode);
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

    private void updateTabData(MonitoringTabData tab, String filePath, String fileName, int monitoringFrequency, boolean toggleMonitoring, boolean openContentButton, boolean parseAsArrayToggle, boolean clearContentToggle, String versionControlMode) {
        tab.setFilePath(filePath);
        tab.setFileName(fileName);
        tab.setMonitoringFrequency(monitoringFrequency);
        tab.setToggleMonitoring(toggleMonitoring);
        tab.setOpenContentButton(openContentButton);
        tab.setParseAsArrayToggle(parseAsArrayToggle);
        tab.setClearContentToggle(clearContentToggle);
        tab.setVersionControlMode(versionControlMode);
    }

    public String getTranslate(String key) {
        return bundle.getString(key);
    }

    @Override public void loadSavedLanguage() {
        String savedLanguage = settingsModel.loadLanguageSetting();
        Locale locale = LanguageManager.getLocale(savedLanguage);
        LanguageManager.switchLanguage(locale);
    }

    @Override public void switchLanguage(Locale newLocale) {}

    @Override public void updateUI() {
        bundle = LanguageManager.getResourceBundle();
        updateLabels(bundle);
        updateToggleMonitoringText();
        updateClearContentToggleText();
        updateComboBoxItems();
        if (monitoringTabData != null) {
            setSelectedVersion(monitoringTabData.getVersionControlMode());
        }
    }

    public void updateLabels(ResourceBundle bundle) {
        this.bundle = bundle;
        filePathLabel.setText(bundle.getString("monitoringTabController.filePathLabel"));
        fileNameLabel.setText(bundle.getString("monitoringTabController.fileNameLabel"));
        monitoringFrequencyLabel.setText(bundle.getString("monitoringTabController.monitoringFrequencyLabel"));
        versionControlModeLabel.setText(bundle.getString("monitoringTabController.versionControlModeLabel"));
        openContentButton.setText(bundle.getString("monitoringTabController.openContentButton"));
        viewFileContentButton.setText(bundle.getString("monitoringTabController.viewFileContentButton"));
        versionControlButton.setText(bundle.getString("monitoringTabController.versionControlButton"));
        saveSettingsButton.setText(bundle.getString("monitoringTabController.saveSettingsButton"));
    }

    public void updateToggleMonitoringText() {
        toggleMonitoring.setText(toggleMonitoring.isSelected() ? bundle.getString("monitoringTabController.toggleMonitoring.on") : bundle.getString("monitoringTabController.toggleMonitoring.off"));
    }

    public void updateClearContentToggleText() {
        clearContentToggle.setText(clearContentToggle.isSelected() ? bundle.getString("monitoringTabController.clearContentToggle.on") : bundle.getString("monitoringTabController.clearContentToggle.off"));
    }

    public void setTooltips() {
        Tooltip.install(toggleMonitoring, new Tooltip(getTranslate("monitoringTabController.toggleMonitoring.tooltip")));
        Tooltip.install(openContentButton, new Tooltip(getTranslate("monitoringTabController.openContentButton.tooltip")));
        Tooltip.install(viewFileContentButton, new Tooltip(getTranslate("monitoringTabController.viewFileContentButton.tooltip")));
        Tooltip.install(versionControlButton, new Tooltip(getTranslate("monitoringTabController.versionControlButton.tooltip")));
        Tooltip.install(clearContentToggle, new Tooltip(getTranslate("monitoringTabController.clearContentToggle.tooltip")));
        Tooltip.install(saveSettingsButton, new Tooltip(getTranslate("monitoringTabController.saveSettingsButton.tooltip")));
    }

    public void updateComboBoxItems() {
        ObservableList<String> versionControlModes = FXCollections.observableArrayList(
                bundle.getString("monitoringTabController.versionControlModeComboBox.symbol"),
                bundle.getString("monitoringTabController.versionControlModeComboBox.word"),
                bundle.getString("monitoringTabController.versionControlModeComboBox.line"),
                bundle.getString("monitoringTabController.versionControlModeComboBox.tooltip"),
                bundle.getString("monitoringTabController.versionControlModeComboBox.color")
        );
        versionControlModeComboBox.setItems(versionControlModes);
    }

    public void setSelectedVersion(String versionControlMode) {
        if (versionControlMode != null && !versionControlMode.isEmpty()) {
            String translatedMode = switch (versionControlMode) {
                case "символ", "symbol" -> bundle.getString("monitoringTabController.versionControlModeComboBox.symbol");
                case "слово", "word" -> bundle.getString("monitoringTabController.versionControlModeComboBox.word");
                case "строка", "line" -> bundle.getString("monitoringTabController.versionControlModeComboBox.line");
                case "подсказка", "tooltip" -> bundle.getString("monitoringTabController.versionControlModeComboBox.tooltip");
                case "color" -> bundle.getString("monitoringTabController.versionControlModeComboBox.color");
                default -> bundle.getString("monitoringTabController.versionControlModeComboBox.symbol");
            };
            versionControlModeComboBox.getSelectionModel().select(translatedMode);
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

    private void openVersionControlWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dev/station/ui/monitoring/VersionControlWindow.fxml"));
        Parent root = loader.load();

        VersionControlWindowController controller = loader.getController();
        String textArea = fileContentArea.getText();

        VersionControlMode versionControlMode = getSelectedVersionControlMode();
        controller.prepareToVersionControl(textArea, versionControlMode, monitoringService);
        monitoringService.addFileChangeListener(controller);

        Scene scene = new Scene(root, 825, 600);
        Stage versionControlWindowStage = new Stage();
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
        versionControlWindowStage.show();
        fileContentArea.setVisible(false);
    }

    private void setMultilingual() {
        bundle = LanguageManager.getResourceBundle();
        notificationManager = new NotificationManager(bundle);
        LanguageManager.registerNotificationManager(notificationManager);
    }

    /**
     * Loading user values
     */
    public void loadData(MonitoringTabData tabData) {
        setMonitoringTabData(tabData);

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

    public void updateUI(ResourceBundle bundle) {}
}
