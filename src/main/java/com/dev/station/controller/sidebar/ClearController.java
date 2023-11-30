package com.dev.station.controller.sidebar;

import com.dev.station.Localizable;
import com.dev.station.controller.MainController;
import com.dev.station.controller.forms.AddPathFormController;
import com.dev.station.entity.PathData;
import com.dev.station.manager.LanguageManager;
import com.dev.station.manager.NotificationManager;
import com.dev.station.manager.clear.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class ClearController implements Localizable {
    private final Preferences prefs = MainController.prefs;
    private boolean isRestorationPerformed = false;
    private boolean isRestorationPerformed2 = false;
    ResourceBundle bundle;
    private MenuItem renameItem;
    private MenuItem setDefaultItem;
    private NotificationManager notificationManager;
    private PathManager pathManager;
    private TableManager tableManager;
    private DialogManager dialogManager;
    private RecycleBinManager recycleBinManager;
    private TabManager tabManager;

    @FXML private ToggleButton toggleMoveFiles;
    @FXML private ToggleButton toggleReturnFiles;
    @FXML private ToggleButton toggleClearRecycleBin;
    @FXML private ToggleButton toggleMoveFiles2;
    @FXML private ToggleButton toggleReturnFiles2;
    @FXML private ToggleButton toggleClearRecycleBin2;
    @FXML private TabPane tabPane;
    @FXML private TableView<PathData> pathsTable;
    @FXML private TableColumn<PathData, Number> numberColumn;
    @FXML private TableColumn<PathData, String> nameColumn;
    @FXML private TableColumn<PathData, String> pathColumn;
    @FXML private TableColumn<PathData, String> exclusionsColumn;
    @FXML private Label settingsDir;

    public ClearController() {
        LanguageManager.registerForUpdates(this::updateUI);
    }

    public boolean isRestorationPerformed() {
        return isRestorationPerformed;
    }

    public void setRestorationPerformed(boolean restorationPerformed) {
        isRestorationPerformed = restorationPerformed;
    }

    public boolean isRestorationPerformed2() {
        return isRestorationPerformed2;
    }

    public void setRestorationPerformed2(boolean restorationPerformed2) {
        isRestorationPerformed2 = restorationPerformed2;
    }

    @FXML
    private void initialize() {
        bundle = LanguageManager.getResourceBundle();
        notificationManager = new NotificationManager(bundle);
        LanguageManager.registerNotificationManager(notificationManager);

        loadSavedLanguage();
        definitionManagers();
        setupTabPane();
        setupTable();

        pathManager.loadPaths();
        pathsTable.setItems(pathManager.getPathsList());
    }

    private void definitionManagers() {
        pathManager = new PathManager(prefs, this, notificationManager);
        tableManager = new TableManager();
        dialogManager = new DialogManager();
        recycleBinManager = new RecycleBinManager(this, notificationManager, toggleReturnFiles, toggleReturnFiles2);
        tabManager = new TabManager(this);
    }

    private void setupTabPane() {
        recycleBinManager.defineRecycleBins();
        tabManager.setupTabPane(tabPane);
    }

    private void setupTable() {
        tableManager.setupTable(numberColumn, nameColumn, pathColumn, exclusionsColumn, pathsTable);
    }

    @Override
    public void loadSavedLanguage() {
        String savedLanguage = prefs.get("selectedLanguage", "English");
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
        ResourceBundle bundle = LanguageManager.getResourceBundle();

        notificationManager.updateResourceBundle(bundle);

        toggleMoveFiles.setText(getTranslate("toggleMoveFiles"));
        toggleMoveFiles2.setText(getTranslate("toggleMoveFiles"));

        nameColumn.setText(bundle.getString("nameColumn"));
        pathColumn.setText(bundle.getString("pathColumn"));
        exclusionsColumn.setText(bundle.getString("exclusionsColumn"));
        settingsDir.setText(getTranslate("settingsDir"));
        setTooltips();
    }

    private void setTooltips() {
        Tooltip.install(toggleReturnFiles, new Tooltip(getTranslate("toggleReturnFilesHint")));
        Tooltip.install(toggleReturnFiles2, new Tooltip(getTranslate("toggleReturnFilesHint")));
        Tooltip.install(toggleClearRecycleBin, new Tooltip(getTranslate("toggleClearRecycleBinHint")));
        Tooltip.install(toggleClearRecycleBin2, new Tooltip(getTranslate("toggleClearRecycleBinHint")));
    }

    @FXML
    private void moveFilesToRecycleBin(ActionEvent event) {
        if(toggleMoveFiles.isSelected() || toggleMoveFiles2.isSelected()) {
            recycleBinManager.moveFilesToRecycleBin(event, toggleMoveFiles, toggleMoveFiles2);
        }
    }

    @FXML
    private void returnFromRecycleBin(ActionEvent event) {
        if(toggleReturnFiles.isSelected() || toggleReturnFiles2.isSelected()) {
            recycleBinManager.returnFromRecycleBin(event);
        }
    }

    @FXML
    private void clearRecycleBin(ActionEvent event) {
        if(toggleClearRecycleBin.isSelected() || toggleClearRecycleBin2.isSelected()) {
            recycleBinManager.clearRecycleBin(event, toggleClearRecycleBin, toggleClearRecycleBin2);
        }
    }

    @FXML public void handleAddPath(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/forms/AddPathForm.fxml"));
            loader.setResources(bundle);
            Parent root = loader.load();

            AddPathFormController addPathFormController = loader.getController();
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

    public String getTranslate(String key) {
        return bundle.getString(key);
    }

    private void updateTable() {
        pathManager.loadPaths();
    }
}