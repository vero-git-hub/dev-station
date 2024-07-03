package com.dev.station.controller.header;

import com.dev.station.entity.ImageSettings;
import com.dev.station.model.SettingsModel;
import com.dev.station.util.alert.AlertUtils;
import com.dev.station.util.alert.HeaderAlertUtils;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImagesController {
    @FXML private TilePane imagesTilePane;
    SettingsModel settingsModel = new SettingsModel();

    public void initialize() {
        String theme = settingsModel.loadThemeSetting();
        updateImagesLayout(theme);

        loadImages();
    }

    public void loadImages() {
        imagesTilePane.getChildren().clear();

        ImageSettings settings = settingsModel.loadImageSettings();

        if (settings != null) {
            String imagesFolderPath = settings.getPath();
            File folder = new File(imagesFolderPath);
            File[] listOfFiles = folder.listFiles();

            if (listOfFiles != null) {
                for (File file : listOfFiles) {
                    if (file.isFile() && isImageFile(file.toPath())) {
                        ImageView imageView = createImageView(file, settings);
                        imagesTilePane.getChildren().add(imageView);
                    }
                }
            }
        } else {
            HeaderAlertUtils.showErrorAlert("Failed load", "Image settings not loaded.");
        }
    }

    private ImageView createImageView(File imageFile, ImageSettings settings) {
        ImageView imageView = new ImageView(new Image(imageFile.toURI().toString()));

        if (!settings.isKeepOriginalSize()) {
            imageView.setFitWidth(settings.getWidth());
            imageView.setFitHeight(settings.getHeight());
            imageView.setPreserveRatio(true);
        }

        return imageView;
    }

    private boolean isImageFile(Path path) {
        try {
            String mimeType = Files.probeContentType(path);
            return mimeType != null && mimeType.startsWith("image");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void updateImagesLayout(String theme) {
        if (imagesTilePane != null) {
            imagesTilePane.getStyleClass().clear();
            imagesTilePane.getStyleClass().add("images-layout");
            if ("dark".equals(theme)) {
                imagesTilePane.getStyleClass().add("dark-theme-images");
            } else {
                imagesTilePane.getStyleClass().add("light-theme-images");
            }
        }
    }
}