/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.behavior.richtext;

import com.sighs.apricityui.behavior.SelectionUnits;
import com.sighs.apricityui.behavior.richtext.RichTextRange;
import com.sighs.apricityui.dom.TextNode;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.init.Node;
import java.util.ArrayList;
import java.util.List;

public class RangeBridge {
    private Element unit;
    private int startNorm;
    private int endNorm;

    public RangeBridge() {
        this(null, 0, 0);
    }

    private RangeBridge(Element unit, int startNorm, int endNorm) {
        this.unit = unit;
        this.startNorm = Math.min(startNorm, endNorm);
        this.endNorm = Math.max(startNorm, endNorm);
    }

    public static RangeBridge fromUnitOffsets(Element unit, int startNorm, int endNorm) {
        if (unit == null) {
            return null;
        }
        return new RangeBridge(unit, startNorm, endNorm);
    }

    public static RangeBridge fromNodeOffsets(Node startNode, int startOffset, Node endNode, int endOffset) {
        Element startUnit = RangeBridge.resolveUnitOf(startNode);
        Element endUnit = RangeBridge.resolveUnitOf(endNode);
        if (startUnit == null || endUnit == null) {
            return null;
        }
        if (startUnit != endUnit) {
            return RangeBridge.fromUnitOffsets(startUnit, RangeBridge.toUnitOffset(startUnit, startNode, startOffset), RangeBridge.toUnitOffset(startUnit, endNode, endOffset));
        }
        return RangeBridge.fromUnitOffsets(startUnit, RangeBridge.toUnitOffset(startUnit, startNode, startOffset), RangeBridge.toUnitOffset(startUnit, endNode, endOffset));
    }

    public Element getUnit() {
        return this.unit;
    }

    public Node getStartContainer() {
        if (this.unit == null) {
            return null;
        }
        return RichTextRange.fromUnitOffset(this.unit, this.startNorm).container();
    }

    public int getStartOffset() {
        if (this.unit == null) {
            return 0;
        }
        return RichTextRange.fromUnitOffset(this.unit, this.startNorm).offset();
    }

    public Node getEndContainer() {
        if (this.unit == null) {
            return null;
        }
        return RichTextRange.fromUnitOffset(this.unit, this.endNorm).container();
    }

    public int getEndOffset() {
        if (this.unit == null) {
            return 0;
        }
        return RichTextRange.fromUnitOffset(this.unit, this.endNorm).offset();
    }

    public boolean getCollapsed() {
        return this.startNorm == this.endNorm;
    }

    public void setStart(Node node, int offset) {
        Element targetUnit = RangeBridge.resolveUnitOf(node);
        if (targetUnit == null) {
            return;
        }
        if (this.unit == null) {
            this.unit = targetUnit;
        }
        this.startNorm = RangeBridge.toUnitOffset(this.unit, node, offset);
        if (this.startNorm > this.endNorm) {
            this.endNorm = this.startNorm;
        }
    }

    public void setEnd(Node node, int offset) {
        Element targetUnit = RangeBridge.resolveUnitOf(node);
        if (targetUnit == null) {
            return;
        }
        if (this.unit == null) {
            this.unit = targetUnit;
        }
        this.endNorm = RangeBridge.toUnitOffset(this.unit, node, offset);
        if (this.endNorm < this.startNorm) {
            this.startNorm = this.endNorm;
        }
    }

    public void collapse(boolean toStart) {
        if (toStart) {
            this.endNorm = this.startNorm;
        } else {
            this.startNorm = this.endNorm;
        }
    }

    public String toString() {
        if (this.unit == null || this.startNorm == this.endNorm) {
            return "";
        }
        RichTextRange range = RichTextRange.fromUnitOffsets(this.unit, this.startNorm, this.endNorm);
        return range == null ? "" : range.toString();
    }

    public void selectNodeContents(Element element) {
        Element targetUnit = RangeBridge.resolveUnitOf(element);
        if (targetUnit == null) {
            return;
        }
        String flat = SelectionUnits.flattenedSelectableText(targetUnit);
        int length = flat == null ? 0 : flat.length();
        this.unit = targetUnit;
        this.startNorm = 0;
        this.endNorm = length;
    }

    public int[] toUnitOffsets() {
        return new int[]{this.startNorm, this.endNorm};
    }

    public static int toUnitOffset(Element unit, Node node, int offset) {
        if (unit == null || node == null) {
            return 0;
        }
        return RichTextRange.toUnitOffset(unit, new RichTextRange.RichTextEndpoint(node, offset));
    }

    public static RangeAnchor resolveAnchor(Node node, int offset) {
        Element unit = RangeBridge.resolveUnitOf(node);
        if (unit == null) {
            return null;
        }
        return new RangeAnchor(unit, RangeBridge.toUnitOffset(unit, node, offset));
    }

    public static Element resolveUnitOf(Node node) {
        Element parent;
        Node node2;
        Element element;
        Element e;
        if (node == null) {
            return null;
        }
        Element element2 = node instanceof Element ? (e = (Element)node) : (element = (node2 = node.getParentNode()) instanceof Element ? (parent = (Element)node2) : null);
        if (element == null) {
            return null;
        }
        return SelectionUnits.resolveUnit(element);
    }

    static List<TextNode> collectTextNodes(Element root, int whatToShow) {
        ArrayList<TextNode> result = new ArrayList<TextNode>();
        if (root == null) {
            return result;
        }
        RangeBridge.collectTextNodesRecursive(root, whatToShow, result);
        return result;
    }

    private static void collectTextNodesRecursive(Node current, int whatToShow, List<TextNode> out) {
        for (Node child : current.getChildNodes()) {
            if (child instanceof TextNode) {
                TextNode textNode = (TextNode)child;
                out.add(textNode);
                continue;
            }
            if (!(child instanceof Element)) continue;
            Element childElement = (Element)child;
            RangeBridge.collectTextNodesRecursive(childElement, whatToShow, out);
        }
    }

    public record RangeAnchor(Element unit, int offset) {
    }
}

