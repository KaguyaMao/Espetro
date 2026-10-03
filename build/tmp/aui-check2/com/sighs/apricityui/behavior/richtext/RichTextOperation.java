/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.behavior.richtext;

import com.sighs.apricityui.init.Element;

public record RichTextOperation(Element unit, String type, int start, int end, String text, String html, int cursorBefore, int cursorAfter) {
    public static RichTextOperation insertText(Element unit, int at, String insertedText, int before, int after) {
        return new RichTextOperation(unit, "insertText", at, at, insertedText, null, before, after);
    }

    public static RichTextOperation deleteText(Element unit, int at, String deletedText, int before, int after) {
        return new RichTextOperation(unit, "deleteText", at, at + deletedText.length(), deletedText, null, before, after);
    }

    public static RichTextOperation insertHtml(Element unit, int at, String html, int before, int after) {
        return new RichTextOperation(unit, "insertHtml", at, at, null, html, before, after);
    }

    public static RichTextOperation deleteHtml(Element unit, int at, int end, String deletedHtml, int before, int after) {
        return new RichTextOperation(unit, "deleteHtml", at, end, null, deletedHtml, before, after);
    }

    public static RichTextOperation insertBr(Element unit, int at, int before, int after) {
        return new RichTextOperation(unit, "insertBr", at, at, null, null, before, after);
    }

    public static RichTextOperation deleteBr(Element unit, int at, int before, int after) {
        return new RichTextOperation(unit, "deleteBr", at, at + 1, null, null, before, after);
    }

    public static RichTextOperation splitBlock(Element unit, int at, String newTag, int before, int after) {
        return new RichTextOperation(unit, "splitBlock", at, at, null, newTag, before, after);
    }

    public static RichTextOperation mergeBackward(Element unit, int mergeOffset, int before, int after) {
        return new RichTextOperation(unit, "mergeBackward", mergeOffset, mergeOffset, null, null, before, after);
    }

    public static RichTextOperation mergeForward(Element unit, int nextLength, String nextTag, int before, int after) {
        return new RichTextOperation(unit, "mergeForward", nextLength, nextLength, null, nextTag, before, after);
    }

    public RichTextOperation inverse() {
        return switch (this.type) {
            case "insertText" -> RichTextOperation.deleteText(this.unit, this.start, this.text, this.cursorAfter, this.cursorBefore);
            case "deleteText" -> RichTextOperation.insertText(this.unit, this.start, this.text, this.cursorAfter, this.cursorBefore);
            case "insertHtml" -> RichTextOperation.deleteHtml(this.unit, this.start, this.cursorAfter, this.html, this.cursorAfter, this.cursorBefore);
            case "deleteHtml" -> RichTextOperation.insertHtml(this.unit, this.start, this.html, this.cursorAfter, this.cursorBefore);
            case "insertBr" -> RichTextOperation.deleteBr(this.unit, this.start, this.cursorAfter, this.cursorBefore);
            case "deleteBr" -> RichTextOperation.insertBr(this.unit, this.start, this.cursorAfter, this.cursorBefore);
            case "splitBlock" -> RichTextOperation.mergeBackward(this.unit, this.start, this.cursorAfter, this.cursorBefore);
            case "mergeBackward" -> RichTextOperation.splitBlock(this.unit, this.start, RichTextOperation.blockTagOf(this.unit), this.cursorAfter, this.cursorBefore);
            case "mergeForward" -> RichTextOperation.splitBlock(this.unit, this.start, this.html, this.cursorAfter, this.cursorBefore);
            default -> throw new IllegalStateException("unknown operation type: " + this.type);
        };
    }

    private static String blockTagOf(Element unit) {
        return unit == null ? "P" : unit.tagName;
    }

    public boolean mergeableWith(RichTextOperation previous) {
        return previous != null && previous.unit == this.unit && "insertText".equals(previous.type) && "insertText".equals(this.type) && previous.start + previous.text.length() == this.start;
    }

    public RichTextOperation merge(RichTextOperation previous) {
        return new RichTextOperation(this.unit, "insertText", previous.start, this.start, previous.text + this.text, null, previous.cursorBefore, this.cursorAfter);
    }

    @Override
    public String toString() {
        return this.type + "(" + (this.unit == null ? "?" : this.unit.tagName) + "@" + this.start + "," + this.end + ",'" + this.text + "'," + this.html + ")";
    }
}

