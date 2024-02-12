package com.dev.station.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {
    private static ResourceBundle resourceBundle;
    private static List<Runnable> updateActions = new ArrayList<>();
    private static List<NotificationManager> notificationManagers = new ArrayList<>();

    public static void switchLanguage(Locale locale) {
        resourceBundle = ResourceBundle.getBundle("messages", locale);
        notifyControllers();
        updateNotificationManagers();
    }

    private static void notifyControllers() {
        updateActions.forEach(Runnable::run);
    }

    private static void updateNotificationManagers() {
        for (NotificationManager manager : notificationManagers) {
            manager.updateResourceBundle(resourceBundle);
        }
    }

    public static void registerForUpdates(Runnable updateAction) {
        updateActions.add(updateAction);
    }

    public static void registerNotificationManager(NotificationManager manager) {
        notificationManagers.add(manager);
    }

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public static Locale getLocale(String savedLanguage) {
        return switch (savedLanguage) {
            case "EN" -> new Locale("en", "US");
            case "RU" -> new Locale("ru", "RU");
            default -> Locale.getDefault();
        };
    }
}