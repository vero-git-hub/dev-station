package com.dev.station.controller;

import com.dev.station.Localizable;
import com.dev.station.manager.LanguageManager;
import com.dev.station.model.SettingsModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainController implements Localizable {

    @FXML private StackPane contentArea;
    @FXML private Button scriptsButton;
    @FXML private Button clearButton;
    @FXML private Button driverButton;
    @FXML private Button homeButton;
    @FXML private Button imagesButton;
    @FXML private Button settingsButton;
    @FXML private Button pingButton;
    @FXML private Button monitoringButton;
    @FXML private Label footerLabel;
    @FXML public Button switchThemeButton;
    @FXML private ComboBox<String> languageComboBox;
    @FXML private VBox menuVBox;
    ResourceBundle bundle;
    private SettingsModel settingsModel;

    public MainController() {
        LanguageManager.registerForUpdates(this::updateUI);
        settingsModel = new SettingsModel();

    }

    @FXML
    public void initialize() {
        loadSavedLanguage();
        setButtonActions();
        footerLabel.setText("v0.3");
    }

    private void removeActiveButtonClass() {
        for (var child : menuVBox.getChildren()) {
            if (child instanceof Button) {
                Button button = (Button) child;
                button.getStyleClass().remove("active-button");
            }
        }
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

        monitoringButton.setOnAction(event -> {
            loadMonitoringContent();
            setActiveButton(monitoringButton);
        });
    }

    @Override
    public void loadSavedLanguage() {
        setLanguageComboBoxSizes(59, 35);

        String savedLanguage = settingsModel.loadLanguageSetting();
        languageComboBox.setValue(savedLanguage);

        languageComboBox.setCellFactory(lv -> new ListCell<String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);

                    setOnMouseEntered(event -> setCursor(Cursor.HAND));
                    setOnMouseExited(event -> setCursor(Cursor.DEFAULT));
                }
            }
        });

        languageComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals(oldVal)) {
                Locale newLocale = LanguageManager.getLocale(newVal);
                switchLanguage(newLocale);
            }
        });

        languageComboBox.setOnMouseClicked(event -> {
            //TODO: action after changing language
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
        monitoringButton.setText(getTranslate("monitoringMenu"));

        setButtonImage(scriptsButton, "/images/sidebar/program-48.png");
        setButtonImage(driverButton, "/images/sidebar/selenium-webdriver-48.png");
        setButtonImage(clearButton, "/images/sidebar/clear-48.png");
        setButtonImage(pingButton, "/images/sidebar/globe-with-meridians-48.png");
        setButtonImage(monitoringButton, "/images/sidebar/monitoring-48.png");

        setTooltips();
    }

    private void setButtonImage(Button button, String imagePath) {
        Image monitoringImage = new Image(getClass().getResourceAsStream(imagePath));
        ImageView monitoringImageView = new ImageView(monitoringImage);
        monitoringImageView.setFitHeight(25);
        monitoringImageView.setFitWidth(25);
        button.setGraphic(monitoringImageView);
        // button.setContentDisplay(ContentDisplay.RIGHT);
    }

    private void setTooltips() {
        Tooltip.install(scriptsButton, new Tooltip(getTranslate("scriptsMenuHint")));
        Tooltip.install(driverButton, new Tooltip(getTranslate("driverMenuHint")));
        Tooltip.install(clearButton, new Tooltip(getTranslate("clearMenuHint")));
        Tooltip.install(pingButton, new Tooltip(getTranslate("pingMenuHint")));
        Tooltip.install(monitoringButton, new Tooltip(getTranslate("monitoringMenuHint")));

        Tooltip.install(homeButton, new Tooltip(getTranslate("homeButtonHint")));
        Tooltip.install(imagesButton, new Tooltip(getTranslate("imagesButtonHint")));
        Tooltip.install(settingsButton, new Tooltip(getTranslate("settingsButtonHint")));
        Tooltip.install(switchThemeButton, new Tooltip(getTranslate("switchThemeButton")));
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

    private void loadMonitoringContent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dev/station/ui/sidebar/MonitoringLayout.fxml"));
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
        monitoringButton.getStyleClass().remove("active-button");

        activeButton.getStyleClass().add("active-button");
    }

    @FXML
    private void handleImagesButtonAction() {
        removeActiveButtonClass();
        try {
            Node imagesContent = FXMLLoader.load(getClass().getResource("/com/dev/station/ui/header/ImagesLayout.fxml"));
            contentArea.getChildren().setAll(imagesContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSettingsButtonAction() {
        removeActiveButtonClass();
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
        removeActiveButtonClass();
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