package com.dev.station.entity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ChromeVersionFinder {
    public String getChromeVersion() throws IOException, InterruptedException {
        String registryKey = "HKEY_CURRENT_USER\\Software\\Google\\Chrome\\BLBeacon";

        Process process = Runtime.getRuntime().exec(new String[]{"cmd", "/c", "reg query \"" + registryKey + "\" /v version"});
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("version")) {
                return line.split("\\s+")[line.split("\\s+").length - 1];
            }
        }
        reader.close();

        return "Could not determine Chrome version";
    }

    public static void main(String[] args) {
        ChromeVersionFinder finder = new ChromeVersionFinder();
        try {
            String chromeVersion = finder.getChromeVersion();
            System.out.println("Chrome Version: " + chromeVersion);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}