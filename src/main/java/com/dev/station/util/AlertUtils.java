package com.dev.station.util;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;


public class AlertUtils {

    public static String PATH_TO_STYLE_FILE = "/styles/style.css";
    public static String PATH_TO_IMAGE_SUCCESS_ALERT = "/images/alert/check-mark-32.png";
    private static final String PATH_TO_CLOSE_ICON = "/images/alert/close-24.png";

    public static void showErrorAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showInformationAlert(String title, String content) {
        showAutoCloseAlert("Changes saved successfully");
    }

    public static void showAutoCloseAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initStyle(StageStyle.TRANSPARENT);
        alert.setHeaderText(null);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(AlertUtils.class.getResource(PATH_TO_STYLE_FILE).toExternalForm());
        dialogPane.getStyleClass().add("my-dialog");

        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.setAlwaysOnTop(true);
        stage.getIcons().clear();

        dialogPane.getButtonTypes().clear();

        HBox content = new HBox();
        content.setSpacing(10);
        content.setAlignment(Pos.CENTER_LEFT);

        ImageView imageView = new ImageView(new Image(AlertUtils.class.getResourceAsStream(PATH_TO_IMAGE_SUCCESS_ALERT)));
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(24);

        StackPane textContainer = new StackPane(new Label(message));
        textContainer.setAlignment(Pos.CENTER_LEFT);

        Image closeImage = new Image(AlertUtils.class.getResourceAsStream(PATH_TO_CLOSE_ICON));
        ImageView closeIcon = new ImageView(closeImage);
        closeIcon.setFitHeight(16);
        closeIcon.setFitWidth(16);
        closeIcon.setPreserveRatio(true);

        Button closeButton = new Button();
        closeButton.setGraphic(closeIcon);
        closeButton.getStyleClass().add("close-button");

        Region spacer = new Region();

        content.getChildren().addAll(imageView, textContainer, spacer, closeButton);

        dialogPane.setGraphic(null);
        dialogPane.setContent(content);

        closeButton.setOnAction(event -> stage.close());

        alert.show();

        PauseTransition delay = new PauseTransition(Duration.seconds(5));
        delay.setOnFinished(e -> stage.close());
        delay.play();
    }
}