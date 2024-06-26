package com.dev.station.manager;

import com.dev.station.model.SettingsModel;

import java.util.Locale;

public class SettingsManager {

    private final SettingsModel settingsModel;

    public SettingsManager() {
        this.settingsModel = new SettingsModel();
    }

    public String loadLanguageSetting() {
        return settingsModel.loadLanguageSetting();
    }

    public void saveLanguageSetting(String language) {
        settingsModel.saveLanguageSetting(language);
    }

    public Locale getLocale(String language) {
        return LanguageManager.getLocale(language);
    }

    public void switchLanguage(Locale newLocale) {
        LanguageManager.switchLanguage(newLocale);
    }
}
