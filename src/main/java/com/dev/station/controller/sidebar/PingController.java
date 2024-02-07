package com.dev.station.controller.sidebar;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PingController {

    @FXML TextField websiteUrlField;
    @FXML Button scanSiteButton;
    @FXML private TextArea terminalOutputArea;
    @FXML private ProgressIndicator scanProgressIndicator;
    @FXML private TextFlow resultTextFlow;

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
                boolean isAvailable = false;

                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                    if (line.contains("время=") || line.contains("time=")) {
                        isAvailable = true;
                    }
                }

                String finalOutput = output.toString();
                boolean finalIsAvailable = isAvailable;
                Platform.runLater(() -> {
                    terminalOutputArea.setText(finalOutput);
                    updateResultTextFlow(websiteUrlText, finalIsAvailable);
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

    private void updateResultTextFlow(String websiteUrl, boolean isAvailable) {
        Platform.runLater(() -> {
            resultTextFlow.getChildren().clear();

            Text resultText = new Text("Result: ");
            Text urlText = new Text(websiteUrl + " ");
            Text availableText = new Text(isAvailable ? "available" : "not available");

            urlText.setUnderline(true);
            availableText.setFill(isAvailable ? Color.GREEN : Color.RED);

            resultTextFlow.getChildren().addAll(resultText, urlText, availableText);
        });
    }
}