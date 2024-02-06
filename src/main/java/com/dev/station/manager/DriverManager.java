package com.dev.station.manager;

import com.dev.station.entity.WebParser;
import com.dev.station.entity.driver.version.VersionExtractor;
import com.dev.station.entity.driver.version.VersionFinder;
import com.dev.station.util.AlertUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.prefs.Preferences;

public class DriverManager {
    private final NotificationManager notificationManager;

    public DriverManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public String getWebsiteVersion(String url) {
        return new WebParser().parseWebsiteForVersion(url, notificationManager);
    }

    public String getCurrentVersion(String pathToChromeDriver) {
        String version = getChromeDriverVersion(pathToChromeDriver);
        version = VersionExtractor.extractVersionNumber(version);

        return version;
    }

    /**
     * Starting the process with the command to get the chromedriver version
     * Reading command output from a process's output stream
     * (the version is expected to be on the first line of output)
     * @param pathToChromeDriver
     * @return
     */
    public static String getChromeDriverVersion(String pathToChromeDriver) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(pathToChromeDriver, "--version");
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String versionOutput = reader.readLine();

            int exitCode = process.waitFor();
            if (exitCode == 0 && versionOutput != null) {
                return versionOutput;
            } else {
                AlertUtils.showErrorAlert("Error version", "Error getting ChromeDriver version");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return "Could not determine ChromeDriver version";
    }

}
