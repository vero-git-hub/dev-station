package com.dev.station.controller.sidebar;

import com.dev.station.Localizable;
import com.dev.station.entity.CategoryData;
import com.dev.station.entity.ProgramData;
import com.dev.station.manager.LanguageManager;
import com.dev.station.manager.LaunchManager;
import com.dev.station.manager.NotificationManager;
import com.dev.station.model.ScriptsModel;
import com.dev.station.model.SettingsModel;
import com.dev.station.util.AlertUtils;
import com.dev.station.util.FileUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.util.*;

public class ScriptsController implements Localizable {

    @FXML private TextField categoryNameInputField;
    @FXML private VBox categoryContainer;
    @FXML private Button saveCategoryButton;
    ResourceBundle bundle;
    NotificationManager notificationManager;
    LaunchManager launchManager;
    private ScriptsModel scriptsModel;
    Button renameCategoryButton;
    Button deleteCategoryButton;
    SettingsModel settingsModel;
    Button saveButton;
    Button cancelButton;
    Button deleteButton;
    Button launchButton;

    public ScriptsController() {
        LanguageManager.registerForUpdates(this::updateUI);
        settingsModel = new SettingsModel();
    }

    @FXML private void initialize() {
        bundle = LanguageManager.getResourceBundle();

        notificationManager = new NotificationManager(bundle);
        LanguageManager.registerNotificationManager(notificationManager);

        loadSavedLanguage();

        scriptsModel = new ScriptsModel();
        launchManager = new LaunchManager(notificationManager);

        loadCategories();
        setUIText();
    }

    @FXML private void handleSaveCategory(ActionEvent event) {
        String categoryName  = categoryNameInputField.getText().trim();
        if (!categoryName.isEmpty()) {
            scriptsModel.handleSaveCategory(event, categoryName);
            loadCategories();
            categoryNameInputField.clear();
        }
    }

    private void loadCategories() {
        if (scriptsModel == null) {
            return;
        }

        VBox mainContainer = new VBox(5);

        List<CategoryData> categories = scriptsModel.loadCategoryData();

        for (int i = 0; i < categories.size(); i++) {
            CategoryData category = categories.get(i);

            VBox root = new VBox();
            //root.setPadding(new Insets(10));
            root.setAlignment(Pos.CENTER);

            TitledPane categoryTitledPane = new TitledPane();
            categoryTitledPane.setAlignment(Pos.CENTER);

            HBox contentPane = new HBox();
            contentPane.setAlignment(Pos.CENTER);
            contentPane.setPadding(new Insets(0, 10, 0, 35));
            contentPane.minWidthProperty().bind(categoryTitledPane.widthProperty());

            HBox region = new HBox();
            region.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(region, Priority.ALWAYS);

            HBox leftBox = new HBox(10);
            Label programCountLabel = getOrderLabel(category);
            Tooltip.install(programCountLabel, new Tooltip(getTranslate("programCountLabel")));
            Label nameLabel = getNameLabel(category);
            leftBox.getChildren().addAll(programCountLabel, nameLabel);

            HBox rightBox = new HBox(10);
            Button editButton = getEditButton(category, nameLabel);
            Button deleteButton = getDeleteButton(category);
            Button addButton = getAddButton(category);
            rightBox.getChildren().addAll(editButton, deleteButton, addButton);

            contentPane.getChildren().addAll(leftBox, region, rightBox);

            categoryTitledPane.setGraphic(contentPane);
            categoryTitledPane.getStyleClass().add("category-titled-pane");

            VBox programsBox = new VBox(5);

            for (ProgramData program : category.getPrograms()) {
                TitledPane programTitledPane = new TitledPane();
                programTitledPane.setText(program.getProgramName());

                VBox programDetailsBox = createProgramDetails(program, programTitledPane);
                programTitledPane.setContent(programDetailsBox);
                programTitledPane.setExpanded(false);

                programsBox.getChildren().add(programTitledPane);
            }

            categoryTitledPane.setContent(new VBox(contentPane, programsBox));
            categoryTitledPane.setExpanded(true);

            mainContainer.getChildren().add(categoryTitledPane);
        }

        categoryContainer.getChildren().setAll(mainContainer);
    }

    private Label getOrderLabel(CategoryData category) {
        String text = String.valueOf(category.getPrograms().size());
        Label orderLabel = new Label(text);
        orderLabel.setPadding(new Insets(5, 10, 5, 0));
        orderLabel.getStyleClass().add("category-order-label");
        orderLabel.setOnMouseEntered(event -> orderLabel.setStyle("-fx-background-color: lightgrey; -fx-text-fill: #333"));
        orderLabel.setOnMouseExited(event -> orderLabel.setStyle(null));
        return orderLabel;
    }

    private Label getNameLabel(CategoryData category) {
        Label nameLabel = new Label(category.getName().toUpperCase());
        nameLabel.setPadding(new Insets(5, 10, 5, 0));
        HBox.setMargin(nameLabel, new Insets(0, 16, 0, 16));
        nameLabel.getStyleClass().add("category-name-label");
        return nameLabel;
    }

    private Button getAddButton(CategoryData category) {
        Image image = new Image(getClass().getResourceAsStream("/images/scripts/rs-class-add-block.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);

        Button addScriptButton = new Button(getTranslate("addScriptButton").toUpperCase(), imageView);
        addScriptButton.getStyleClass().add("button-add");
        addScriptButton.setOnAction(event -> {
            showAddScriptDialog(category);
        });
        addScriptButton = setHoverEffects(addScriptButton);
        return addScriptButton;
    }

    private Button getDeleteButton(CategoryData category) {
        Image fullImage = new Image(getClass().getResourceAsStream("/images/scripts/rs-reward-board-icon.png"));
        ImageView imageView = new ImageView(fullImage);

        Rectangle clip = new Rectangle(0, 0, 16, 34);
        imageView.setClip(clip);

        imageView.setViewport(new Rectangle2D(0, 15, 17, 17));

        deleteCategoryButton = new Button();
        deleteCategoryButton.setGraphic(imageView);

        deleteCategoryButton.setOnAction(event -> {
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
        deleteCategoryButton.setTooltip(new Tooltip(getTranslate("deleteCategoryButton")));
        deleteCategoryButton = setHoverEffects(deleteCategoryButton);
        return deleteCategoryButton;
    }

    private Button getEditButton(CategoryData category, Label nameLabel) {
        Image fullImage = new Image(getClass().getResourceAsStream("/images/scripts/rs-reward-board-icon.png"));
        ImageView imageView = new ImageView(fullImage);

        Rectangle clip = new Rectangle(0, 0, 16, 34);
        imageView.setClip(clip);

        imageView.setViewport(new Rectangle2D(0, 31, 17, 17));

        renameCategoryButton = new Button();
        renameCategoryButton.setGraphic(imageView);

        renameCategoryButton.setOnAction(event -> {
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

        renameCategoryButton.setTooltip(new Tooltip(getTranslate("renameCategoryButton")));

        renameCategoryButton = setHoverEffects(renameCategoryButton);
        return renameCategoryButton;
    }

    private Button setHoverEffects(Button button) {
        button.setOnMouseEntered(event -> button.setStyle("-fx-background-color: lightgrey;"));
        button.setOnMouseExited(event -> button.setStyle(null));
        return button;
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
        actionComboBox.getItems().addAll("run", "other (future)");
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

        Label nameLabel = new Label(getTranslate("programsNameLabel"));
        TextField nameField = new TextField(program.getProgramName());

        Label actionLabel = new Label(getTranslate("programsActionLabel"));
        ComboBox<String> actionComboBox = new ComboBox<>();
        actionComboBox.getItems().addAll(getTranslate("actionComboBoxRun"), getTranslate("actionComboBoxOther"));

        String programAction = program.getAction();
        if(programAction.equals("run") || programAction.equals("запуск")) {
            actionComboBox.setValue(getTranslate("actionComboBoxRun"));
        } else {
            actionComboBox.setValue(getTranslate("actionComboBoxOther"));
        }

        Label descriptionLabel = new Label(getTranslate("programsDescriptionLabel"));
        TextField descriptionField = new TextField(program.getDescription());

        Label pathLabel = new Label(getTranslate("programsPathLabel"));
        TextField pathField = new TextField(program.getProgramPath());

        Label categoryLabel = new Label(getTranslate("programsCategoryLabel"));
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

        saveButton = createSaveButton(program, nameField, pathField, descriptionField, actionComboBox, categoryComboBox, programTitledPane);
        cancelButton = createCancelButton(programTitledPane);
        deleteButton = createDeleteButton(program, programTitledPane);
        launchButton = createLaunchButton(program);

        setButtonStyles(Arrays.asList(saveButton, cancelButton, deleteButton, launchButton));
        setTooltips();

        HBox buttonsBox = new HBox(10, saveButton, cancelButton, deleteButton, launchButton);

        grid.add(buttonsBox, 1, 5);

        VBox contentBox = new VBox(grid);
        return contentBox;
    }

    private void setButtonStyles(List<Button> list) {
        for (Button btn : list) {
            btn.getStyleClass().add("clickable");
        }
    }

    private Button createSaveButton(ProgramData program, TextField nameField, TextField pathField, TextField descriptionField, ComboBox<String> actionComboBox, ComboBox<CategoryData> categoryComboBox, TitledPane programTitledPane) {
        Button saveButton = new Button();
        Image image = new Image(getClass().getResourceAsStream("/images/crud/save-30.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(25);
        imageView.setFitWidth(25);
        saveButton.setGraphic(imageView);
        saveButton.setOnAction(event -> {
            String programName = nameField.getText();
            String programPath = pathField.getText();
            String description = descriptionField.getText();
            String action = actionComboBox.getValue();
            CategoryData selectedCategory = categoryComboBox.getValue();

            String programExtension = FileUtils.getFileExtension(programPath);

            if (("run".equals(action) || "запуск".equals(action))
                    && (!("exe".equalsIgnoreCase(programExtension) || "jar".equalsIgnoreCase(programExtension)))) {
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
        return saveButton;
    }

    private Button createCancelButton(TitledPane programTitledPane) {
        Button cancelButton = new Button();
        Image image = new Image(getClass().getResourceAsStream("/images/action/return-30-1.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(25);
        imageView.setFitWidth(25);
        cancelButton.setGraphic(imageView);
        cancelButton.setOnAction(event -> {
            if (programTitledPane != null) {
                programTitledPane.setExpanded(false);
            }
        });
        return cancelButton;
    }

    private Button createDeleteButton(ProgramData program, TitledPane programTitledPane) {
        Button deleteButton = new Button();
        Image image = new Image(getClass().getResourceAsStream("/images/action/clear-48.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(25);
        imageView.setFitWidth(25);
        deleteButton.setGraphic(imageView);
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

                loadCategories();
            }
        });
        return deleteButton;
    }

    private Button createLaunchButton(ProgramData program) {
        Button launchButton = new Button();
        Image image = new Image(getClass().getResourceAsStream("/images/action/start-48.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(25);
        imageView.setFitWidth(25);
        launchButton.setGraphic(imageView);
        launchButton.setOnAction(event -> {
            String path = program.getProgramPath();
            String extension = program.getProgramExtension();
            String action = program.getAction();

            if("run".equals(action) || "запуск".equals(action)) {
                if("exe".toLowerCase().equals(extension)) {
                    launchManager.launchApplication(path);
                } else if ("jar".toLowerCase().equals(extension)) {
                    if(launchManager != null){
                        launchManager.launchJarApplication(path);
                    }
                } else {
                    AlertUtils.showErrorAlert("Wrong extension", "Check extension, only exe or jar is correct for 'run' action.");
                }
            }

        });
        return launchButton;
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

    private void setUIText() {
        saveCategoryButton.setText(getTranslate("saveCategoryButton"));
        categoryNameInputField.setPromptText(getTranslate("categoryNameInputField"));
    }

    private void setTooltips() {
        Tooltip.install(saveButton, new Tooltip(getTranslate("saveButton")));
        Tooltip.install(cancelButton, new Tooltip(getTranslate("cancelButton")));
        Tooltip.install(deleteButton, new Tooltip(getTranslate("deleteButton")));
        Tooltip.install(launchButton, new Tooltip(getTranslate("launchButton")));
    }

    private String getTranslate(String key) {
        return bundle.getString(key);
    }

    @Override public void loadSavedLanguage() {
        String savedLanguage = settingsModel.loadLanguageSetting();
        Locale locale = LanguageManager.getLocale(savedLanguage);
        LanguageManager.switchLanguage(locale);
    }

    @Override public void switchLanguage(Locale newLocale) {
        LanguageManager.switchLanguage(newLocale);
        updateUI();
    }

    @Override public void updateUI() {
        bundle = LanguageManager.getResourceBundle();
        setUIText();
        loadCategories();
    }
}