/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.behavior.richtext;

import com.sighs.apricityui.behavior.richtext.RangeBridge;
import com.sighs.apricityui.behavior.richtext.RichTextRange;
import com.sighs.apricityui.behavior.richtext.RichTextSelection;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Node;

public class SelectionBridge {
    private final Document document;

    public SelectionBridge(Document document) {
        this.document = document;
    }

    private RichTextSelection selection() {
        return this.document == null ? null : this.document.getRichTextSelection();
    }

    public Node getAnchorNode() {
        RichTextSelection s = this.selection();
        if (s == null || !s.hasAnchor() || s.getAnchorUnit() == null) {
            return null;
        }
        return RichTextRange.fromUnitOffset(s.getAnchorUnit(), s.getAnchorOffset()).container();
    }

    public int getAnchorOffset() {
        RichTextSelection s = this.selection();
        if (s == null || !s.hasAnchor() || s.getAnchorUnit() == null) {
            return 0;
        }
        RichTextRange.RichTextEndpoint ep = RichTextRange.fromUnitOffset(s.getAnchorUnit(), s.getAnchorOffset());
        return ep == null ? 0 : ep.offset();
    }

    public Node getFocusNode() {
        RichTextSelection s = this.selection();
        if (s == null || !s.hasAnchor() || s.getEndUnit() == null) {
            return null;
        }
        return RichTextRange.fromUnitOffset(s.getEndUnit(), s.getEndOffset()).container();
    }

    public int getFocusOffset() {
        RichTextSelection s = this.selection();
        if (s == null || !s.hasAnchor() || s.getEndUnit() == null) {
            return 0;
        }
        RichTextRange.RichTextEndpoint ep = RichTextRange.fromUnitOffset(s.getEndUnit(), s.getEndOffset());
        return ep == null ? 0 : ep.offset();
    }

    public int getRangeCount() {
        RichTextSelection s = this.selection();
        return s != null && s.hasAnchor() ? 1 : 0;
    }

    public RangeBridge getRangeAt(int index) {
        RichTextSelection s = this.selection();
        if (s == null || !s.hasAnchor() || index != 0 || s.getAnchorUnit() == null) {
            return null;
        }
        return RangeBridge.fromUnitOffsets(s.getAnchorUnit(), Math.min(s.getAnchorOffset(), s.getEndOffset()), Math.max(s.getAnchorOffset(), s.getEndOffset()));
    }

    public void setBaseAndExtent(Node anchorNode, int anchorOffset, Node focusNode, int focusOffset) {
        RichTextSelection s = this.selection();
        if (s == null) {
            return;
        }
        RangeBridge.RangeAnchor anchor = RangeBridge.resolveAnchor(anchorNode, anchorOffset);
        RangeBridge.RangeAnchor focus = RangeBridge.resolveAnchor(focusNode, focusOffset);
        if (anchor == null || focus == null) {
            return;
        }
        if (anchor.unit() == focus.unit()) {
            s.setRange(anchor.unit(), anchor.offset(), focus.unit(), focus.offset());
        } else {
            s.setRange(anchor.unit(), anchor.offset(), anchor.unit(), RangeBridge.toUnitOffset(anchor.unit(), focusNode, focusOffset));
        }
    }

    public void removeAllRanges() {
        RichTextSelection s = this.selection();
        if (s != null) {
            s.clear();
        }
    }

    public void collapse(Node node, int offset) {
        RichTextSelection s = this.selection();
        if (s == null) {
            return;
        }
        RangeBridge.RangeAnchor anchor = RangeBridge.resolveAnchor(node, offset);
        if (anchor == null) {
            return;
        }
        s.setCollapsed(anchor.unit(), anchor.offset());
    }

    public void extend(Node node, int offset) {
        RichTextSelection s = this.selection();
        if (s == null) {
            return;
        }
        RangeBridge.RangeAnchor focus = RangeBridge.resolveAnchor(node, offset);
        if (focus == null) {
            return;
        }
        s.extendTo(focus.unit(), focus.offset());
    }
}

