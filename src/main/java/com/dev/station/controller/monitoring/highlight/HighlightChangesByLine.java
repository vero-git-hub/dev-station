package com.dev.station.controller.monitoring.highlight;

import org.fxmisc.richtext.model.StyleSpans;

import java.util.Collection;

/**
 * For highlight changes by line
 */
public class HighlightChangesByLine implements HighlightStrategy {

    @Override
    public StyleSpans<Collection<String>> highlightChanges(String oldContent, String newContent) {
//        StringBuilder highlightedText = new StringBuilder("<style>");
//        highlightedText.append(".added { background-color: #ccffcc; } ");
//        highlightedText.append(".removed { background-color: #ffcccc; }</style><pre>");
//
//        String[] oldLines = oldContent.split("\\r?\\n");
//        String[] newLines = newContent.split("\\r?\\n");
//
//        int minLength = Math.min(oldLines.length, newLines.length);
//        for (int i = 0; i < minLength; i++) {
//            String oldLine = oldLines[i];
//            String newLine = newLines[i];
//
//            if (oldLine.equals(newLine)) {
//                highlightedText.append(escapeHtml(oldLine) + "\n");
//            } else {
//                if (i < oldLines.length) {
//                    highlightedText.append("<span class='removed'>");
//                    highlightedText.append(escapeHtml(oldLine) + "\n");
//                    highlightedText.append("</span>");
//                }
//                if (i < newLines.length) {
//                    highlightedText.append("<span class='added'>");
//                    highlightedText.append(escapeHtml(newLine) + "\n");
//                    highlightedText.append("</span>");
//                }
//            }
//        }
//
//        if (oldLines.length > newLines.length) {
//            for (int i = minLength; i < oldLines.length; i++) {
//                highlightedText.append("<span class='removed'>");
//                highlightedText.append(escapeHtml(oldLines[i]) + "\n");
//                highlightedText.append("</span>");
//            }
//        } else if (newLines.length > oldLines.length) {
//            for (int i = minLength; i < newLines.length; i++) {
//                highlightedText.append("<span class='added'>");
//                highlightedText.append(escapeHtml(newLines[i]) + "\n");
//                highlightedText.append("</span>");
//            }
//        }
//
//        highlightedText.append("</pre>");
//        String textToHtml = "<html><body>" + highlightedText.toString() + "</body></html>";
//        Platform.runLater(() -> webEngine.loadContent(textToHtml));
        return null;
    }
}
