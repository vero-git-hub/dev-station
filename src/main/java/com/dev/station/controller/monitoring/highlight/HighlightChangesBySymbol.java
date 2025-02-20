package com.dev.station.controller.monitoring.highlight;

import org.apache.commons.text.diff.CommandVisitor;
import org.apache.commons.text.diff.EditScript;
import org.apache.commons.text.diff.StringsComparator;
import org.fxmisc.richtext.model.StyleSpans;

import java.util.Collection;

public class HighlightChangesBySymbol implements HighlightStrategy {

    private String escapeHtml(String string) {
        String escapedText = string.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
//                .replace("\n", "<br>")
//                .replace(" ", "&nbsp;");
        //setLogging("INFO", "Escaped Text: " + escapedText);
        return escapedText;
    }

    @Override
    public StyleSpans<Collection<String>> highlightChanges(String oldContent, String newContent) {
        StringBuilder highlightedText = new StringBuilder(
                "<style>" +
                        ".added { background-color: #ccffcc; } " +
                        ".removed { background-color: #ffcccc; } " +
                        "</style><pre>"
        );

        StringsComparator comp = new StringsComparator(oldContent, newContent);
        EditScript<Character> script = comp.getScript();
        script.visit(new CommandVisitor<Character>() {
            @Override
            public void visitInsertCommand(Character object) {
                highlightedText.append("<span class='added'>").append(escapeHtml(String.valueOf(object))).append("</span>");
            }

            @Override
            public void visitDeleteCommand(Character object) {
                highlightedText.append("<span class='removed'>").append(escapeHtml(String.valueOf(object))).append("</span>");
            }

            @Override
            public void visitKeepCommand(Character object) {
                highlightedText.append(escapeHtml(String.valueOf(object)));
            }
        });

        highlightedText.append("</pre>");
        //return "<html><body>" + highlightedText.toString() + "</body></html>";
//        String textToHtml = "<html><body>" + highlightedText.toString() + "</body></html>";
//        Platform.runLater(() -> webEngine.loadContent(textToHtml));
        return null;
    }
}
