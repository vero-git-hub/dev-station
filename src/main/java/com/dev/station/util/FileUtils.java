package com.dev.station.util;

import com.dev.station.manager.WindowManager;
import com.dev.station.service.FileMonitoringService;
import com.dev.station.util.alert.AlertUtils;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;

public class FileUtils {
    /**
     * Returns the file extension from the given path.
     * @param filePath The path to the file.
     * @return The file extension, or an empty string if the extension is not found.
     */
    public static String getFileExtension(String filePath) {
        if (filePath != null && filePath.lastIndexOf('.') > 0) {
            return filePath.substring(filePath.lastIndexOf('.') + 1);
        }
        return "";
    }

    public static void clearFileAndSetLastModified(String filePath, FileMonitoringService monitoringService, String errorMessage) {
        try {
            File file = new File(filePath);

            PrintWriter writer = new PrintWriter(file);
            writer.print("");
            writer.close();

            boolean success = file.setLastModified(System.currentTimeMillis() + 1000);
            if (!success) {
                AlertUtils.showErrorAlert("", errorMessage);
                return;
            }

            if (monitoringService != null) {
                monitoringService.updateLastModified(file.lastModified());
            }
        } catch (IOException e) {
            AlertUtils.showErrorAlert("", e.getMessage());
        }
    }

    public boolean fileExists(String filePath, String fileName) {
        String fullFilePath = filePath + File.separator + fileName;
        File file = new File(fullFilePath);
        return file.exists();
    }

    public boolean fileExists(String fullPath) {
        File file = new File(fullPath);
        return file.exists();
    }

    public void displayFileContent(String fullFilePath, UIUpdater uiUpdater) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fullFilePath))) {
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append("\n");
            }

            Stage stage = uiUpdater.createContentDisplayStage(content.toString());
            WindowManager.addStage(stage);
            stage.show();
        } catch (IOException e) {
            AlertUtils.showErrorAlert("", e.getMessage());
            e.printStackTrace();
        }
    }
}