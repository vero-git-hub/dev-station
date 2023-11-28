package com.dev.station.controller.sidebar;

import com.dev.station.controller.MainController;
import com.dev.station.entity.RecoveryContext;
import com.dev.station.entity.RecycleBin;
import com.dev.station.manager.FileManager;
import com.dev.station.manager.NotificationManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

public class ClearController {
    private final Preferences prefs = MainController.prefs;
    private boolean isRestorationPerformed = false;
    private boolean isRestorationPerformed2 = false;
    private RecycleBin recycleBin;
    private RecycleBin recycleBin2;
    ResourceBundle bundle;
    private NotificationManager notificationManager;
    @FXML private ToggleButton toggleMoveFiles;
    @FXML private ToggleButton toggleReturnFiles;
    @FXML private ToggleButton toggleClearRecycleBin;
    @FXML private ToggleButton toggleMoveFiles2;
    @FXML private ToggleButton toggleReturnFiles2;
    @FXML private ToggleButton toggleClearRecycleBin2;
    @FXML private TabPane tabPane;

    @FXML
    private void initialize() {
        Locale locale = Locale.getDefault();
        //locale = new Locale("en");
        bundle = ResourceBundle.getBundle("messages", locale);
        this.notificationManager = new NotificationManager(bundle);

        toggleMoveFiles.setText(bundle.getString("toggleMoveFiles"));
        toggleMoveFiles2.setText(bundle.getString("toggleMoveFiles"));

        defineRecycleBin();
        defineRecycleBin2();

        tabPane.getTabs().forEach(this::setupTabContextMenu);

        String defaultTabId = prefs.get("defaultTabId", null);
        if (defaultTabId != null) {
            tabPane.getTabs().stream()
                    .filter(tab -> tab.getId().equals(defaultTabId))
                    .findFirst()
                    .ifPresent(tabPane.getSelectionModel()::select);
        }

        tabPane.getTabs().forEach(tab -> {
            String tabId = tab.getId();
            if (tabId != null && prefs.get(tabId, null) != null) {
                tab.setText(prefs.get(tabId, tab.getText()));
            }
        });
    }

    private void defineRecycleBin() {
        String recycleBinPath = prefs.get("firstRecycleBin", "C:\\Default\\RecycleBinPath");
        this.recycleBin = new RecycleBin(recycleBinPath);
    }

    private void defineRecycleBin2() {
        String recycleBinPath = prefs.get("secondRecycleBin", "C:\\Default\\RecycleBinPath");
        this.recycleBin2 = new RecycleBin(recycleBinPath);
    }

    private void setupTabContextMenu(Tab tab) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem renameItem = new MenuItem(getTranslate("renameItem"));
        MenuItem setDefaultItem = new MenuItem(getTranslate("setDefaultItem"));
        contextMenu.getItems().addAll(renameItem, setDefaultItem);

        renameItem.setOnAction(e -> handleRenameTab(tab));
        setDefaultItem.setOnAction(e -> handleSetDefaultTab(tab));

        tab.setContextMenu(contextMenu);
    }

    private void handleRenameTab(Tab tab) {
        TextInputDialog dialog = new TextInputDialog(tab.getText());
        dialog.setTitle(getTranslate("titleRenameTab"));
        dialog.setHeaderText(getTranslate("headerTextRenameTab"));
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            tab.setText(name);
            prefs.put(tab.getId(), name);
        });
    }

    private void handleSetDefaultTab(Tab tab) {
        prefs.put("defaultTabId", tab.getId());
    }

    @FXML
    private void moveFilesToRecycleBin(ActionEvent event) {
        Object source = event.getSource();
        String rootFolderPath;
        RecycleBin currentRecycleBin;

        if (source == toggleMoveFiles) {
            rootFolderPath = prefs.get("fieldClearFirstFolder", "C:\\Default\\Path");
            currentRecycleBin = recycleBin;
            performRecycleBinActions(source, rootFolderPath, currentRecycleBin);
            isRestorationPerformed = false;
        } else if (source == toggleMoveFiles2) {
            rootFolderPath = prefs.get("fieldClearSecondFolder", "C:\\Default\\Path");
            currentRecycleBin = recycleBin2;
            performRecycleBinActions(source, rootFolderPath, currentRecycleBin);
            isRestorationPerformed2 = false;
        } else {
            notificationManager.showErrorAlert("moveFilesToRecycleBinMethodError");
        }
    }

    private void performRecycleBinActions(Object source, String rootFolderPath, RecycleBin recycleBin) {
        ToggleButton toggleButton = (ToggleButton) source;
        if (toggleButton.isSelected()) {

            clearRecycleBinContents(recycleBin);

            try {
                FileManager.clearFolderContents(rootFolderPath, "cache", recycleBin);
                FileManager.clearFolderContents(rootFolderPath, "log", recycleBin);
                FileManager.clearFolderContents(rootFolderPath, "tmp", recycleBin);
                FileManager.clearVarFolderContents(Paths.get(rootFolderPath, "var").toString(), recycleBin);

                notificationManager.showInformationAlert("moveFilesToRecycleBinSuccess");
            } catch (IOException e) {
                notificationManager.showErrorAlert("moveFilesToRecycleBinError");
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void clearRecycleBin(ActionEvent event) {
        Object source = event.getSource();
        if (source == toggleClearRecycleBin) {
            handleClearRecycleBin(recycleBin, "firstRecycleBin");
        } else if (source == toggleClearRecycleBin2) {
            handleClearRecycleBin(recycleBin2, "secondRecycleBin");
        }
    }

    private void handleClearRecycleBin(RecycleBin recycleBin, String keyRecycleBin) {
        if (recycleBin.getRecycleBinPath() == null || !Files.exists(recycleBin.getRecycleBinPath())) {
            notificationManager.showErrorAlert("handleClearRecycleBinNotExist");
            return;
        }

        String recycleBinPathString = prefs.get(keyRecycleBin, "C:\\Default\\RecycleBinPath");
        Path recycleBinPath = Paths.get(recycleBinPathString);

        try {
            if (Files.exists(recycleBinPath) && Files.isDirectory(recycleBinPath)) {
                try (Stream<Path> paths = Files.walk(recycleBinPath)) {
                    paths.filter(p -> !p.equals(recycleBinPath))
                            .sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                }
                recycleBin.clearMetadata();
                notificationManager.showInformationAlert("handleClearRecycleBinSuccess");
            } else {
                notificationManager.showErrorAlert("handleClearRecycleBinPathError");
            }
        } catch (IOException e) {
            notificationManager.showErrorAlert("handleClearRecycleBinError");
            e.printStackTrace();
        }
    }

    @FXML
    private void returnFromRecycleBin(ActionEvent event) {
        RecoveryContext context = getRecoveryContext(event);
        if (context == null || !preRecoveryChecks(context)) return;

        performRecovery(context);
    }

    private RecoveryContext getRecoveryContext(ActionEvent event) {
        Object source = event.getSource();
        if (source == toggleReturnFiles) {
            return new RecoveryContext(recycleBin, isRestorationPerformed, "fieldClearFirstFolder", source);
        } else if (source == toggleReturnFiles2) {
            return new RecoveryContext(recycleBin2, isRestorationPerformed2, "fieldClearSecondFolder", source);
        } else {
            notificationManager.showErrorAlert("returnFromRecycleBinMethodError");
            return null;
        }
    }

    private boolean preRecoveryChecks(RecoveryContext context) {
        if (context.currentRecycleBin.getRecycleBinPath() == null || !Files.exists(context.currentRecycleBin.getRecycleBinPath())) {
            notificationManager.showErrorAlert("returnFromRecycleBinNull");
            return false;
        }

        if (context.isRestorationPerformedFlag) {
            notificationManager.showInformationAlert("returnFromRecycleBinInfo");
            return false;
        }

        return true;
    }

    private void performRecovery(RecoveryContext context) {
        try {
            deleteFilesBeforeRecovery(context.keyFieldClearFolder);

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(context.currentRecycleBin.getRecycleBinPath())) {
                for (Path fileInRecycleBin : stream) {
                    String fileName = fileInRecycleBin.getFileName().toString();
                    context.currentRecycleBin.restoreFromRecycleBin(fileName);
                }
            }
            context.currentRecycleBin.clearMetadata();
            notificationManager.showInformationAlert("returnFromRecycleBinSuccess");
            if (context.source == toggleReturnFiles) {
                isRestorationPerformed = true;
            } else if (context.source == toggleReturnFiles2) {
                isRestorationPerformed2 = true;
            }
        } catch (IOException e) {
            notificationManager.showErrorAlert("returnFromRecycleBinError");
            e.printStackTrace();
        }
    }

    private void deleteFilesBeforeRecovery(String keyFieldClearFolder) {
        String rootFolderPath = prefs.get(keyFieldClearFolder, "C:\\Default\\Path");

        try {
            FileManager.deleteFolderContents(rootFolderPath, "cache");
            FileManager.deleteFolderContents(rootFolderPath, "log");
            FileManager.deleteFolderContents(rootFolderPath, "tmp");
            FileManager.deleteVarFolderContents(Paths.get(rootFolderPath, "var").toString());

            notificationManager.showInformationAlert("deleteFilesBeforeRecoverySuccess");
        } catch (IOException e) {
            notificationManager.showErrorAlert("deleteFilesBeforeRecoveryError");
            e.printStackTrace();
        }
    }

    private void clearRecycleBinContents(RecycleBin recycleBin) {
        try {
            if (Files.exists(recycleBin.getRecycleBinPath()) && Files.isDirectory(recycleBin.getRecycleBinPath())) {
                try (Stream<Path> paths = Files.walk(recycleBin.getRecycleBinPath())) {
                    paths.filter(p -> !p.equals(recycleBin.getRecycleBinPath()))
                            .sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                }
                recycleBin.clearMetadata();
            }
        } catch (IOException e) {
            notificationManager.showErrorAlert("clearRecycleBinContentsError");
            e.printStackTrace();
        }
    }

    private String getTranslate(String key) {
        return bundle.getString(key);
    }
}