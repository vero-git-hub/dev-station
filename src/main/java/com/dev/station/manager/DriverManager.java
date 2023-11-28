package com.dev.station.manager;

import com.dev.station.entity.WebParser;
import com.dev.station.entity.driver.version.VersionFinder;

import java.io.IOException;
import java.util.prefs.Preferences;

public class DriverManager {
    private final NotificationManager notificationManager;

    public DriverManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public String getWebsiteVersion(Preferences prefs) {
        return new WebParser().parseWebsiteForVersion(prefs, notificationManager);
    }

    public String getCurrentVersion(Preferences prefs) {
        VersionFinder finder = new VersionFinder(notificationManager);
        String version = null;
        try {
            version = finder.getVersion(prefs);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            notificationManager.showErrorAlert("getCurrentVersionError");
        }
        return version;
    }
}
