package com.dev.station.controller.header;

import com.dev.station.controller.MainController;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.prefs.Preferences;

public class ImagesController {
    private final Preferences prefs = MainController.prefs;
    @FXML
    private TilePane imagesTilePane;

    public void initialize() {
        loadImages();
    }

    public void loadImages() {
        imagesTilePane.getChildren().clear();

        String imagesFolderPath = prefs.get("imagesFolderPath", "default/path");
        File folder = new File(imagesFolderPath);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile() && isImageFile(file.toPath())) {
                    ImageView imageView = createImageView(file);
                    imagesTilePane.getChildren().add(imageView);
                }
            }
        }
    }

    private ImageView createImageView(File imageFile) {
        ImageView imageView = new ImageView(new Image(imageFile.toURI().toString()));

        boolean useOriginalSize = prefs.getBoolean("useOriginalSizeCheckbox", false);
        if (!useOriginalSize) {
            int width = prefs.getInt("imageWidthField", 100);
            int height = prefs.getInt("imageHeightField", 100);
            imageView.setFitWidth(width);
            imageView.setFitHeight(height);
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
}