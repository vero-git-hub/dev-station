package com.dev.station.entity;

import com.dev.station.manager.NotificationManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class WebParser {
    public String parseWebsiteForVersion(String url, NotificationManager notificationManager) {
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