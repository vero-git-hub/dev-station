package com.dev.station.controller.sidebar;

import com.dev.station.util.AlertUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class PingController {

    @FXML TextField websiteUrlField;
    @FXML Button scanSiteButton;
    @FXML private TextArea terminalOutputArea;
    @FXML private ProgressIndicator scanProgressIndicator;
    @FXML private TextFlow resultTextFlow;

    @FXML
    public void initialize() {
        websiteUrlField.setOnKeyPressed(event -> handleEnterPressed(event));
    }

     private void handleEnterPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleScanSite();
        }
    }

    @FXML public void handleScanSite() {
        String websiteUrlText = extractDomain(websiteUrlField.getText());

        if(websiteUrlText != null) {
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
    }

    @FXML
    private void handleClearInput() {
        websiteUrlField.clear();
    }

    public String extractDomain(String urlString) {
        try {
            URL url = new URL(urlString);
            String host = url.getHost();

            if (host.startsWith("www.")) {
                host = host.substring(4);
            }
            return host;
        } catch (Exception e) {
            AlertUtils.showErrorAlert("Error extract domain", "Please enter valid url.");
            return null;
        }
    }

    private void updateResultTextFlow(String websiteUrl, boolean isAvailable) {
        Platform.runLater(() -> {
            resultTextFlow.getChildren().clear();

            Text resultText = new Text("Result: ");
            Text urlText = new Text(websiteUrl + " ");
            Text availableText = new Text(isAvailable ? "available" : "not available");

            resultText.getStyleClass().add("result-text");
            urlText.getStyleClass().add("url-text");

            urlText.setUnderline(true);
            availableText.setFill(isAvailable ? Color.GREEN : Color.RED);

            resultTextFlow.getChildren().addAll(resultText, urlText, availableText);
        });
    }
}