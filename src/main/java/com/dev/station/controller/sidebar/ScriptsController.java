package com.dev.station.controller.sidebar;

import com.dev.station.entity.CategoryData;
import com.dev.station.manager.LanguageManager;
import com.dev.station.model.ScriptsModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.ResourceBundle;

public class ScriptsController {
    ResourceBundle bundle;
    private ScriptsModel scriptsModel = new ScriptsModel();
    @FXML private TextField categoryInputField;
    @FXML private VBox categoryContainer;

    @FXML private void initialize() {
        bundle = LanguageManager.getResourceBundle();
        loadCategories();
    }

    @FXML
    private void handleSaveCategory(ActionEvent event) {
        String categoryName  = categoryInputField.getText().trim();
        if (!categoryName.isEmpty()) {
            scriptsModel.handleSaveCategory(event, categoryName);
            loadCategories();
            categoryInputField.clear();
        }

    }

    private void loadCategories() {
        categoryContainer.getChildren().clear();
        List<CategoryData> categories = scriptsModel.loadCategoryData();
        for (CategoryData category : categories) {
            HBox categoryBox = new HBox(10);
            Label categoryLabel = new Label(category.getName());
            categoryBox.getChildren().add(categoryLabel);
            categoryContainer.getChildren().add(categoryBox);
        }
    }

}