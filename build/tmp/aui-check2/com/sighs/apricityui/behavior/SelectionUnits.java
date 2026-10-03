/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.behavior;

import com.sighs.apricityui.dom.TextNode;
import com.sighs.apricityui.element.AbstractText;
import com.sighs.apricityui.element.RichText;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.init.Node;
import com.sighs.apricityui.layout.Flex;
import com.sighs.apricityui.layout.Layout;
import com.sighs.apricityui.layout.NormalFlow;
import com.sighs.apricityui.style.Interaction;
import com.sighs.apricityui.style.Text;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class SelectionUnits {
    public static final char BR_SENTINEL = '\u0001';
    public static final char OBJECT_SENTINEL = '\u0002';
    private static final Set<String> INLINE_MARKUP_TAGS = Set.of("U", "STRONG", "EM", "S", "B", "I", "SPAN", "A", "CODE", "MARK", "SMALL", "SUB", "SUP", "INS", "DEL", "Q", "ABBR", "CITE", "DFN", "KBD", "SAMP", "TIME", "VAR", "FONT");

    private SelectionUnits() {
    }

    public static boolean isAtomicObject(Element element) {
        boolean supportedTag;
        if (element == null) {
            return false;
        }
        switch (element.tagName) {
            case "IMG": 
            case "HR": 
            case "SVG": 
            case "CANVAS": 
            case "TEXTURE": 
            case "SPRITE": {
                boolean bl = true;
                break;
            }
            default: {
                boolean bl = supportedTag = false;
            }
        }
        if (!supportedTag) {
            return false;
        }
        Element current = element.parentElement;
        while (current != null) {
            if (current instanceof RichText) {
                return true;
            }
            current = current.parentElement;
        }
        return false;
    }

    public static boolean isSelectionUnit(Element element) {
        if (element == null || element instanceof AbstractText) {
            return false;
        }
        if (INLINE_MARKUP_TAGS.contains(element.tagName.toUpperCase(Locale.ROOT))) {
            return false;
        }
        if (NormalFlow.isInlineTextPaintedByAncestor(element)) {
            return false;
        }
        if (!Interaction.isUserSelectable(element)) {
            return false;
        }
        return !SelectionUnits.flattenedSelectableText(element).isEmpty();
    }

    public static Element resolveUnit(Element element) {
        Element current = element;
        while (current != null) {
            if (current instanceof AbstractText) {
                return null;
            }
            if (SelectionUnits.isSelectionUnit(current)) {
                return current;
            }
            current = current.parentElement;
        }
        return null;
    }

    public static UnitContext resolveUnitContext(Element element) {
        if (element == null) {
            return null;
        }
        Element unit = SelectionUnits.resolveUnit(element);
        if (unit == null) {
            return null;
        }
        if (element == unit) {
            String text = SelectionUnits.flattenedSelectableText(unit);
            return text.isEmpty() ? null : new UnitContext(unit, 0, text);
        }
        String own = SelectionUnits.ownSelectableText(element);
        if (own.isEmpty()) {
            return null;
        }
        return new UnitContext(unit, SelectionUnits.baseOffsetOfDescendant(unit, element), own);
    }

    public static String flattenedSelectableText(Element element) {
        if (element == null || element instanceof AbstractText) {
            return "";
        }
        Document document = element.document;
        if (document == null) {
            return SelectionUnits.computeFlattenedSelectableText(element);
        }
        return document.getCachedFlattened(element);
    }

    public static String computeFlattenedSelectableText(Element element) {
        if (element == null || element instanceof AbstractText) {
            return "";
        }
        String display = element.getComputedStyle().display;
        if (Layout.isFlexDisplay(display) || Layout.isGridDisplay(display)) {
            StringBuilder flexText = new StringBuilder();
            for (String fragment : SelectionUnits.flexTextFragments(element)) {
                flexText.append(fragment);
            }
            return flexText.toString();
        }
        StringBuilder raw = new StringBuilder();
        SelectionUnits.flattenRaw(element, element, raw);
        if (raw.length() == 0) {
            return "";
        }
        String normalized = Text.normalizeWhiteSpaceContent(raw.toString(), Text.getWhiteSpace(element));
        if (normalized == null || normalized.isEmpty()) {
            return "";
        }
        if (normalized.indexOf(1) >= 0) {
            normalized = normalized.replace('\u0001', '\n');
        }
        if (normalized.indexOf(2) >= 0) {
            normalized = normalized.replace('\u0002', '\ufffc');
        }
        return normalized;
    }

    public static String ownSelectableText(Element element) {
        String raw;
        if (element == null) {
            return "";
        }
        if (!element.childNodes.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (Node child : element.childNodes) {
                if (!(child instanceof TextNode)) continue;
                TextNode textNode = (TextNode)child;
                builder.append(textNode.getTextContent());
            }
            raw = builder.toString();
            if (raw.isEmpty()) {
                raw = element.innerText == null ? "" : element.innerText;
            }
        } else {
            String string = raw = element.innerText == null ? "" : element.innerText;
        }
        if (raw.isEmpty()) {
            return "";
        }
        String normalized = Text.normalizeWhiteSpaceContent(raw, Text.getWhiteSpace(element));
        return normalized == null ? "" : normalized;
    }

    public static int baseOffsetOfDescendant(Element unit, Node descendant) {
        if (unit == null || descendant == null) {
            return 0;
        }
        if (descendant == unit) {
            return 0;
        }
        String display = unit.getComputedStyle().display;
        if (Layout.isFlexDisplay(display) || Layout.isGridDisplay(display)) {
            return SelectionUnits.baseOffsetInFlexDirectText(unit, descendant);
        }
        StringBuilder raw = new StringBuilder();
        if (SelectionUnits.findRawPrefix(unit, unit, descendant, raw)) {
            return SelectionUnits.normalizedPrefixLength(raw.toString(), Text.getWhiteSpace(unit));
        }
        return 0;
    }

    private static int normalizedPrefixLength(String raw, String whiteSpace) {
        if (raw == null || raw.isEmpty()) {
            return 0;
        }
        String normalized = Text.normalizeWhiteSpaceContent(raw + "\u0000", whiteSpace);
        if (normalized == null) {
            return 0;
        }
        return Math.max(0, normalized.length() - 1);
    }

    public static List<Element> enumerateUnits(Document document) {
        if (document == null) {
            return new ArrayList<Element>();
        }
        return document.getCachedUnits();
    }

    public static List<Element> computeUnits(Document document) {
        ArrayList<Element> result = new ArrayList<Element>();
        if (document == null) {
            return result;
        }
        for (Element element : document.getElements()) {
            if (!SelectionUnits.isSelectionUnit(element)) continue;
            result.add(element);
        }
        return result;
    }

    public static boolean paintsTextViaRuns(Element element) {
        if (element == null || element instanceof AbstractText) {
            return false;
        }
        Document document = element.document;
        if (document == null) {
            return SelectionUnits.computePaintsTextViaRuns(element);
        }
        return document.getCachedPaintsRuns(element);
    }

    public static boolean computePaintsTextViaRuns(Element element) {
        if (element == null || element instanceof AbstractText) {
            return false;
        }
        if (element.getRenderChildNodes().isEmpty()) {
            return false;
        }
        String display = element.getComputedStyle().display;
        if (Layout.isGridDisplay(display)) {
            return false;
        }
        if (Layout.isFlexDisplay(display)) {
            return !Flex.computeDirectTextLayouts(element).isEmpty();
        }
        if (element.getRenderChildren().isEmpty()) {
            for (Node child : element.getRenderChildNodes()) {
                TextNode textNode;
                if (!(child instanceof TextNode) || (textNode = (TextNode)child).getTextContent().isEmpty()) continue;
                return false;
            }
        }
        return !NormalFlow.computeTextRuns(element).isEmpty();
    }

    public static List<String> flexTextFragments(Element element) {
        String normalized;
        ArrayList<String> fragments = new ArrayList<String>();
        if (element == null) {
            return fragments;
        }
        for (Node child : element.getRenderChildNodes()) {
            TextNode textNode;
            String normalized2;
            if (!(child instanceof TextNode) || (normalized2 = Text.normalizeWhiteSpaceContent((textNode = (TextNode)child).getTextContent(), Text.getWhiteSpace(element))) == null || normalized2.isBlank()) continue;
            fragments.add(normalized2);
        }
        if (fragments.isEmpty() && element.innerText != null && !element.innerText.isBlank() && (normalized = Text.normalizeWhiteSpaceContent(element.innerText, Text.getWhiteSpace(element))) != null && !normalized.isBlank()) {
            fragments.add(normalized);
        }
        return fragments;
    }

    public static RawText rawTextOf(Element unit) {
        if (unit == null || unit instanceof AbstractText) {
            return null;
        }
        Document document = unit.document;
        if (document == null) {
            return SelectionUnits.computeRawTextOf(unit);
        }
        return document.getCachedRaw(unit);
    }

    public static RawText computeRawTextOf(Element unit) {
        if (unit == null || unit instanceof AbstractText) {
            return null;
        }
        String display = unit.getComputedStyle().display;
        if (Layout.isFlexDisplay(display) || Layout.isGridDisplay(display)) {
            return SelectionUnits.buildFlexRawText(unit);
        }
        StringBuilder raw = new StringBuilder();
        SelectionUnits.flattenRaw(unit, unit, raw);
        if (raw.length() == 0) {
            return null;
        }
        return SelectionUnits.normalizeWithSpans(unit, raw.toString());
    }

    public static String rawRangeForNormalizedRange(Element unit, int nStart, int nEnd) {
        RawText rawText = SelectionUnits.rawTextOf(unit);
        if (rawText == null) {
            return "";
        }
        return rawText.rawRangeForNormalizedRange(nStart, nEnd);
    }

    public static boolean isLineBreak(Element element) {
        return element != null && "BR".equals(element.tagName);
    }

    public static int runLineStart(NormalFlow.TextRunLayout run, int lineIndex) {
        if (run == null || lineIndex <= 0) {
            return 0;
        }
        String content = run.text() == null ? null : run.text().content;
        List<String> lines = run.lines();
        if (lines == null || content == null) {
            return 0;
        }
        int cursor = 0;
        for (int i = 0; i < lineIndex; ++i) {
            String current = lines.get(i);
            String next = lines.get(i + 1);
            int searchFrom = cursor + current.length();
            int found = next == null ? -1 : content.indexOf(next, searchFrom);
            cursor = found >= 0 ? found : searchFrom;
        }
        return cursor;
    }

    private static void flattenRaw(Element unit, Element current, StringBuilder raw) {
        boolean contributed = false;
        for (Node child : current.getRenderChildNodes()) {
            Element childElement;
            if (child instanceof TextNode) {
                TextNode textNode = (TextNode)child;
                String content = textNode.getTextContent();
                if (content != null && !content.isEmpty()) {
                    contributed = true;
                }
                raw.append(content);
                continue;
            }
            if (!(child instanceof Element) || (childElement = (Element)child) instanceof AbstractText) continue;
            if (SelectionUnits.isLineBreak(childElement)) {
                raw.append('\u0001');
                contributed = true;
                continue;
            }
            if (SelectionUnits.isAtomicObject(childElement)) {
                raw.append('\u0002');
                contributed = true;
                continue;
            }
            if (SelectionUnits.isSelectionUnit(childElement)) continue;
            int before = raw.length();
            SelectionUnits.flattenRaw(unit, childElement, raw);
            if (raw.length() == before) continue;
            contributed = true;
        }
        if (!contributed && current.innerText != null && !current.innerText.isEmpty()) {
            raw.append(current.innerText);
        }
    }

    private static boolean findRawPrefix(Element unit, Element current, Node target, StringBuilder raw) {
        for (Node child : current.getRenderChildNodes()) {
            Element childElement;
            if (child == target) {
                return true;
            }
            if (child instanceof TextNode) {
                TextNode textNode = (TextNode)child;
                raw.append(textNode.getTextContent());
                continue;
            }
            if (!(child instanceof Element) || (childElement = (Element)child) instanceof AbstractText) continue;
            if (SelectionUnits.isLineBreak(childElement)) {
                if (child == target) {
                    return true;
                }
                raw.append('\u0001');
                continue;
            }
            if (SelectionUnits.isAtomicObject(childElement)) {
                if (child == target) {
                    return true;
                }
                raw.append('\u0002');
                continue;
            }
            if (SelectionUnits.isSelectionUnit(childElement) || !SelectionUnits.findRawPrefix(unit, childElement, target, raw)) continue;
            return true;
        }
        return false;
    }

    private static int baseOffsetInFlexDirectText(Element unit, Node target) {
        String normalized;
        int base = 0;
        boolean anyNonBlankTextNode = false;
        for (Node child : unit.getRenderChildNodes()) {
            if (!(child instanceof TextNode)) continue;
            TextNode textNode = (TextNode)child;
            if (child == target) {
                return base;
            }
            String normalized2 = Text.normalizeWhiteSpaceContent(textNode.getTextContent(), Text.getWhiteSpace(unit));
            if (normalized2 == null || normalized2.isBlank()) continue;
            anyNonBlankTextNode = true;
            base += normalized2.length();
        }
        if (!(anyNonBlankTextNode || unit.innerText == null || unit.innerText.isBlank() || (normalized = Text.normalizeWhiteSpaceContent(unit.innerText, Text.getWhiteSpace(unit))) == null || normalized.isBlank())) {
            base += normalized.length();
        }
        return base;
    }

    private static RawText normalizeWithSpans(Element unit, String raw) {
        String value;
        String whiteSpace = Text.getWhiteSpace(unit);
        StringBuilder normalized = new StringBuilder(raw.length());
        ArrayList<int[]> spans = new ArrayList<int[]>();
        ArrayList<int[]> skipped = new ArrayList<int[]>();
        switch (value = whiteSpace == null ? "normal" : whiteSpace) {
            case "pre": 
            case "pre-wrap": 
            case "break-spaces": {
                SelectionUnits.normalizePre(raw, normalized, spans);
                break;
            }
            case "pre-line": {
                SelectionUnits.normalizePreLine(raw, normalized, spans, skipped);
                break;
            }
            default: {
                SelectionUnits.normalizeSingleLine(raw, normalized, spans, skipped);
            }
        }
        return new RawText(raw, normalized.toString(), SelectionUnits.startsOf(spans), SelectionUnits.endsOf(spans), (int[][])skipped.toArray((T[])new int[0][]));
    }

    private static RawText buildFlexRawText(Element unit) {
        String content;
        String fragment;
        String whiteSpace = Text.getWhiteSpace(unit);
        StringBuilder raw = new StringBuilder();
        StringBuilder normalized = new StringBuilder();
        ArrayList<int[]> spans = new ArrayList<int[]>();
        ArrayList<int[]> skipped = new ArrayList<int[]>();
        boolean anyNonBlank = false;
        for (Node child : unit.getRenderChildNodes()) {
            TextNode textNode;
            String content2;
            if (!(child instanceof TextNode) || (content2 = (textNode = (TextNode)child).getTextContent()) == null || content2.isEmpty()) continue;
            String fragment2 = Text.normalizeWhiteSpaceContent(content2, whiteSpace);
            if (fragment2 == null || fragment2.isBlank()) {
                int base = raw.length();
                raw.append(content2);
                skipped.add(new int[]{base, base + content2.length()});
                continue;
            }
            anyNonBlank = true;
            SelectionUnits.appendFragment(raw, normalized, spans, content2, whiteSpace);
        }
        if (!(anyNonBlank || unit.innerText == null || unit.innerText.isBlank() || (fragment = Text.normalizeWhiteSpaceContent(content = unit.innerText, whiteSpace)) == null || fragment.isBlank())) {
            SelectionUnits.appendFragment(raw, normalized, spans, content, whiteSpace);
        }
        if (normalized.length() == 0) {
            return null;
        }
        return new RawText(raw.toString(), normalized.toString(), SelectionUnits.startsOf(spans), SelectionUnits.endsOf(spans), (int[][])skipped.toArray((T[])new int[0][]));
    }

    private static void appendFragment(StringBuilder raw, StringBuilder normalized, ArrayList<int[]> spans, String content, String whiteSpace) {
        String value;
        int base = raw.length();
        raw.append(content);
        StringBuilder fragmentNormalized = new StringBuilder();
        ArrayList<int[]> fragmentSpans = new ArrayList<int[]>();
        switch (value = whiteSpace == null ? "normal" : whiteSpace) {
            case "pre": 
            case "pre-wrap": 
            case "break-spaces": {
                SelectionUnits.normalizePre(content, fragmentNormalized, fragmentSpans);
                break;
            }
            case "pre-line": {
                ArrayList<int[]> fragmentSkipped = new ArrayList<int[]>();
                SelectionUnits.normalizePreLine(content, fragmentNormalized, fragmentSpans, fragmentSkipped);
                break;
            }
            default: {
                ArrayList<int[]> fragmentSkipped = new ArrayList();
                SelectionUnits.normalizeSingleLine(content, fragmentNormalized, fragmentSpans, fragmentSkipped);
            }
        }
        for (int i = 0; i < fragmentSpans.size(); ++i) {
            int[] span = fragmentSpans.get(i);
            normalized.append(fragmentNormalized.charAt(i));
            spans.add(new int[]{base + span[0], base + span[1]});
        }
    }

    private static void normalizePre(String raw, StringBuilder normalized, ArrayList<int[]> spans) {
        for (int i = 0; i < raw.length(); ++i) {
            char c = raw.charAt(i);
            if (c == '\r') {
                int spanEnd = i + 1;
                if (i + 1 < raw.length() && raw.charAt(i + 1) == '\n') {
                    spanEnd = i + 2;
                }
                SelectionUnits.emit(normalized, spans, '\n', i, spanEnd);
                if (spanEnd != i + 2) continue;
                ++i;
                continue;
            }
            SelectionUnits.emit(normalized, spans, c, i, i + 1);
        }
    }

    private static void normalizeSingleLine(String raw, StringBuilder normalized, ArrayList<int[]> spans, ArrayList<int[]> skipped) {
        boolean pendingSpace = false;
        boolean emitted = false;
        int pendingStart = -1;
        for (int i = 0; i < raw.length(); ++i) {
            char c = raw.charAt(i);
            if (c == '\r') {
                if (!pendingSpace) {
                    pendingStart = i;
                }
                if (i + 1 < raw.length() && raw.charAt(i + 1) == '\n') {
                    ++i;
                }
                pendingSpace = true;
                continue;
            }
            if (c == '\n' || SelectionUnits.isCollapsibleSpace(c)) {
                if (!pendingSpace) {
                    pendingStart = i;
                }
                pendingSpace = true;
                continue;
            }
            if (pendingSpace && emitted) {
                SelectionUnits.emit(normalized, spans, ' ', pendingStart, i);
            } else if (pendingSpace) {
                skipped.add(new int[]{pendingStart, i});
            }
            SelectionUnits.emit(normalized, spans, c, i, i + 1);
            pendingSpace = false;
            emitted = true;
        }
        if (pendingSpace) {
            skipped.add(new int[]{pendingStart, raw.length()});
        }
    }

    private static void normalizePreLine(String raw, StringBuilder normalized, ArrayList<int[]> spans, ArrayList<int[]> skipped) {
        int lineStart = 0;
        for (int i = 0; i <= raw.length(); ++i) {
            int c;
            boolean end = i >= raw.length();
            int n = c = end ? 10 : (int)raw.charAt(i);
            if (c == 13) {
                int spanEnd = i + 1;
                if (i + 1 < raw.length() && raw.charAt(i + 1) == '\n') {
                    spanEnd = i + 2;
                }
                SelectionUnits.appendCollapsedLine(raw, lineStart, i, normalized, spans, skipped);
                if (!end) {
                    SelectionUnits.emit(normalized, spans, '\n', i, spanEnd);
                }
                lineStart = spanEnd;
                i = spanEnd - 1;
                continue;
            }
            if (c != 10) continue;
            SelectionUnits.appendCollapsedLine(raw, lineStart, i, normalized, spans, skipped);
            if (!end) {
                SelectionUnits.emit(normalized, spans, '\n', i, i + 1);
            }
            lineStart = i + 1;
        }
    }

    private static void appendCollapsedLine(String raw, int lineStart, int lineEnd, StringBuilder normalized, ArrayList<int[]> spans, ArrayList<int[]> skipped) {
        boolean pendingSpace = false;
        boolean emitted = false;
        int pendingStart = -1;
        for (int i = lineStart; i < lineEnd; ++i) {
            char c = raw.charAt(i);
            if (SelectionUnits.isCollapsibleSpace(c)) {
                if (!pendingSpace) {
                    pendingStart = i;
                }
                pendingSpace = true;
                continue;
            }
            if (pendingSpace && emitted) {
                SelectionUnits.emit(normalized, spans, ' ', pendingStart, i);
            } else if (pendingSpace) {
                skipped.add(new int[]{pendingStart, i});
            }
            SelectionUnits.emit(normalized, spans, c, i, i + 1);
            pendingSpace = false;
            emitted = true;
        }
        if (pendingSpace) {
            skipped.add(new int[]{pendingStart, lineEnd});
        }
    }

    private static void emit(StringBuilder normalized, ArrayList<int[]> spans, char c, int rawStart, int rawEnd) {
        normalized.append(c);
        spans.add(new int[]{rawStart, rawEnd});
    }

    private static int[] startsOf(ArrayList<int[]> spans) {
        int[] result = new int[spans.size()];
        for (int i = 0; i < spans.size(); ++i) {
            result[i] = spans.get(i)[0];
        }
        return result;
    }

    private static int[] endsOf(ArrayList<int[]> spans) {
        int[] result = new int[spans.size()];
        for (int i = 0; i < spans.size(); ++i) {
            result[i] = spans.get(i)[1];
        }
        return result;
    }

    private static boolean isCollapsibleSpace(char c) {
        return c == ' ' || c == '\t' || c == '\u000b' || c == '\f';
    }

    public record UnitContext(Element unit, int baseOffset, String text) {
    }

    public record RawText(String raw, String normalized, int[] rawStart, int[] rawEnd, int[][] skippedRuns) {
        public String rawRangeForNormalizedRange(int nStart, int nEnd) {
            String substring;
            int end;
            if (this.raw == null || this.normalized == null || this.rawStart == null || this.rawEnd == null) {
                return "";
            }
            if (this.normalized.isEmpty() || this.raw.isEmpty()) {
                return "";
            }
            int length = this.normalized.length();
            int start = Math.max(0, Math.min(nStart, length));
            if (start >= (end = Math.max(start, Math.min(nEnd, length)))) {
                return "";
            }
            int rawStartIndex = this.rawStart[start];
            int rawEndIndex = this.rawEnd[end - 1];
            if (start == 0) {
                rawStartIndex = this.extendLeft(rawStartIndex);
            }
            if (end == length) {
                rawEndIndex = this.extendRight(rawEndIndex);
            }
            if ((substring = this.raw.substring(rawStartIndex, rawEndIndex)).indexOf(1) >= 0) {
                substring = substring.replace('\u0001', '\n');
            }
            if (substring.indexOf(2) >= 0) {
                substring = substring.replace('\u0002', '\ufffc');
            }
            return substring;
        }

        private int extendLeft(int index) {
            boolean changed;
            if (this.skippedRuns == null) {
                return index;
            }
            do {
                changed = false;
                for (int[] run : this.skippedRuns) {
                    if (run[1] != index || run[0] == run[1]) continue;
                    index = run[0];
                    changed = true;
                }
            } while (changed);
            return index;
        }

        private int extendRight(int index) {
            boolean changed;
            if (this.skippedRuns == null) {
                return index;
            }
            do {
                changed = false;
                for (int[] run : this.skippedRuns) {
                    if (run[0] != index || run[0] == run[1]) continue;
                    index = run[1];
                    changed = true;
                }
            } while (changed);
            return index;
        }
    }

    public record UnitOffset(Element unit, int offset) {
    }
}

