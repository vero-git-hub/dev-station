package com.dev.station.controller.tab;

import com.dev.station.Localizable;
import com.dev.station.controller.MainController;
import com.dev.station.controller.forms.AddPathFormController;
import com.dev.station.file.JsonTabsManager;
import com.dev.station.file.PathData;
import com.dev.station.file.TabData;
import com.dev.station.manager.LanguageManager;
import com.dev.station.manager.NotificationManager;
import com.dev.station.manager.clear.PathManager;
import com.dev.station.manager.clear.RecycleBinManager;
import com.dev.station.manager.clear.TableManager;
import com.dev.station.model.SettingsModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class TabController implements Localizable {

    @FXML private ToggleButton toggleMoveFiles;
    @FXML private ToggleButton toggleReturnFiles;
    @FXML private ToggleButton toggleClearRecycleBin;
    @FXML private TableView<PathData> pathsTable;
    @FXML private TableColumn<PathData, Number> numberColumn;
    @FXML private TableColumn<PathData, String> nameColumn;
    @FXML private TableColumn<PathData, String> pathColumn;
    @FXML private TableColumn<PathData, String> exclusionsColumn;
    @FXML private TableColumn<PathData, Void> editColumn;
    @FXML private TableColumn<PathData, Void> deleteColumn;
    @FXML private Label settingsDir;
    @FXML private Button addNewPath;
    @FXML private TextField recycleBinPathField;
    private final Preferences prefs = MainController.prefs;
    private boolean isRestorationPerformed = false;
    ResourceBundle bundle;
    private NotificationManager notificationManager;
    private PathManager pathManager;
    private TableManager tableManager;
    private RecycleBinManager recycleBinManager;
    private Tab myTab;
    SettingsModel settingsModel;

    public TabController() {
        LanguageManager.registerForUpdates(this::updateUI);
        settingsModel = new SettingsModel();
    }

    public boolean isRestorationPerformed() {
        return isRestorationPerformed;
    }

    public void setRestorationPerformed(boolean restorationPerformed) {
        isRestorationPerformed = restorationPerformed;
    }

    public Tab getMyTab() {
        return myTab;
    }

    public void setMyTab(Tab myTab) {
        this.myTab = myTab;
    }

    @FXML public void initialize() {
        setMultilingual();
        loadSavedLanguage();
        definitionManagers();
    }

    private void definitionManagers() {
        pathManager = new PathManager(prefs, notificationManager);
        tableManager = new TableManager();
        recycleBinManager = new RecycleBinManager(this, notificationManager, toggleReturnFiles);
    }

    private void setMultilingual() {
        bundle = LanguageManager.getResourceBundle();
        notificationManager = new NotificationManager(bundle);
        LanguageManager.registerNotificationManager(notificationManager);
    }

    public void updateUI(ResourceBundle bundle) {
        notificationManager.updateResourceBundle(bundle);

        toggleMoveFiles.setText(getTranslate("toggleMoveFiles"));

        nameColumn.setText(bundle.getString("nameColumn"));
        pathColumn.setText(bundle.getString("pathColumn"));
        exclusionsColumn.setText(bundle.getString("exclusionsColumn"));
        settingsDir.setText(getTranslate("settingsDir"));
        setTooltips();
    }

    @FXML
    private void moveFilesToRecycleBin(ActionEvent event) {
        if(toggleMoveFiles.isSelected()) {
            recycleBinManager.moveFilesToRecycleBin(event, toggleMoveFiles, myTab.getId());
        }
    }

    @FXML
    private void returnFromRecycleBin(ActionEvent event) {
        if(toggleReturnFiles.isSelected()) {
            recycleBinManager.returnFromRecycleBin(event);
        }
    }

    @FXML
    private void clearRecycleBin() {
        if(toggleClearRecycleBin.isSelected()) {
            recycleBinManager.clearRecycleBin(myTab.getId());
        }
    }

    @FXML public void handleAddPath() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dev/station/ui/forms/AddPathForm.fxml"));
            loader.setResources(bundle);
            Parent root = loader.load();

            AddPathFormController addPathFormController = loader.getController();
            addPathFormController.setPathManager(pathManager);
            addPathFormController.setTabId(myTab.getId());
            addPathFormController.setTabController(this);

            addPathFormController.setDataSavedListener(this::updateTable);

            Stage stage = new Stage();
            stage.setTitle(getTranslate("addPathFormTitle"));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSaveRecycleBinPath() {
        String recycleBinPath = recycleBinPathField.getText().trim();

        if (!recycleBinPath.isEmpty()) {
            JsonTabsManager jsonTabsManager = new JsonTabsManager();

            List<TabData> tabs = jsonTabsManager.loadTabs();

            for (TabData tab : tabs) {
                if (tab.getId().equals(myTab.getId())) {
                    tab.setRecycleBinPath(recycleBinPath);
                    break;
                }
            }

            jsonTabsManager.saveTabs(tabs);
        }
    }

    public String getTranslate(String key) {
        return bundle.getString(key);
    }

    private void updateTable() {
        pathManager.loadPaths();
    }

    @Override
    public void loadSavedLanguage() {
        String savedLanguage = settingsModel.loadLanguageSetting();
        Locale locale = LanguageManager.getLocale(savedLanguage);
        LanguageManager.switchLanguage(locale);
    }

    @Override
    public void switchLanguage(Locale newLocale) {
        LanguageManager.switchLanguage(newLocale);
        updateUI();
    }

    @Override
    public void updateUI() {
        notificationManager.updateResourceBundle(bundle);

        toggleMoveFiles.setText(getTranslate("toggleMoveFiles"));

        nameColumn.setText(bundle.getString("nameColumn"));
        pathColumn.setText(bundle.getString("pathColumn"));
        exclusionsColumn.setText(bundle.getString("exclusionsColumn"));
        settingsDir.setText(getTranslate("settingsDir"));
        recycleBinPathField.setPromptText(getTranslate("recycleBinPathField"));
        setTooltips();
    }

    private void setTooltips() {
        Tooltip.install(toggleReturnFiles, new Tooltip(getTranslate("toggleReturnFilesHint")));
        Tooltip.install(toggleClearRecycleBin, new Tooltip(getTranslate("toggleClearRecycleBinHint")));
        Tooltip.install(addNewPath, new Tooltip());
    }

    public void loadData(TabData tabData) {
        recycleBinPathField.setText(tabData.getRecycleBinPath());

        setupTableColumns();

        ObservableList<PathData> paths = FXCollections.observableArrayList(tabData.getPaths());
        pathsTable.setItems(paths);
    }

    public void setupTableColumns() {
        setupEditButtonColumn();
        setupDeleteButtonColumn();
        tableManager.setupTable(numberColumn, nameColumn, pathColumn, exclusionsColumn, editColumn, deleteColumn, pathsTable);
    }

    private void setupEditButtonColumn() {
        editColumn.setCellFactory(param -> new TableCell<PathData, Void>() {
            private final Button editButton = new Button("Edit");

            {
                editButton.setOnAction(event -> {
                    PathData data = getTableView().getItems().get(getIndex());

                    handleEditAction(data);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });
    }

    /**
     * Edit tab path
     * @param data
     */
    private void handleEditAction(PathData data) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dev/station/ui/forms/AddPathForm.fxml"));
            loader.setResources(bundle);
            Parent root = loader.load();

            AddPathFormController addPathFormController = loader.getController();
            addPathFormController.setPathManager(pathManager);
            addPathFormController.setTabId(myTab.getId());
            addPathFormController.setTabController(this);
            addPathFormController.setEditMode(true, data);
            addPathFormController.setCurrentPathData(data);

            addPathFormController.setDataSavedListener(this::updateTable);

            Stage stage = new Stage();
            stage.setTitle(getTranslate("editPathFormTitle"));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupDeleteButtonColumn() {
        deleteColumn.setCellFactory(param -> new TableCell<PathData, Void>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    PathData data = getTableView().getItems().get(getIndex());
                    handleDeleteAction(data);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
    }

    /**
     * Delete tab path
     * @param data
     */
    private void handleDeleteAction(PathData data) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Path");
        alert.setContentText("Are you sure you want to delete this path?");

        ButtonType buttonTypeYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonTypeYes) {

            pathsTable.getItems().remove(data);

            JsonTabsManager jsonTabsManager = new JsonTabsManager();
            List<TabData> tabs = jsonTabsManager.loadTabs();

            boolean foundAndDeleted = false;
            for (TabData tab : tabs) {
                if (tab.getId().equals(myTab.getId())) {
                    foundAndDeleted = tab.getPaths().removeIf(path -> path.equals(data));
                    break;
                }
            }

            if (foundAndDeleted) {
                boolean savedSuccessfully = jsonTabsManager.saveTabs(tabs);
                if (!savedSuccessfully) {
                    System.err.println("Failed to save tabs after deletion.");
                    pathsTable.getItems().add(data);
                } else {
                    updatePathsTable();
                }
            }
        }
    }

    public void updatePathsTable() {
        JsonTabsManager jsonTabsManager = new JsonTabsManager();
        List<TabData> tabs = jsonTabsManager.loadTabs();

        for (TabData tab : tabs) {
            if (tab.getId().equals(this.myTab.getId())) {
                ObservableList<PathData> paths = FXCollections.observableArrayList(tab.getPaths());
                pathsTable.setItems(paths);
                break;
            }
        }

        pathsTable.refresh();
    }
}