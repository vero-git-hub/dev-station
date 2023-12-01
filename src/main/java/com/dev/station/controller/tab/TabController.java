package com.dev.station.controller.tab;

import com.dev.station.Localizable;
import com.dev.station.controller.MainController;
import com.dev.station.controller.forms.AddPathFormController;
import com.dev.station.entity.PathData;
import com.dev.station.file.JsonTabsManager;
import com.dev.station.file.TabData;
import com.dev.station.manager.LanguageManager;
import com.dev.station.manager.NotificationManager;
import com.dev.station.manager.clear.PathManager;
import com.dev.station.manager.clear.RecycleBinManager;
import com.dev.station.manager.clear.TableManager;
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
    private final Preferences prefs = MainController.prefs;
    @FXML
    private ToggleButton toggleMoveFiles;
    @FXML private ToggleButton toggleReturnFiles;
    @FXML private ToggleButton toggleClearRecycleBin;
    @FXML private TableView<PathData> pathsTable;
    @FXML private TableColumn<PathData, Number> numberColumn;
    @FXML private TableColumn<PathData, String> nameColumn;
    @FXML private TableColumn<PathData, String> pathColumn;
    @FXML private TableColumn<PathData, String> exclusionsColumn;
    @FXML private Label settingsDir;
    @FXML private Button addNewPath;
    @FXML private TextField recycleBinPathField;
    private MenuItem renameItem;
    private MenuItem setDefaultItem;
    private boolean isRestorationPerformed = false;
    ResourceBundle bundle;
    private NotificationManager notificationManager;
    private PathManager pathManager;
    private TableManager tableManager;
    private RecycleBinManager recycleBinManager;
    private String tabId;
    private Tab myTab;

    public TabController() {
        LanguageManager.registerForUpdates(this::updateUI);
    }

    public boolean isRestorationPerformed() {
        return isRestorationPerformed;
    }

    public void setRestorationPerformed(boolean restorationPerformed) {
        isRestorationPerformed = restorationPerformed;
    }

    public PathManager getPathManager() {
        return pathManager;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public void setMyTab(Tab myTab) {
        this.myTab = myTab;
    }

    @FXML public void initialize() {
        setMultilingual();
        loadSavedLanguage();
        definitionManagers();
        setupTable();

        pathManager.loadPaths();
        pathsTable.setItems(pathManager.getPathsList());
    }

    private void setupTable() {
        tableManager.setupTable(numberColumn, nameColumn, pathColumn, exclusionsColumn, pathsTable);
    }

    private void definitionManagers() {
        pathManager = new PathManager(prefs, this, notificationManager);
        tableManager = new TableManager();
        recycleBinManager = new RecycleBinManager(this, notificationManager, toggleMoveFiles);
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
            recycleBinManager.moveFilesToRecycleBin(event, toggleMoveFiles);
        }
    }

    @FXML
    private void returnFromRecycleBin(ActionEvent event) {
        if(toggleReturnFiles.isSelected()) {
            recycleBinManager.returnFromRecycleBin(event);
        }
    }

    @FXML
    private void clearRecycleBin(ActionEvent event) {
        if(toggleClearRecycleBin.isSelected()) {
            recycleBinManager.clearRecycleBin(event, toggleClearRecycleBin);
        }
    }

    @FXML public void handleAddPath(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/forms/AddPathForm.fxml"));
            loader.setResources(bundle);
            Parent root = loader.load();

            AddPathFormController addPathFormController = loader.getController();
            addPathFormController.setPathManager(pathManager);
            addPathFormController.setTabId(myTab.getId());

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
}