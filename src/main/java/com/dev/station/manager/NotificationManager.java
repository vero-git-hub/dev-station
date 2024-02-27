package com.dev.station.manager;

import com.dev.station.util.AlertUtils;

import java.util.ResourceBundle;

public class NotificationManager {
    private ResourceBundle bundle;

    public NotificationManager(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public void updateResourceBundle(ResourceBundle newBundle) {
        this.bundle = newBundle;
    }

    public void showInformationAlert(String key) {
        String message = getTranslatedText(key);
        AlertUtils.showSuccessAlert(bundle.getString("informationAlert"), message);
    }

    public void showErrorAlert(String messageKey) {
        String message = getTranslatedText("errorAlert") + " " + getTranslatedText(messageKey);
        AlertUtils.showErrorAlert(bundle.getString("errorAlert"), message);
    }

    public void showDetailsErrorAlert(String message) {
        AlertUtils.showErrorAlert(getTranslatedText("errorAlert"),message);
    }

    private String getTranslatedText(String key) {
        return bundle.containsKey(key) ? bundle.getString(key) : key;
    }
}
