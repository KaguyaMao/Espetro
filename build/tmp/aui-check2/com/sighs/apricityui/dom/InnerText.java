/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.dom;

import com.sighs.apricityui.dom.TextNode;
import com.sighs.apricityui.dom.TextTransform;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.init.Node;
import com.sighs.apricityui.layout.Layout;
import com.sighs.apricityui.parser.CSS;
import com.sighs.apricityui.style.Interaction;
import com.sighs.apricityui.style.Style;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class InnerText {
    private static final Set<String> REPLACED_CONTENT_TAGS = Set.of("INPUT", "TEXTAREA", "IFRAME", "AUDIO", "VIDEO", "CANVAS", "OBJECT", "IMG", "IMAGE");

    private InnerText() {
    }

    public static String get(Element element) {
        if (element == null) {
            return "";
        }
        if (!InnerText.hasRenderedBox(element)) {
            return element.getTextContent();
        }
        Builder builder = new Builder();
        InnerText.appendElement(element, builder, true, false);
        return builder.finish();
    }

    public static void set(Element element, String value) {
        if (element == null) {
            return;
        }
        String normalized = InnerText.normalizeLineEndings(value == null ? "" : value);
        element.setTextContent("");
        if (normalized.isEmpty() || element.document == null) {
            return;
        }
        int start = 0;
        for (int i = 0; i <= normalized.length(); ++i) {
            if (i < normalized.length() && normalized.charAt(i) != '\n') continue;
            if (i > start) {
                element.appendChild(element.document.createTextNode(normalized.substring(start, i)));
            }
            if (i < normalized.length()) {
                element.appendChild(element.document.createElement("br"));
            }
            start = i + 1;
        }
    }

    private static void appendElement(Element element, Builder builder, boolean root, boolean blockified) {
        int boundaryLines;
        if (element == null || element.isPseudoElement()) {
            return;
        }
        if (!root && !InnerText.isRendered(element)) {
            return;
        }
        String tag = InnerText.normalizedTag(element);
        if ("BR".equals(tag)) {
            if (!root) {
                builder.hardBreak();
            }
            return;
        }
        if (root && ("TR".equals(tag) || "table-row".equals(InnerText.effectiveDisplay(element)))) {
            InnerText.appendTableRow(element, builder);
            return;
        }
        if (root && ("TD".equals(tag) || "TH".equals(tag) || "table-cell".equals(InnerText.effectiveDisplay(element)))) {
            InnerText.appendChildren(element, builder, Interaction.isVisible(element), false);
            return;
        }
        boolean visible = Interaction.isVisible(element);
        String display = InnerText.effectiveDisplay(element);
        boolean paragraph = "P".equals(tag);
        boolean independentInline = InnerText.isIndependentInline(display) || "BUTTON".equals(tag);
        boolean block = paragraph || blockified || InnerText.isBlockLevel(display) || InnerText.isOutOfFlow(element);
        int n = boundaryLines = paragraph ? 2 : 1;
        if (root && REPLACED_CONTENT_TAGS.contains(tag)) {
            return;
        }
        if (!root && "HR".equals(tag)) {
            builder.requiredBreak(1);
            InnerText.appendChildren(element, builder, visible, false);
            builder.requiredBreak(1);
            return;
        }
        if (!root && REPLACED_CONTENT_TAGS.contains(tag)) {
            if (block) {
                builder.requiredBreak(1);
            } else {
                builder.atomicBoundary();
            }
            return;
        }
        if (!root && visible && block) {
            builder.requiredBreak(boundaryLines);
        }
        if ("SELECT".equals(tag)) {
            if (visible) {
                InnerText.appendSelect(element, builder);
            }
        } else if (InnerText.isTableContainer(tag, display)) {
            if ("inline-table".equals(display) && !root) {
                InnerText.appendIndependentTable(element, builder, visible);
            } else if (visible) {
                InnerText.appendTable(element, builder);
            }
        } else if (independentInline && !root) {
            InnerText.appendIndependentInline(element, builder, visible);
        } else if ("DETAILS".equals(tag) && !element.hasAttribute("open")) {
            InnerText.appendClosedDetails(element, builder);
        } else {
            InnerText.appendChildren(element, builder, visible, InnerText.isFlexOrGrid(display));
        }
        if (!root && visible && block) {
            builder.requiredBreak(boundaryLines);
        }
    }

    private static void appendChildren(Element element, Builder builder, boolean visible, boolean blockifyChildren) {
        if (element.childNodes.isEmpty()) {
            if (visible && element.innerText != null && !element.innerText.isEmpty()) {
                builder.text(element.innerText, InnerText.whiteSpaceOf(element), InnerText.textTransformOf(element), InnerText.languageOf(element));
            }
            return;
        }
        for (Node child : element.childNodes) {
            if (child instanceof TextNode) {
                TextNode textNode = (TextNode)child;
                if (!visible) continue;
                builder.text(textNode.getTextContent(), InnerText.whiteSpaceOf(element), InnerText.textTransformOf(element), InnerText.languageOf(element));
                continue;
            }
            if (!(child instanceof Element)) continue;
            Element childElement = (Element)child;
            InnerText.appendElement(childElement, builder, false, blockifyChildren);
        }
    }

    private static void appendIndependentInline(Element element, Builder parent, boolean visible) {
        parent.atomicBoundary();
        if (visible) {
            Builder inner = new Builder();
            InnerText.appendChildren(element, inner, true, InnerText.isFlexOrGrid(InnerText.effectiveDisplay(element)));
            parent.literal(inner.finish());
        } else {
            Builder inner = new Builder();
            InnerText.appendChildren(element, inner, false, InnerText.isFlexOrGrid(InnerText.effectiveDisplay(element)));
            parent.literal(inner.finish());
        }
        parent.atomicBoundary();
    }

    private static void appendIndependentTable(Element element, Builder parent, boolean visible) {
        parent.atomicBoundary();
        if (visible) {
            Builder inner = new Builder();
            InnerText.appendTable(element, inner);
            parent.literal(inner.finish());
        }
        parent.atomicBoundary();
    }

    private static void appendClosedDetails(Element details, Builder builder) {
        for (Node child : details.childNodes) {
            Element element;
            if (!(child instanceof Element) || !"SUMMARY".equals(InnerText.normalizedTag(element = (Element)child))) continue;
            InnerText.appendElement(element, builder, false, false);
            return;
        }
    }

    private static void appendSelect(Element select, Builder builder) {
        ArrayList<Element> options = new ArrayList<Element>();
        InnerText.collectOptions(select, options);
        for (int i = 0; i < options.size(); ++i) {
            if (i > 0) {
                builder.hardBreak();
            }
            Element option = (Element)options.get(i);
            builder.text(option.getTextContent(), InnerText.whiteSpaceOf(option), InnerText.textTransformOf(option), InnerText.languageOf(option));
        }
    }

    private static void collectOptions(Element current, List<Element> options) {
        for (Node child : current.childNodes) {
            if (!(child instanceof Element)) continue;
            Element element = (Element)child;
            if ("OPTION".equals(InnerText.normalizedTag(element))) {
                options.add(element);
                continue;
            }
            InnerText.collectOptions(element, options);
        }
    }

    private static void appendTable(Element table, Builder builder) {
        ArrayList<Element> rows = new ArrayList<Element>();
        InnerText.collectRows(table, rows);
        if (!rows.isEmpty()) {
            for (int i = 0; i < rows.size(); ++i) {
                if (i > 0) {
                    builder.hardBreak();
                }
                InnerText.appendTableRow((Element)rows.get(i), builder);
            }
            return;
        }
        List<Element> cells = InnerText.directCells(table);
        if (!cells.isEmpty()) {
            InnerText.appendCells(cells, builder);
            return;
        }
        InnerText.appendChildren(table, builder, Interaction.isVisible(table), false);
    }

    private static void collectRows(Element current, List<Element> rows) {
        for (Node child : current.childNodes) {
            Element element;
            if (!(child instanceof Element) || !InnerText.isRendered(element = (Element)child)) continue;
            if ("TR".equals(InnerText.normalizedTag(element)) || "table-row".equals(InnerText.effectiveDisplay(element))) {
                rows.add(element);
                continue;
            }
            InnerText.collectRows(element, rows);
        }
    }

    private static void appendTableRow(Element row, Builder builder) {
        List<Element> cells = InnerText.directCells(row);
        if (cells.isEmpty()) {
            InnerText.appendChildren(row, builder, Interaction.isVisible(row), false);
        } else {
            InnerText.appendCells(cells, builder);
        }
    }

    private static List<Element> directCells(Element parent) {
        ArrayList<Element> cells = new ArrayList<Element>();
        for (Node child : parent.childNodes) {
            Element element;
            if (!(child instanceof Element) || !InnerText.isRendered(element = (Element)child)) continue;
            String tag = InnerText.normalizedTag(element);
            String display = InnerText.effectiveDisplay(element);
            if (!"TD".equals(tag) && !"TH".equals(tag) && !"table-cell".equals(display)) continue;
            cells.add(element);
        }
        return cells;
    }

    private static void appendCells(List<Element> cells, Builder builder) {
        for (int i = 0; i < cells.size(); ++i) {
            if (i > 0) {
                builder.tab();
            }
            Element cell = cells.get(i);
            Builder inner = new Builder();
            InnerText.appendChildren(cell, inner, Interaction.isVisible(cell), false);
            builder.literal(inner.finish());
        }
    }

    private static boolean hasRenderedBox(Element element) {
        if (!element.isConnected()) {
            return false;
        }
        Element current = element;
        while (current != null) {
            if (current.hasAttribute("hidden")) {
                return false;
            }
            if ("none".equals(InnerText.effectiveDisplay(current))) {
                return false;
            }
            if (current != element && REPLACED_CONTENT_TAGS.contains(InnerText.normalizedTag(current))) {
                return false;
            }
            current = current.parentElement;
        }
        return true;
    }

    private static boolean isRendered(Element element) {
        return element != null && !element.hasAttribute("hidden") && !"none".equals(InnerText.effectiveDisplay(element));
    }

    private static boolean isBlockLevel(String display) {
        if (display == null) {
            return true;
        }
        return switch (display) {
            case "inline", "inline-block", "inline-flex", "inline-grid", "inline-table", "contents", "none" -> false;
            default -> true;
        };
    }

    private static boolean isIndependentInline(String display) {
        return "inline-block".equals(display) || "inline-flex".equals(display) || "inline-grid".equals(display) || "inline-table".equals(display);
    }

    private static boolean isFlexOrGrid(String display) {
        return Layout.isFlexDisplay(display) || Layout.isGridDisplay(display);
    }

    private static boolean isOutOfFlow(Element element) {
        String position = element.getComputedStyle().position;
        return "absolute".equals(position) || "fixed".equals(position);
    }

    private static boolean isTableContainer(String tag, String display) {
        return "TABLE".equals(tag) || "table".equals(display) || "inline-table".equals(display);
    }

    private static String effectiveDisplay(Element element) {
        String tag;
        String declared = InnerText.declaredDisplay(element);
        if (declared != null) {
            String normalized = declared.trim().toLowerCase(Locale.ROOT);
            if (normalized.equals("table") || normalized.equals("inline-table") || normalized.equals("table-row") || normalized.equals("table-cell") || normalized.equals("table-caption") || normalized.equals("contents")) {
                return normalized;
            }
            String computed = element.getComputedStyle().display;
            return computed == null ? "block" : computed.trim().toLowerCase(Locale.ROOT);
        }
        return switch (tag = InnerText.normalizedTag(element)) {
            case "TABLE" -> "table";
            case "TR" -> "table-row";
            case "TD", "TH" -> "table-cell";
            case "CAPTION" -> "table-caption";
            default -> InnerText.computedDisplay(element);
        };
    }

    private static String declaredDisplay(Element element) {
        CSS.Declaration declaredCss = element.cssCache.get("display");
        String declared = declaredCss == null ? null : declaredCss.value();
        String styleAttribute = element.getAttribute("style");
        if (styleAttribute != null && styleAttribute.toLowerCase(Locale.ROOT).contains("display")) {
            declared = element.getInlineStylePropertyValue("display");
        }
        return declared;
    }

    private static String computedDisplay(Element element) {
        Style style = element.getComputedStyle();
        return style.display == null ? "block" : style.display.trim().toLowerCase(Locale.ROOT);
    }

    private static String whiteSpaceOf(Element element) {
        String value = element.getComputedStyle().whiteSpace;
        if ((value == null || value.equals("normal")) && "PRE".equals(InnerText.normalizedTag(element))) {
            return "pre";
        }
        return value == null ? "normal" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static String textTransformOf(Element element) {
        String value = element.getComputedStyle().textTransform;
        return value == null ? "none" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static String languageOf(Element element) {
        Element current = element;
        while (current != null) {
            String language = current.getAttribute("lang");
            if (language != null && !language.isBlank()) {
                return language;
            }
            current = current.parentElement;
        }
        return "";
    }

    private static String normalizedTag(Element element) {
        return element.tagName == null ? "" : element.tagName.trim().toUpperCase(Locale.ROOT);
    }

    private static String normalizeLineEndings(String value) {
        return value.replace("\r\n", "\n").replace('\r', '\n');
    }

    private static final class Builder {
        private final StringBuilder output = new StringBuilder();
        private boolean pendingSpace;
        private boolean preserveNextLeadingSpace;
        private boolean lineBoundary = true;
        private int requiredBreaks;

        private Builder() {
        }

        void text(String raw, String whiteSpace, String transform, String language) {
            if (raw == null || raw.isEmpty()) {
                return;
            }
            String value = TextTransform.apply(raw, transform, language);
            switch (whiteSpace) {
                case "pre": 
                case "pre-wrap": 
                case "break-spaces": {
                    this.appendPreserved(value);
                    break;
                }
                case "pre-line": {
                    this.appendCollapsed(value, true);
                    break;
                }
                default: {
                    this.appendCollapsed(value, false);
                }
            }
        }

        void literal(String value) {
            if (value == null || value.isEmpty()) {
                return;
            }
            this.flushRequiredBreaks();
            this.flushPendingSpace(false);
            this.output.append(value);
            this.lineBoundary = value.charAt(value.length() - 1) == '\n' || value.charAt(value.length() - 1) == '\t';
            this.preserveNextLeadingSpace = false;
        }

        void hardBreak() {
            this.pendingSpace = false;
            this.flushRequiredBreaks();
            this.output.append('\n');
            this.lineBoundary = true;
            this.preserveNextLeadingSpace = false;
        }

        void requiredBreak(int count) {
            this.pendingSpace = false;
            this.requiredBreaks = Math.max(this.requiredBreaks, Math.max(0, count));
            this.preserveNextLeadingSpace = false;
        }

        void tab() {
            this.pendingSpace = false;
            this.flushRequiredBreaks();
            this.output.append('\t');
            this.lineBoundary = true;
            this.preserveNextLeadingSpace = false;
        }

        void atomicBoundary() {
            this.flushRequiredBreaks();
            this.flushPendingSpace(true);
            this.preserveNextLeadingSpace = true;
        }

        String finish() {
            this.pendingSpace = false;
            this.requiredBreaks = 0;
            return this.output.toString();
        }

        private void appendCollapsed(String value, boolean preserveNewlines) {
            String normalized = InnerText.normalizeLineEndings(value);
            for (int i = 0; i < normalized.length(); ++i) {
                char current = normalized.charAt(i);
                if (current == '\n' && preserveNewlines) {
                    this.hardBreak();
                    continue;
                }
                if (Builder.isCollapsibleSpace(current) || current == '\n') {
                    this.pendingSpace = true;
                    continue;
                }
                this.appendCharacter(current);
            }
        }

        private void appendPreserved(String value) {
            String normalized = InnerText.normalizeLineEndings(value);
            for (int i = 0; i < normalized.length(); ++i) {
                char current = normalized.charAt(i);
                if (current == '\n') {
                    this.flushRequiredBreaks();
                    this.flushPendingSpace(false);
                    this.output.append('\n');
                    this.lineBoundary = true;
                    this.preserveNextLeadingSpace = false;
                    continue;
                }
                this.flushRequiredBreaks();
                this.flushPendingSpace(false);
                this.output.append(current);
                this.lineBoundary = false;
                this.preserveNextLeadingSpace = false;
            }
        }

        private void appendCharacter(char value) {
            this.flushRequiredBreaks();
            this.flushPendingSpace(false);
            this.output.append(value);
            this.lineBoundary = false;
            this.preserveNextLeadingSpace = false;
        }

        private void flushPendingSpace(boolean force) {
            if (!this.pendingSpace) {
                return;
            }
            if (force || this.preserveNextLeadingSpace || !this.lineBoundary) {
                this.output.append(' ');
                this.lineBoundary = false;
            }
            this.pendingSpace = false;
        }

        private void flushRequiredBreaks() {
            if (this.requiredBreaks <= 0) {
                return;
            }
            if (this.output.length() > 0) {
                this.output.append("\n".repeat(this.requiredBreaks));
            }
            this.requiredBreaks = 0;
            this.lineBoundary = true;
            this.preserveNextLeadingSpace = false;
        }

        private static boolean isCollapsibleSpace(char value) {
            return value == ' ' || value == '\t' || value == '\f' || value == '\u000b';
        }
    }
}

