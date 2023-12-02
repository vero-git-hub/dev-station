package com.dev.station.manager.clear;

import com.dev.station.controller.MainController;
import com.dev.station.controller.tab.TabController;
import com.dev.station.file.PathData;
import com.dev.station.entity.RecoveryContext;
import com.dev.station.entity.RecycleBin;
import com.dev.station.file.JsonTabsManager;
import com.dev.station.file.TabData;
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
import java.util.*;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

public class RecycleBinManager {
    private RecycleBin recycleBin;
    private final Preferences prefs = MainController.prefs;
    private final TabController tabController;
    private final NotificationManager notificationManager;
    private ToggleButton toggleReturnFiles;

    public RecycleBinManager(TabController tabController, NotificationManager notificationManager, ToggleButton toggleReturnFiles) {
        this.tabController = tabController;
        this.notificationManager = notificationManager;
        this.toggleReturnFiles = toggleReturnFiles;
    }

    public void moveFilesToRecycleBin(ActionEvent event, ToggleButton toggleMoveFiles, String tabId) {
        Object source = event.getSource();
        if (source == toggleMoveFiles) {
            JsonTabsManager jsonTabsManager = new JsonTabsManager();
            List<TabData> tabs = jsonTabsManager.loadTabs();
            TabData currentTabData = null;

            for (TabData tab : tabs) {
                if (tab.getId().equals(tabId)) {
                    currentTabData = tab;
                    break;
                }
            }

            if (currentTabData == null) {
                notificationManager.showErrorAlert("moveFilesToRecycleBinMethodError");
                return;
            }

            RecycleBin currentRecycleBin = new RecycleBin(currentTabData.getRecycleBinPath());
            clearRecycleBinContents(currentRecycleBin);
            for (PathData pathData : currentTabData.getPaths()) {
                moveToCart(pathData, currentRecycleBin);
            }
        } else {
            notificationManager.showErrorAlert("moveFilesToRecycleBinMethodError");
        }
    }

    private void moveToCart(PathData pathData, RecycleBin recycleBin) {
        try {
            Set<String> exclusions = new HashSet<>(pathData.getExclusions());
            FileManager.clearFolderContents(pathData.getPath(), recycleBin, exclusions);
            notificationManager.showInformationAlert("moveFilesToRecycleBinSuccess");
        } catch (IOException e) {
            notificationManager.showErrorAlert("moveFilesToRecycleBinError");
            e.printStackTrace();
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
            return new RecoveryContext(recycleBin, tabController.isRestorationPerformed(), "fieldClearFirstFolder", source);
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
                tabController.setRestorationPerformed(true);
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

    public void clearRecycleBin(String tabId) {
        JsonTabsManager jsonTabsManager = new JsonTabsManager();
        List<TabData> tabs = jsonTabsManager.loadTabs();
        String recycleBinPathString = null;

        for (TabData tab : tabs) {
            if (tab.getId().equals(tabId)) {
                recycleBinPathString = tab.getRecycleBinPath();
                break;
            }
        }

        if (recycleBinPathString == null || recycleBinPathString.isEmpty()) {
            notificationManager.showErrorAlert("handleClearRecycleBinNotExist");
            return;
        }

        Path recycleBinPath = Paths.get(recycleBinPathString);

        try {
            if (Files.exists(recycleBinPath) && Files.isDirectory(recycleBinPath)) {
                try (Stream<Path> paths = Files.walk(recycleBinPath)) {
                    paths.filter(p -> !p.equals(recycleBinPath))
                            .sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                }
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