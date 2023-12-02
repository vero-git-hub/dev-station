package com.dev.station.manager;

import com.dev.station.entity.RecycleBin;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class FileManager {

    public static void clearFolderContents(String directoryPath, RecycleBin recycleBin, Set<String> exclusions) throws IOException {
        Path dirPath = Paths.get(directoryPath);

        if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
            return;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
            for (Path path : stream) {
                if (exclusions.contains(path.getFileName().toString())) {
                    continue;
                }

                if (Files.isDirectory(path)) {
                    clearFolderContents(path.toString(), recycleBin, exclusions);
                }

                recycleBin.moveToRecycleBin(path);
            }
        }
    }

    public static void deleteFolderContents(String folderPathString) throws IOException {
        Path folderPath = Paths.get(folderPathString);
        if (Files.exists(folderPath) && Files.isDirectory(folderPath)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath)) {
                for (Path path : stream) {
                    if (Files.isDirectory(path)) {
                        deleteFolderContents(path.toString());
                        Files.delete(path);
                    } else {
                        Files.delete(path);
                    }
                }
            }
        }
    }
}