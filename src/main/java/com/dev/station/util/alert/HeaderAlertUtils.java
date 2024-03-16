package com.dev.station.util.alert;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class HeaderAlertUtils {

    private static final String PATH_TO_ERROR_ICON = "/images/alert/red-cross-30.png";
    public static final String PATH_TO_SUCCESS_ICON = "/images/alert/check-mark-32.png";
    private static final String PATH_TO_INFO_ICON = "/images/alert/info-40.png";

    private HBox notificationArea;

    public HeaderAlertUtils(HBox notificationArea) {
        this.notificationArea = notificationArea;
    }

    public void showSuccessMessage(String message) {
        Platform.runLater(() -> {
            clearNotifications();
            Node notificationNode = createNotificationNode(message, "success");
            notificationArea.getChildren().add(notificationNode);
        });
    }

    public void showErrorMessage(String message) {
        Platform.runLater(() -> {
            clearNotifications();
            Node notificationNode = createNotificationNode(message, "error");
            notificationArea.getChildren().add(notificationNode);
        });
    }

    private Node createNotificationNode(String message, String messageType) {
        Label messageLabel = new Label();
        messageLabel.setId("notificationMessageLabel");
        messageLabel.setText("");

        String iconPath = messageType.equals("error") ? PATH_TO_ERROR_ICON : PATH_TO_SUCCESS_ICON;
        ImageView iconView = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
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

    private void clearNotifications() {
        notificationArea.getChildren().clear();
    }
}
