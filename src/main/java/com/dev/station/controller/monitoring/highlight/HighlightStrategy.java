package com.dev.station.controller.monitoring.highlight;

import org.fxmisc.richtext.model.StyleSpans;
import java.util.Collection;

public interface HighlightStrategy {
    StyleSpans<Collection<String>> highlightChanges(String oldContent, String newContent);
}
