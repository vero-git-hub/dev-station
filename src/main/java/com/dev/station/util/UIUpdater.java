package com.dev.station.util;

import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class UIUpdater {
    private final ResourceBundle bundle;

    public UIUpdater(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public void updateLabels(Label filePathLabel, Label fileNameLabel, Label monitoringFrequencyLabel, Label versionControlModeLabel,
                             Button openContentButton, Button viewFileContentButton, Button versionControlButton, Button saveSettingsButton) {
        filePathLabel.setText(bundle.getString("monitoringTabController.filePathLabel"));
        fileNameLabel.setText(bundle.getString("monitoringTabController.fileNameLabel"));
        monitoringFrequencyLabel.setText(bundle.getString("monitoringTabController.monitoringFrequencyLabel"));
        versionControlModeLabel.setText(bundle.getString("monitoringTabController.versionControlModeLabel"));
        openContentButton.setText(bundle.getString("monitoringTabController.openContentButton"));
        viewFileContentButton.setText(bundle.getString("monitoringTabController.viewFileContentButton"));
        versionControlButton.setText(bundle.getString("monitoringTabController.versionControlButton"));
        saveSettingsButton.setText(bundle.getString("monitoringTabController.saveSettingsButton"));
    }

    public void updateToggleMonitoringText(ToggleButton toggleMonitoring) {
        toggleMonitoring.setText(toggleMonitoring.isSelected() ? bundle.getString("monitoringTabController.toggleMonitoring.on") : bundle.getString("monitoringTabController.toggleMonitoring.off"));
    }

    public void updateClearContentToggleText(ToggleButton clearContentToggle) {
        clearContentToggle.setText(clearContentToggle.isSelected() ? bundle.getString("monitoringTabController.clearContentToggle.on") : bundle.getString("monitoringTabController.clearContentToggle.off"));
    }

    public void setTooltips(ToggleButton toggleMonitoring, Button openContentButton, Button viewFileContentButton,
                            Button versionControlButton, ToggleButton clearContentToggle, Button saveSettingsButton) {
        Tooltip.install(toggleMonitoring, new Tooltip(bundle.getString("monitoringTabController.toggleMonitoring.tooltip")));
        Tooltip.install(openContentButton, new Tooltip(bundle.getString("monitoringTabController.openContentButton.tooltip")));
        Tooltip.install(viewFileContentButton, new Tooltip(bundle.getString("monitoringTabController.viewFileContentButton.tooltip")));
        Tooltip.install(versionControlButton, new Tooltip(bundle.getString("monitoringTabController.versionControlButton.tooltip")));
        Tooltip.install(clearContentToggle, new Tooltip(bundle.getString("monitoringTabController.clearContentToggle.tooltip")));
        Tooltip.install(saveSettingsButton, new Tooltip(bundle.getString("monitoringTabController.saveSettingsButton.tooltip")));
    }

    public void setComboBoxItems(ComboBox<String> versionControlModeComboBox) {
        versionControlModeComboBox.setItems(FXCollections.observableArrayList(
                bundle.getString("monitoringTabController.versionControlModeComboBox.symbol"),
                bundle.getString("monitoringTabController.versionControlModeComboBox.word"),
                bundle.getString("monitoringTabController.versionControlModeComboBox.line"),
                bundle.getString("monitoringTabController.versionControlModeComboBox.tooltip"),
                bundle.getString("monitoringTabController.versionControlModeComboBox.color")
        ));
    }

    public Stage createContentDisplayStage(String content) {
        Stage stage = new Stage();
        VBox root = new VBox();
        TextArea textArea = new TextArea();
        textArea.setText(content);
        textArea.setEditable(false);

        VBox.setVgrow(textArea, Priority.ALWAYS);
        root.getChildren().add(textArea);

        Scene scene = new Scene(root, 825, 600);
        stage.setScene(scene);
        stage.setTitle(bundle.getString("monitoringTabController.handleViewFileAction.stage"));

        return stage;
    }

    public Stage createStage(Parent root, String titleKey) {
        Stage stage = new Stage();
        Scene scene = new Scene(root, 825, 600);
        stage.setTitle(bundle.getString(titleKey));
        stage.setScene(scene);
        return stage;
    }

    public void setSelectedVersion(ComboBox<String> versionControlModeComboBox, String versionControlMode) {
        if (versionControlMode != null && !versionControlMode.isEmpty()) {
            String translatedMode = switch (versionControlMode) {
                case "символ", "symbol" -> bundle.getString("monitoringTabController.versionControlModeComboBox.symbol");
                case "слово", "word" -> bundle.getString("monitoringTabController.versionControlModeComboBox.word");
                case "строка", "line" -> bundle.getString("monitoringTabController.versionControlModeComboBox.line");
                case "подсказка", "tooltip" -> bundle.getString("monitoringTabController.versionControlModeComboBox.tooltip");
                case "цвет", "color" -> bundle.getString("monitoringTabController.versionControlModeComboBox.color");
                default -> bundle.getString("monitoringTabController.versionControlModeComboBox.symbol");
            };
            versionControlModeComboBox.getSelectionModel().select(translatedMode);
        }
    }

    public void updateToggleButtonText(ToggleButton toggleMonitoring, ToggleButton clearContentToggle) {
        updateToggleMonitoring(toggleMonitoring);
        updateClearContentToggle(clearContentToggle);
    }

    private void updateToggleMonitoring(ToggleButton toggleMonitoring) {
        if (toggleMonitoring.isSelected()) {
            toggleMonitoring.setText(bundle.getString("monitoringTabController.toggleMonitoring.on"));
        } else {
            toggleMonitoring.setText(bundle.getString("monitoringTabController.toggleMonitoring.off"));
        }
    }

    private void updateClearContentToggle(ToggleButton clearContentToggle) {
        if(clearContentToggle.isSelected()) {
            clearContentToggle.setText(bundle.getString("monitoringTabController.clearContentToggle.on"));
        } else {
            clearContentToggle.setText(bundle.getString("monitoringTabController.clearContentToggle.off"));
        }
    }

    public void updateControlStates(ToggleButton toggleMonitoring, ToggleButton clearContentToggle, Button saveSettingsButton, TextField filePath, TextField fileName, TextField monitoringFrequency, ComboBox<String> versionControlModeComboBox) {
        boolean monitoringActive = toggleMonitoring.isSelected();
        //openContentButton.setDisable(monitoringActive);
        clearContentToggle.setDisable(monitoringActive);
        //versionControlButton.setDisable(monitoringActive);
        //viewFileContentButton.setDisable(monitoringActive);
        saveSettingsButton.setDisable(monitoringActive);
        filePath.setDisable(monitoringActive);
        fileName.setDisable(monitoringActive);
        monitoringFrequency.setDisable(monitoringActive);
        versionControlModeComboBox.setDisable(monitoringActive);
    }

    public void setTimerComboBoxItems(ComboBox<Integer> monitoringFrequencyComboBox) {
        // Populate monitoringFrequencyComboBox with values from 0 to 60
        for (int i = 0; i <= 60; i++) {
            monitoringFrequencyComboBox.getItems().add(i);
        }
        monitoringFrequencyComboBox.setValue(0); // Set default value
    }
}