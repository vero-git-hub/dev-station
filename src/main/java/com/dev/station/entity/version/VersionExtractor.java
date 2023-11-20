package com.dev.station.entity.version;

public class VersionExtractor {
    public static String extractVersion(String version) {
        String[] parts = version.split("\\.");
        if (parts.length < 3) {
            return version;
        }

        return parts[0] + "." + parts[1] + "." + parts[2];
    }
}