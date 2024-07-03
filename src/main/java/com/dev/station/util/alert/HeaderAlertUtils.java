package com.dev.station.util.alert;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Duration;

public class HeaderAlertUtils {

    private static final String PATH_TO_ERROR_ICON = "/images/alert/red-cross-30.png";
    public static final String PATH_TO_SUCCESS_ICON = "/images/alert/check-mark-32.png";
    private static final String PATH_TO_INFO_ICON = "/images/alert/info-40.png";
    public static final String PATH_TO_CLOSE_ICON = "/images/alert/close-24.png";

    private static HBox notificationArea;

    public HeaderAlertUtils(HBox notificationArea) {
        HeaderAlertUtils.notificationArea = notificationArea;
    }

    // Hello message
    public static void showSuccessMessage(String message) {
        Platform.runLater(() -> {
            clearNotifications();
            Node notificationNode = createNotificationNode(message, "success");
            notificationArea.getChildren().add(notificationNode);
        });
    }

    public static void showErrorMessage(String message) {
        Platform.runLater(() -> {
            clearNotifications();
            Node notificationNode = createNotificationNode(message, "error");
            notificationArea.getChildren().add(notificationNode);
        });
    }

    private static Node createNotificationNode(String message, String messageType) {
        Label messageLabel = new Label();
        messageLabel.setId("notificationMessageLabel");
        messageLabel.setText("");

        String iconPath = messageType.equals("error") ? PATH_TO_ERROR_ICON : PATH_TO_SUCCESS_ICON;
        ImageView iconView = new ImageView(new Image(HeaderAlertUtils.class.getResourceAsStream(iconPath)));
        iconView.setFitHeight(24);
        iconView.setFitWidth(24);
        iconView.setPreserveRatio(true);

        HBox hbox = new HBox(5);
        hbox.getChildren().addAll(iconView, messageLabel);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setStyle("-fx-background-color: transparent;");

        Timeline typingTimeline = new Timeline();
        for (int i = 0; i <= message.length(); i++) {
            final int finalI = i;
            KeyFrame frame = new KeyFrame(Duration.millis(i * 200), actionEvent -> {
                messageLabel.setText(message.substring(0, finalI));
            });
            typingTimeline.getKeyFrames().add(frame);
        }

        typingTimeline.setOnFinished(typingFinishedEvent -> {
            PauseTransition pauseBeforeBlinking = new PauseTransition(Duration.seconds(1));
            pauseBeforeBlinking.setOnFinished(pauseFinishedEvent -> {
                Timeline blinkTimeline = new Timeline(
                        new KeyFrame(Duration.seconds(0), blinkEvent1 -> messageLabel.setVisible(false)),
                        new KeyFrame(Duration.seconds(0.5), blinkEvent2 -> messageLabel.setVisible(true)),
                        new KeyFrame(Duration.seconds(1), blinkEvent3 -> messageLabel.setVisible(false)),
                        new KeyFrame(Duration.seconds(1.5), blinkEvent4 -> messageLabel.setVisible(true))
                );
                blinkTimeline.setCycleCount(2);

                blinkTimeline.setOnFinished(blinkFinishedEvent -> {
                    Timeline fadeOutTimeline = new Timeline(
                            new KeyFrame(Duration.seconds(0.5), fadeEvent1 -> hbox.setOpacity(1)),
                            new KeyFrame(Duration.seconds(2), fadeEvent2 -> hbox.setOpacity(0))
                    );
                    fadeOutTimeline.setOnFinished(fadeFinishedEvent -> hbox.getChildren().clear());
                    fadeOutTimeline.play();
                });

                blinkTimeline.play();
            });
            pauseBeforeBlinking.play();
        });

        typingTimeline.play();

        return hbox;
    }

    // All messages
    private static void clearNotifications() {
        if(notificationArea.getChildren() != null) {
            notificationArea.getChildren().clear();
        }
    }

    public static void showErrorAlert(String title, String content) {
        String message = (title == null || title.isEmpty()) ? content : title + " - " + content;
        showCustomAlert(message, PATH_TO_ERROR_ICON, 5);
    }

    public static void showSuccessAlert(String title, String content) {
        showCustomAlert(content, PATH_TO_SUCCESS_ICON, 5);
    }

    public static void showInformationAlert(String title, String content) {
        showCustomAlert(content, PATH_TO_INFO_ICON, 5);
    }

    /**
     * Shows a custom notification.
     * @param message Message to display.
     * @param iconPath Path to the icon (can be null if the icon is not needed).
     * @param duration Duration of notification display in seconds.
     */
    public static void showCustomAlert(String message, String iconPath, double duration) {
        Platform.runLater(() -> {
            clearNotifications();
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

            closeButton.setOnAction(e -> notificationArea.getChildren().remove(hbox));
            hbox.getChildren().add(closeButton);

            hbox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-background-radius: 5px;");

            notificationArea.getChildren().add(hbox);

            if (duration > 0) {
                PauseTransition delay = new PauseTransition(Duration.seconds(duration));
                delay.setOnFinished(e -> notificationArea.getChildren().remove(hbox));
                delay.play();
            }
        });
    }
}
