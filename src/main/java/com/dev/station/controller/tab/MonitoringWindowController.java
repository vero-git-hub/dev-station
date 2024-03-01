package com.dev.station.controller.tab;

import com.dev.station.service.FileChangeListener;
import com.dev.station.service.FileMonitoringService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Display file content in a new window
 */
public class MonitoringWindowController implements FileChangeListener {

    @FXML private TextArea monitoringTextArea;
    private boolean clearFileAfterReading = false;
    private String filePathToClear;
    private FileMonitoringService monitoringService;

    public void setInitialContent(String content) {
        Platform.runLater(() -> monitoringTextArea.setText(content));
    }

    public String getCurrentContent() {
        return monitoringTextArea.getText();
    }

    public void setClearFileAfterReading(boolean clearFileAfterReading) {
        this.clearFileAfterReading = clearFileAfterReading;
    }

    public void setFilePathToClear(String filePath) {
        this.filePathToClear = filePath;
    }

    public void setMonitoringService(FileMonitoringService service) {
        this.monitoringService = service;
    }

    @Override public void onFileChange(String content) {
        Platform.runLater(() -> {
            monitoringTextArea.setText(content);
            if (clearFileAfterReading) {
                try {
                    File file = new File(filePathToClear);

                    PrintWriter writer = new PrintWriter(file);
                    writer.print("");
                    writer.close();

                    boolean success = file.setLastModified(System.currentTimeMillis() + 1000);

                    if (monitoringService != null) {
                        monitoringService.updateLastModified(file.lastModified());
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}