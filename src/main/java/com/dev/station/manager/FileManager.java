package com.dev.station.manager;

import com.dev.station.entity.RecycleBin;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager {

    public static void clearFolderContents(String rootFolderPath, String folderName, RecycleBin recycleBin) throws IOException {
        Path folderPath = Paths.get(rootFolderPath, folderName);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath)) {
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    clearFolderContents(path.toString(), "", recycleBin);
                    recycleBin.moveToRecycleBin(path);
                } else {
                    recycleBin.moveToRecycleBin(path);
                }
            }
        }
    }

    public static void clearVarFolderContents(String varFolderPath, RecycleBin recycleBin) throws IOException {
        Path varPath = Paths.get(varFolderPath);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(varPath)) {
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    if (!path.getFileName().toString().equals("selenium".toLowerCase())) {
                        clearFolderContents(path.toString(), "", recycleBin);
                        recycleBin.moveToRecycleBin(path);
                    }
                } else {
                    recycleBin.moveToRecycleBin(path);
                }
            }
        }
    }

    public static void deleteFolderContents(String rootFolderPath, String folderName) throws IOException {
        System.out.println("delete!");
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