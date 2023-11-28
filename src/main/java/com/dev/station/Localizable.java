package com.dev.station;

import java.util.Locale;
import java.util.ResourceBundle;

public interface Localizable {
    void loadSavedLanguage();

    void switchLanguage(Locale newLocale);

    void updateUI();
}
