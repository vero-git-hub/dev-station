package com.dev.station.controller;

import com.dev.station.entity.ProcessHolder;
import com.dev.station.entity.RecycleBin;
import com.dev.station.util.AlertUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

public class ProgramController {
    private Preferences prefs;
    private Process ubuntuProcess;
    private Process phpStormProcess;
    private Process seleniumProcess;
    private boolean isUbuntuRunning = false;
    private boolean isPhpStormRunning = false;
    private boolean isSeleniumRunning = false;
    private boolean isRestorationPerformed = false;
    @FXML
    private ToggleButton toggleUbuntu;
    @FXML
    private ToggleButton togglePhpStorm;
    @FXML
    private ToggleButton toggleSelenium;
    @FXML
    private ToggleButton toggleVariableFolder;
    @FXML
    public ToggleButton toggleRecycleBinFolder;
    @FXML
    public ToggleButton toggleClearRecycleBinFolder;
    private RecycleBin recycleBin;

    public void init(Preferences prefs) {
        this.prefs = prefs;
        String recycleBinPath = prefs.get("recycleBinFolderPath", "C:\\Default\\RecycleBinPath");
        this.recycleBin = new RecycleBin(recycleBinPath);
    }

    @FXML
    public void handleToggleUbuntu(ActionEvent actionEvent) {
        if (toggleUbuntu.isSelected()) {
            launchApplication("ubuntuPath", "C:\\Program Files\\Ubuntu\\ubuntu.exe", new ProcessHolder(ubuntuProcess, isUbuntuRunning));
        } else {
            closeUbuntu();
        }
    }

    @FXML
    private void handleTogglePhpStorm() {
        if (togglePhpStorm.isSelected()) {
            launchApplication("phpStormPath", "C:\\Program Files\\PhpStorm\\phpstorm.exe", new ProcessHolder(phpStormProcess, isPhpStormRunning));
        } else {
            closePhpStorm();
        }
    }

    @FXML
    private void handleToggleSelenium() {
        if (toggleSelenium.isSelected()) {
            boolean launchedExe = launchApplication("seleniumPath", "C:\\Program Files\\Selenium\\selenium.exe", new ProcessHolder(seleniumProcess, isSeleniumRunning));

            if (launchedExe) {
                launchJarApplication("seleniumJARPath", "C:\\Program Files\\Selenium\\selenium.jar", new ProcessHolder(seleniumProcess, isSeleniumRunning));
            }
        } else {
            closeSelenium();
        }
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

    private boolean launchApplication(String pathKey, String defaultPath, ProcessHolder processHolder) {
        try {
            if (!processHolder.isRunning) {
                String path = prefs.get(pathKey, defaultPath);
                processHolder.process = new ProcessBuilder(path).start();
                processHolder.isRunning = true;
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Failed to start", "The specified file cannot be found.\n Check the file path in the Settings tab.");
        }
        return false;
    }

    private void launchJarApplication(String pathKey, String defaultPath, ProcessHolder processHolder) {
        try {
            if (!processHolder.isRunning) {
                String jarPath = prefs.get(pathKey, defaultPath);
                ProcessBuilder pb = new ProcessBuilder("java", "-jar", jarPath);
                processHolder.process = pb.start();

                boolean isFinished = processHolder.process.waitFor(2, TimeUnit.SECONDS);
                if (isFinished && processHolder.process.exitValue() != 0) {
                    throw new IOException("Error running jar: process terminated with exit code " + processHolder.process.exitValue());
                }
                processHolder.isRunning = true;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Failed to start", "The specified JAR file cannot be found or failed to start.\nCheck the file path in the Settings tab.");
        }
    }

    public void closeUbuntu() {}

    public void closePhpStorm() {
        // close PhpStorm
    }

    private void closeSelenium() {}
}