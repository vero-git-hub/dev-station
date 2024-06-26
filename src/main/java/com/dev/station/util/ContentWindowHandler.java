package com.dev.station.util;

import com.dev.station.controller.tab.MonitoringWindowController;
import com.dev.station.manager.WindowManager;
import com.dev.station.service.FileMonitoringService;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

import java.io.IOException;

public class ContentWindowHandler {
    private final UIUpdater uiUpdater;
    private final TextArea fileContentArea;
    private final ToggleButton toggleMonitoring;
    private final ToggleButton clearContentToggle;
    private final FileMonitoringService monitoringService;
    private final String fullFilePath;

    public ContentWindowHandler(UIUpdater uiUpdater, TextArea fileContentArea, ToggleButton toggleMonitoring, ToggleButton clearContentToggle, FileMonitoringService monitoringService, String fullFilePath) {
        this.uiUpdater = uiUpdater;
        this.fileContentArea = fileContentArea;
        this.toggleMonitoring = toggleMonitoring;
        this.clearContentToggle = clearContentToggle;
        this.monitoringService = monitoringService;
        this.fullFilePath = fullFilePath;
    }

    public void openContentWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dev/station/ui/tab/MonitoringWindow.fxml"));
        Parent root = loader.load();

        MonitoringWindowController controller = loader.getController();
        controller.setInitialContent(fileContentArea.getText());

        controller.setClearFileAfterReading(clearContentToggle.isSelected());
        controller.setFilePathToClear(fullFilePath);
        controller.setMonitoringService(monitoringService);

        monitoringService.addFileChangeListener(controller);

        Stage monitoringWindowStage = uiUpdater.createStage(root, "monitoringTabController.handleOpenContentButtonAction.stage");
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
}
