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
        setupEditButtonColumn(editColumn);
        tableManager.setupTable(numberColumn, nameColumn, pathColumn, exclusionsColumn, editColumn, pathsTable);
    }

    private TableColumn<PathData, Void> setupEditButtonColumn(TableColumn<PathData, Void> editColumn) {
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
        return editColumn;
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