package com.dev.station.controller.sidebar;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PingController {

    @FXML TextField websiteUrlField;
    @FXML Button scanSiteButton;
    @FXML private TextArea terminalOutputArea;

    public void handleScanSite(ActionEvent actionEvent) {
        String websiteUrl = websiteUrlField.getText();
        String command = "ping " + websiteUrl;

        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "CP866"));
            String line;
            StringBuilder output = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            terminalOutputArea.setText(output.toString());

            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            terminalOutputArea.setText("An error occurred while executing the ping command.");
        }
    }

}