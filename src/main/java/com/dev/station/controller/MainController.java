package com.dev.station.controller;

import com.dev.station.Localizable;
import com.dev.station.manager.LanguageManager;
import com.dev.station.model.SettingsModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class MainController implements Localizable {
    public static Preferences prefs;
    ResourceBundle bundle;
    @FXML private StackPane contentArea;
    @FXML private Button scriptsButton;
    @FXML private Button clearButton;
    @FXML private Button driverButton;
    @FXML private Button homeButton;
    @FXML private Button imagesButton;
    @FXML private Button settingsButton;
    @FXML private Button pingButton;
    @FXML private Label footerLabel;
    @FXML public Button switchThemeButton;
    @FXML private ComboBox<String> languageComboBox;

    private SettingsModel settingsModel;

    public MainController() {
        LanguageManager.registerForUpdates(this::updateUI);
        settingsModel = new SettingsModel();
    }

    @FXML
    public void initialize() {
        prefs = Preferences.userNodeForPackage(getClass());
        loadSavedLanguage();

        setButtonActions();
        footerLabel.setText("v0.2");
    }

    private void setButtonActions() {
        scriptsButton.setOnAction(event -> {
            loadManuallyContent();
            setActiveButton(scriptsButton);
        });

        driverButton.setOnAction(event -> {
            loadSeleniumContent();
            setActiveButton(driverButton);
        });

        clearButton.setOnAction(event -> {
            loadClearContent();
            setActiveButton(clearButton);
        });

        pingButton.setOnAction(event -> {
            loadPingContent();
            setActiveButton(pingButton);
        });
    }

    @Override
    public void loadSavedLanguage() {
        setLanguageComboBoxSizes(58, 35);

        String savedLanguage = settingsModel.loadLanguageSetting();
        languageComboBox.setValue(savedLanguage);

        languageComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals(oldVal)) {
                Locale newLocale = LanguageManager.getLocale(newVal);
                switchLanguage(newLocale);
            }
        });

        Locale locale = LanguageManager.getLocale(savedLanguage);
        LanguageManager.switchLanguage(locale);
    }

    private void setLanguageComboBoxSizes(int width, int height) {
        languageComboBox.setPrefWidth(width);
        languageComboBox.setPrefHeight(height);
    }

    @Override
    public void switchLanguage(Locale newLocale) {
        String newLanguage;
        if (newLocale.equals(Locale.ENGLISH)) {
            newLanguage = "EN";
        } else if (newLocale.equals(new Locale("ru", "RU"))) {
            newLanguage = "RU";
        } else {
            newLanguage = "EN";
        }

        settingsModel.saveLanguageSetting(newLanguage);

        LanguageManager.switchLanguage(newLocale);
        updateUI();
    }

    @Override
    public void updateUI() {
        bundle = LanguageManager.getResourceBundle();
        scriptsButton.setText(getTranslate("scriptsMenu"));
        driverButton.setText(getTranslate("driverMenu"));
        clearButton.setText(getTranslate("clearMenu"));
        pingButton.setText(getTranslate("pingMenu"));

        setTooltips();
    }

    private void setTooltips() {
        Tooltip.install(scriptsButton, new Tooltip(getTranslate("scriptsMenuHint")));
        Tooltip.install(driverButton, new Tooltip(getTranslate("driverMenuHint")));
        Tooltip.install(clearButton, new Tooltip(getTranslate("clearMenuHint")));
        Tooltip.install(pingButton, new Tooltip(getTranslate("pingMenuHint")));

        Tooltip.install(homeButton, new Tooltip(getTranslate("homeButtonHint")));
        Tooltip.install(imagesButton, new Tooltip(getTranslate("imagesButtonHint")));
        Tooltip.install(settingsButton, new Tooltip(getTranslate("settingsButtonHint")));
    }

    private void loadClearContent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dev/station/ui/sidebar/ClearLayout.fxml"));
            Node layout = loader.load();
            contentArea.getChildren().setAll(layout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSeleniumContent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dev/station/ui/sidebar/DriverLayout.fxml"));
            Node seleniumLayout = loader.load();
            contentArea.getChildren().setAll(seleniumLayout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadManuallyContent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dev/station/ui/sidebar/ScriptsLayout.fxml"));
            Node programLayout = loader.load();
            contentArea.getChildren().setAll(programLayout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPingContent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dev/station/ui/sidebar/PingLayout.fxml"));
            Node programLayout = loader.load();
            contentArea.getChildren().setAll(programLayout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setActiveButton(Button activeButton) {
        scriptsButton.getStyleClass().remove("active-button");
        driverButton.getStyleClass().remove("active-button");
        clearButton.getStyleClass().remove("active-button");
        pingButton.getStyleClass().remove("active-button");

        activeButton.getStyleClass().add("active-button");
    }

    @FXML
    private void handleImagesButtonAction() {
        try {
            Node imagesContent = FXMLLoader.load(getClass().getResource("/com/dev/station/ui/header/ImagesLayout.fxml"));
            contentArea.getChildren().setAll(imagesContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSettingsButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dev/station/ui/header/SettingsLayout.fxml"));
            Node settings = loader.load();
            contentArea.getChildren().setAll(settings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void returnHome() {
        contentArea.getChildren().clear();
    }

    private String getTranslate(String key) {
        return bundle.getString(key);
    }

    @FXML public void switchTheme(ActionEvent actionEvent) {
        Scene scene = switchThemeButton.getScene();
        boolean isDark = scene.getStylesheets().stream().anyMatch(s -> s.contains("dark-theme.css"));
        scene.getStylesheets().clear();

        if (isDark) {
            scene.getStylesheets().add(getClass().getResource("/styles/light-theme.css").toExternalForm());
            settingsModel.saveThemeSetting("light");
        } else {
            scene.getStylesheets().add(getClass().getResource("/styles/dark-theme.css").toExternalForm());
            settingsModel.saveThemeSetting("dark");
        }
    }
}