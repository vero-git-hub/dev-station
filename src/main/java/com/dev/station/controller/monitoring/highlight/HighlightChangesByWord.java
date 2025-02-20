package com.dev.station.controller.monitoring.highlight;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import org.fxmisc.richtext.model.StyleSpans;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.fxmisc.richtext.model.StyleSpansBuilder;
import java.util.Collections;

/**
 * Identifying differences & changes by words
 */
public class HighlightChangesByWord implements HighlightStrategy {
    @Override
    public StyleSpans<Collection<String>> highlightChanges(String oldContent, String newContent) {
        List<AbstractDelta<String>> deltas = computeDeltas(oldContent, newContent);
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        int currentIndex = 0;
        for (AbstractDelta<String> delta : deltas) {
            int deltaStart = delta.getSource().getPosition();
            int oldSize = delta.getSource().getLines().size();

            // Add unchanged text before changing
            spansBuilder.add(Collections.emptyList(), deltaStart - currentIndex);
            currentIndex = deltaStart;

            // Processing Changes
            switch (delta.getType()) {
                case DELETE:
                    spansBuilder.add(Collections.singleton("removed"), oldSize);
                    break;
                case INSERT:
                    spansBuilder.add(Collections.singleton("added"), delta.getTarget().getLines().size());
                    break;
                case CHANGE:
                    spansBuilder.add(Collections.singleton("removed"), oldSize);
                    currentIndex += oldSize;
                    spansBuilder.add(Collections.singleton("added"), delta.getTarget().getLines().size());
                    break;
            }

            currentIndex += oldSize;
        }

        // Add remaining unchanged text
        spansBuilder.add(Collections.emptyList(), oldContent.length() - currentIndex);

        return spansBuilder.create();
    }

    private List<AbstractDelta<String>> computeDeltas(String oldContent, String newContent) {
        String[] oldTokens = oldContent.split("(?<=\\s)|(?=\\s+)");
        String[] newTokens = newContent.split("(?<=\\s)|(?=\\s+)");
        return DiffUtils.diff(Arrays.asList(oldTokens), Arrays.asList(newTokens)).getDeltas();
    }

//    private StyleSpans<Collection<String>> buildHighlightingStyleSpans(List<String> oldWords, List<String> newWords, List<AbstractDelta<String>> deltas) {
//        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
//        int currentIndex = 0;
//
//        System.out.println("Start of creation StyleSpans");
//        for (AbstractDelta<String> delta : deltas) {
//            int originalPos = delta.getSource().getPosition();
//            int originalSize = delta.getSource().getLines().size();
//            int revisedSize = delta.getTarget().getLines().size();
//
//            System.out.printf("Delta: %s, Pos: %d, OriginalSize: %d, RevisedSize: %d%n", delta.getType(), originalPos, originalSize, revisedSize);
//
//            // Adding unchanged text before changing
//            spansBuilder.add(Collections.emptyList(), originalPos - currentIndex);
//            System.out.printf("Added unchanged span: %d%n", originalPos - currentIndex);
//            currentIndex += originalPos - currentIndex;
//
//            // Adding a style to the changed part
//            String styleClass = getStyleClassByDeltaType(delta.getType());
//            spansBuilder.add(Collections.singleton(styleClass), Math.max(originalSize, revisedSize));
//            System.out.printf("Added style '%s' at length %d%n", styleClass, Math.max(originalSize, revisedSize));
//
//            if (delta.getType() != DeltaType.INSERT) {
//                currentIndex += originalSize;
//            }
//        }
//
//        // Add the remaining unchanged text
//        spansBuilder.add(Collections.emptyList(), oldWords.size() - currentIndex);
//        System.out.printf("Added final unmodified span: %d%n", oldWords.size() - currentIndex);
//
//        return spansBuilder.create();
//    }
//
//    private String getStyleClassByDeltaType(DeltaType type) {
//        switch (type) {
//            case DELETE: return "deleted";
//            case INSERT: return "inserted";
//            case CHANGE: return "changed";
//            default: return "";
//        }
//    }

}
