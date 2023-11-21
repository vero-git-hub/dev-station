package com.dev.station.entity.driver;

import com.dev.station.util.AlertUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.prefs.Preferences;

public class UpdateFinder {
    public String findUpdateLink(Preferences prefs) {
        try {
            Document doc = Jsoup.connect(prefs.get("websiteUrl", "")).get();

            Elements stableSection = doc.select("#stable .table-wrapper tbody tr.status-ok");

            for (Element row : stableSection) {
                String chromeDriver = row.select("th:nth-of-type(1) code").text();
                String win64 = row.select("th:nth-of-type(2) code").text();

                if (chromeDriver.equals("chromedriver") && win64.equals("win64")) {
                    Elements tdElements = row.select("td");
                    if (tdElements.isEmpty()) {
                        AlertUtils.showErrorAlert("Error getting driver URL", "TD elements not found");
                        continue;
                    }

                    Element linkElement = tdElements.select("code").first();
                    if (linkElement == null) {
                        AlertUtils.showErrorAlert("Error getting driver URL", "Code element not found in TD");
                        continue;
                    }

                    return linkElement.text();
                }
            }
        } catch (IOException e) {
            AlertUtils.showErrorAlert("Error getting driver URL", "HTML structure has changed.");
            e.printStackTrace();
        }
        return "Couldn't find link";
    }
}