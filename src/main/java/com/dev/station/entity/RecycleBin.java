package com.dev.station.entity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class RecycleBin {
    private Path recycleBinPath;
    private Path metadataPath;

    public RecycleBin(String recycleBinPath) {
        this.recycleBinPath = Paths.get(recycleBinPath);
        this.metadataPath = this.recycleBinPath.resolve("metadata.txt");

        try {
            if (!Files.exists(metadataPath)) {
                Files.createFile(metadataPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Path getRecycleBinPath() {
        return recycleBinPath;
    }

    public void moveToRecycleBin(Path file) throws IOException {
        String uniqueFileName = createUniqueFileName(file.getFileName().toString());
        Path target = recycleBinPath.resolve(uniqueFileName);
        Files.move(file, target, StandardCopyOption.REPLACE_EXISTING);

        try (BufferedWriter writer = Files.newBufferedWriter(metadataPath, StandardOpenOption.APPEND)) {
            writer.write(uniqueFileName + " -> " + file.toString());
            writer.newLine();
        }
    }

    private String createUniqueFileName(String originalName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        int randomNum = new Random().nextInt(10000);
        return originalName + "_" + timestamp + "_" + randomNum;
    }

    public void restoreFromRecycleBin(String fileName) throws IOException {
        Path fileInRecycleBin = recycleBinPath.resolve(fileName);
        Path originalPath = getOriginalPath(fileName);

        if (originalPath != null) {
            Files.move(fileInRecycleBin, originalPath, StandardCopyOption.REPLACE_EXISTING);
            removeMetadataEntry(fileName);
        } else {
            System.out.println("The source path for file " + fileName + " was not found.");
        }
    }

    private void removeMetadataEntry(String fileName) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(metadataPath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith(fileName + " -> ")) {
                    lines.add(line);
                }
            }
        }
        try (BufferedWriter writer = Files.newBufferedWriter(metadataPath, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    private Path getOriginalPath(String uniqueFileName) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(metadataPath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" -> ");
                if (parts.length == 2 && parts[0].equals(uniqueFileName)) {
                    return Paths.get(parts[1]);
                }
            }
        }
        return null;
    }

    public void clearMetadata() throws IOException {
        if (Files.exists(metadataPath)) {
            Files.delete(metadataPath);
            Files.createFile(metadataPath);
        }
    }
}