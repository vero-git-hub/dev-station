package com.dev.station.controller.sidebar;

import com.dev.station.entity.CategoryData;
import com.dev.station.entity.ProgramData;
import com.dev.station.manager.LanguageManager;
import com.dev.station.model.ScriptsModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
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
        Accordion mainAccordion = new Accordion();

        List<CategoryData> categories = scriptsModel.loadCategoryData();
        for (CategoryData category : categories) {
            TitledPane categoryTitledPane = new TitledPane();
            categoryTitledPane.setText("");

            HBox header = createCategoryHeader(category);
            categoryTitledPane.setGraphic(header);

            Accordion programsAccordion = new Accordion();

            for (ProgramData program : category.getPrograms()) {
                TitledPane programTitledPane = new TitledPane();
                programTitledPane.setText(program.getProgramName());

                VBox programDetails = new VBox();

                programDetails.getChildren().add(new Label("Path: " + program.getProgramPath()));
                programDetails.getChildren().add(new Label("Extension: " + program.getProgramExtension()));

                programTitledPane.setContent(programDetails);
                programsAccordion.getPanes().add(programTitledPane);
            }

            categoryTitledPane.setContent(programsAccordion);
            mainAccordion.getPanes().add(categoryTitledPane);
        }

        categoryContainer.getChildren().setAll(mainAccordion);
    }

    private HBox createCategoryHeader(CategoryData category) {
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label orderLabel = new Label(String.valueOf(category.getId()));
        orderLabel.setPadding(new Insets(5, 10, 5, 0));

        Label nameLabel = new Label(category.getName());
        nameLabel.setPadding(new Insets(5, 10, 5, 0));

        Button editButton = new Button("Edit");
        editButton.setOnAction(event -> {
            // TODO: logic for editing a program
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> {
            // TODO: logic for deleting a program
        });

        Button addButton = new Button("Add program");
        addButton.setOnAction(event -> {
            // TODO: logic for adding a program
        });

        header.getChildren().addAll(orderLabel, nameLabel, editButton, deleteButton, addButton);

        return header;
    }
}