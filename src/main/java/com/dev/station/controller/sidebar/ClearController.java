package com.dev.station.controller.sidebar;

import com.dev.station.controller.MainController;
import com.dev.station.entity.RecycleBin;
import com.dev.station.manager.FileManager;
import com.dev.station.util.AlertUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Optional;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

public class ClearController {
    private final Preferences prefs = MainController.prefs;
    private boolean isRestorationPerformed = false;
    private boolean isRestorationPerformed2 = false;
    private RecycleBin recycleBin;
    private RecycleBin recycleBin2;
    @FXML private ToggleButton toggleMoveFiles;
    @FXML private ToggleButton toggleReturnFiles;
    @FXML private ToggleButton toggleClearRecycleBin;
    @FXML private ToggleButton toggleMoveFiles2;
    @FXML private ToggleButton toggleReturnFiles2;
    @FXML private ToggleButton toggleClearRecycleBin2;
    @FXML private TabPane tabPane;

    @FXML
    private void initialize() {
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
        MenuItem renameItem = new MenuItem("Rename");
        MenuItem setDefaultItem = new MenuItem("Set as Default");
        contextMenu.getItems().addAll(renameItem, setDefaultItem);

        renameItem.setOnAction(e -> handleRenameTab(tab));
        setDefaultItem.setOnAction(e -> handleSetDefaultTab(tab));

        tab.setContextMenu(contextMenu);
    }

    private void handleRenameTab(Tab tab) {
        TextInputDialog dialog = new TextInputDialog(tab.getText());
        dialog.setTitle("Renaming a tab");
        dialog.setHeaderText("Enter a new name for the tab:");
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
    private void moveFilesToRecycleBin() {
        if (toggleMoveFiles.isSelected()) {
            String rootFolderPath = prefs.get("fieldClearFirstFolder", "C:\\Default\\Path");

            clearRecycleBinContents(recycleBin);

            try {
                FileManager.clearFolderContents(rootFolderPath, "cache", recycleBin);
                FileManager.clearFolderContents(rootFolderPath, "log", recycleBin);
                FileManager.clearFolderContents(rootFolderPath, "tmp", recycleBin);
                FileManager.clearVarFolderContents(Paths.get(rootFolderPath, "var").toString(), recycleBin);

                AlertUtils.showInformationAlert("Success", "Successfully cleared contents of all folders.");
                isRestorationPerformed = false;
            } catch (IOException e) {
                e.printStackTrace();
                AlertUtils.showErrorAlert("Error Clearing Folders", "Failed to clear contents of folders: " + e.getMessage());
            }
        }
    }

    @FXML
    private void returnFromRecycleBin() {
        if(toggleReturnFiles.isSelected()) {
            if (recycleBin.getRecycleBinPath() == null || !Files.exists(recycleBin.getRecycleBinPath())) {
                AlertUtils.showErrorAlert("Error", "Recycle bin path is not set or does not exist.");
                return;
            }

            if (isRestorationPerformed) {
                AlertUtils.showInformationAlert("Notice", "Files have already been restored.");
                return;
            }

            try {
                deleteFilesBeforeRecovery("fieldClearFirstFolder");

                try (DirectoryStream<Path> stream = Files.newDirectoryStream(recycleBin.getRecycleBinPath())) {
                    for (Path fileInRecycleBin : stream) {
                        String fileName = fileInRecycleBin.getFileName().toString();
                        recycleBin.restoreFromRecycleBin(fileName);
                    }
                }
                recycleBin.clearMetadata();
                AlertUtils.showInformationAlert("Success", "All files have been restored from the recycle bin.");
                isRestorationPerformed = true;
            } catch (IOException e) {
                e.printStackTrace();
                AlertUtils.showErrorAlert("Error Restoring Files", "Failed to restore files: " + e.getMessage());
            }
        }
    }

    @FXML
    private void clearRecycleBin() {
        if(toggleClearRecycleBin.isSelected()) {
            handleClearRecycleBin(recycleBin, "firstRecycleBin");
        }
    }

    @FXML
    private void moveFilesToRecycleBin2() {
        if (toggleMoveFiles2.isSelected()) {
            String rootFolderPath = prefs.get("fieldClearSecondFolder", "C:\\Default\\Path");

            clearRecycleBinContents(recycleBin2);

            try {
                FileManager.clearFolderContents(rootFolderPath, "cache", recycleBin2);
                FileManager.clearFolderContents(rootFolderPath, "log", recycleBin2);
                FileManager.clearFolderContents(rootFolderPath, "tmp", recycleBin2);
                FileManager.clearVarFolderContents(Paths.get(rootFolderPath, "var").toString(), recycleBin2);

                AlertUtils.showInformationAlert("Success", "Successfully cleared contents of all folders.");
                isRestorationPerformed2 = false;
            } catch (IOException e) {
                e.printStackTrace();
                AlertUtils.showErrorAlert("Error Clearing Folders", "Failed to clear contents of folders: " + e.getMessage());
            }
        }
    }

    @FXML
    private void returnFromRecycleBin2() {
        if(toggleReturnFiles2.isSelected()) {
            if (recycleBin2.getRecycleBinPath() == null || !Files.exists(recycleBin2.getRecycleBinPath())) {
                AlertUtils.showErrorAlert("Error", "Recycle bin path is not set or does not exist.");
                return;
            }

            if (isRestorationPerformed2) {
                AlertUtils.showInformationAlert("Notice", "Files have already been restored.");
                return;
            }

            try {
                deleteFilesBeforeRecovery("fieldClearSecondFolder");

                try (DirectoryStream<Path> stream = Files.newDirectoryStream(recycleBin2.getRecycleBinPath())) {
                    for (Path fileInRecycleBin : stream) {
                        String fileName = fileInRecycleBin.getFileName().toString();
                        recycleBin2.restoreFromRecycleBin(fileName);
                    }
                }
                recycleBin2.clearMetadata();
                AlertUtils.showInformationAlert("Success", "All files have been restored from the recycle bin.");
                isRestorationPerformed2 = true;
            } catch (IOException e) {
                e.printStackTrace();
                AlertUtils.showErrorAlert("Error Restoring Files", "Failed to restore files: " + e.getMessage());
            }
        }
    }

    private void deleteFilesBeforeRecovery(String keyFieldClearFolder) {
        String rootFolderPath = prefs.get(keyFieldClearFolder, "C:\\Default\\Path");

        try {
            FileManager.deleteFolderContents(rootFolderPath, "cache");
            FileManager.deleteFolderContents(rootFolderPath, "log");
            FileManager.deleteFolderContents(rootFolderPath, "tmp");
            FileManager.deleteVarFolderContents(Paths.get(rootFolderPath, "var").toString());

            AlertUtils.showInformationAlert("Success", "Successfully cleared contents of all folders.");
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Error Clearing Folders", "Failed to clear contents of folders: " + e.getMessage());
        }
    }

    @FXML
    private void clearRecycleBin2() {
        if(toggleClearRecycleBin2.isSelected()) {
            handleClearRecycleBin(recycleBin2, "secondRecycleBin");
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
            e.printStackTrace();
            AlertUtils.showErrorAlert("Error Clearing Recycle Bin", "Failed to clear the recycle bin: " + e.getMessage());
        }
    }

    private void handleClearRecycleBin(RecycleBin recycleBin, String keyRecycleBin) {
        if (recycleBin.getRecycleBinPath() == null || !Files.exists(recycleBin.getRecycleBinPath())) {
            AlertUtils.showErrorAlert("Error", "Recycle bin path is not set or does not exist.");
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
                AlertUtils.showInformationAlert("Success", "Recycle bin cleared successfully.");
            } else {
                AlertUtils.showErrorAlert("Error", "Recycle bin path does not exist.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Error Clearing Recycle Bin", "Failed to clear recycle bin: " + e.getMessage());
        }
    }
}