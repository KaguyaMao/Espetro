/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.canvas;

import com.sighs.apricityui.canvas.CanvasState;
import com.sighs.apricityui.canvas.CanvasStyleUtil;
import com.sighs.apricityui.canvas.CanvasTextMetrics;
import com.sighs.apricityui.element.Canvas;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.util.Locale;

final class CanvasTextSupport {
    private CanvasTextSupport() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static CanvasTextMetrics measureText(Canvas canvas, CanvasState state, String text) {
        if (text == null) {
            return new CanvasTextMetrics(0.0);
        }
        Graphics2D g = canvas.getSurface().createGraphics();
        try {
            Canvas.applyGraphicsDefaults(g);
            Font font = CanvasStyleUtil.parseFont(state.font);
            FontMetrics metrics = g.getFontMetrics(font);
            CanvasTextMetrics canvasTextMetrics = new CanvasTextMetrics(metrics.stringWidth(text));
            return canvasTextMetrics;
        }
        finally {
            g.dispose();
        }
    }

    static Shape buildTextOutline(Graphics2D g, CanvasState state, String text, double x, double y) {
        Font font = CanvasStyleUtil.parseFont(state.font);
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics(font);
        double drawX = CanvasTextSupport.resolveTextX(state, metrics, text, x);
        double drawY = CanvasTextSupport.resolveTextY(state, metrics, y);
        FontRenderContext frc = g.getFontRenderContext();
        GlyphVector glyphVector = font.createGlyphVector(frc, text);
        return glyphVector.getOutline((float)drawX, (float)drawY);
    }

    private static double resolveTextX(CanvasState state, FontMetrics metrics, String text, double x) {
        String align;
        int width = metrics.stringWidth(text == null ? "" : text);
        return switch (align = state.textAlign == null ? "start" : state.textAlign.toLowerCase(Locale.ROOT)) {
            case "center" -> x - (double)width / 2.0;
            case "right", "end" -> x - (double)width;
            default -> x;
        };
    }

    private static double resolveTextY(CanvasState state, FontMetrics metrics, double y) {
        String baseline;
        return switch (baseline = state.textBaseline == null ? "alphabetic" : state.textBaseline.toLowerCase(Locale.ROOT)) {
            case "top", "hanging" -> y + (double)metrics.getAscent();
            case "middle" -> y + (double)(metrics.getAscent() - metrics.getDescent()) / 2.0;
            case "bottom", "ideographic" -> y - (double)metrics.getDescent();
            default -> y;
        };
    }
}

