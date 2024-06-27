package com.dev.station.controller.monitoring;

import com.dev.station.service.FileChangeListener;
import com.dev.station.service.FileContentProvider;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
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
    private static final String FILE_2_PATH = "file4.txt";
    private static final int CHECK_INTERVAL = 30000; // 30 seconds

    private TextFlow file1Content;
    private FileTime lastModifiedTime;
    private Stage stage;
    private String initialContent;
    private Timer timer;

    public FileMonitorAppColor() {}

    public void setInitialContent(String content) {
        this.initialContent = content;
    }

    public void setFile1Path(String file1Path) {
        this.file1Path = file1Path;
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        file1Content = new TextFlow();

        if (initialContent != null) {
            Platform.runLater(() -> {
                file1Content.getChildren().clear();
                file1Content.getChildren().add(new Text(initialContent));
            });
        }

        VBox root = new VBox(10, file1Content);
        Scene scene = new Scene(root, 600, 400);

        stage.setScene(scene);
        stage.setTitle("File Monitor");
        stage.show();
        startMonitoring();
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
            copyFileContent(file1Path, FILE_2_PATH);
            clearFileContent(file1Path);
            updateLastModifiedTime(file1Path);

            timer = new Timer(true);
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        checkFileChanges();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, CHECK_INTERVAL, CHECK_INTERVAL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayFileContent(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        Platform.runLater(() -> {
            file1Content.getChildren().clear();
            lines.forEach(line -> file1Content.getChildren().add(new Text(line + "\n")));
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
        if (!currentModifiedTime.equals(lastModifiedTime)) {
            highlightChanges();
            copyFileContent(file1Path, FILE_2_PATH);
            clearFileContent(file1Path);
            updateLastModifiedTime(file1Path);
        }
    }

    private void highlightChanges() throws IOException {
        List<String> file1Lines = Files.readAllLines(Paths.get(file1Path));
        List<String> file2Lines = Files.readAllLines(Paths.get(FILE_2_PATH));

        Platform.runLater(() -> {
            file1Content.getChildren().clear();
            for (int i = 0; i < file1Lines.size(); i++) {
                Text text = new Text(file1Lines.get(i) + "\n");
                if (i >= file2Lines.size() || !file1Lines.get(i).equals(file2Lines.get(i))) {
                    text.setStyle("-fx-fill: red;");
                }
                file1Content.getChildren().add(text);
            }
        });
    }

    public void stopMonitoring() {
        if (timer != null) {
            timer.cancel();
            timer = null;
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
