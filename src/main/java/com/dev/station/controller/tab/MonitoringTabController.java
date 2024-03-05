package com.dev.station.controller.tab;

import com.dev.station.Localizable;
import com.dev.station.manager.LanguageManager;
import com.dev.station.manager.NotificationManager;
import com.dev.station.manager.WindowManager;
import com.dev.station.manager.monitoring.MonitoringJsonTabsManager;
import com.dev.station.manager.monitoring.MonitoringTabData;
import com.dev.station.model.SettingsModel;
import com.dev.station.service.FileChangeListener;
import com.dev.station.service.FileContentProvider;
import com.dev.station.service.FileMonitoringService;
import com.dev.station.util.AlertUtils;
import com.dev.station.util.FileUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Timer;

public class MonitoringTabController implements Localizable, FileChangeListener {

    @FXML public Label filePathLabel;
    @FXML public TextField filePath;
    @FXML public Label fileNameLabel;
    @FXML public TextField fileName;
    @FXML public Label monitoringFrequencyLabel;
    @FXML public TextField monitoringFrequency;
    @FXML public ToggleButton toggleMonitoring;
    @FXML public ToggleGroup monitoringToggleGroup;
    @FXML public Button openContentButton;
    @FXML public ToggleButton parseAsArrayToggle;
    @FXML public ToggleButton clearContentToggle;
    @FXML public Button saveSettingsButton;
    @FXML public TextArea fileContentArea;
    @FXML public Button viewFileContentButton;
    ResourceBundle bundle;
    private NotificationManager notificationManager;
    private Tab myTab;
    SettingsModel settingsModel;
    private Timer timer;
    FileMonitoringService monitoringService;
    String fullFilePath;
    private Stage monitoringWindowStage;

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

    /**
     * @param actionEvent
     * Start and stop monitoring in textArea
     */
    @FXML public void handleMonitoringAction(ActionEvent actionEvent) {
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

            monitoringService.setFileChangeListener(controller);

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

    @FXML public void handleViewFileAction(ActionEvent actionEvent) {
        fullFilePath = filePath.getText() + "/" + fileName.getText();

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
        boolean parseAsArrayToggleValue = parseAsArrayToggle.isSelected();
        boolean clearContentToggleValue = clearContentToggle.isSelected();

        String tabIdToUpdate = myTab.getId();

        updateMonitoringTab(tabIdToUpdate, filePathValue, fileNameValue, frequency, toggleMonitoringValue, false, parseAsArrayToggleValue, clearContentToggleValue);
    }

    public void initialize() {
        bundle = LanguageManager.getResourceBundle();

        notificationManager = new NotificationManager(bundle);
        LanguageManager.registerNotificationManager(notificationManager);

        setMultilingual();
        loadSavedLanguage();
    }

    private void updateMonitoringTab(String tabId, String filePath, String fileName, int monitoringFrequency, boolean toggleMonitoring, boolean openContentButton, boolean parseAsArrayToggle, boolean clearContentToggle) {
        MonitoringJsonTabsManager jsonTabsManager = new MonitoringJsonTabsManager();
        List<MonitoringTabData> tabs = jsonTabsManager.loadMonitoringTabs(1, "Monitoring");

        for (MonitoringTabData tab : tabs) {
            if (tab.getId().equals(tabId)) {
                tab.setFilePath(filePath);
                tab.setFileName(fileName);
                tab.setMonitoringFrequency(monitoringFrequency);
                tab.setToggleMonitoring(toggleMonitoring);
                tab.setOpenContentButton(openContentButton);
                tab.setParseAsArrayToggle(parseAsArrayToggle);
                tab.setClearContentToggle(clearContentToggle);
                break;
            }
        }

        boolean success = jsonTabsManager.saveMonitoringTabs(1, "Monitoring", tabs);
        if (success) {
            AlertUtils.showSuccessAlert("",getTranslate("alerts.successSaving"));
        } else {
            AlertUtils.showErrorAlert("", getTranslate("alerts.errorSaving"));
        }
    }

    private void setMultilingual() {
        bundle = LanguageManager.getResourceBundle();
        notificationManager = new NotificationManager(bundle);
        LanguageManager.registerNotificationManager(notificationManager);
    }

    public void updateUI(ResourceBundle bundle) {}

    public void loadData(MonitoringTabData tabData) {
        filePath.setText(tabData.getFilePath());
        fileName.setText(tabData.getFileName());
        monitoringFrequency.setText(String.valueOf(tabData.getMonitoringFrequency()));
        toggleMonitoring.setSelected(tabData.isToggleMonitoring());
        parseAsArrayToggle.setSelected(tabData.isParseAsArrayToggle());
        clearContentToggle.setSelected(tabData.isClearContentToggle());

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

        filePathLabel.setText(getTranslate("monitoringTabController.filePathLabel"));
        fileNameLabel.setText(getTranslate("monitoringTabController.fileNameLabel"));
        monitoringFrequencyLabel.setText(getTranslate("monitoringTabController.monitoringFrequencyLabel"));

        toggleMonitoring.setText(getTranslate("monitoringTabController.toggleMonitoring"));
        openContentButton.setText(getTranslate("monitoringTabController.openContentButton"));
        viewFileContentButton.setText(getTranslate("monitoringTabController.viewFileContentButton"));
        parseAsArrayToggle.setText(getTranslate("monitoringTabController.parseAsArrayToggle"));
        clearContentToggle.setText(getTranslate("monitoringTabController.clearContentToggle"));
        saveSettingsButton.setText(getTranslate("monitoringTabController.saveSettingsButton"));
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
}
