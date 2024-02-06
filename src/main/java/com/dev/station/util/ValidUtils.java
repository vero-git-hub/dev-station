package com.dev.station.util;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.InvalidPathException;

public class ValidUtils {

    public static boolean isValidDirectoryPath(String path) {
        try {
            return Files.isDirectory(Paths.get(path));
        } catch (InvalidPathException | NullPointerException ex) {
            return false;
        }
    }

    public static boolean isValidRegistryKey(String registryKey) {
        String regex = "^(HKEY_CURRENT_USER|HKEY_LOCAL_MACHINE|HKEY_CLASSES_ROOT|HKEY_USERS|HKEY_CURRENT_CONFIG)\\\\([\\w\\d\\s]+\\\\?)*$";
        return registryKey.matches(regex);
    }

    public static boolean isValidFileName(String fileName) {
        String regex = "^[^<>:\"/\\\\|?*]+\\.\\w+$";
        return fileName.matches(regex);
    }

    public static boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }

    public static boolean isValidPath(String path) {
        try {
            Paths.get(path);
            return true;
        } catch (InvalidPathException | NullPointerException ex) {
            return false;
        }
    }

    /**
     * Checks that the file exists and is not a directory.
     * @param path The file path to check.
     * @return true if the file exists and is not a directory, false otherwise.
     */
    public static boolean doesFileExist(String path) {
        try {
            return Files.exists(Paths.get(path)) && !Files.isDirectory(Paths.get(path));
        } catch (InvalidPathException | NullPointerException ex) {
            return false;
        }
    }

    /**
     * Checks that the file has the .exe extension.
     * @param path The file path to check.
     * @return true if the file has the .exe extension, false otherwise.
     */
    public static boolean hasExecutableExtension(String path) {
        return path.toLowerCase().endsWith(".exe");
    }

    /**
     * Combines file existence and extension checks.
     * @param path The file path to validate.
     * @return true if the file exists, is not a directory, and has the .exe extension.
     */
    public static boolean isValidExecutablePath(String path) {
        return doesFileExist(path) && hasExecutableExtension(path);
    }

}
