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

    public static void deleteFolderContents(String rootFolderPath, String folderName) throws IOException {
        Path folderPath = Paths.get(rootFolderPath, folderName);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath)) {
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    deleteFolderContents(path.toString(), "");
                    Files.delete(path);
                } else {
                    Files.delete(path);
                }
            }
        }
    }

    public static void deleteVarFolderContents(String varFolderPath) throws IOException {
        Path varPath = Paths.get(varFolderPath);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(varPath)) {
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    if (!path.getFileName().toString().equalsIgnoreCase("selenium")) {
                        deleteFolderContents(path.toString(), "");
                        Files.delete(path);
                    }
                } else {
                    Files.delete(path);
                }
            }
        }
    }
}