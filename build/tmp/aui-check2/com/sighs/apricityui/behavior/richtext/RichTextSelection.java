/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.behavior.richtext;

import com.sighs.apricityui.behavior.SelectionUnits;
import com.sighs.apricityui.behavior.TextSelection;
import com.sighs.apricityui.behavior.richtext.RichTextNavigation;
import com.sighs.apricityui.behavior.richtext.RichTextRange;
import com.sighs.apricityui.element.RichText;
import com.sighs.apricityui.event.MouseEvent;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.init.Node;
import java.util.ArrayList;
import java.util.List;

public final class RichTextSelection {
    private final Document owner;
    private Element anchorUnit = null;
    private int anchorOffset = 0;
    private Element endUnit = null;
    private int endOffset = 0;
    private String direction = "none";
    private boolean selecting = false;
    private Element lastNotifiedAnchorUnit = null;
    private int lastNotifiedAnchorOffset = -1;
    private Element lastNotifiedEndUnit = null;
    private int lastNotifiedEndOffset = -1;

    public RichTextSelection(Document owner) {
        this.owner = owner;
    }

    public boolean isActive() {
        if (this.anchorUnit == null || this.endUnit == null) {
            return false;
        }
        if (this.anchorUnit == this.endUnit) {
            return this.anchorOffset != this.endOffset;
        }
        return true;
    }

    public boolean collapsed() {
        return this.anchorUnit == this.endUnit && this.anchorOffset == this.endOffset;
    }

    public boolean hasAnchor() {
        return this.anchorUnit != null;
    }

    public boolean isSelecting() {
        return this.selecting;
    }

    public void setSelecting(boolean selecting) {
        this.selecting = selecting;
    }

    public Element getAnchorUnit() {
        return this.anchorUnit;
    }

    public int getAnchorOffset() {
        return this.anchorOffset;
    }

    public Element getEndUnit() {
        return this.endUnit;
    }

    public int getEndOffset() {
        return this.endOffset;
    }

    public String getDirection() {
        return this.direction;
    }

    public void setCollapsed(Element unit, int offset) {
        if (unit == null) {
            return;
        }
        if (this.owner != null) {
            this.owner.clearDocumentSelection();
        }
        int clamped = RichTextSelection.clamp(offset, 0, RichTextSelection.flattenedLength(unit));
        this.anchorUnit = unit;
        this.anchorOffset = clamped;
        this.endUnit = unit;
        this.endOffset = clamped;
        this.direction = "none";
        this.markDirty();
        this.notifyChange();
    }

    public void extendTo(Element unit, int offset) {
        if (unit == null) {
            return;
        }
        if (!this.hasAnchor()) {
            this.setCollapsed(unit, offset);
            return;
        }
        if (this.owner != null) {
            this.owner.clearDocumentSelection();
        }
        this.endUnit = unit;
        this.endOffset = RichTextSelection.clamp(offset, 0, RichTextSelection.flattenedLength(unit));
        this.updateDirection();
        this.markDirty();
        this.notifyChange();
    }

    public void setRange(Element anchorUnit, int anchorOffset, Element endUnit, int endOffset) {
        if (anchorUnit == null || endUnit == null) {
            return;
        }
        if (this.owner != null) {
            this.owner.clearDocumentSelection();
        }
        this.anchorUnit = anchorUnit;
        this.anchorOffset = RichTextSelection.clamp(anchorOffset, 0, RichTextSelection.flattenedLength(anchorUnit));
        this.endUnit = endUnit;
        this.endOffset = RichTextSelection.clamp(endOffset, 0, RichTextSelection.flattenedLength(endUnit));
        this.updateDirection();
        this.markDirty();
        this.notifyChange();
    }

    public void clear() {
        if (this.anchorUnit == null && this.endUnit == null && !this.selecting) {
            return;
        }
        this.anchorUnit = null;
        this.anchorOffset = 0;
        this.endUnit = null;
        this.endOffset = 0;
        this.direction = "none";
        this.selecting = false;
        this.markDirty();
        this.notifyChange();
    }

    public void selectAll(Element unit) {
        if (unit == null) {
            return;
        }
        if (this.owner != null) {
            this.owner.clearDocumentSelection();
        }
        int length = RichTextSelection.flattenedLength(unit);
        this.anchorUnit = unit;
        this.anchorOffset = 0;
        this.endUnit = unit;
        this.endOffset = length;
        this.direction = "forward";
        this.markDirty();
        this.notifyChange();
    }

    public void selectAllInRoot() {
        List<Element> units = this.unitsInRoot();
        if (units.isEmpty()) {
            return;
        }
        Element first = units.get(0);
        Element last = units.get(units.size() - 1);
        this.setRange(first, 0, last, RichTextSelection.flattenedLength(last));
    }

    public void setFromPoint(Element hit, MouseEvent event, boolean shiftExtend) {
        if (hit == null || event == null) {
            this.clear();
            return;
        }
        SelectionUnits.UnitOffset target = TextSelection.resolveUnitOffset(hit, event.clientX, event.clientY);
        if (target == null) {
            this.clear();
            return;
        }
        if (shiftExtend && this.hasAnchor()) {
            this.extendTo(target.unit(), target.offset());
        } else if (event.clickCount >= 3) {
            this.setRange(target.unit(), 0, target.unit(), RichTextSelection.flattenedLength(target.unit()));
        } else if (event.clickCount == 2) {
            String text = SelectionUnits.flattenedSelectableText(target.unit());
            int[] word = TextSelection.wordRange(text, target.offset());
            if (word == null) {
                this.setCollapsed(target.unit(), target.offset());
            } else {
                this.setRange(target.unit(), word[0], target.unit(), word[1]);
            }
        } else {
            this.setCollapsed(target.unit(), target.offset());
        }
        this.setSelecting(true);
    }

    public String getSelectedText() {
        if (!this.isActive()) {
            return "";
        }
        if (this.anchorUnit == this.endUnit) {
            int min = Math.min(this.anchorOffset, this.endOffset);
            int max = Math.max(this.anchorOffset, this.endOffset);
            return SelectionUnits.rawRangeForNormalizedRange(this.anchorUnit, min, max);
        }
        boolean reversed = this.compareOffsets(this.anchorUnit, this.anchorOffset, this.endUnit, this.endOffset) > 0;
        Element firstUnit = reversed ? this.endUnit : this.anchorUnit;
        int firstOffset = reversed ? this.endOffset : this.anchorOffset;
        Element lastUnit = reversed ? this.anchorUnit : this.endUnit;
        int lastOffset = reversed ? this.anchorOffset : this.endOffset;
        List<Element> units = this.unitsInRoot();
        int firstIndex = units.indexOf(firstUnit);
        int lastIndex = units.indexOf(lastUnit);
        if (firstIndex < 0 || lastIndex < firstIndex) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = firstIndex; i <= lastIndex; ++i) {
            int end;
            Element unit = units.get(i);
            int length = RichTextSelection.flattenedLength(unit);
            int start = i == firstIndex ? Math.min(firstOffset, length) : 0;
            int n = end = i == lastIndex ? Math.min(lastOffset, length) : length;
            if (i > firstIndex) {
                builder.append('\n');
            }
            if (start >= end) continue;
            builder.append(SelectionUnits.rawRangeForNormalizedRange(unit, start, end));
        }
        return builder.toString();
    }

    public int[] localRangeForUnit(Element unit) {
        int[] nArray;
        int end;
        if (unit == null || !this.isActive()) {
            return null;
        }
        if (this.anchorUnit == this.endUnit) {
            int[] nArray2;
            int max;
            if (unit != this.anchorUnit) {
                return null;
            }
            int min = Math.min(this.anchorOffset, this.endOffset);
            if (min == (max = Math.max(this.anchorOffset, this.endOffset))) {
                nArray2 = null;
            } else {
                int[] nArray3 = new int[2];
                nArray3[0] = min;
                nArray2 = nArray3;
                nArray3[1] = max;
            }
            return nArray2;
        }
        boolean reversed = this.compareOffsets(this.anchorUnit, this.anchorOffset, this.endUnit, this.endOffset) > 0;
        Element firstUnit = reversed ? this.endUnit : this.anchorUnit;
        int firstOffset = reversed ? this.endOffset : this.anchorOffset;
        Element lastUnit = reversed ? this.anchorUnit : this.endUnit;
        int lastOffset = reversed ? this.anchorOffset : this.endOffset;
        List<Element> units = this.unitsInRoot();
        int unitIndex = units.indexOf(unit);
        int firstIndex = units.indexOf(firstUnit);
        int lastIndex = units.indexOf(lastUnit);
        if (unitIndex < firstIndex || unitIndex > lastIndex || firstIndex < 0 || lastIndex < 0) {
            return null;
        }
        int length = RichTextSelection.flattenedLength(unit);
        int start = unitIndex == firstIndex ? Math.min(firstOffset, length) : 0;
        int n = end = unitIndex == lastIndex ? Math.min(lastOffset, length) : length;
        if (start >= end) {
            nArray = null;
        } else {
            int[] nArray4 = new int[2];
            nArray4[0] = start;
            nArray = nArray4;
            nArray4[1] = end;
        }
        return nArray;
    }

    public boolean coversUnit(Element unit) {
        if (unit == null) {
            return false;
        }
        if (unit == this.anchorUnit || unit == this.endUnit) {
            return true;
        }
        return this.localRangeForUnit(unit) != null;
    }

    public void moveLeft(boolean keepSelection) {
        Element previous;
        if (!this.hasAnchor()) {
            return;
        }
        if (!keepSelection && !this.collapsed() && this.isSingleObjectSelection()) {
            this.moveFocus(this.endUnit, Math.min(this.anchorOffset, this.endOffset), false);
            return;
        }
        int offset = this.endOffset;
        Element unit = this.endUnit;
        if (this.collapsed() && offset - 1 >= 0 && RichTextSelection.isObjectAt(unit, offset - 1)) {
            this.setRange(unit, offset - 1, unit, offset);
            return;
        }
        if (offset <= 0 && (previous = this.previousUnit(unit)) != null) {
            this.moveFocus(previous, RichTextSelection.flattenedLength(previous), keepSelection);
            return;
        }
        this.moveFocus(unit, offset - 1, keepSelection);
    }

    public void moveRight(boolean keepSelection) {
        Element next;
        if (!this.hasAnchor()) {
            return;
        }
        if (!keepSelection && !this.collapsed() && this.isSingleObjectSelection()) {
            this.moveFocus(this.endUnit, Math.max(this.anchorOffset, this.endOffset), false);
            return;
        }
        int offset = this.endOffset;
        Element unit = this.endUnit;
        if (this.collapsed() && RichTextSelection.isObjectAt(unit, offset)) {
            this.setRange(unit, offset, unit, offset + 1);
            return;
        }
        if (offset >= RichTextSelection.flattenedLength(unit) && (next = this.nextUnit(unit)) != null) {
            this.moveFocus(next, 0, keepSelection);
            return;
        }
        this.moveFocus(unit, offset + 1, keepSelection);
    }

    private boolean isSingleObjectSelection() {
        if (this.anchorUnit == null || this.anchorUnit != this.endUnit) {
            return false;
        }
        int min = Math.min(this.anchorOffset, this.endOffset);
        int max = Math.max(this.anchorOffset, this.endOffset);
        return max - min == 1 && RichTextSelection.isObjectAt(this.anchorUnit, min);
    }

    private static boolean isObjectAt(Element unit, int offset) {
        String flattened = SelectionUnits.flattenedSelectableText(unit);
        return flattened != null && offset >= 0 && offset < flattened.length() && flattened.charAt(offset) == '\ufffc';
    }

    public void moveUp(boolean keepSelection) {
        if (!this.hasAnchor()) {
            return;
        }
        this.moveFocus(this.endUnit, RichTextNavigation.lineMoveOffset(this.endUnit, this.endOffset, -1), keepSelection);
    }

    public void moveDown(boolean keepSelection) {
        if (!this.hasAnchor()) {
            return;
        }
        this.moveFocus(this.endUnit, RichTextNavigation.lineMoveOffset(this.endUnit, this.endOffset, 1), keepSelection);
    }

    public void moveToHome(boolean keepSelection) {
        if (!this.hasAnchor()) {
            return;
        }
        this.moveFocus(this.endUnit, RichTextNavigation.lineStartOffset(this.endUnit, this.endOffset), keepSelection);
    }

    public void moveToEnd(boolean keepSelection) {
        if (!this.hasAnchor()) {
            return;
        }
        this.moveFocus(this.endUnit, RichTextNavigation.lineEndOffset(this.endUnit, this.endOffset), keepSelection);
    }

    private void moveFocus(Element unit, int newOffset, boolean keepSelection) {
        if (unit == null) {
            return;
        }
        int clamped = RichTextSelection.clamp(newOffset, 0, RichTextSelection.flattenedLength(unit));
        if (!keepSelection) {
            this.setCollapsed(unit, clamped);
            return;
        }
        if (!this.collapsed()) {
            this.endUnit = unit;
            this.endOffset = clamped;
        } else {
            this.anchorUnit = this.endUnit;
            this.anchorOffset = this.endOffset;
            this.endUnit = unit;
            this.endOffset = clamped;
        }
        this.updateDirection();
        this.markDirty();
        this.notifyChange();
    }

    public RichTextRange toRange() {
        if (this.anchorUnit == null || this.endUnit == null) {
            return null;
        }
        if (this.anchorUnit != this.endUnit) {
            return null;
        }
        int min = Math.min(this.anchorOffset, this.endOffset);
        int max = Math.max(this.anchorOffset, this.endOffset);
        return RichTextRange.fromUnitOffsets(this.anchorUnit, min, max);
    }

    private void updateDirection() {
        if (this.anchorUnit == null || this.endUnit == null) {
            this.direction = "none";
            return;
        }
        if (this.anchorUnit == this.endUnit && this.anchorOffset == this.endOffset) {
            this.direction = "none";
            return;
        }
        this.direction = this.compareOffsets(this.anchorUnit, this.anchorOffset, this.endUnit, this.endOffset) < 0 ? "forward" : "backward";
    }

    private int compareOffsets(Element unitA, int offsetA, Element unitB, int offsetB) {
        if (unitA == unitB) {
            return Integer.compare(offsetA, offsetB);
        }
        List<Element> units = this.unitsInRoot();
        int a = units.indexOf(unitA);
        int b = units.indexOf(unitB);
        if (a < 0 || b < 0) {
            return Integer.compare(a < 0 ? -1 : a, b < 0 ? -1 : b);
        }
        return Integer.compare(a, b);
    }

    public List<Element> unitsInRoot() {
        Element root = RichTextSelection.richTextRoot(this.anchorUnit);
        if (root == null) {
            root = RichTextSelection.richTextRoot(this.endUnit);
        }
        if (root == null && this.owner != null) {
            root = RichTextSelection.richTextRoot(this.owner.getFocusedElement());
        }
        if (root == null) {
            return List.of();
        }
        ArrayList<Element> result = new ArrayList<Element>();
        if (SelectionUnits.isSelectionUnit(root)) {
            result.add(root);
        }
        RichTextSelection.collectUnits(root, result);
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
            RichTextSelection.collectUnits(childElement, out);
        }
    }

    private static Element richTextRoot(Element element) {
        Element current = element;
        while (current != null) {
            if (current instanceof RichText) {
                return current;
            }
            current = current.parentElement;
        }
        return null;
    }

    private Element previousUnit(Element unit) {
        List<Element> units = this.unitsInRoot();
        int index = units.indexOf(unit);
        return index > 0 ? units.get(index - 1) : null;
    }

    private Element nextUnit(Element unit) {
        List<Element> units = this.unitsInRoot();
        int index = units.indexOf(unit);
        return index >= 0 && index + 1 < units.size() ? units.get(index + 1) : null;
    }

    private static int flattenedLength(Element unit) {
        String flattened = SelectionUnits.flattenedSelectableText(unit);
        return flattened == null ? 0 : flattened.length();
    }

    private static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        return Math.min(value, max);
    }

    private void markDirty() {
        if (this.owner == null) {
            return;
        }
        this.owner.markDirty(1);
    }

    private void notifyChange() {
        if (this.owner == null) {
            return;
        }
        if (this.anchorUnit == this.lastNotifiedAnchorUnit && this.anchorOffset == this.lastNotifiedAnchorOffset && this.endUnit == this.lastNotifiedEndUnit && this.endOffset == this.lastNotifiedEndOffset) {
            return;
        }
        this.lastNotifiedAnchorUnit = this.anchorUnit;
        this.lastNotifiedAnchorOffset = this.anchorOffset;
        this.lastNotifiedEndUnit = this.endUnit;
        this.lastNotifiedEndOffset = this.endOffset;
        this.owner.dispatchSelectionChange();
    }
}

