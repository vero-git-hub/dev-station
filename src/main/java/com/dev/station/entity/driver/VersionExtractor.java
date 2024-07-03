package com.dev.station.entity.driver;

import com.dev.station.util.alert.AlertUtils;
import com.dev.station.util.alert.HeaderAlertUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionExtractor {

    /**
     * Extract version number from the "ChromeDriver [version_number] (1234)" format
     * @param versionOutput
     * @return
     */
    public static String extractVersionNumber(String versionOutput) {
        String versionPattern = "\\d+\\.\\d+\\.\\d+\\.\\d+";
        Pattern pattern = Pattern.compile(versionPattern);
        Matcher matcher = pattern.matcher(versionOutput);

        if (matcher.find()) {
            return matcher.group();
        } else {
            HeaderAlertUtils.showErrorAlert("Error extract version", "Version number not found.");
            return "";
        }
    }

}