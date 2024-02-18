package com.dev.station.controller.sidebar;

import com.dev.station.Localizable;
import com.dev.station.manager.LanguageManager;
import com.dev.station.model.SettingsModel;
import com.dev.station.util.AlertUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import java.awt.Desktop;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class PingController implements Localizable {

    @FXML TextField websiteUrlField;
    @FXML Button scanSiteButton;
    @FXML private TextArea terminalOutputArea;
    @FXML private ProgressIndicator scanProgressIndicator;
    @FXML private TextFlow resultTextFlow;
    @FXML private Button clearFieldButton;
    @FXML Label labelAboveTerminal;
    private SettingsModel settingsModel;
    ResourceBundle bundle;
    Text resultText;

    public PingController() {
        LanguageManager.registerForUpdates(this::updateUI);
        settingsModel = new SettingsModel();
    }

    @FXML public void initialize() {
        loadSavedLanguage();
        websiteUrlField.setOnKeyPressed(event -> handleEnterPressed(event));
        setUIText();
        setTooltips();
    }

    @FXML public void handleScanSite() {
        String originalUrl = websiteUrlField.getText();
        String websiteUrlText = extractDomain(originalUrl);

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
                        if (line.contains("время=") || line.contains("time=") || line.contains("<1мс") || line.contains("<1ms")) {
                            isAvailable = true;
                        }
                    }

                    String finalOutput = output.toString();
                    boolean finalIsAvailable = isAvailable;
                    Platform.runLater(() -> {
                        terminalOutputArea.setText(finalOutput);
                        updateResultTextFlow(websiteUrlText, finalIsAvailable, originalUrl);
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

    @FXML private void handleClearInput() {
        websiteUrlField.clear();
    }

    private void setUIText() {
        websiteUrlField.setPromptText(getTranslate("websiteUrlField"));
        labelAboveTerminal.setText(getTranslate("labelAboveTerminal"));
    }

    private void setTooltips() {
        Tooltip.install(scanSiteButton, new Tooltip(getTranslate("scanSiteButton")));
        Tooltip.install(clearFieldButton, new Tooltip(getTranslate("clearFieldButton")));
    }

    private String getTranslate(String key) {
        return bundle.getString(key);
    }

    private void handleEnterPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleScanSite();
        }
    }

    private String extractDomain(String urlString) {
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

    private void updateResultTextFlow(String websiteUrl, boolean isAvailable, String originalUrl) {
        Platform.runLater(() -> {
            resultTextFlow.getChildren().clear();

            resultText = new Text(getTranslate("resultText"));

            Text urlText = new Text(websiteUrl + " ");
            Text availableText = new Text(isAvailable ? getTranslate("siteAvailableText") : getTranslate("siteUnavailableText"));

            resultText.getStyleClass().add("result-text");

            urlText.getStyleClass().add("url-text");
            urlText.getStyleClass().add("clickable");

            urlText.setUnderline(true);
            availableText.setFill(isAvailable ? Color.GREEN : Color.RED);

            openSite(urlText, originalUrl);

            resultTextFlow.getChildren().addAll(resultText, urlText, availableText);
        });
    }

    private void openSite(Text urlText, String originalUrl) {
        urlText.setOnMouseClicked(event -> {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                try {
                    Desktop.getDesktop().browse(new URI(originalUrl));
                } catch (Exception e) {
                    AlertUtils.showErrorAlert("Error opening site","Please check the url (must start with http/https)");
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void loadSavedLanguage() {
        String savedLanguage = settingsModel.loadLanguageSetting();
        Locale locale = LanguageManager.getLocale(savedLanguage);
        LanguageManager.switchLanguage(locale);
    }

    @Override
    public void switchLanguage(Locale newLocale) {
        LanguageManager.switchLanguage(newLocale);
        updateUI();
    }

    @Override
    public void updateUI() {
        bundle = LanguageManager.getResourceBundle();
        setUIText();
        setTooltips();
    }
}