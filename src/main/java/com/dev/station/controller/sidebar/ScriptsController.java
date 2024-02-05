package com.dev.station.controller.sidebar;

import com.dev.station.entity.CategoryData;
import com.dev.station.entity.ProgramData;
import com.dev.station.manager.LanguageManager;
import com.dev.station.model.ScriptsModel;
import com.dev.station.util.FileUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;
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

        for (int i = 0; i < categories.size(); i++) {
            CategoryData category = categories.get(i);

            TitledPane categoryTitledPane = new TitledPane();
            categoryTitledPane.setText("");

            HBox header = createCategoryHeader(category, i + 1);
            categoryTitledPane.setGraphic(header);

            Accordion programsAccordion = new Accordion();

            for (ProgramData program : category.getPrograms()) {
                TitledPane programTitledPane = new TitledPane();
                programTitledPane.setText(program.getProgramName());

                VBox programDetailsBox = createProgramDetails(program, programTitledPane);
                programTitledPane.setContent(programDetailsBox);

                programsAccordion.getPanes().add(programTitledPane);
            }

            categoryTitledPane.setContent(programsAccordion);
            mainAccordion.getPanes().add(categoryTitledPane);
        }

        categoryContainer.getChildren().setAll(mainAccordion);
    }

    private HBox createCategoryHeader(CategoryData category, int orderNumber) {
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label orderLabel = new Label(String.valueOf(orderNumber));
        orderLabel.setPadding(new Insets(5, 10, 5, 0));

        Label nameLabel = new Label(category.getName());
        nameLabel.setPadding(new Insets(5, 10, 5, 0));

        Button editButton = new Button("Rename");
        editButton.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog(category.getName());
            dialog.setTitle("Renaming a category");
            dialog.setHeaderText("Changing the category name");
            dialog.setContentText("Enter a new name:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(newName -> {
                if (!newName.trim().isEmpty()) {
                    scriptsModel.renameCategory(category.getId(), newName);

                    nameLabel.setText(newName);
                }
            });
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Deletion confirmation");
            confirmationAlert.setHeaderText("Delete a category");
            confirmationAlert.setContentText("Are you sure you want to delete this category?");

            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                scriptsModel.checkAndDeleteCategory(category);
                loadCategories();
            }
        });

        Button addButton = new Button("Add script");
        addButton.setOnAction(event -> {
            // TODO: logic for adding a script
        });

        header.getChildren().addAll(orderLabel, nameLabel, editButton, deleteButton, addButton);

        return header;
    }

    /**
     * Create program form in the category
     * @param program
     * @return
     */
    private VBox createProgramDetails(ProgramData program, TitledPane programTitledPane) {
        GridPane grid = createGridPane();

        Label nameLabel = new Label("Script name:");
        TextField nameField = new TextField(program.getProgramName());

        Label actionLabel = new Label("Action:");
        ComboBox<String> actionComboBox = new ComboBox<>();
        actionComboBox.getItems().addAll("run", "other action");
        actionComboBox.setValue("run");

        Label descriptionLabel = new Label("Description:");
        TextField descriptionField = new TextField("open " + program.getProgramName());

        Label pathLabel = new Label("Path name (exe/jar):");
        TextField pathField = new TextField(program.getProgramPath());

        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(descriptionLabel, 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(actionLabel, 0, 2);
        grid.add(actionComboBox, 1, 2);
        grid.add(pathLabel, 0, 3);
        grid.add(pathField, 1, 3);

        Button saveButton = new Button("Save");
        saveButton.setOnAction(event -> {
            String programName = nameField.getText();
            String programPath = pathField.getText();

            String description = descriptionField.getText();
            String action = actionComboBox.getValue();

            String programExtension = "";
            if (action.equals("run")) {
                programExtension = FileUtils.getFileExtension(programPath);
            }

            int programId = program.getId();
            int categoryId = program.getCategoryId();

            ProgramData updatedProgram = new ProgramData(programId, programName, programPath, programExtension, description, action, categoryId);

            scriptsModel.saveProgramData(updatedProgram, categoryId);

            programTitledPane.setExpanded(false);
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> {
            if (programTitledPane != null) {
                programTitledPane.setExpanded(false);
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Deletion confirmation");
            confirmationAlert.setHeaderText("Deleting a script");
            confirmationAlert.setContentText("Are you sure you want to remove this script?");

            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                int programId = program.getId();
                int categoryId = program.getCategoryId();

                scriptsModel.deleteProgram(programId, categoryId);

                updateUIAfterDeletion();
            }
        });

        HBox buttonsBox = new HBox(10, saveButton, cancelButton, deleteButton);
        grid.add(buttonsBox, 1, 4);

        VBox contentBox = new VBox(grid);
        return contentBox;
    }

    private GridPane createGridPane() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        return grid;
    }

    private void updateUIAfterDeletion() {
        loadCategories();
    }

}