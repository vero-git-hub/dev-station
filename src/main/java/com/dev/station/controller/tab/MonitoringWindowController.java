package com.dev.station.controller.tab;

import com.dev.station.service.FileChangeListener;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

/**
 * Display file content in a new window
 */
public class MonitoringWindowController implements FileChangeListener {

    @FXML private TextArea monitoringTextArea;

    public void setInitialContent(String content) {
        Platform.runLater(() -> monitoringTextArea.setText(content));
    }

    public String getCurrentContent() {
        return monitoringTextArea.getText();
    }

    @Override public void onFileChange(String content) {
        Platform.runLater(() -> monitoringTextArea.setText(content));
    }
}