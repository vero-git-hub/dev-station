package com.dev.station.manager;

import com.dev.station.entity.WebParser;
import com.dev.station.entity.driver.version.VersionFinder;
import com.dev.station.util.AlertUtils;

import java.io.IOException;
import java.util.prefs.Preferences;

public class DriverManager {
    public static String getWebsiteVersion(Preferences prefs) {
        return new WebParser().parseWebsiteForVersion(prefs);
    }

    public static String getCurrentVersion(Preferences prefs) {
        VersionFinder finder = new VersionFinder();
        String version = null;
        try {
            version = finder.getVersion(prefs);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Failed to get driver version", "Check the registry and the method for getting the version.");
        }
        return version;
    }
}
