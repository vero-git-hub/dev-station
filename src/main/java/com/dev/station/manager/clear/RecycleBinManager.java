package com.dev.station.manager.clear;

import com.dev.station.controller.MainController;
import com.dev.station.controller.sidebar.ClearController;
import com.dev.station.entity.RecoveryContext;
import com.dev.station.entity.RecycleBin;
import com.dev.station.manager.FileManager;
import com.dev.station.manager.NotificationManager;
import javafx.event.ActionEvent;
import javafx.scene.control.ToggleButton;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

public class RecycleBinManager {
    private RecycleBin recycleBin;
    private RecycleBin recycleBin2;
    private final Preferences prefs = MainController.prefs;
    private final ClearController clearController;
    private final NotificationManager notificationManager;
    private ToggleButton toggleReturnFiles;
    private ToggleButton toggleReturnFiles2;

    public RecycleBinManager(ClearController clearController, NotificationManager notificationManager, ToggleButton toggleReturnFiles, ToggleButton toggleReturnFiles2) {
        this.clearController = clearController;
        this.notificationManager = notificationManager;
        this.toggleReturnFiles = toggleReturnFiles;
        this.toggleReturnFiles2 = toggleReturnFiles2;
    }

    public void defineRecycleBins() {
        defineRecycleBin();
        defineRecycleBin2();
    }

    private void defineRecycleBin() {
        String recycleBinPath = prefs.get("firstRecycleBin", "C:\\Default\\RecycleBinPath");
        this.recycleBin = new RecycleBin(recycleBinPath);
    }

    private void defineRecycleBin2() {
        String recycleBinPath = prefs.get("secondRecycleBin", "C:\\Default\\RecycleBinPath");
        this.recycleBin2 = new RecycleBin(recycleBinPath);
    }

    public void moveFilesToRecycleBin(ActionEvent event, ToggleButton toggleMoveFiles, ToggleButton toggleMoveFiles2) {
        Object source = event.getSource();
        String rootFolderPath;
        RecycleBin currentRecycleBin;

        if (source == toggleMoveFiles) {
            rootFolderPath = prefs.get("fieldClearFirstFolder", "C:\\Default\\Path");
            currentRecycleBin = recycleBin;
            moveToCart(source, rootFolderPath, currentRecycleBin);
            clearController.setRestorationPerformed(false);
        } else if (source == toggleMoveFiles2) {
            rootFolderPath = prefs.get("fieldClearSecondFolder", "C:\\Default\\Path");
            currentRecycleBin = recycleBin2;
            moveToCart(source, rootFolderPath, currentRecycleBin);
            clearController.setRestorationPerformed2(false);
        } else {
            notificationManager.showErrorAlert("moveFilesToRecycleBinMethodError");
        }
    }

    public void moveToCart(Object source, String rootFolderPath, RecycleBin recycleBin) {
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

    public void returnFromRecycleBin(ActionEvent event) {
        RecoveryContext context = getRecoveryContext(event);
        if (context == null || !preRecoveryChecks(context)) return;

        performRecovery(context);
    }

    private RecoveryContext getRecoveryContext(ActionEvent event) {
        Object source = event.getSource();
        if (source == toggleReturnFiles) {
            return new RecoveryContext(recycleBin, clearController.isRestorationPerformed(), "fieldClearFirstFolder", source);
        } else if (source == toggleReturnFiles2) {
            return new RecoveryContext(recycleBin2, clearController.isRestorationPerformed2(), "fieldClearSecondFolder", source);
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
                clearController.setRestorationPerformed(true);
            } else if (context.source == toggleReturnFiles2) {
                clearController.setRestorationPerformed2(true);
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

    public void clearRecycleBin(ActionEvent event, ToggleButton toggleClearRecycleBin, ToggleButton toggleClearRecycleBin2) {
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
}