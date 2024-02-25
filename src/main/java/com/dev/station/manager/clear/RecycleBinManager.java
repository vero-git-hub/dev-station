package com.dev.station.manager.clear;

import com.dev.station.controller.tab.TabController;
import com.dev.station.entity.RecoveryContext;
import com.dev.station.entity.RecycleBin;
import com.dev.station.file.JsonTabsManager;
import com.dev.station.file.PathData;
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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class RecycleBinManager {
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
            List<TabData> tabs = jsonTabsManager.loadTabs(1, "Clear");
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
            notificationManager.showInformationAlert("moveFilesToRecycleBinSuccess");
        } else {
            notificationManager.showErrorAlert("moveFilesToRecycleBinMethodError");
        }
    }

    private void moveToCart(PathData pathData, RecycleBin recycleBin) {
        try {
            Set<String> exclusions = new HashSet<>(pathData.getExclusions());
            FileManager.clearFolderContents(pathData.getPath(), recycleBin, exclusions);

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
            JsonTabsManager jsonTabsManager = new JsonTabsManager();
            List<TabData> tabs = jsonTabsManager.loadTabs(1, "Clear");
            String currentTabId = tabController.getMyTab().getId();

            TabData currentTab = tabs.stream().filter(tab -> tab.getId().equals(currentTabId)).findFirst().orElse(null);

            if (currentTab != null) {
                String recycleBinPath = currentTab.getRecycleBinPath();
                RecycleBin recycleBin = new RecycleBin(recycleBinPath);
                return new RecoveryContext(recycleBin, tabController.isRestorationPerformed(), currentTabId, source);
            }
        }
        notificationManager.showErrorAlert("returnFromRecycleBinMethodError");
        return null;
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

    private void deleteFilesBeforeRecovery(String tabId) {
        JsonTabsManager jsonTabsManager = new JsonTabsManager();
        List<TabData> tabs = jsonTabsManager.loadTabs(1, "Clear");

        TabData currentTab = tabs.stream()
                .filter(tab -> tab.getId().equals(tabId))
                .findFirst()
                .orElse(null);

        if (currentTab != null) {
            for (PathData pathData : currentTab.getPaths()) {
                try {
                    String path = pathData.getPath();
                    if (Files.exists(Paths.get(path)) && Files.isDirectory(Paths.get(path))) {
                        FileManager.deleteFolderContents(path);
                    }
                } catch (IOException e) {
                    notificationManager.showErrorAlert("deleteFilesBeforeRecoveryError: " + pathData.getPath());
                    e.printStackTrace();
                }
            }
            notificationManager.showInformationAlert("deleteFilesBeforeRecoverySuccess");
        } else {
            notificationManager.showErrorAlert("Tab not found");
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
        List<TabData> tabs = jsonTabsManager.loadTabs(1, "Clear");
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