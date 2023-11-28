package com.dev.station.entity;

import com.dev.station.manager.NotificationManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.prefs.Preferences;

public class WebParser {
    public String parseWebsiteForVersion(Preferences prefs, NotificationManager notificationManager) {
        String url = prefs.get("websiteUrl", "");
        String version = null;
        try {
            Document doc = Jsoup.connect(url).get();
            Element versionElement = doc.select("#stable p code").first();

            if (versionElement != null) {
                version = versionElement.text();
            } else {
                notificationManager.showErrorAlert("parseWebsiteForVersionParsingError");
            }
        } catch (IOException e) {
            notificationManager.showErrorAlert("parseWebsiteForVersionFailedConnection");
            e.printStackTrace();
        }
        return version;
    }
}