package com.dev.station.controller.monitoring;

import com.dev.station.service.FileChangeListener;
import com.dev.station.service.FileContentProvider;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FileMonitorAppColor extends Application implements FileChangeListener {
    private String file1Path;
    private String file2Path;
    private TextFlow file1Content;
    private TextFlow lineNumberFlow;
    private int checkInterval;
    private FileTime lastModifiedTime;
    private Stage stage;
    private String initialContent;
    private Timer timer;
    private Timer buttonTimer;
    private String tabId;
    private boolean clearContentToggle;
    private Button refreshButton;
    private int remainingTime;
    private String windowTitle;
    private final String refreshButtonText = "Update";
    private final String refreshButtonSecondText = "s";

    public FileMonitorAppColor() {}

    public void setInitialContent(String content) {
        this.initialContent = content;
    }

    public void setFile1Path(String file1Path) {
        this.file1Path = file1Path;
    }

    public void setFile2Path(String file2Path) {
        this.file2Path = file2Path;
    }

    public void setCheckInterval(int checkInterval) {
        this.checkInterval = checkInterval * 1000;
    }

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public void setClearContentToggle(boolean clearContentToggle) {
        this.clearContentToggle = clearContentToggle;
    }

    public void setWindowTitle(String windowTitle) {
        this.windowTitle = windowTitle;
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        file1Content = new TextFlow();
        lineNumberFlow = new TextFlow();
        refreshButton = new Button(refreshButtonText);

        refreshButton.setOnAction(event -> {
            try {
                displayFileContent(file1Path);
                remainingTime = checkInterval / 1000;  // Reset the timer
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        if (initialContent != null) {
            Platform.runLater(() -> {
                file1Content.getChildren().clear();
                file1Content.getChildren().add(new Text(initialContent));
                updateLineNumbers();
            });
        }

        ScrollPane scrollPane = new ScrollPane(file1Content);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        ScrollPane lineNumberScrollPane = new ScrollPane(lineNumberFlow);
        lineNumberScrollPane.setFitToWidth(true);
        lineNumberScrollPane.setFitToHeight(true);
        lineNumberScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        scrollPane.vvalueProperty().bindBidirectional(lineNumberScrollPane.vvalueProperty());

        HBox contentBox = new HBox(lineNumberScrollPane, scrollPane);
        VBox root = new VBox(10, refreshButton, contentBox);
        Scene scene = new Scene(root, 800, 600);

        stage.setScene(scene);
        stage.setTitle(windowTitle != null ? windowTitle : "File Monitor");
        stage.show();
        startMonitoring();
        startButtonTimer();
    }

    public void getCurrentContent(ContentConsumer consumer) {
        Platform.runLater(() -> {
            StringBuilder currentContent = new StringBuilder();
            for (var node : file1Content.getChildren()) {
                if (node instanceof Text) {
                    currentContent.append(((Text) node).getText());
                }
            }
            consumer.accept(currentContent.toString());
        });
    }

    private void startMonitoring() {
        try {
            displayFileContent(file1Path);
            copyFileContent(file1Path, file2Path);
            if (clearContentToggle) {
                clearFileContent(file1Path);
            }
            updateLastModifiedTime(file1Path);

            timer = new Timer(true);
            // start monitoring at a fixed interval
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        checkFileChanges();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, checkInterval, checkInterval);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startButtonTimer() {
        buttonTimer = new Timer(true);
        buttonTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    remainingTime--;
                    if (remainingTime <= 0) {
                        remainingTime = checkInterval / 1000;
                    }
                    refreshButton.setText(refreshButtonText + "(" + remainingTime + refreshButtonSecondText + ")");
                });
            }
        }, 1000, 1000);
    }

    private void displayFileContent(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        Platform.runLater(() -> {
            file1Content.getChildren().clear();
            lines.forEach(line -> file1Content.getChildren().add(new Text(line + "\n")));
            updateLineNumbers();
        });
    }

    private void updateLineNumbers() {
        Platform.runLater(() -> {
            lineNumberFlow.getChildren().clear();
            int lineNumber = 1;
            for (var node : file1Content.getChildren()) {
                if (node instanceof Text) {
                    lineNumberFlow.getChildren().add(new Text(lineNumber + "\n"));
                    lineNumber++;
                }
            }
        });
    }

    private void copyFileContent(String sourcePath, String destPath) throws IOException {
        Files.copy(Paths.get(sourcePath), Paths.get(destPath), StandardCopyOption.REPLACE_EXISTING);
    }

    private void clearFileContent(String filePath) throws IOException {
        Files.write(Paths.get(filePath), new byte[0]);
    }

    private void updateLastModifiedTime(String filePath) throws IOException {
        lastModifiedTime = Files.getLastModifiedTime(Paths.get(filePath));
    }

    private void checkFileChanges() throws IOException {
        FileTime currentModifiedTime = Files.getLastModifiedTime(Paths.get(file1Path));
//        System.out.println("Current Monitoring Time: " + System.currentTimeMillis());
//        System.out.println("Last Modified Time: " + currentModifiedTime.toMillis());
        if (!currentModifiedTime.equals(lastModifiedTime)) {
            highlightChanges();
            copyFileContent(file1Path, file2Path);
            if (clearContentToggle) {
                clearFileContent(file1Path);
            }
            updateLastModifiedTime(file1Path);
        }
    }

    private void highlightChanges() throws IOException {
        List<String> file1Lines = Files.readAllLines(Paths.get(file1Path));
        List<String> file2Lines = Files.readAllLines(Paths.get(file2Path));

        Platform.runLater(() -> {
            file1Content.getChildren().clear();
            for (int i = 0; i < file1Lines.size(); i++) {
                Text text = new Text(file1Lines.get(i) + "\n");
                if (i >= file2Lines.size() || !file1Lines.get(i).equals(file2Lines.get(i))) {
                    text.setStyle("-fx-fill: red;");
                }
                file1Content.getChildren().add(text);
                updateLineNumbers();
            }
        });
    }

    public void stopMonitoring() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (buttonTimer != null) {
            buttonTimer.cancel();
            buttonTimer = null;
        }
        deleteFile(file2Path);
    }

    private void deleteFile(String filePath) {
        try {
            Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFileChange(FileContentProvider contentProvider) {
        Platform.runLater(() -> {
            try {
                String content = contentProvider.getContent();
                displayFileContent(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
