package com.dev.station.util;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class AlertUtils {

    public static String PATH_TO_STYLE_FILE = "/styles/style.css";
    public static final String PATH_TO_SUCCESS_ICON = "/images/alert/check-mark-32.png";
    public static final String PATH_TO_ERROR_ICON = "/images/alert/red-cross-30.png";
    public static final String PATH_TO_CLOSE_ICON = "/images/alert/close-24.png";

    public static void showErrorAlert(String title, String content) {
        showCustomAlert(title + " - " + content, PATH_TO_ERROR_ICON, 5);
    }

    public static void showInformationAlert(String title, String content) {
        showCustomAlert("Changes saved successfully", PATH_TO_SUCCESS_ICON, 5);
    }

    /**
     * Shows a custom notification.
     * @param message Message to display.
     * @param iconPath Path to the icon (can be null if the icon is not needed).
     * @param duration Duration of notification display in seconds.
     */
    public static void showCustomAlert(String message, String iconPath, double duration) {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);

            HBox hbox = new HBox(10);
            hbox.setAlignment(Pos.CENTER_LEFT);

            if (iconPath != null && !iconPath.isEmpty()) {
                ImageView icon = new ImageView(new Image(AlertUtils.class.getResourceAsStream(iconPath)));
                icon.setFitHeight(24);
                icon.setPreserveRatio(true);
                hbox.getChildren().add(icon);
            }

            Label messageLabel = new Label(message);
            hbox.getChildren().add(messageLabel);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            hbox.getChildren().add(spacer);

            Image closeImage = new Image(AlertUtils.class.getResourceAsStream(PATH_TO_CLOSE_ICON));
            ImageView closeIcon = new ImageView(closeImage);
            closeIcon.setFitHeight(16);
            closeIcon.setFitWidth(16);
            closeIcon.setPreserveRatio(true);

            Button closeButton = new Button();
            closeButton.setGraphic(closeIcon);
            closeButton.getStyleClass().add("close-button");

            closeButton.setOnAction(e -> stage.close());
            hbox.getChildren().add(closeButton);

            VBox vbox = new VBox(hbox);
            vbox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-background-radius: 5px; -fx-padding: 10;");

            Scene scene = new Scene(vbox);
            scene.setFill(null);
            stage.setScene(scene);

            stage.show();

            if (duration > 0) {
                PauseTransition delay = new PauseTransition(Duration.seconds(duration));
                delay.setOnFinished(e -> stage.close());
                delay.play();
            }
        });
    }
}