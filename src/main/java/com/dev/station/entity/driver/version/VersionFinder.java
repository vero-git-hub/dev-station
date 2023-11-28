package com.dev.station.entity.driver.version;

import com.dev.station.manager.NotificationManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.prefs.Preferences;

public class VersionFinder {
    private final NotificationManager notificationManager;

    public VersionFinder(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public String getVersion(Preferences prefs) throws IOException, InterruptedException {
        String registryKey = prefs.get("registryKey", "");

        Process process = Runtime.getRuntime().exec(new String[]{"cmd", "/c", "reg query \"" + registryKey + "\" /v version"});
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("version")) {
                return line.split("\\s+")[line.split("\\s+").length - 1];
            }
        }
        reader.close();

        notificationManager.showErrorAlert("getVersionError");
        return "";
    }
}