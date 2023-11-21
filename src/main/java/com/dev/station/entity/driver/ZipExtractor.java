package com.dev.station.entity.driver;

import java.io.*;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipExtractor {

    public static void extractDriver(String zipFilePath, String outputDir, String fileNameToExtract) throws IOException {
        Path zipPath = Paths.get(zipFilePath);
        Path outputPath = Paths.get(outputDir);

        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipPath))) {
            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {
                if (!zipEntry.isDirectory() && zipEntry.getName().endsWith(fileNameToExtract)) {
                    Path newFilePath = outputPath.resolve(Paths.get(zipEntry.getName()).getFileName());
                    extractFile(zis, newFilePath);
                    break;
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        }

        Files.delete(zipPath);
    }

    private static void extractFile(ZipInputStream zipIn, Path filePath) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath.toFile()))) {
            byte[] bytesIn = new byte[4096];
            int read;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
    }
}