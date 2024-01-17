package com.dev.station.util;

public class FileUtils {
    /**
     * Returns the file extension from the given path.
     * @param filePath The path to the file.
     * @return The file extension, or an empty string if the extension is not found.
     */
    public static String getFileExtension(String filePath) {
        if (filePath != null && filePath.lastIndexOf('.') > 0) {
            return filePath.substring(filePath.lastIndexOf('.') + 1);
        }
        return "";
    }
}
