/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 */
package com.sighs.apricityui.behavior;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sighs.apricityui.behavior.DocumentSelection;
import com.sighs.apricityui.behavior.SelectionUnits;
import com.sighs.apricityui.dom.CommentNode;
import com.sighs.apricityui.dom.TextNode;
import com.sighs.apricityui.element.AbstractText;
import com.sighs.apricityui.element.RichText;
import com.sighs.apricityui.event.Event;
import com.sighs.apricityui.event.MouseEvent;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.init.Node;
import com.sighs.apricityui.layout.Box;
import com.sighs.apricityui.layout.Flex;
import com.sighs.apricityui.layout.Layout;
import com.sighs.apricityui.layout.NormalFlow;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.parser.Color;
import com.sighs.apricityui.render.FontDrawer;
import com.sighs.apricityui.render.Graph;
import com.sighs.apricityui.render.Rect;
import com.sighs.apricityui.style.Interaction;
import com.sighs.apricityui.style.Text;
import com.sighs.apricityui.util.TextMetrics;
import java.util.List;

public final class TextSelection {
    private final Element owner;

    public TextSelection(Element owner) {
        this.owner = owner;
    }

    public void addEventListeners() {
        this.owner.addInternalEventListener("mousedown", event -> {
            if (!(event instanceof MouseEvent)) {
                return;
            }
            MouseEvent mouseEvent = (MouseEvent)event;
            if (event.target != this.owner) {
                return;
            }
            this.handleMouseDown(mouseEvent);
        });
        this.owner.addInternalEventListener("mousemove", event -> {
            if (!(event instanceof MouseEvent)) {
                return;
            }
            MouseEvent mouseEvent = (MouseEvent)event;
            if (event.target != this.owner) {
                return;
            }
            this.handleMouseMove(mouseEvent);
        });
        this.owner.addInternalEventListener("mouseup", event -> {
            if (this.owner.document == null) {
                return;
            }
            this.handleMouseUp((Event)event);
        });
    }

    private void handleMouseDown(MouseEvent event) {
        if (TextSelection.isInsideRichTextEditor(this.owner)) {
            return;
        }
        Document document = this.owner.document;
        if (document == null) {
            return;
        }
        if (event.button == 2) {
            return;
        }
        document.endTextDrag();
        SelectionUnits.UnitOffset hit = TextSelection.resolveUnitOffset(this.owner, event.clientX, event.clientY);
        DocumentSelection selection = document.getDocumentSelection();
        if (hit == null) {
            document.clearDocumentSelection();
            return;
        }
        if (Interaction.isUserSelectAll(this.owner)) {
            Element unit = hit.unit();
            selection.selectUnit(unit);
            selection.setSelecting(false);
            document.setFocusedElement(unit);
            return;
        }
        int clickCount = event.clickCount;
        if (clickCount >= 3) {
            Element unit = hit.unit();
            selection.selectUnit(unit);
            selection.setSelecting(true);
            document.setFocusedElement(unit);
            return;
        }
        if (clickCount == 2) {
            Element unit = hit.unit();
            String text = SelectionUnits.flattenedSelectableText(unit);
            int[] word = TextSelection.wordRange(text, hit.offset());
            if (word != null) {
                selection.collapse(unit, word[0]);
                selection.extendTo(unit, word[1]);
            } else {
                selection.collapse(unit, hit.offset());
            }
            selection.setSelecting(true);
            document.setFocusedElement(unit);
            return;
        }
        if (event.shiftKey && selection.hasAnchor()) {
            selection.extendTo(hit.unit(), hit.offset());
        } else if (clickCount == 1 && TextSelection.isInsideSelection(selection, hit)) {
            document.beginTextDrag(document.getDocumentSelectedText(), event.clientX, event.clientY);
        } else {
            selection.collapse(hit.unit(), hit.offset());
        }
        selection.setSelecting(true);
        document.setFocusedElement(hit.unit());
    }

    private static boolean isInsideSelection(DocumentSelection selection, SelectionUnits.UnitOffset hit) {
        int[] range = selection.localRangeForUnit(hit.unit());
        if (range == null) {
            return false;
        }
        return hit.offset() >= range[0] && hit.offset() <= range[1];
    }

    private void handleMouseMove(MouseEvent event) {
        SelectionUnits.UnitOffset hit;
        Document document = this.owner.document;
        if (TextSelection.isInsideRichTextEditor(this.owner)) {
            return;
        }
        if (document == null) {
            return;
        }
        if (event.activeElementRedirect) {
            return;
        }
        DocumentSelection selection = document.getDocumentSelection();
        if (!selection.isSelecting()) {
            return;
        }
        if (document.isTextDragPending()) {
            document.updateTextDrag(event.clientX, event.clientY);
            if (document.isTextDragging()) {
                return;
            }
        }
        if ((hit = TextSelection.resolveUnitOffset(this.owner, event.clientX, event.clientY)) == null) {
            return;
        }
        selection.extendTo(hit.unit(), hit.offset());
    }

    private void handleMouseUp(Event rawEvent) {
        String draggedText;
        AbstractText input;
        Object object;
        Document document = this.owner.document;
        if (rawEvent instanceof MouseEvent) {
            MouseEvent mouseEvent = (MouseEvent)rawEvent;
            if (mouseEvent.activeElementRedirect) {
                return;
            }
        }
        if (document.isTextDragging() && rawEvent.target == this.owner && (object = rawEvent.target) instanceof AbstractText && (input = (AbstractText)object).canEditText() && (draggedText = document.getDraggedText()) != null && !draggedText.isEmpty()) {
            input.insertText(draggedText);
        }
        document.endTextDrag();
        document.getDocumentSelection().setSelecting(false);
    }

    public static SelectionUnits.UnitOffset resolveUnitOffset(Element hit, double x, double y) {
        if (hit == null) {
            return null;
        }
        Element unit = SelectionUnits.resolveUnit(hit);
        if (unit == null) {
            return null;
        }
        return new SelectionUnits.UnitOffset(unit, TextSelection.locateOffsetInUnit(unit, x, y));
    }

    private static int locateOffsetInUnit(Element unit, double x, double y) {
        if (unit == null) {
            return 0;
        }
        String display = unit.getComputedStyle().display;
        if (Layout.isFlexDisplay(display)) {
            return TextSelection.locateFlexDirect(unit, x, y);
        }
        if (Layout.isGridDisplay(display)) {
            return TextSelection.locateLeaf(unit, x, y);
        }
        if (SelectionUnits.paintsTextViaRuns(unit)) {
            return TextSelection.locateRuns(unit, x, y);
        }
        return TextSelection.locateLeaf(unit, x, y);
    }

    private static int locateRuns(Element unit, double x, double y) {
        int objectHit = TextSelection.locateObjectHit(unit, x, y);
        if (objectHit >= 0) {
            return objectHit;
        }
        List<NormalFlow.TextRunLayout> runs = NormalFlow.computeTextRuns(unit);
        if (runs.isEmpty()) {
            return 0;
        }
        Rect rect = Rect.of(unit);
        Position contentPos = rect.getContentPosition();
        boolean alignDirect = TextSelection.shouldAlignDirectTextRuns(unit);
        double contentWidth = alignDirect ? Box.of(unit).innerSize().width() : 0.0;
        String flattened = SelectionUnits.flattenedSelectableText(unit);
        int bestRunIndex = -1;
        int bestLineIndex = -1;
        double bestDistance = Double.MAX_VALUE;
        for (int r = 0; r < runs.size(); ++r) {
            NormalFlow.TextRunLayout run = runs.get(r);
            if (run == null || run.text() == null || run.lines() == null) continue;
            double lineHeight = run.text().lineHeight;
            for (int i = 0; i < run.lines().size(); ++i) {
                double distance;
                String line = run.lines().get(i);
                if (line == null || line.isEmpty()) continue;
                double lineY0 = contentPos.y + run.y() + (double)i * lineHeight;
                double d = y < lineY0 ? lineY0 - y : (distance = y > lineY0 + lineHeight ? y - (lineY0 + lineHeight) : 0.0);
                if (!(distance < bestDistance)) continue;
                bestDistance = distance;
                bestRunIndex = r;
                bestLineIndex = i;
            }
        }
        if (bestRunIndex < 0) {
            return 0;
        }
        NormalFlow.TextRunLayout chosenRun = runs.get(bestRunIndex);
        double chosenLineY0 = contentPos.y + chosenRun.y() + (double)bestLineIndex * chosenRun.text().lineHeight;
        double chosenLineY1 = chosenLineY0 + chosenRun.text().lineHeight;
        int bestOffset = 0;
        double bestXDistance = Double.MAX_VALUE;
        for (int r = 0; r < runs.size(); ++r) {
            NormalFlow.TextRunLayout run = runs.get(r);
            if (run == null || run.text() == null || run.lines() == null) continue;
            Text runText = run.text();
            double lineHeight = runText.lineHeight;
            int runBase = SelectionUnits.baseOffsetOfDescendant(unit, run.node() != null ? run.node() : run.owner());
            for (int i = 0; i < run.lines().size(); ++i) {
                int lineLocal;
                double xDistance;
                double lineY0;
                double lineY1;
                String line = run.lines().get(i);
                if (line == null || line.isEmpty() || (lineY1 = (lineY0 = contentPos.y + run.y() + (double)i * lineHeight) + lineHeight) <= chosenLineY0 || lineY0 >= chosenLineY1) continue;
                double lineWidth = Text.measureLine(runText, line);
                double alignOffset = alignDirect && run.owner() == unit ? TextMetrics.computeAlignedX(runText, contentWidth, lineWidth, i == 0) : 0.0;
                double lineX0 = contentPos.x + (i == 0 ? run.x() : 0.0) + alignOffset - unit.scrollLeft;
                double lineX1 = lineX0 + lineWidth;
                double d = x < lineX0 ? lineX0 - x : (xDistance = x > lineX1 ? x - lineX1 : 0.0);
                if (xDistance > bestXDistance) continue;
                bestXDistance = xDistance;
                double relativeX = x - lineX0;
                if (relativeX <= 0.0) {
                    lineLocal = 0;
                } else if (relativeX >= lineWidth) {
                    lineLocal = line.length();
                } else {
                    double charWidth;
                    double currentWidth = 0.0;
                    lineLocal = 0;
                    for (int c = 0; c < line.length() && !(relativeX <= currentWidth + (charWidth = Text.measureLine(runText, line.substring(c, c + 1))) / 2.0); ++c) {
                        currentWidth += charWidth;
                        ++lineLocal;
                    }
                }
                int global = runBase + SelectionUnits.runLineStart(run, i) + lineLocal;
                bestOffset = Math.min(global, flattened.length());
            }
        }
        return bestOffset;
    }

    private static int locateObjectHit(Element unit, double x, double y) {
        if (unit == null) {
            return -1;
        }
        int[] hit = new int[]{-1};
        TextSelection.locateObjectHitRecursive(unit, unit, x, y, hit);
        return hit[0];
    }

    private static void locateObjectHitRecursive(Element unit, Element current, double x, double y, int[] hit) {
        if (hit[0] >= 0) {
            return;
        }
        for (Node child : current.getRenderChildNodes()) {
            if (hit[0] >= 0) {
                return;
            }
            if (!(child instanceof Element)) continue;
            Element childElement = (Element)child;
            if (SelectionUnits.isAtomicObject(childElement)) {
                Element.DOMRect rect = childElement.getBoundingClientRect();
                if (rect == null || !(rect.width > 0.0) || !(rect.height > 0.0) || !(x >= rect.x) || !(x <= rect.x + rect.width) || !(y >= rect.y) || !(y <= rect.y + rect.height)) continue;
                hit[0] = SelectionUnits.baseOffsetOfDescendant(unit, childElement);
                return;
            }
            if (childElement instanceof AbstractText) continue;
            TextSelection.locateObjectHitRecursive(unit, childElement, x, y, hit);
        }
    }

    private static int locateFlexDirect(Element unit, double x, double y) {
        List<Flex.DirectTextLayout> layouts = Flex.computeDirectTextLayouts(unit);
        if (layouts.isEmpty()) {
            return 0;
        }
        List<String> fragments = SelectionUnits.flexTextFragments(unit);
        String flattened = SelectionUnits.flattenedSelectableText(unit);
        Position origin = Position.forRender(unit);
        Box box = Box.of(unit);
        double originX = origin.x + box.getMarginLeft();
        double originY = origin.y + box.getMarginTop();
        int bestOffset = 0;
        double bestDistance = Double.MAX_VALUE;
        int fragmentIndex = 0;
        int accumulatedBase = 0;
        for (Flex.DirectTextLayout layout : layouts) {
            int local;
            double distance;
            Text text = layout.text();
            if (text == null || text.content == null || text.content.isEmpty()) continue;
            int base = accumulatedBase;
            if (fragmentIndex < fragments.size() && fragments.get(fragmentIndex).equals(text.content)) {
                accumulatedBase += text.content.length();
                ++fragmentIndex;
            } else {
                fragmentIndex = fragments.size();
            }
            double px = originX + layout.position().x - unit.scrollLeft;
            double py = originY + layout.position().y - unit.scrollTop;
            double d = y < py ? py - y : (distance = y > py + text.lineHeight ? y - (py + text.lineHeight) : 0.0);
            if (distance > bestDistance) continue;
            bestDistance = distance;
            double lineWidth = Text.measureLine(text, text.content);
            double relativeX = x - px;
            if (relativeX <= 0.0) {
                local = 0;
            } else if (relativeX >= lineWidth) {
                local = text.content.length();
            } else {
                double charWidth;
                double currentWidth = 0.0;
                local = 0;
                for (int c = 0; c < text.content.length() && !(relativeX <= currentWidth + (charWidth = Text.measureLine(text, text.content.substring(c, c + 1))) / 2.0); ++c) {
                    currentWidth += charWidth;
                    ++local;
                }
            }
            bestOffset = Math.min(base + local, flattened.length());
        }
        return bestOffset;
    }

    public static boolean isPositionOverSelectableText(Element unit, double x, double y) {
        if (unit == null) {
            return false;
        }
        String display = unit.getComputedStyle().display;
        if (Layout.isFlexDisplay(display)) {
            return TextSelection.overFlexDirectText(unit, x, y);
        }
        if (Layout.isGridDisplay(display)) {
            return TextSelection.overLeafText(unit, x, y);
        }
        if (SelectionUnits.paintsTextViaRuns(unit)) {
            return TextSelection.overRunsText(unit, x, y);
        }
        return TextSelection.overLeafText(unit, x, y);
    }

    private static boolean overFlexDirectText(Element unit, double x, double y) {
        Position origin = Position.forRender(unit);
        Box box = Box.of(unit);
        double ox = origin.x + box.getMarginLeft();
        double oy = origin.y + box.getMarginTop();
        for (Flex.DirectTextLayout layout : Flex.computeDirectTextLayouts(unit)) {
            if (layout == null || layout.text() == null || layout.position() == null) continue;
            Text text = layout.text();
            if (text.content == null || text.content.isEmpty()) continue;
            double px = ox + layout.position().x - unit.scrollLeft;
            double py = oy + layout.position().y - unit.scrollTop;
            if (y < py || y >= py + text.lineHeight) continue;
            double lineWidth = Text.measureLine(text, text.content);
            if (!(x >= px) || !(x <= px + lineWidth)) continue;
            return true;
        }
        return false;
    }

    private static boolean overRunsText(Element unit, double x, double y) {
        List<NormalFlow.TextRunLayout> runs = NormalFlow.computeTextRuns(unit);
        if (runs.isEmpty()) {
            return false;
        }
        Position contentPos = Rect.of(unit).getContentPosition();
        boolean alignDirect = TextSelection.shouldAlignDirectTextRuns(unit);
        double contentWidth = alignDirect ? Box.of(unit).innerSize().width() : 0.0;
        for (NormalFlow.TextRunLayout run : runs) {
            if (run == null || run.text() == null || run.lines() == null) continue;
            double lineHeight = run.text().lineHeight;
            for (int i = 0; i < run.lines().size(); ++i) {
                double alignOffset;
                double lineY0;
                String line = run.lines().get(i);
                if (line == null || line.isEmpty() || y < (lineY0 = contentPos.y + run.y() + (double)i * lineHeight) || y >= lineY0 + lineHeight) continue;
                double lineWidth = Text.measureLine(run.text(), line);
                double lineX0 = contentPos.x + (i == 0 ? run.x() : 0.0) + (alignOffset = alignDirect && run.owner() == unit ? TextMetrics.computeAlignedX(run.text(), contentWidth, lineWidth, i == 0) : 0.0) - unit.scrollLeft;
                if (!(x >= lineX0) || !(x <= lineX0 + lineWidth)) continue;
                return true;
            }
        }
        return false;
    }

    private static boolean overLeafText(Element unit, double x, double y) {
        double contentHeight;
        Text text = TextSelection.selectableTextFor(unit);
        if (text == null || text.content == null || text.content.isEmpty()) {
            return false;
        }
        Box box = Box.of(unit);
        double contentWidth = box.innerSize().width();
        List<String> lines = unit.resolveRenderedLines(text, contentWidth, contentHeight = box.innerSize().height());
        if (lines.isEmpty()) {
            return false;
        }
        String display = unit.getComputedStyle().display;
        boolean flexLike = Layout.isFlexDisplay(display) || Layout.isGridDisplay(display);
        Position contentPos = Rect.of(unit).getContentPosition();
        Position flexTextOffset = flexLike ? unit.getFlexTextOffset() : Position.ZERO;
        double textHeight = Math.max(text.lineHeight, (double)lines.size() * text.lineHeight);
        double drawY = contentPos.y + (flexLike ? flexTextOffset.y : TextMetrics.computeVerticalOffset(text, contentHeight, textHeight));
        for (int i = 0; i < lines.size(); ++i) {
            double lineY = drawY + (double)i * text.lineHeight;
            if (y < lineY || y >= lineY + text.lineHeight) continue;
            String line = lines.get(i);
            double lineWidth = Text.measureLine(text, line);
            double drawX = contentPos.x + (flexLike ? TextMetrics.computeFlexTextAlignedX(unit, text, contentWidth, lineWidth) : TextMetrics.computeAlignedX(text, contentWidth, lineWidth, i == 0));
            double startX = drawX - unit.scrollLeft;
            if (!(x >= startX) || !(x <= startX + lineWidth)) continue;
            return true;
        }
        return false;
    }

    private static int locateLeaf(Element unit, double x, double y) {
        double charWidth;
        Text text = TextSelection.selectableTextFor(unit);
        if (text.content == null || text.content.isEmpty()) {
            return 0;
        }
        Box box = Box.of(unit);
        double contentWidth = box.innerSize().width();
        double contentHeight = box.innerSize().height();
        Text.WrappedText wrapped = Text.wrapCached(unit, text);
        List<String> lines = unit.resolveRenderedLines(text, contentWidth, contentHeight);
        if (lines.isEmpty()) {
            return 0;
        }
        int[] starts = wrapped.starts();
        double lineHeight = text.lineHeight;
        double textHeight = Math.max(lineHeight, (double)lines.size() * lineHeight);
        String display = unit.getComputedStyle().display;
        boolean flexLike = Layout.isFlexDisplay(display) || Layout.isGridDisplay(display);
        Position flexTextOffset = flexLike ? unit.getFlexTextOffset() : Position.ZERO;
        Position contentPos = Rect.of(unit).getContentPosition();
        double drawY = contentPos.y + (flexLike ? flexTextOffset.y : TextMetrics.computeVerticalOffset(text, contentHeight, textHeight));
        int lineIndex = 0;
        double bestDistance = Double.MAX_VALUE;
        for (int i = 0; i < lines.size(); ++i) {
            double distance;
            double lineY = drawY + (double)i * lineHeight;
            double d = y < lineY ? lineY - y : (distance = y > lineY + lineHeight ? y - (lineY + lineHeight) : 0.0);
            if (distance == 0.0) {
                lineIndex = i;
                break;
            }
            if (!(distance < bestDistance)) continue;
            bestDistance = distance;
            lineIndex = i;
        }
        String line = lines.get(lineIndex);
        double lineWidth = Text.measureLine(text, line);
        double drawX = contentPos.x + (flexLike ? TextMetrics.computeFlexTextAlignedX(unit, text, contentWidth, lineWidth) : TextMetrics.computeAlignedX(text, contentWidth, lineWidth, lineIndex == 0));
        int lineStart = starts[lineIndex];
        double relativeX = x - (drawX - unit.scrollLeft);
        if (relativeX <= 0.0) {
            return lineStart;
        }
        double currentWidth = 0.0;
        int cursor = 0;
        for (int i = 0; i < line.length() && !(relativeX <= currentWidth + (charWidth = TextSelection.measureSegment(unit, line.substring(i, i + 1))) / 2.0); ++i) {
            currentWidth += charWidth;
            ++cursor;
        }
        return Math.min(lineStart + cursor, lineStart + line.length());
    }

    public static int[] wordRange(String text, int offset) {
        int end;
        int clamped;
        int start;
        if (text == null || text.isEmpty()) {
            return null;
        }
        for (start = clamped = Math.max(0, Math.min(offset, text.length())); start > 0 && !TextSelection.isWordSeparator(text.charAt(start - 1)); --start) {
        }
        for (end = clamped; end < text.length() && !TextSelection.isWordSeparator(text.charAt(end)); ++end) {
        }
        if (start == end) {
            return null;
        }
        return new int[]{start, end};
    }

    private static boolean isInsideRichTextEditor(Element element) {
        Element e = element;
        while (e != null) {
            if (e instanceof RichText) {
                return true;
            }
            e = e.parentElement;
        }
        return false;
    }

    private static boolean isWordSeparator(char c) {
        return Character.isWhitespace(c);
    }

    private static boolean shouldAlignDirectTextRuns(Element element) {
        boolean hasText = false;
        for (Node child : element.getRenderChildNodes()) {
            Element childElement;
            if (child instanceof CommentNode) continue;
            if (child instanceof TextNode) {
                TextNode textNode = (TextNode)child;
                hasText |= textNode.getTextContent() != null && !textNode.getTextContent().isEmpty();
                continue;
            }
            if (child instanceof Element && !Layout.isInFlow((childElement = (Element)child).getComputedStyle())) continue;
            return false;
        }
        return hasText;
    }

    public boolean canSelectInnerText() {
        return SelectionUnits.resolveUnitContext(this.owner) != null;
    }

    public boolean hasInnerTextSelection() {
        SelectionUnits.UnitContext context = SelectionUnits.resolveUnitContext(this.owner);
        if (context == null) {
            return false;
        }
        return this.viewLocalRange(context) != null;
    }

    public String getSelectedInnerText() {
        SelectionUnits.UnitContext context = SelectionUnits.resolveUnitContext(this.owner);
        if (context == null) {
            return "";
        }
        int[] range = this.viewLocalRange(context);
        if (range == null) {
            return "";
        }
        return SelectionUnits.rawRangeForNormalizedRange(context.unit(), context.baseOffset() + range[0], context.baseOffset() + range[1]);
    }

    public void selectAllInnerText() {
        if (this.owner.document == null) {
            return;
        }
        SelectionUnits.UnitContext context = SelectionUnits.resolveUnitContext(this.owner);
        if (context == null) {
            return;
        }
        DocumentSelection selection = this.owner.document.getDocumentSelection();
        int start = context.baseOffset();
        selection.collapse(context.unit(), start);
        selection.extendTo(context.unit(), start + context.text().length());
        this.owner.addDirtyFlags(1);
    }

    public void clearTextSelection() {
        if (this.owner.document == null) {
            return;
        }
        SelectionUnits.UnitContext context = SelectionUnits.resolveUnitContext(this.owner);
        if (context == null) {
            return;
        }
        DocumentSelection selection = this.owner.document.getDocumentSelection();
        if (selection.getAnchorUnit() != context.unit() && selection.getEndUnit() != context.unit()) {
            return;
        }
        selection.clear();
        this.owner.addDirtyFlags(1);
    }

    private int[] viewLocalRange(SelectionUnits.UnitContext context) {
        int end;
        if (this.owner.document == null) {
            return null;
        }
        int[] unitRange = this.owner.document.resolveUnitSelectionRange(context.unit());
        if (unitRange == null) {
            return null;
        }
        int start = Math.max(unitRange[0] - context.baseOffset(), 0);
        if (start >= (end = Math.min(unitRange[1] - context.baseOffset(), context.text().length()))) {
            return null;
        }
        return new int[]{start, end};
    }

    public void drawInnerTextSelection(PoseStack poseStack, Rect rectRenderer) {
        if (this.owner == null || this.owner.document == null) {
            return;
        }
        if (!this.owner.document.hasAnyActiveSelection()) {
            return;
        }
        if (SelectionUnits.paintsTextViaRuns(this.owner)) {
            return;
        }
        SelectionUnits.UnitContext context = SelectionUnits.resolveUnitContext(this.owner);
        if (context == null) {
            return;
        }
        int[] range = this.viewLocalRange(context);
        if (range == null) {
            return;
        }
        Text baseText = this.selectableText();
        if (!TextSelection.hasDrawableText(baseText.content)) {
            return;
        }
        Text.WrappedText wrapped = Text.wrapCached(this.owner, baseText);
        int[] starts = wrapped.starts();
        int min = range[0];
        int max = range[1];
        if (min >= max) {
            return;
        }
        Position contentPos = rectRenderer.getContentPosition();
        double contentWidth = Box.of(this.owner).innerSize().width();
        double contentHeight = Box.of(this.owner).innerSize().height();
        List<String> lines = this.owner.resolveRenderedLines(baseText, contentWidth, contentHeight);
        double textHeight = Math.max(baseText.lineHeight, (double)lines.size() * baseText.lineHeight);
        double baseY = contentPos.y + TextMetrics.computeVerticalOffset(baseText, contentHeight, textHeight);
        for (int i = 0; i < lines.size(); ++i) {
            int drawEnd;
            String line = lines.get(i);
            int lineStart = starts[i];
            int selectableEnd = lineStart + TextSelection.commonPrefixLength(line, wrapped.lines().get(i));
            int drawStart = Math.max(min, lineStart);
            if (drawStart >= (drawEnd = Math.min(max, selectableEnd))) continue;
            double lineWidth = Text.measureLine(baseText, line);
            double drawX = contentPos.x + TextMetrics.computeAlignedX(baseText, contentWidth, lineWidth, i == 0);
            double startX = this.measureTextSegmentWidth(line.substring(0, drawStart - lineStart)) - this.owner.scrollLeft;
            double endX = this.measureTextSegmentWidth(line.substring(0, drawEnd - lineStart)) - this.owner.scrollLeft;
            float x0 = (float)(drawX + startX);
            float x1 = (float)(drawX + endX);
            float y0 = (float)(baseY + (double)i * baseText.lineHeight);
            float y1 = y0 + (float)baseText.lineHeight;
            Graph.drawFillRect(poseStack.m_85850_().m_252922_(), x0, y0, x1, y1, Text.getSelectionColor(this.owner));
        }
    }

    public void drawInnerText(PoseStack poseStack, Rect rectRenderer) {
        if (this.owner == null || this.owner.document == null) {
            return;
        }
        if (SelectionUnits.paintsTextViaRuns(this.owner)) {
            return;
        }
        Text text = this.selectableText();
        Position contentPos = rectRenderer.getContentPosition();
        text.color = new Color(Text.getFontColor(this.owner));
        if (!TextSelection.hasDrawableText(text.content)) {
            return;
        }
        double contentWidth = Box.of(this.owner).innerSize().width();
        double contentHeight = Box.of(this.owner).innerSize().height();
        List<String> lines = this.owner.resolveRenderedLines(text, contentWidth, contentHeight);
        double textHeight = Math.max(text.lineHeight, (double)lines.size() * text.lineHeight);
        boolean flexLike = Layout.isFlexDisplay(this.owner.getComputedStyle().display) || Layout.isGridDisplay(this.owner.getComputedStyle().display);
        Position flexTextOffset = flexLike ? this.owner.getFlexTextOffset() : Position.ZERO;
        double drawY = contentPos.y + (flexLike ? flexTextOffset.y : TextMetrics.computeVerticalOffset(text, contentHeight, textHeight));
        Position linePos = new Position(0.0, 0.0);
        for (int i = 0; i < lines.size(); ++i) {
            String line = lines.get(i);
            double lineWidth = Text.measureLine(text, line);
            double drawX = contentPos.x + (flexLike ? TextMetrics.computeFlexTextAlignedX(this.owner, text, contentWidth, lineWidth) : TextMetrics.computeAlignedX(text, contentWidth, lineWidth, i == 0));
            text.content = line;
            linePos.x = drawX - this.owner.scrollLeft;
            linePos.y = drawY + (double)i * text.lineHeight;
            FontDrawer.drawFont(poseStack, text, linePos);
        }
    }

    private Text selectableText() {
        Text base = Text.of(this.owner);
        Text copy = new Text();
        copy.fontSize = base.fontSize;
        copy.fontWeight = base.fontWeight;
        copy.oblique = base.oblique;
        copy.strokeWidth = base.strokeWidth;
        copy.strokeColor = base.strokeColor;
        copy.color = base.color;
        copy.textDecoration = base.textDecoration;
        copy.fontFamily = base.fontFamily;
        copy.lineHeight = base.lineHeight;
        copy.direction = base.direction;
        copy.textAlign = base.textAlign;
        copy.verticalAlign = base.verticalAlign;
        copy.whiteSpace = base.whiteSpace;
        copy.fontMode = base.fontMode;
        copy.textIndent = base.textIndent;
        copy.letterSpacing = base.letterSpacing;
        copy.rasterBackgroundColor = base.rasterBackgroundColor;
        copy.content = SelectionUnits.ownSelectableText(this.owner);
        return copy;
    }

    private static Text selectableTextFor(Element unit) {
        Text base = Text.of(unit);
        Text copy = new Text();
        copy.fontSize = base.fontSize;
        copy.fontWeight = base.fontWeight;
        copy.oblique = base.oblique;
        copy.strokeWidth = base.strokeWidth;
        copy.strokeColor = base.strokeColor;
        copy.color = base.color;
        copy.textDecoration = base.textDecoration;
        copy.fontFamily = base.fontFamily;
        copy.lineHeight = base.lineHeight;
        copy.direction = base.direction;
        copy.textAlign = base.textAlign;
        copy.verticalAlign = base.verticalAlign;
        copy.whiteSpace = base.whiteSpace;
        copy.fontMode = base.fontMode;
        copy.textIndent = base.textIndent;
        copy.letterSpacing = base.letterSpacing;
        copy.rasterBackgroundColor = base.rasterBackgroundColor;
        copy.content = SelectionUnits.flattenedSelectableText(unit);
        return copy;
    }

    private double measureTextSegmentWidth(String segment) {
        if (segment == null || segment.isEmpty()) {
            return 0.0;
        }
        Text base = Text.of(this.owner);
        Text copy = TextMetrics.cloneTextForSegment(base, segment, Color.BLACK);
        return Text.measureLine(copy, segment);
    }

    private static double measureSegment(Element unit, String segment) {
        if (segment == null || segment.isEmpty()) {
            return 0.0;
        }
        Text base = Text.of(unit);
        Text copy = TextMetrics.cloneTextForSegment(base, segment, Color.BLACK);
        return Text.measureLine(copy, segment);
    }

    private static int commonPrefixLength(String a, String b) {
        int i;
        if (a == null || b == null) {
            return 0;
        }
        int n = Math.min(a.length(), b.length());
        for (i = 0; i < n && a.charAt(i) == b.charAt(i); ++i) {
        }
        return i;
    }

    private static boolean hasDrawableText(String content) {
        if (content == null || content.isEmpty()) {
            return false;
        }
        for (int i = 0; i < content.length(); ++i) {
            char value = content.charAt(i);
            if (value == '\ufffc' || Character.isWhitespace(value) || Character.isSpaceChar(value)) continue;
            return true;
        }
        return false;
    }
}

