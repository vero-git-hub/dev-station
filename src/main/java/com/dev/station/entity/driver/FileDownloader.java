package com.dev.station.entity.driver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileDownloader {
    public static String downloadFile(String fileURL, String saveDir) throws IOException {
        URL url = new URL(fileURL);
        Path targetPath = Paths.get(saveDir).resolve(Paths.get(url.getPath()).getFileName().toString());

        try (InputStream in = url.openStream()) {
            Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        return targetPath.toString();
    }
}