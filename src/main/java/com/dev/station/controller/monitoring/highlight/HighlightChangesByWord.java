package com.dev.station.controller.monitoring.highlight;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.DeltaType;
import com.github.difflib.patch.Patch;
import org.fxmisc.richtext.model.StyleSpans;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import java.util.Collections;
import java.util.List;

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

            // Добавить неизмененный текст до начала изменения
            spansBuilder.add(Collections.emptyList(), deltaStart - currentIndex);
            currentIndex = deltaStart;

            // Обработка изменений
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

        // Добавить оставшийся неизмененный текст
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
//        System.out.println("Начало создания StyleSpans"); // Для отладки
//        for (AbstractDelta<String> delta : deltas) {
//            int originalPos = delta.getSource().getPosition();
//            int originalSize = delta.getSource().getLines().size();
//            int revisedSize = delta.getTarget().getLines().size();
//
//            System.out.printf("Delta: %s, Pos: %d, OriginalSize: %d, RevisedSize: %d%n", delta.getType(), originalPos, originalSize, revisedSize); // Для отладки
//
//            // Добавляем неизмененный текст до начала изменения
//            spansBuilder.add(Collections.emptyList(), originalPos - currentIndex);
//            System.out.printf("Добавлен неизмененный спан: %d%n", originalPos - currentIndex); // Для отладки
//            currentIndex += originalPos - currentIndex;
//
//            // Добавляем стиль для измененной части
//            String styleClass = getStyleClassByDeltaType(delta.getType());
//            spansBuilder.add(Collections.singleton(styleClass), Math.max(originalSize, revisedSize));
//            System.out.printf("Добавлен стиль '%s' на длине %d%n", styleClass, Math.max(originalSize, revisedSize)); // Для отладки
//
//            if (delta.getType() != DeltaType.INSERT) {
//                currentIndex += originalSize;
//            }
//        }
//
//        // Добавляем оставшийся неизмененный текст
//        spansBuilder.add(Collections.emptyList(), oldWords.size() - currentIndex);
//        System.out.printf("Добавлен конечный неизмененный спан: %d%n", oldWords.size() - currentIndex); // Для отладки
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
