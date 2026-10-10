/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.behavior;

import com.sighs.apricityui.behavior.SelectionUnits;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import java.util.List;

public final class DocumentSelection {
    private final Document owner;
    private Element anchorUnit = null;
    private int anchorOffset = 0;
    private Element endUnit = null;
    private int endOffset = 0;
    private boolean selecting = false;

    public DocumentSelection(Document owner) {
        this.owner = owner;
    }

    public void collapse(Element unit, int offset) {
        if (this.owner != null) {
            this.owner.bumpSelectionCache();
        }
        if (this.owner != null) {
            this.owner.clearRichTextSelection();
        }
        this.anchorUnit = unit;
        this.anchorOffset = offset;
        this.endUnit = unit;
        this.endOffset = offset;
        this.markDirty();
    }

    public void extendTo(Element unit, int offset) {
        if (this.anchorUnit == null) {
            this.collapse(unit, offset);
            return;
        }
        if (this.owner != null) {
            this.owner.bumpSelectionCache();
        }
        if (this.owner != null) {
            this.owner.clearRichTextSelection();
        }
        this.endUnit = unit;
        this.endOffset = offset;
        this.markDirty();
    }

    public void clear() {
        if (this.anchorUnit == null && this.endUnit == null && !this.selecting) {
            return;
        }
        if (this.owner != null) {
            this.owner.bumpSelectionCache();
        }
        this.anchorUnit = null;
        this.anchorOffset = 0;
        this.endUnit = null;
        this.endOffset = 0;
        this.selecting = false;
        this.markDirty();
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

    public Element getEndUnit() {
        return this.endUnit;
    }

    public int getAnchorOffset() {
        return this.anchorOffset;
    }

    public int getEndOffset() {
        return this.endOffset;
    }

    public boolean selectAll(Document document) {
        List<Element> units;
        if (this.owner != null) {
            this.owner.bumpSelectionCache();
        }
        if (this.owner != null) {
            this.owner.clearRichTextSelection();
        }
        if ((units = SelectionUnits.enumerateUnits(document)).isEmpty()) {
            return false;
        }
        Element first = units.get(0);
        Element last = units.get(units.size() - 1);
        this.anchorUnit = first;
        this.anchorOffset = 0;
        this.endUnit = last;
        this.endOffset = SelectionUnits.flattenedSelectableText(last).length();
        this.markDirty();
        return true;
    }

    public void selectUnit(Element unit) {
        if (unit == null) {
            return;
        }
        if (this.owner != null) {
            this.owner.bumpSelectionCache();
        }
        if (this.owner != null) {
            this.owner.clearRichTextSelection();
        }
        int length = SelectionUnits.flattenedSelectableText(unit).length();
        this.collapse(unit, 0);
        this.extendTo(unit, length);
    }

    public String getSelectedText(Document document) {
        int finalEndOffset;
        if (this.anchorUnit == null || this.endUnit == null) {
            return "";
        }
        List<Element> units = SelectionUnits.enumerateUnits(document);
        int anchorIndex = units.indexOf(this.anchorUnit);
        int endIndex = units.indexOf(this.endUnit);
        if (anchorIndex < 0 || endIndex < 0) {
            return "";
        }
        int minIndex = Math.min(anchorIndex, endIndex);
        int maxIndex = Math.max(anchorIndex, endIndex);
        boolean reversed = anchorIndex > endIndex || anchorIndex == endIndex && this.anchorOffset > this.endOffset;
        int startOffset = reversed ? this.endOffset : this.anchorOffset;
        int n = finalEndOffset = reversed ? this.anchorOffset : this.endOffset;
        if (minIndex == maxIndex) {
            int max;
            String text = SelectionUnits.flattenedSelectableText(this.anchorUnit);
            int min = Math.max(0, Math.min(startOffset, finalEndOffset));
            return min >= (max = Math.min(text.length(), Math.max(startOffset, finalEndOffset))) ? "" : SelectionUnits.rawRangeForNormalizedRange(this.anchorUnit, min, max);
        }
        StringBuilder builder = new StringBuilder();
        String firstText = SelectionUnits.flattenedSelectableText(units.get(minIndex));
        if (startOffset < firstText.length()) {
            builder.append(SelectionUnits.rawRangeForNormalizedRange(units.get(minIndex), startOffset, firstText.length()));
        }
        for (int i = minIndex + 1; i <= maxIndex; ++i) {
            String piece;
            int take;
            String text = SelectionUnits.flattenedSelectableText(units.get(i));
            int n2 = take = i == maxIndex ? Math.min(finalEndOffset, text.length()) : text.length();
            if (take <= 0 || (piece = SelectionUnits.rawRangeForNormalizedRange(units.get(i), 0, take)).isEmpty()) continue;
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(piece);
        }
        return builder.toString();
    }

    public int[] localRangeForUnit(Element unit) {
        int end;
        int start;
        if (unit == null || this.anchorUnit == null || this.endUnit == null) {
            return null;
        }
        List<Element> units = SelectionUnits.enumerateUnits(this.owner);
        int anchorIndex = units.indexOf(this.anchorUnit);
        int endIndex = units.indexOf(this.endUnit);
        if (anchorIndex < 0 || endIndex < 0) {
            return null;
        }
        int unitIndex = units.indexOf(unit);
        if (unitIndex < 0) {
            return null;
        }
        int minIndex = Math.min(anchorIndex, endIndex);
        int maxIndex = Math.max(anchorIndex, endIndex);
        if (unitIndex < minIndex || unitIndex > maxIndex) {
            return null;
        }
        int unitLength = SelectionUnits.flattenedSelectableText(unit).length();
        if (minIndex == maxIndex) {
            start = Math.min(this.anchorOffset, this.endOffset);
            end = Math.max(this.anchorOffset, this.endOffset);
        } else if (unitIndex == minIndex) {
            int boundary = anchorIndex < endIndex ? this.anchorOffset : this.endOffset;
            start = Math.min(boundary, unitLength);
            end = unitLength;
        } else if (unitIndex == maxIndex) {
            int boundary = anchorIndex > endIndex ? this.anchorOffset : this.endOffset;
            start = 0;
            end = Math.max(0, Math.min(boundary, unitLength));
        } else {
            start = 0;
            end = unitLength;
        }
        if (start >= end) {
            return null;
        }
        return new int[]{start, end};
    }

    public int unitIndex(Document document, Element unit) {
        if (document == null || unit == null) {
            return -1;
        }
        return SelectionUnits.enumerateUnits(document).indexOf(unit);
    }

    public boolean unitOrderedBefore(Document document, Element first, Element second) {
        if (first == null || second == null || first == second) {
            return false;
        }
        int a = this.unitIndex(document, first);
        int b = this.unitIndex(document, second);
        return a >= 0 && b >= 0 && a < b;
    }

    private void markDirty() {
        if (this.owner == null) {
            return;
        }
        this.owner.markDirty(1);
    }
}

