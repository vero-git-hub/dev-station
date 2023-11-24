package com.dev.station.controller.sidebar;

import com.dev.station.controller.MainController;
import com.dev.station.entity.RecycleBin;
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

public class CleanController {
    private final Preferences prefs = MainController.prefs;
    private boolean isRestorationPerformed = false;
    private RecycleBin recycleBin;
    @FXML private ToggleButton toggleVariableFolder;
    @FXML private ToggleButton toggleRecycleBinFolder;
    @FXML private ToggleButton toggleClearRecycleBinFolder;
    @FXML private TabPane tabPane;

    @FXML
    private void initialize() {
        String recycleBinPath = prefs.get("recycleBinFolderPath", "C:\\Default\\RecycleBinPath");
        this.recycleBin = new RecycleBin(recycleBinPath);

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
    private void handleToggleVariableFolder() {
        if (toggleVariableFolder.isSelected()) {
            String rootFolderPath = prefs.get("variableFolderPath", "C:\\Default\\Path");

            clearRecycleBinContents();

            try {
                clearFolderContents(rootFolderPath, "cache");
                clearFolderContents(rootFolderPath, "log");
                clearFolderContents(rootFolderPath, "tmp");
                clearVarFolderContents(Paths.get(rootFolderPath, "var").toString());

                AlertUtils.showInformationAlert("Success", "Successfully cleared contents of all folders.");
                resetRestorationState();
            } catch (IOException e) {
                e.printStackTrace();
                AlertUtils.showErrorAlert("Error Clearing Folders", "Failed to clear contents of folders: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleToggleRecycleBinFolder() {
        if(toggleRecycleBinFolder.isSelected()) {
            if (recycleBin.getRecycleBinPath() == null || !Files.exists(recycleBin.getRecycleBinPath())) {
                AlertUtils.showErrorAlert("Error", "Recycle bin path is not set or does not exist.");
                return;
            }

            if (isRestorationPerformed) {
                AlertUtils.showInformationAlert("Notice", "Files have already been restored.");
                return;
            }

            try {
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
    private void handleToggleClearRecycleBinFolder() {
        if(toggleClearRecycleBinFolder.isSelected()) {
            clearRecycleBin();
        }
    }

    private void clearRecycleBinContents() {
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

    private void clearRecycleBin() {
        if (recycleBin.getRecycleBinPath() == null || !Files.exists(recycleBin.getRecycleBinPath())) {
            AlertUtils.showErrorAlert("Error", "Recycle bin path is not set or does not exist.");
            return;
        }

        String recycleBinPathString = prefs.get("recycleBinFolderPath", "C:\\Default\\RecycleBinPath");
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

    private void resetRestorationState() {
        isRestorationPerformed = false;
    }

    private void clearFolderContents(String rootFolderPath, String folderName) throws IOException {
        Path folderPath = Paths.get(rootFolderPath, folderName);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath)) {
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    clearFolderContents(path.toString(), "");
                    recycleBin.moveToRecycleBin(path);
                } else {
                    recycleBin.moveToRecycleBin(path);
                }
            }
        }
    }

    private void clearVarFolderContents(String varFolderPath) throws IOException {
        Path varPath = Paths.get(varFolderPath);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(varPath)) {
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    if (!path.getFileName().toString().equals("selenium".toLowerCase())) {
                        clearFolderContents(path.toString(), "");
                        recycleBin.moveToRecycleBin(path);
                    }
                } else {
                    recycleBin.moveToRecycleBin(path);
                }
            }
        }
    }
}