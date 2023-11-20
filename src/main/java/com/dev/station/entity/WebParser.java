package com.dev.station.entity;

import com.dev.station.util.AlertUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.prefs.Preferences;

public class WebParser {
    public String parseWebsiteForVersion(Preferences prefs) {
        String url = prefs.get("websiteUrl", "");
        String version = null;
        try {
            Document doc = Jsoup.connect(url).get();
            Element versionElement = doc.select("#stable p code").first();

            if (versionElement != null) {
                version = versionElement.text();
            } else {
                AlertUtils.showErrorAlert("Error parsing", "Element with version on the " + url + " page not found.");
            }

        } catch (IOException e) {
            AlertUtils.showErrorAlert("Failed connection", "Error connecting to the " + url + " site.");
            e.printStackTrace();
        }
        return version;
    }
}