/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.behavior.richtext;

import com.sighs.apricityui.behavior.SelectionUnits;
import com.sighs.apricityui.behavior.richtext.RichTextOperation;
import com.sighs.apricityui.behavior.richtext.RichTextRange;
import com.sighs.apricityui.behavior.richtext.RichTextSelection;
import com.sighs.apricityui.dom.TextNode;
import com.sighs.apricityui.element.RichText;
import com.sighs.apricityui.event.Event;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.init.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class RichTextEditing {
    private static final Set<String> ALLOWED_TAGS = Set.of("DIV", "SPAN", "B", "STRONG", "I", "EM", "U", "S", "BR", "A", "IMG", "P", "H1", "H2", "H3", "H4", "H5", "H6", "BLOCKQUOTE", "UL", "OL", "LI");
    private static final Set<String> DANGEROUS_TAGS = Set.of("SCRIPT", "STYLE", "LINK", "IFRAME", "OBJECT", "EMBED");

    private RichTextEditing() {
    }

    public static boolean insertText(RichText element, String text) {
        if (element == null || text == null || text.isEmpty()) {
            return false;
        }
        return RichTextEditing.transform(element, "insertText", text, (rich, selection) -> {
            Element unit = selection.getAnchorUnit();
            int anchor = selection.getAnchorOffset();
            ArrayList<RichTextOperation> ops = new ArrayList<RichTextOperation>();
            int at = RichTextEditing.replaceSelectionOps(unit, selection, anchor, ops);
            ops.add(RichTextOperation.insertText(unit, at, text, at, at + text.length()));
            return ops;
        });
    }

    public static boolean deleteBackward(RichText element) {
        return RichTextEditing.transform(element, "deleteContentBackward", null, (rich, selection) -> {
            Element unit = selection.getAnchorUnit();
            int offset = selection.getAnchorOffset();
            if (selection.collapsed()) {
                if (unit != null && unit != rich && offset <= 0) {
                    Element previous = RichTextEditing.previousSiblingBlock(unit);
                    if (previous == null || !previous.tagName.equals(unit.tagName)) {
                        return List.of();
                    }
                    int mergeOffset = RichTextEditing.flattenedLength(previous);
                    return List.of(RichTextOperation.mergeBackward(unit, mergeOffset, offset, mergeOffset));
                }
                if (offset <= 0) {
                    return List.of();
                }
                return List.of(RichTextEditing.deleteHtmlOp(unit, offset - 1, offset, offset, offset - 1));
            }
            return RichTextEditing.deleteSelectionOps(rich, selection);
        });
    }

    public static boolean deleteForward(RichText element) {
        return RichTextEditing.transform(element, "deleteContentForward", null, (rich, selection) -> {
            Element unit = selection.getAnchorUnit();
            int offset = selection.getAnchorOffset();
            if (selection.collapsed()) {
                int length = RichTextEditing.flattenedLength(unit);
                if (unit != null && unit != rich && offset >= length) {
                    Element next = RichTextEditing.nextSiblingBlock(unit);
                    if (next == null || !next.tagName.equals(unit.tagName)) {
                        return List.of();
                    }
                    int nextLength = RichTextEditing.flattenedLength(next);
                    return List.of(RichTextOperation.mergeForward(unit, nextLength, next.tagName, offset, offset));
                }
                if (offset >= length) {
                    return List.of();
                }
                return List.of(RichTextEditing.deleteHtmlOp(unit, offset, offset + 1, offset, offset));
            }
            return RichTextEditing.deleteSelectionOps(rich, selection);
        });
    }

    public static boolean insertParagraph(RichText element) {
        return RichTextEditing.transform(element, "insertParagraph", null, (rich, selection) -> {
            Element unit = selection.getAnchorUnit();
            int anchor = selection.getAnchorOffset();
            ArrayList<RichTextOperation> ops = new ArrayList<RichTextOperation>();
            int at = RichTextEditing.replaceSelectionOps(unit, selection, anchor, ops);
            if (unit != null && unit != rich) {
                ops.add(RichTextOperation.splitBlock(unit, at, unit.tagName, at, 0));
            } else {
                ops.add(RichTextOperation.insertBr(unit != null ? unit : rich, at, at, at + 1));
            }
            return ops;
        });
    }

    public static boolean deleteSelection(RichText element) {
        return RichTextEditing.transform(element, "deleteByCut", null, (rich, selection) -> RichTextEditing.deleteSelectionOps(rich, selection));
    }

    public static boolean moveObject(RichText element, Element object, int targetOffset) {
        if (element == null || object == null || element.document == null || !element.canEditText()) {
            return false;
        }
        if (object.getParentNode() == null) {
            return false;
        }
        RichTextSelection selection = element.document.getRichTextSelection();
        if (selection == null || !selection.hasAnchor()) {
            return false;
        }
        Element objectUnit = RichTextEditing.blockOf(element, object);
        if (selection.getAnchorUnit() != objectUnit) {
            return false;
        }
        int objectStart = SelectionUnits.baseOffsetOfDescendant(objectUnit, object);
        if (objectStart == targetOffset) {
            return false;
        }
        int insertAt = targetOffset > objectStart ? targetOffset - 1 : targetOffset;
        int before = selection.getAnchorOffset();
        String html = RichTextRange.fromUnitOffsets(objectUnit, objectStart, objectStart + 1).toHtml();
        if (html.isEmpty()) {
            return false;
        }
        ArrayList<RichTextOperation> operations = new ArrayList<RichTextOperation>();
        operations.add(RichTextOperation.deleteHtml(objectUnit, objectStart, objectStart + 1, html, before, objectStart));
        operations.add(RichTextOperation.insertHtml(objectUnit, insertAt, html, insertAt, insertAt + 1));
        return RichTextEditing.transformOperations(element, "moveObject", html, operations);
    }

    public static boolean pasteText(RichText element, String text) {
        if (element == null || text == null || text.isEmpty()) {
            return false;
        }
        String normalized = text.replace("\r\n", "\n").replace('\r', '\n');
        if (normalized.isEmpty()) {
            return false;
        }
        return RichTextEditing.transform(element, "insertFromPaste", normalized, (rich, selection) -> {
            Element unit = selection.getAnchorUnit();
            int anchor = selection.getAnchorOffset();
            ArrayList<RichTextOperation> ops = new ArrayList<RichTextOperation>();
            int at = RichTextEditing.replaceSelectionOps(unit, selection, anchor, ops);
            ops.add(RichTextOperation.insertText(unit, at, normalized, at, at + normalized.length()));
            return ops;
        });
    }

    public static boolean pasteHtml(RichText element, String html) {
        if (element == null || html == null) {
            return false;
        }
        String insertHtml = RichTextEditing.protectWhitespace(RichTextEditing.sanitizeHtml(element, RichTextEditing.protectWhitespace(html)));
        if (insertHtml.isEmpty()) {
            return false;
        }
        int insertedLength = RichTextEditing.htmlTextLength(element, insertHtml);
        if (insertedLength <= 0) {
            return false;
        }
        return RichTextEditing.transform(element, "insertFromPaste", insertHtml, (rich, selection) -> {
            Element unit = selection.getAnchorUnit();
            int anchor = selection.getAnchorOffset();
            ArrayList<RichTextOperation> ops = new ArrayList<RichTextOperation>();
            int at = RichTextEditing.replaceSelectionOps(unit, selection, anchor, ops);
            ops.add(RichTextOperation.insertHtml(unit, at, insertHtml, at, at + insertedLength));
            return ops;
        });
    }

    public static boolean undo(RichText element) {
        if (element == null || !element.canEditText()) {
            return false;
        }
        if (!RichTextEditing.dispatchBeforeInput(element, "historyUndo", null)) {
            return false;
        }
        if (!element.undoInternal()) {
            return false;
        }
        RichTextEditing.dispatchInput(element, "historyUndo", null);
        return true;
    }

    public static boolean redo(RichText element) {
        if (element == null || !element.canEditText()) {
            return false;
        }
        if (!RichTextEditing.dispatchBeforeInput(element, "historyRedo", null)) {
            return false;
        }
        if (!element.redoInternal()) {
            return false;
        }
        RichTextEditing.dispatchInput(element, "historyRedo", null);
        return true;
    }

    private static int replaceSelectionOps(Element unit, RichTextSelection selection, int anchor, List<RichTextOperation> ops) {
        if (!selection.collapsed()) {
            int[] range = RichTextEditing.selectionRangeIn(selection, unit);
            if (range == null) {
                return anchor;
            }
            ops.add(RichTextEditing.deleteHtmlOp(unit, range[0], range[1], anchor, range[0]));
            return range[0];
        }
        return anchor;
    }

    private static int[] selectionRangeIn(RichTextSelection selection, Element unit) {
        int[] range = selection.localRangeForUnit(unit);
        if (range != null) {
            return range;
        }
        return new int[]{0, RichTextEditing.flattenedLength(unit)};
    }

    private static List<RichTextOperation> deleteSelectionOps(RichText rich, RichTextSelection selection) {
        if (selection.collapsed()) {
            return List.of();
        }
        Element anchor = selection.getAnchorUnit();
        Element end = selection.getEndUnit();
        if (anchor == null || end == null) {
            return List.of();
        }
        if (anchor == end) {
            int[] range = RichTextEditing.selectionRangeIn(selection, anchor);
            return List.of(RichTextEditing.deleteHtmlOp(anchor, range[0], range[1], selection.getAnchorOffset(), range[0]));
        }
        List<Element> units = RichTextEditing.richTextUnits(rich);
        int anchorIndex = units.indexOf(anchor);
        int endIndex = units.indexOf(end);
        if (anchorIndex < 0 || endIndex < 0) {
            return List.of();
        }
        int minIndex = Math.min(anchorIndex, endIndex);
        int maxIndex = Math.max(anchorIndex, endIndex);
        boolean reversed = anchorIndex > endIndex || anchorIndex == endIndex && selection.getAnchorOffset() > selection.getEndOffset();
        ArrayList<RichTextOperation> ops = new ArrayList<RichTextOperation>();
        for (int i = minIndex; i <= maxIndex; ++i) {
            int endOffset;
            int start;
            Element unit = units.get(i);
            int length = RichTextEditing.flattenedLength(unit);
            if (i == minIndex) {
                boundary = reversed ? selection.getEndOffset() : selection.getAnchorOffset();
                start = Math.min(boundary, length);
                endOffset = length;
            } else if (i == maxIndex) {
                boundary = reversed ? selection.getAnchorOffset() : selection.getEndOffset();
                start = 0;
                endOffset = Math.max(0, Math.min(boundary, length));
            } else {
                start = 0;
                endOffset = length;
            }
            if (start >= endOffset) continue;
            ops.add(RichTextEditing.deleteHtmlOp(unit, start, endOffset, selection.getAnchorOffset(), start));
        }
        return ops;
    }

    private static RichTextOperation deleteHtmlOp(Element unit, int start, int end, int before, int after) {
        RichTextRange range = RichTextRange.fromUnitOffsets(unit, start, end);
        String html = range == null ? "" : range.toHtml();
        return RichTextOperation.deleteHtml(unit, start, end, html, before, after);
    }

    private static List<Element> richTextUnits(RichText rich) {
        ArrayList<Element> result = new ArrayList<Element>();
        if (SelectionUnits.isSelectionUnit(rich)) {
            result.add(rich);
        }
        RichTextEditing.collectUnits(rich, result);
        return result;
    }

    private static void collectUnits(Element current, List<Element> out) {
        for (Node child : current.getChildNodes()) {
            if (!(child instanceof Element)) continue;
            Element childElement = (Element)child;
            if (SelectionUnits.isSelectionUnit(childElement)) {
                out.add(childElement);
                continue;
            }
            RichTextEditing.collectUnits(childElement, out);
        }
    }

    private static Element previousSiblingBlock(Element block) {
        Element element;
        Node previous = block.getPreviousSibling();
        return previous instanceof Element ? (element = (Element)previous) : null;
    }

    private static Element nextSiblingBlock(Element block) {
        Element element;
        Node next = block.getNextSibling();
        return next instanceof Element ? (element = (Element)next) : null;
    }

    private static boolean isInRichText(Element richtext, Element unit) {
        Element e = unit;
        while (e != null) {
            if (e == richtext) {
                return true;
            }
            e = e.parentElement;
        }
        return false;
    }

    private static Element blockOf(RichText rich, Element object) {
        Element e = object.parentElement;
        while (e != null && e != rich) {
            if (e != rich && SelectionUnits.isSelectionUnit(e)) {
                return e;
            }
            e = e.parentElement;
        }
        return rich;
    }

    private static int flattenedLength(Element unit) {
        String flattened = SelectionUnits.flattenedSelectableText(unit);
        return flattened == null ? 0 : flattened.length();
    }

    private static int htmlTextLength(RichText rich, String protectedHtml) {
        Element wrapper = rich.document.createHTML("<div contenteditable>" + protectedHtml + "</div>");
        if (wrapper == null) {
            return 0;
        }
        return RichTextEditing.rawCharCount(wrapper);
    }

    private static int rawCharCount(Element element) {
        int count = 0;
        for (Node child : element.getChildNodes()) {
            if (child instanceof TextNode) {
                TextNode textNode = (TextNode)child;
                count += textNode.getTextContent().length();
                continue;
            }
            if (!(child instanceof Element)) continue;
            Element childElement = (Element)child;
            if (SelectionUnits.isLineBreak(childElement) || SelectionUnits.isAtomicObject(childElement)) {
                ++count;
                continue;
            }
            count += RichTextEditing.rawCharCount(childElement);
        }
        return count;
    }

    public static void applyOperation(RichText element, RichTextOperation operation) {
        if (element == null || element.document == null || operation == null) {
            return;
        }
        RichTextSelection selection = element.document.getRichTextSelection();
        boolean customCaret = false;
        switch (operation.type()) {
            case "insertText": {
                RichTextEditing.insertNodesAt(operation.unit(), operation.start(), List.of(element.document.createTextNode(operation.text())));
                break;
            }
            case "deleteText": {
                RichTextEditing.deleteRange(operation.unit(), operation.start(), operation.start() + operation.text().length(), false);
                break;
            }
            case "insertHtml": {
                RichTextEditing.insertHtmlAt(operation.unit(), operation.start(), operation.html());
                break;
            }
            case "deleteHtml": {
                RichTextEditing.deleteRange(operation.unit(), operation.start(), operation.end(), true);
                break;
            }
            case "insertBr": {
                RichTextEditing.insertNodesAt(operation.unit(), operation.start(), List.of(element.document.createElement("BR")));
                break;
            }
            case "deleteBr": {
                RichTextEditing.deleteRange(operation.unit(), operation.start(), operation.start() + 1, true);
                break;
            }
            case "splitBlock": {
                Element newBlock = RichTextEditing.splitBlockAt(element, selection, operation);
                if (newBlock != null) {
                    selection.setCollapsed(newBlock, 0);
                }
                customCaret = true;
                break;
            }
            case "mergeBackward": {
                Element merged = RichTextEditing.mergeBackwardAt(element, selection, operation);
                if (merged != null) {
                    selection.setCollapsed(merged, operation.start());
                }
                customCaret = true;
                break;
            }
            case "mergeForward": {
                Element merged = RichTextEditing.mergeForwardAt(element, selection, operation);
                if (merged != null) {
                    selection.setCollapsed(merged, operation.start());
                }
                customCaret = true;
                break;
            }
            default: {
                throw new IllegalStateException("unknown operation type: " + operation.type());
            }
        }
        if (!customCaret && selection != null && selection.hasAnchor()) {
            Element unit = operation.unit();
            if (!RichTextEditing.isInRichText(element, unit)) {
                unit = element;
            }
            int length = RichTextEditing.flattenedLength(unit);
            selection.setCollapsed(unit, Math.max(0, Math.min(operation.cursorAfter(), length)));
        }
    }

    private static Element splitBlockAt(RichText element, RichTextSelection selection, RichTextOperation operation) {
        Node startNode;
        Element block;
        Element element2 = block = selection != null && selection.getAnchorUnit() != null ? selection.getAnchorUnit() : operation.unit();
        if (block == null) {
            return null;
        }
        int offset = Math.max(0, Math.min(operation.start(), RichTextEditing.flattenedLength(block)));
        RichTextRange.RichTextEndpoint point = RichTextRange.fromUnitOffset(block, offset);
        Node node = point.container();
        if (node instanceof TextNode) {
            TextNode textNode = (TextNode)node;
            int nodeOffset = Math.max(0, Math.min(point.offset(), textNode.getTextContent().length()));
            startNode = nodeOffset >= textNode.getTextContent().length() ? textNode.getNextSibling() : (nodeOffset <= 0 ? textNode : textNode.splitText(nodeOffset));
        } else {
            Node nodeOffset = point.container();
            if (nodeOffset instanceof Element) {
                Element containerElement = (Element)nodeOffset;
                int index = Math.max(0, Math.min(point.offset(), containerElement.getChildNodes().size()));
                startNode = index < containerElement.getChildNodes().size() ? containerElement.getChildNodes().get(index) : null;
            } else {
                startNode = null;
            }
        }
        String tag = operation.html() != null && !operation.html().isEmpty() ? operation.html() : block.tagName;
        Element newBlock = element.document.createElement(tag);
        Node node2 = startNode;
        while (node2 != null) {
            Node next = node2.getNextSibling();
            newBlock.appendChild(node2);
            node2 = next;
        }
        block.getParentNode().insertBefore(newBlock, block.getNextSibling());
        return newBlock;
    }

    private static Element mergeBackwardAt(RichText element, RichTextSelection selection, RichTextOperation operation) {
        Element block;
        Element element2 = block = selection != null && selection.getAnchorUnit() != null ? selection.getAnchorUnit() : operation.unit();
        if (block == null) {
            return null;
        }
        Node previous = block.getPreviousSibling();
        if (!(previous instanceof Element)) {
            return null;
        }
        Element previousBlock = (Element)previous;
        ArrayList<Node> children = new ArrayList<Node>(block.getChildNodes());
        for (Node child : children) {
            previousBlock.appendChild(child);
        }
        Node parent = block.getParentNode();
        if (parent != null) {
            parent.removeChild(block);
        }
        return previousBlock;
    }

    private static Element mergeForwardAt(RichText element, RichTextSelection selection, RichTextOperation operation) {
        Element block;
        Element element2 = block = selection != null && selection.getAnchorUnit() != null ? selection.getAnchorUnit() : operation.unit();
        if (block == null) {
            return null;
        }
        Node next = block.getNextSibling();
        if (!(next instanceof Element)) {
            return null;
        }
        Element nextBlock = (Element)next;
        ArrayList<Node> children = new ArrayList<Node>(nextBlock.getChildNodes());
        for (Node child : children) {
            block.appendChild(child);
        }
        Node parent = nextBlock.getParentNode();
        if (parent != null) {
            parent.removeChild(nextBlock);
        }
        return block;
    }

    private static void deleteRange(Element unit, int start, int end, boolean removeElements) {
        RichTextRange range = RichTextRange.fromUnitOffsets(unit, start, end);
        if (range != null) {
            range.deleteContents(removeElements);
        }
    }

    private static void insertHtmlAt(Element unit, int at, String html) {
        if (unit == null || unit.document == null) {
            return;
        }
        Element wrapper = unit.document.createHTML("<div contenteditable>" + RichTextEditing.protectWhitespace(html) + "</div>");
        if (wrapper == null) {
            return;
        }
        ArrayList<Node> nodes = new ArrayList<Node>(wrapper.getChildNodes());
        RichTextEditing.insertNodesAt(unit, at, nodes);
    }

    static String protectWhitespace(String html) {
        if (html == null || html.isEmpty()) {
            return html;
        }
        if (html.trim().isEmpty()) {
            return "&#32;";
        }
        return html.replaceAll("(^|>)([ \\t\\r\\n]+)", "$1&#32;");
    }

    private static void insertNodesAt(Element unit, int at, List<Node> nodes) {
        Node reference;
        Node parent;
        if (unit == null || nodes.isEmpty()) {
            return;
        }
        RichTextRange.RichTextEndpoint point = RichTextRange.fromUnitOffset(unit, at);
        Node node = point.container();
        if (node instanceof TextNode) {
            TextNode textNode = (TextNode)node;
            int offset = Math.max(0, Math.min(point.offset(), textNode.getTextContent().length()));
            if (offset >= textNode.getTextContent().length()) {
                parent = textNode.getParentNode();
                reference = textNode.getNextSibling();
            } else if (offset <= 0) {
                parent = textNode.getParentNode();
                reference = textNode;
            } else {
                TextNode tail = textNode.splitText(offset);
                parent = textNode.getParentNode();
                reference = tail;
            }
        } else {
            Node offset = point.container();
            if (offset instanceof Element) {
                Element containerElement = (Element)offset;
                int index = Math.max(0, Math.min(point.offset(), containerElement.getChildNodes().size()));
                parent = containerElement;
                reference = index < containerElement.getChildNodes().size() ? containerElement.getChildNodes().get(index) : null;
            } else {
                return;
            }
        }
        for (Node node2 : nodes) {
            parent.insertBefore(node2, reference);
        }
    }

    private static boolean transform(RichText element, String inputType, String data, OpFactory factory) {
        if (element == null || element.document == null || !element.canEditText()) {
            return false;
        }
        RichTextSelection selection = element.document.getRichTextSelection();
        if (selection == null || !selection.hasAnchor()) {
            return false;
        }
        if (!RichTextEditing.isInRichText(element, selection.getAnchorUnit())) {
            String flat = SelectionUnits.flattenedSelectableText(element);
            int length = flat == null ? 0 : flat.length();
            selection.setCollapsed(element, length);
            if (!RichTextEditing.isInRichText(element, selection.getAnchorUnit())) {
                return false;
            }
        }
        if (!RichTextEditing.dispatchBeforeInput(element, inputType, data)) {
            return false;
        }
        List<RichTextOperation> operations = factory.create(element, selection);
        if (operations.isEmpty()) {
            return false;
        }
        return RichTextEditing.applyAndRecord(element, operations, inputType, data);
    }

    private static boolean transformOperations(RichText element, String inputType, String data, List<RichTextOperation> operations) {
        if (element == null || element.document == null || !element.canEditText()) {
            return false;
        }
        RichTextSelection selection = element.document.getRichTextSelection();
        if (selection == null || !selection.hasAnchor()) {
            return false;
        }
        if (!RichTextEditing.isInRichText(element, selection.getAnchorUnit())) {
            return false;
        }
        if (operations == null || operations.isEmpty()) {
            return false;
        }
        if (!RichTextEditing.dispatchBeforeInput(element, inputType, data)) {
            return false;
        }
        return RichTextEditing.applyAndRecord(element, operations, inputType, data);
    }

    private static boolean applyAndRecord(RichText element, List<RichTextOperation> operations, String inputType, String data) {
        for (RichTextOperation operation : operations) {
            RichTextEditing.applyOperation(element, operation);
            element.pushUndo(operation);
        }
        RichTextRange.normalize(element);
        RichTextEditing.markTransformDirty(element);
        RichTextEditing.dispatchInput(element, inputType, data);
        return true;
    }

    private static void markTransformDirty(RichText element) {
        element.getRenderer().text.clear();
        element.addDirtyFlags(1);
        if (element.document != null) {
            element.document.markDirty(element, 5);
        }
    }

    private static boolean dispatchBeforeInput(RichText element, String inputType, String data) {
        Event.InputEvent event = new Event.InputEvent((Object)element, "beforeinput", true, inputType, data);
        event.cancelable = true;
        Event.markTrustedFromCurrentDispatch(event);
        Event.tiggerEvent(event);
        return !event.defaultPrevented;
    }

    private static void dispatchInput(RichText element, String inputType, String data) {
        Event.InputEvent event = new Event.InputEvent((Object)element, "input", true, inputType, data);
        Event.markTrustedFromCurrentDispatch(event);
        Event.tiggerEvent(event);
    }

    public static String sanitizeHtml(RichText element, String html) {
        Element wrapper;
        if (html == null || html.isEmpty()) {
            return "";
        }
        try {
            wrapper = element.document.createHTML("<div contenteditable>" + html + "</div>");
        }
        catch (Exception ignored) {
            return "";
        }
        if (wrapper == null) {
            return "";
        }
        RichTextEditing.sanitizeNode(wrapper);
        return wrapper.getInnerHTML();
    }

    private static void sanitizeNode(Element element) {
        ArrayList<Node> children = new ArrayList<Node>(element.getChildNodes());
        for (Node child : children) {
            if (!(child instanceof Element)) continue;
            Element childElement = (Element)child;
            String tag = childElement.tagName.toUpperCase(Locale.ROOT);
            if (!ALLOWED_TAGS.contains(tag)) {
                if (DANGEROUS_TAGS.contains(tag)) {
                    element.removeChild(childElement);
                    continue;
                }
                RichTextEditing.sanitizeNode(childElement);
                Node next = childElement.getNextSibling();
                ArrayList<Node> lifted = new ArrayList<Node>(childElement.getChildNodes());
                for (Node node : lifted) {
                    element.insertBefore(node, next);
                }
                element.removeChild(childElement);
                continue;
            }
            RichTextEditing.filterAttributes(childElement);
            RichTextEditing.sanitizeNode(childElement);
        }
    }

    private static void filterAttributes(Element element) {
        HashMap<String, String> attributes = element.getAttributes();
        for (String name : new ArrayList(attributes.keySet())) {
            String lower = name.toLowerCase(Locale.ROOT);
            boolean allowed = "style".equals(lower) || "href".equals(lower) && "A".equals(element.tagName) || ("src".equals(lower) || "alt".equals(lower)) && "IMG".equals(element.tagName);
            if (allowed) continue;
            attributes.remove(name);
        }
    }

    private static interface OpFactory {
        public List<RichTextOperation> create(RichText var1, RichTextSelection var2);
    }
}

