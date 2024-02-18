package com.dev.station.entity.driver;

import com.dev.station.entity.DriverSettings;
import com.dev.station.manager.NotificationManager;
import com.dev.station.model.SettingsModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class UpdateFinder {
    private final NotificationManager notificationManager;
    SettingsModel settingsModel;

    public UpdateFinder(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
        settingsModel = new SettingsModel();
    }

    public String findUpdateLink() {
        try {
            DriverSettings driverSettings = settingsModel.readDriverSettings();
            String url = driverSettings.getWebsiteUrl();

            Document doc = Jsoup.connect(url).get();

            Elements stableSection = doc.select("#stable .table-wrapper tbody tr.status-ok");

            for (Element row : stableSection) {
                String chromeDriver = row.select("th:nth-of-type(1) code").text();
                String win64 = row.select("th:nth-of-type(2) code").text();

                if (chromeDriver.equals("chromedriver") && win64.equals("win64")) {
                    Elements tdElements = row.select("td");
                    if (tdElements.isEmpty()) {
                        notificationManager.showErrorAlert("findUpdateLinkTDError");
                        continue;
                    }

                    Element linkElement = tdElements.select("code").first();
                    if (linkElement == null) {
                        notificationManager.showErrorAlert("findUpdateLinkCodeError");
                        continue;
                    }

                    return linkElement.text();
                }
            }
        } catch (IOException e) {
            notificationManager.showErrorAlert("findUpdateLinkHTMLError");
            e.printStackTrace();
        }
        notificationManager.showErrorAlert("missingLink");
        return "";
    }
}