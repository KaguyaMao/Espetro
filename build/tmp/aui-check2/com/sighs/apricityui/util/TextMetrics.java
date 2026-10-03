/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.util;

import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Flex;
import com.sighs.apricityui.layout.Layout;
import com.sighs.apricityui.parser.Color;
import com.sighs.apricityui.style.Text;

public final class TextMetrics {
    private TextMetrics() {
    }

    public static void copyTextForRun(Text base, Text out) {
        out.fontSize = base.fontSize;
        out.fontWeight = base.fontWeight;
        out.oblique = base.oblique;
        out.strokeWidth = base.strokeWidth;
        out.strokeColor = base.strokeColor;
        out.color = base.color;
        out.textDecoration = base.textDecoration;
        out.fontFamily = base.fontFamily;
        out.lineHeight = base.lineHeight;
        out.direction = base.direction;
        out.textAlign = base.textAlign;
        out.verticalAlign = base.verticalAlign;
        out.whiteSpace = base.whiteSpace;
        out.fontMode = base.fontMode;
        out.textIndent = 0.0;
        out.letterSpacing = base.letterSpacing;
        out.size = null;
        out.rasterBackgroundColor = base.rasterBackgroundColor;
    }

    public static Text cloneTextForSegment(Text base, String content, Color fallbackStrokeColor) {
        Text copy = new Text();
        TextMetrics.copyTextForRun(base, copy);
        copy.strokeColor = base.strokeColor == null ? fallbackStrokeColor : base.strokeColor;
        copy.color = base.color == null ? Color.BLACK : base.color;
        copy.flexDirect = base.flexDirect;
        copy.content = content == null ? "" : content;
        return copy;
    }

    public static double computeAlignedX(Text text, double contentWidth, double lineWidth, boolean firstLine) {
        double indent;
        double alignOffset = switch (TextMetrics.resolveLogicalTextAlign(text)) {
            case "center" -> (contentWidth - lineWidth) / 2.0;
            case "right" -> contentWidth - lineWidth;
            default -> 0.0;
        };
        double d = indent = firstLine ? text.textIndent : 0.0;
        if (text.isRtl()) {
            indent = -indent;
        }
        return alignOffset + indent;
    }

    public static String resolveLogicalTextAlign(Text text) {
        String align;
        String string = align = text.textAlign == null ? "start" : text.textAlign;
        if (align.equals("start")) {
            return text.isRtl() ? "right" : "left";
        }
        if (align.equals("end")) {
            return text.isRtl() ? "left" : "right";
        }
        if (align.equals("justify")) {
            return text.isRtl() ? "right" : "left";
        }
        return align;
    }

    public static double computeVerticalOffset(Text text, double contentHeight, double textHeight) {
        String align;
        return switch (align = text.verticalAlign == null ? "top" : text.verticalAlign) {
            case "middle", "center" -> (contentHeight - textHeight) / 2.0;
            case "bottom", "text-bottom" -> contentHeight - textHeight;
            default -> 0.0;
        };
    }

    public static double computeFlexTextAlignedX(Element element, Text text, double contentWidth, double lineWidth) {
        String align;
        if (element != null && Layout.isFlexDisplay(element.getComputedStyle().display)) {
            Flex flex = Flex.of(element);
            if (flex.flexDirection.contains("column")) {
                String align2 = flex.alignItems.value();
                if ("center".equals(align2)) {
                    return (contentWidth - lineWidth) / 2.0;
                }
                if ("flex-end".equals(align2) || "end".equals(align2)) {
                    return contentWidth - lineWidth;
                }
            } else {
                String justify = flex.justifyContent.value();
                if ("center".equals(justify)) {
                    return (contentWidth - lineWidth) / 2.0;
                }
                if ("flex-end".equals(justify) || "end".equals(justify)) {
                    return contentWidth - lineWidth;
                }
            }
        }
        String string = align = text == null || text.textAlign == null ? "start" : TextMetrics.resolveLogicalTextAlign(text);
        if ("center".equals(align)) {
            return (contentWidth - lineWidth) / 2.0;
        }
        if ("right".equals(align)) {
            return contentWidth - lineWidth;
        }
        return 0.0;
    }

    public static double computeFlexTextAlignedY(Element element, Text text, double contentHeight) {
        if (element == null || text == null) {
            return 0.0;
        }
        if (Layout.isFlexDisplay(element.getComputedStyle().display)) {
            Flex flex = Flex.of(element);
            if (flex.flexDirection.contains("column")) {
                String justify = flex.justifyContent.value();
                if ("center".equals(justify)) {
                    return (contentHeight - text.lineHeight) / 2.0;
                }
                if ("flex-end".equals(justify) || "end".equals(justify)) {
                    return contentHeight - text.lineHeight;
                }
            } else {
                String align = flex.alignItems.value();
                if ("center".equals(align)) {
                    return (contentHeight - text.lineHeight) / 2.0;
                }
                if ("flex-end".equals(align) || "end".equals(align)) {
                    return contentHeight - text.lineHeight;
                }
            }
        }
        return 0.0;
    }

    public static String ellipsize(Text text, String content, double maxWidth, boolean forceEllipsis) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        if (maxWidth <= 0.0) {
            return "";
        }
        String ellipsis = "...";
        double ellipsisWidth = Text.measureLine(text, ellipsis);
        if (ellipsisWidth >= maxWidth) {
            return "";
        }
        if (!forceEllipsis && Text.measureLine(text, content) <= maxWidth) {
            return content;
        }
        if (forceEllipsis && Text.measureLine(text, content + ellipsis) <= maxWidth) {
            return content + ellipsis;
        }
        for (int end = content.length(); end > 0; --end) {
            String candidate = content.substring(0, end) + ellipsis;
            if (!(Text.measureLine(text, candidate) <= maxWidth)) continue;
            return candidate;
        }
        return ellipsis;
    }
}

