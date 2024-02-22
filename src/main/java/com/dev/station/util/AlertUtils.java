package com.dev.station.util;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.Optional;

public class AlertUtils {

    public static String PATH_TO_STYLE_FILE = "/styles/style.css";
    public static String PATH_TO_IMAGE_SUCCESS_ALERT = "/images/alert/check-mark-32.png";

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
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(AlertUtils.class.getResource(PATH_TO_STYLE_FILE).toExternalForm());
        dialogPane.getStyleClass().add("my-dialog");

        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.setAlwaysOnTop(true);
        stage.getIcons().clear();

        dialogPane.getButtonTypes().clear();

        ImageView imageView = new ImageView(new Image(AlertUtils.class.getResourceAsStream(PATH_TO_IMAGE_SUCCESS_ALERT)));
        dialogPane.setGraphic(imageView);

        ButtonType closeButton = new ButtonType("X");
        alert.getButtonTypes().add(closeButton);

        dialogPane.lookupButton(closeButton).addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            stage.close();
        });

        alert.show();

        PauseTransition delay = new PauseTransition(Duration.seconds(5));
        delay.setOnFinished(e -> stage.close());
        delay.play();
    }
}