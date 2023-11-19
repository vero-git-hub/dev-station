package com.dev.station.entity;

import java.nio.file.*;
import java.io.IOException;

public class RecycleBin {
    private Path recycleBinPath;

    public RecycleBin(String recycleBinPath) {
        this.recycleBinPath = Paths.get(recycleBinPath);
    }

    public void moveToRecycleBin(Path file) throws IOException {
        Path target = recycleBinPath.resolve(file.getFileName());
        Files.move(file, target, StandardCopyOption.REPLACE_EXISTING);
    }

    public void restoreFromRecycleBin(String fileName, Path restorePath) throws IOException {
        Path fileInRecycleBin = recycleBinPath.resolve(fileName);
        Files.move(fileInRecycleBin, restorePath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
    }
}