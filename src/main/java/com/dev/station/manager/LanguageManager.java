package com.dev.station.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {
    private static ResourceBundle resourceBundle;
    private static List<Runnable> updateActions = new ArrayList<>();

    public static void switchLanguage(Locale locale) {
        resourceBundle = ResourceBundle.getBundle("messages", locale);
        notifyControllers();
    }

    private static void notifyControllers() {
        updateActions.forEach(Runnable::run);
    }

    public static void registerForUpdates(Runnable updateAction) {
        updateActions.add(updateAction);
    }

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public static Locale getLocale(String savedLanguage) {
        return switch (savedLanguage) {
            case "English" -> new Locale("en", "US");
            case "Русский" -> new Locale("ru", "RU");
            default -> Locale.getDefault();
        };
    }
}