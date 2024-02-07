package com.dev.station.controller.sidebar;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PingController {

    @FXML TextField websiteUrlField;
    @FXML Button scanSiteButton;
    @FXML private TextArea terminalOutputArea;
    @FXML private ProgressIndicator scanProgressIndicator;

    public void handleScanSite(ActionEvent actionEvent) {
        String websiteUrlText = websiteUrlField.getText();

        scanProgressIndicator.setVisible(true);

        new Thread(() -> {
            try {
                String command = "ping " + websiteUrlText;
                Process process = Runtime.getRuntime().exec(command);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "CP866"));
                String line;
                StringBuilder output = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }

                Platform.runLater(() -> {
                    terminalOutputArea.setText(output.toString());
                    scanProgressIndicator.setVisible(false);
                });

                process.waitFor();
            } catch (Exception e) {
                Platform.runLater(() -> {
                    terminalOutputArea.setText("An error occurred while executing the ping command.");
                    scanProgressIndicator.setVisible(false);
                });
            }
        }).start();
    }
}