package com.dev.station.util;

import com.dev.station.controller.monitoring.VersionControlMode;
import com.dev.station.controller.monitoring.VersionControlWindowController;
import com.dev.station.manager.WindowManager;
import com.dev.station.service.FileMonitoringService;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Control the opening of the version control window
 */
public class VersionControlWindowHandler {

    private final ResourceBundle bundle;
    private final FileMonitoringService monitoringService;
    private final ToggleButton toggleMonitoring;
    private final TextArea fileContentArea;

    public VersionControlWindowHandler(ResourceBundle bundle, FileMonitoringService monitoringService, ToggleButton toggleMonitoring, TextArea fileContentArea) {
        this.bundle = bundle;
        this.monitoringService = monitoringService;
        this.toggleMonitoring = toggleMonitoring;
        this.fileContentArea = fileContentArea;
    }

    public void openVersionControlWindow(String textArea, VersionControlMode versionControlMode) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dev/station/ui/monitoring/VersionControlWindow.fxml"));
        Parent root = loader.load();

        VersionControlWindowController controller = loader.getController();
        controller.prepareToVersionControl(textArea, versionControlMode, monitoringService);
        monitoringService.addFileChangeListener(controller);

        Stage versionControlWindowStage = new Stage();
        versionControlWindowStage.setTitle(bundle.getString("versionControlWindowController.title"));
        versionControlWindowStage.setScene(new Scene(root, 825, 600));
        versionControlWindowStage.setOnCloseRequest(windowEvent -> {
            if (toggleMonitoring.isSelected()) {
                fileContentArea.setVisible(true);
                controller.getCurrentContent(content -> Platform.runLater(() -> fileContentArea.setText(content)));
            }
            monitoringService.removeFileChangeListener(controller);
        });

        WindowManager.addStage(versionControlWindowStage);
        versionControlWindowStage.show();
        fileContentArea.setVisible(false);
    }

    /**
     * Get version control mode from dropdown list
     * @return object of version control mode
     */
    public VersionControlMode getSelectedVersionControlMode(ComboBox<String> versionControlModeComboBox) {
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
}
