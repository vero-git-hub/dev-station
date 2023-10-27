package com.example.devstation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class UserPreferenceManager {

    private static final String DEFAULT_TAB_PROPERTY = "defaultTab";
    private static final String PREFERENCES_FILE = "userPreferences.properties";

    public void saveUserPreference(String tabName) {
        Properties properties = new Properties();
        properties.setProperty(DEFAULT_TAB_PROPERTY, tabName);
        try (FileOutputStream out = new FileOutputStream(PREFERENCES_FILE)) {
            properties.store(out, "User Preferences");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String loadUserPreference() {
        Properties properties = new Properties();
        try (FileInputStream in = new FileInputStream(PREFERENCES_FILE)) {
            properties.load(in);
            return properties.getProperty(DEFAULT_TAB_PROPERTY);
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
