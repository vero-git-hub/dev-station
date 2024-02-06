package com.dev.station.controller.sidebar;

import com.dev.station.Localizable;
import com.dev.station.entity.CategoryData;
import com.dev.station.entity.ProcessHolder;
import com.dev.station.entity.ProgramData;
import com.dev.station.manager.DriverManager;
import com.dev.station.manager.LanguageManager;
import com.dev.station.manager.LaunchManager;
import com.dev.station.manager.NotificationManager;
import com.dev.station.model.ScriptsModel;
import com.dev.station.util.AlertUtils;
import com.dev.station.util.FileUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class ScriptsController implements Localizable {
    ResourceBundle bundle;
    NotificationManager notificationManager;
    LaunchManager launchManager;
    private ScriptsModel scriptsModel;
    @FXML private TextField categoryInputField;
    @FXML private VBox categoryContainer;

    @FXML private void initialize() {
        bundle = LanguageManager.getResourceBundle();

        notificationManager = new NotificationManager(bundle);
        LanguageManager.registerNotificationManager(notificationManager);

        loadSavedLanguage();

        scriptsModel = new ScriptsModel();
        launchManager = new LaunchManager(notificationManager);

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

                    loadCategories();
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
            showAddScriptDialog(category);
        });

        header.getChildren().addAll(orderLabel, nameLabel, editButton, deleteButton, addButton);

        return header;
    }

    /**
     * Modal for adding a new script
     * @param category
     */
    private void showAddScriptDialog(CategoryData category) {
        Dialog<ProgramData> dialog = new Dialog<>();
        dialog.setTitle("Add New Script");
        dialog.setHeaderText("Category '" + category.getName() + "'");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = createGridPane();

        TextField nameField = new TextField();
        TextField pathField = new TextField();
        TextField descriptionField = new TextField();
        ComboBox<String> actionComboBox = new ComboBox<>();
        actionComboBox.getItems().addAll("run", "other action");
        actionComboBox.setValue("run");

        nameField.setPromptText("Script Name");
        pathField.setPromptText("Path");
        descriptionField.setPromptText("Description");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);

        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);

        grid.add(new Label("Action:"), 0, 2);
        grid.add(actionComboBox, 1, 2);

        grid.add(new Label("Path (if 'run' -> exe/jar):"), 0, 3);
        grid.add(pathField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(nameField::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String programExtension = FileUtils.getFileExtension(pathField.getText());

                // TODO: Check extension
                return new ProgramData(-1, nameField.getText(), pathField.getText(), programExtension, descriptionField.getText(), actionComboBox.getValue(), category.getId());
            }
            return null;
        });

        Optional<ProgramData> result = dialog.showAndWait();

        result.ifPresent(newScript -> {
            scriptsModel.saveProgramData(newScript, -1);
            loadCategories();
        });
    }

    /**
     * Script form in the category
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
        actionComboBox.setValue(program.getAction());

        Label descriptionLabel = new Label("Description:");
        TextField descriptionField = new TextField(program.getDescription());

        Label pathLabel = new Label("Path name (exe/jar):");
        TextField pathField = new TextField(program.getProgramPath());

        Label categoryLabel = new Label("Category:");
        ComboBox<CategoryData> categoryComboBox = new ComboBox<>();

        List<CategoryData> categories = scriptsModel.loadCategoryData();
        categoryComboBox.setItems(FXCollections.observableArrayList(categories));

        categoryComboBox.setValue(findCategoryById(categories, program.getCategoryId()));

        categoryComboBox.setCellFactory(param -> new ListCell<CategoryData>() {
            @Override
            protected void updateItem(CategoryData item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        categoryComboBox.setButtonCell(new ListCell<CategoryData>() {
            @Override
            protected void updateItem(CategoryData item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });

        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(descriptionLabel, 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(actionLabel, 0, 2);
        grid.add(actionComboBox, 1, 2);
        grid.add(pathLabel, 0, 3);
        grid.add(pathField, 1, 3);
        grid.add(categoryLabel, 0, 4);
        grid.add(categoryComboBox, 1, 4);

        Button saveButton = new Button("Save");
        saveButton.setOnAction(event -> {
            String programName = nameField.getText();
            String programPath = pathField.getText();
            String description = descriptionField.getText();
            String action = actionComboBox.getValue();
            CategoryData selectedCategory = categoryComboBox.getValue();

            String programExtension = FileUtils.getFileExtension(programPath);

            if ("run".equals(action) && !("exe".equalsIgnoreCase(programExtension) || "jar".equalsIgnoreCase(programExtension))) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "For 'run' action, only '.exe' or '.jar' extensions are allowed.", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            int programId = program.getId();

            int oldCategoryId = program.getCategoryId();
            int newCategoryId = selectedCategory.getId();

            ProgramData updatedProgram = new ProgramData(programId, programName, programPath, programExtension, description, action, newCategoryId);

            scriptsModel.saveProgramData(updatedProgram, oldCategoryId);

            programTitledPane.setExpanded(false);
            loadCategories();

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

        Button launchButton = new Button("Run");
        launchButton.setOnAction(event -> {
            String path = program.getProgramPath();
            String extension = program.getProgramExtension();
            String action = program.getAction();

            if("run".equals(action)) {
                if("exe".toLowerCase().equals(extension)) {
                    launchManager.launchApplication(path);
                } else if ("jar".toLowerCase().equals(extension)) {
                    launchManager.launchJarApplication(path);
                } else {
                    AlertUtils.showErrorAlert("Wrong extension", "Check extension, only exe or jar is correct for 'run' action.");
                }
            }

        });

        HBox buttonsBox = new HBox(10, saveButton, cancelButton, deleteButton, launchButton);
        grid.add(buttonsBox, 1, 5);

        VBox contentBox = new VBox(grid);
        return contentBox;
    }

    private CategoryData findCategoryById(List<CategoryData> categories, int categoryId) {
        for (CategoryData category : categories) {
            if (category.getId() == categoryId) {
                return category;
            }
        }
        return null;
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

    @Override
    public void loadSavedLanguage() {

    }

    @Override
    public void switchLanguage(Locale newLocale) {

    }

    @Override
    public void updateUI() {

    }
}