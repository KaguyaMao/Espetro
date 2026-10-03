/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.behavior.richtext;

import com.sighs.apricityui.behavior.SelectionUnits;
import com.sighs.apricityui.dom.TextNode;
import com.sighs.apricityui.element.AbstractText;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.init.Node;
import com.sighs.apricityui.util.HtmlSerializer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public final class RichTextRange {
    private final Element unit;
    private final int startNorm;
    private final int endNorm;
    private final RichTextEndpoint start;
    private final RichTextEndpoint end;

    private RichTextRange(Element unit, int startNorm, int endNorm, RichTextEndpoint start, RichTextEndpoint end) {
        this.unit = unit;
        this.startNorm = startNorm;
        this.endNorm = endNorm;
        this.start = start;
        this.end = end;
    }

    public static RichTextRange fromUnitOffsets(Element unit, int startNormOffset, int endNormOffset) {
        if (unit == null) {
            return null;
        }
        int min = Math.min(startNormOffset, endNormOffset);
        int max = Math.max(startNormOffset, endNormOffset);
        return new RichTextRange(unit, min, max, RichTextRange.fromUnitOffset(unit, min), RichTextRange.fromUnitOffset(unit, max));
    }

    public static RichTextRange collapse(Element unit, Node container, int offset) {
        if (unit == null || container == null) {
            return null;
        }
        RichTextEndpoint endpoint = new RichTextEndpoint(container, offset);
        int norm = RichTextRange.toUnitOffset(unit, endpoint);
        return new RichTextRange(unit, norm, norm, endpoint, endpoint);
    }

    public static RichTextRange selectNodeContents(Element unit) {
        if (unit == null) {
            return null;
        }
        String flattened = SelectionUnits.flattenedSelectableText(unit);
        int length = flattened == null ? 0 : flattened.length();
        return RichTextRange.fromUnitOffsets(unit, 0, length);
    }

    public Element getUnit() {
        return this.unit;
    }

    public boolean collapsed() {
        return this.startNorm == this.endNorm;
    }

    public RichTextEndpoint start() {
        return this.start;
    }

    public RichTextEndpoint end() {
        return this.end;
    }

    public int[] toUnitOffsets() {
        return new int[]{this.startNorm, this.endNorm};
    }

    public String toString() {
        if (this.unit == null || this.collapsed()) {
            return "";
        }
        return SelectionUnits.rawRangeForNormalizedRange(this.unit, this.startNorm, this.endNorm);
    }

    public String toHtml() {
        if (this.unit == null || this.collapsed()) {
            return "";
        }
        int startRaw = RichTextRange.rawOffsetForNorm(this.unit, this.startNorm);
        int endRaw = RichTextRange.rawOffsetForNorm(this.unit, this.endNorm);
        StringBuilder out = new StringBuilder();
        RichTextRange.clipChildrenToHtml(this.unit, startRaw, endRaw, out, new int[]{0});
        return out.toString();
    }

    private static void clipChildrenToHtml(Element current, int startRaw, int endRaw, StringBuilder out, int[] cursor) {
        for (Node child : current.getChildNodes()) {
            int start;
            if (child instanceof TextNode) {
                int clipEnd;
                int end;
                TextNode textNode = (TextNode)child;
                start = cursor[0];
                cursor[0] = end = start + textNode.getTextContent().length();
                int clipStart = Math.max(start, startRaw);
                if (clipStart >= (clipEnd = Math.min(end, endRaw))) continue;
                out.append(HtmlSerializer.escapeHtml(textNode.getTextContent().substring(clipStart - start, clipEnd - start)));
                continue;
            }
            if (!(child instanceof Element)) continue;
            Element childElement = (Element)child;
            if (SelectionUnits.isLineBreak(childElement)) {
                start = cursor[0];
                cursor[0] = start + 1;
                if (start < startRaw || start >= endRaw) continue;
                out.append("<br>");
                continue;
            }
            if (SelectionUnits.isAtomicObject(childElement)) {
                start = cursor[0];
                cursor[0] = start + 1;
                if (start < startRaw || start >= endRaw) continue;
                out.append(RichTextRange.openTagForClip(childElement));
                continue;
            }
            int elementStart = cursor[0];
            StringBuilder inner = new StringBuilder();
            RichTextRange.clipChildrenToHtml(childElement, startRaw, endRaw, inner, cursor);
            int elementEnd = cursor[0];
            if (elementStart >= endRaw || elementEnd <= startRaw || inner.length() <= 0) continue;
            out.append(RichTextRange.openTagForClip(childElement)).append((CharSequence)inner).append("</").append(childElement.tagName.toLowerCase(Locale.ROOT)).append(">");
        }
    }

    private static String openTagForClip(Element element) {
        String href;
        StringBuilder sb = new StringBuilder("<").append(element.tagName.toLowerCase(Locale.ROOT));
        HashMap<String, String> attributes = element.getAttributes();
        String style = (String)attributes.get("style");
        if (style != null && !style.isEmpty()) {
            sb.append(" style=\"").append(HtmlSerializer.escapeHtml(style)).append("\"");
        }
        if ("A".equals(element.tagName) && (href = (String)attributes.get("href")) != null && !href.isEmpty()) {
            sb.append(" href=\"").append(HtmlSerializer.escapeHtml(href)).append("\"");
        }
        if ("IMG".equals(element.tagName)) {
            String alt;
            String src = (String)attributes.get("src");
            if (src != null && !src.isEmpty()) {
                sb.append(" src=\"").append(HtmlSerializer.escapeHtml(src)).append("\"");
            }
            if ((alt = (String)attributes.get("alt")) != null && !alt.isEmpty()) {
                sb.append(" alt=\"").append(HtmlSerializer.escapeHtml(alt)).append("\"");
            }
        }
        return sb.append(">").toString();
    }

    public void deleteContents() {
        this.deleteContents(true);
    }

    public void deleteContents(boolean removeElements) {
        int endRaw;
        if (this.unit == null || this.collapsed()) {
            return;
        }
        int startRaw = RichTextRange.rawOffsetForNorm(this.unit, this.startNorm);
        boolean changed = RichTextRange.deleteRangeFromChildren(this.unit, startRaw, endRaw = RichTextRange.rawOffsetForNorm(this.unit, this.endNorm), new int[]{0}, removeElements);
        if (changed) {
            RichTextRange.normalize(this.unit);
        }
    }

    private static boolean deleteRangeFromChildren(Element current, int startRaw, int endRaw, int[] cursor, boolean removeElements) {
        boolean changed = false;
        for (Node child : new ArrayList<Node>(current.getChildNodes())) {
            Element childElement;
            int start;
            if (child instanceof TextNode) {
                int clipEnd;
                int end;
                TextNode textNode = (TextNode)child;
                start = cursor[0];
                cursor[0] = end = start + textNode.getTextContent().length();
                int clipStart = Math.max(start, startRaw);
                if (clipStart >= (clipEnd = Math.min(end, endRaw))) continue;
                textNode.replaceData(clipStart - start, clipEnd - clipStart, "");
                changed = true;
                continue;
            }
            if (!(child instanceof Element) || (childElement = (Element)child) instanceof AbstractText) continue;
            if (SelectionUnits.isLineBreak(childElement)) {
                start = cursor[0];
                cursor[0] = start + 1;
                if (start < startRaw || start >= endRaw) continue;
                current.removeChild(childElement);
                changed = true;
                continue;
            }
            if (SelectionUnits.isAtomicObject(childElement)) {
                start = cursor[0];
                cursor[0] = start + 1;
                if (start < startRaw || start >= endRaw) continue;
                current.removeChild(childElement);
                changed = true;
                continue;
            }
            if (SelectionUnits.isSelectionUnit(childElement)) continue;
            int elementStart = cursor[0];
            changed |= RichTextRange.deleteRangeFromChildren(childElement, startRaw, endRaw, cursor, removeElements);
            int elementEnd = cursor[0];
            if (!removeElements || elementStart >= elementEnd || elementStart < startRaw || elementEnd > endRaw) continue;
            current.removeChild(childElement);
            changed = true;
        }
        return changed;
    }

    public Node insertNode(Node node) {
        if (this.unit == null || node == null) {
            return null;
        }
        RichTextEndpoint point = this.start;
        Node node2 = point.container();
        if (node2 instanceof TextNode) {
            TextNode textNode = (TextNode)node2;
            int offset = Math.max(0, Math.min(point.offset(), textNode.getTextContent().length()));
            if (offset == 0) {
                textNode.getParentNode().insertBefore(node, textNode);
            } else if (offset >= textNode.getTextContent().length()) {
                textNode.getParentNode().insertBefore(node, textNode.getNextSibling());
            } else {
                TextNode tail = textNode.splitText(offset);
                textNode.getParentNode().insertBefore(node, tail);
            }
            return node;
        }
        Node offset = point.container();
        if (offset instanceof Element) {
            Element container = (Element)offset;
            int index = Math.max(0, Math.min(point.offset(), container.getChildNodes().size()));
            if (index >= container.getChildNodes().size()) {
                container.appendChild(node);
            } else {
                container.insertBefore(node, container.getChildNodes().get(index));
            }
            return node;
        }
        return null;
    }

    public static RichTextEndpoint fromUnitOffset(Element unit, int normOffset) {
        TextNode textNode;
        int i;
        if (unit == null) {
            return null;
        }
        String flattened = SelectionUnits.flattenedSelectableText(unit);
        int length = flattened == null ? 0 : flattened.length();
        int n = Math.max(0, Math.min(normOffset, length));
        if (length == 0) {
            return new RichTextEndpoint(unit, 0);
        }
        SelectionUnits.RawText rawText = SelectionUnits.rawTextOf(unit);
        if (rawText == null || rawText.raw() == null || rawText.raw().isEmpty() || rawText.rawStart() == null || rawText.rawStart().length == 0) {
            return new RichTextEndpoint(unit, n);
        }
        int[] starts = rawText.rawStart();
        int[] ends = rawText.rawEnd();
        int rawOffset = n >= starts.length ? ends[ends.length - 1] : starts[n];
        ArrayList<Element> objects = new ArrayList<Element>();
        ArrayList<Integer> objectPositions = new ArrayList<Integer>();
        RichTextRange.collectObjects(unit, unit, objects, objectPositions, new int[]{0});
        for (int i2 = 0; i2 < objects.size(); ++i2) {
            int n2;
            int pos = (Integer)objectPositions.get(i2);
            if (rawOffset == pos) {
                int n3;
                Node parent = ((Element)objects.get(i2)).getParentNode();
                if (parent instanceof Element) {
                    Element parentElement = (Element)parent;
                    n3 = parentElement.getChildNodes().indexOf(objects.get(i2));
                } else {
                    n3 = 0;
                }
                int index = n3;
                return new RichTextEndpoint(parent, Math.max(0, index));
            }
            if (rawOffset != pos + 1) continue;
            Node parent = ((Element)objects.get(i2)).getParentNode();
            if (parent instanceof Element) {
                Element parentElement = (Element)parent;
                n2 = parentElement.getChildNodes().indexOf(objects.get(i2)) + 1;
            } else {
                n2 = 1;
            }
            int index = n2;
            return new RichTextEndpoint(parent, index);
        }
        ArrayList<TextNode> textNodes = new ArrayList<TextNode>();
        ArrayList<int[]> rawSpans = new ArrayList<int[]>();
        RichTextRange.collectRawSegments(unit, unit, textNodes, rawSpans, new int[]{0});
        for (i = 0; i < textNodes.size(); ++i) {
            textNode = (TextNode)textNodes.get(i);
            int[] span = (int[])rawSpans.get(i);
            if (rawOffset < span[0] || rawOffset >= span[1]) continue;
            return new RichTextEndpoint(textNode, rawOffset - span[0]);
        }
        if (!textNodes.isEmpty() && rawOffset > 0) {
            for (i = 0; i < textNodes.size(); ++i) {
                if (((int[])rawSpans.get(i))[1] != rawOffset) continue;
                textNode = (TextNode)textNodes.get(i);
                return new RichTextEndpoint(textNode, textNode.getTextContent().length());
            }
        }
        return new RichTextEndpoint(unit, n);
    }

    private static void collectObjects(Element unit, Element current, List<Element> outObjects, List<Integer> outPositions, int[] rawCursor) {
        for (Node child : current.getRenderChildNodes()) {
            Element childElement;
            if (child instanceof TextNode) {
                TextNode textNode = (TextNode)child;
                rawCursor[0] = rawCursor[0] + textNode.getTextContent().length();
                continue;
            }
            if (!(child instanceof Element) || (childElement = (Element)child) instanceof AbstractText) continue;
            if (SelectionUnits.isLineBreak(childElement)) {
                rawCursor[0] = rawCursor[0] + 1;
                continue;
            }
            if (SelectionUnits.isAtomicObject(childElement)) {
                outObjects.add(childElement);
                outPositions.add(rawCursor[0]);
                rawCursor[0] = rawCursor[0] + 1;
                continue;
            }
            if (SelectionUnits.isSelectionUnit(childElement)) continue;
            RichTextRange.collectObjects(unit, childElement, outObjects, outPositions, rawCursor);
        }
    }

    public static int toUnitOffset(Element unit, RichTextEndpoint endpoint) {
        if (unit == null || endpoint == null) {
            return 0;
        }
        Node container = endpoint.container();
        if (container instanceof TextNode) {
            TextNode textNode = (TextNode)container;
            ArrayList<TextNode> textNodes = new ArrayList<TextNode>();
            ArrayList<int[]> rawSpans = new ArrayList<int[]>();
            RichTextRange.collectRawSegments(unit, unit, textNodes, rawSpans, new int[]{0});
            for (int i = 0; i < textNodes.size(); ++i) {
                if (textNodes.get(i) != textNode) continue;
                int[] span = (int[])rawSpans.get(i);
                int rawOffset = span[0] + Math.max(0, Math.min(endpoint.offset(), textNode.getTextContent().length()));
                return RichTextRange.normalizedOffsetForRaw(unit, rawOffset);
            }
            return 0;
        }
        if (container == unit) {
            int length = unit.getChildNodes().size();
            int index = Math.max(0, Math.min(endpoint.offset(), length));
            if (index >= length) {
                String flattened = SelectionUnits.flattenedSelectableText(unit);
                return flattened == null ? 0 : flattened.length();
            }
            Node child = unit.getChildNodes().get(index);
            return SelectionUnits.baseOffsetOfDescendant(unit, child);
        }
        if (container instanceof Element) {
            Element element = (Element)container;
            return SelectionUnits.baseOffsetOfDescendant(unit, element);
        }
        return 0;
    }

    private static int rawOffsetForNorm(Element unit, int normOffset) {
        SelectionUnits.RawText rawText = SelectionUnits.rawTextOf(unit);
        if (rawText == null || rawText.rawStart() == null || rawText.rawStart().length == 0) {
            return 0;
        }
        int[] starts = rawText.rawStart();
        int[] ends = rawText.rawEnd();
        int length = rawText.normalized() == null ? 0 : rawText.normalized().length();
        int n = Math.max(0, Math.min(normOffset, length));
        if (n >= starts.length) {
            return ends[ends.length - 1];
        }
        return starts[n];
    }

    private static int normalizedOffsetForRaw(Element unit, int rawOffset) {
        SelectionUnits.RawText rawText = SelectionUnits.rawTextOf(unit);
        if (rawText == null || rawText.rawStart() == null || rawText.rawStart().length == 0) {
            return 0;
        }
        int[] starts = rawText.rawStart();
        int[] ends = rawText.rawEnd();
        if (rawOffset <= starts[0]) {
            return 0;
        }
        if (rawOffset >= ends[ends.length - 1]) {
            return ends.length;
        }
        int low = 0;
        int high = starts.length - 1;
        while (low < high) {
            int mid = low + high + 1 >>> 1;
            if (starts[mid] <= rawOffset) {
                low = mid;
                continue;
            }
            high = mid - 1;
        }
        if (ends[low] <= rawOffset) {
            return Math.min(low + 1, starts.length);
        }
        return low;
    }

    static void collectRawSegments(Element unit, Element current, List<TextNode> outNodes, List<int[]> outSpans, int[] rawCursor) {
        for (Node child : current.getRenderChildNodes()) {
            Element childElement;
            if (child instanceof TextNode) {
                TextNode textNode = (TextNode)child;
                int start = rawCursor[0];
                int end = start + textNode.getTextContent().length();
                outNodes.add(textNode);
                outSpans.add(new int[]{start, end});
                rawCursor[0] = end;
                continue;
            }
            if (!(child instanceof Element) || (childElement = (Element)child) instanceof AbstractText) continue;
            if (SelectionUnits.isLineBreak(childElement)) {
                rawCursor[0] = rawCursor[0] + 1;
                continue;
            }
            if (SelectionUnits.isAtomicObject(childElement)) {
                rawCursor[0] = rawCursor[0] + 1;
                continue;
            }
            if (SelectionUnits.isSelectionUnit(childElement)) continue;
            RichTextRange.collectRawSegments(unit, childElement, outNodes, outSpans, rawCursor);
        }
    }

    public static void normalize(Element root) {
        ArrayList<Element> elements = new ArrayList<Element>();
        RichTextRange.collectElements(root, elements);
        for (Element element : elements) {
            for (Node child : new ArrayList<Node>(element.getChildNodes())) {
                TextNode textNode;
                if (!(child instanceof TextNode) || !(textNode = (TextNode)child).getTextContent().isEmpty()) continue;
                element.removeChild(textNode);
            }
            Node previous = null;
            for (Node child : new ArrayList<Node>(element.getChildNodes())) {
                TextNode previousText;
                if (!(child instanceof TextNode)) {
                    previous = null;
                    continue;
                }
                TextNode textNode = (TextNode)child;
                if (previous instanceof TextNode && !(previousText = (TextNode)previous).getTextContent().isEmpty()) {
                    previousText.setTextContent(previousText.getTextContent() + textNode.getTextContent());
                    element.removeChild(textNode);
                    continue;
                }
                previous = child;
            }
        }
    }

    private static void collectElements(Element current, List<Element> out) {
        out.add(current);
        for (Node child : current.getChildNodes()) {
            if (!(child instanceof Element)) continue;
            Element childElement = (Element)child;
            RichTextRange.collectElements(childElement, out);
        }
    }

    public record RichTextEndpoint(Node container, int offset) {
    }
}

