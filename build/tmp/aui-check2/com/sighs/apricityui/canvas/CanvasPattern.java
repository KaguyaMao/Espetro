/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.canvas;

import com.sighs.apricityui.canvas.DOMMatrix;
import java.awt.Paint;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Locale;

public class CanvasPattern {
    private final BufferedImage image;
    private final String repetition;
    private AffineTransform transform = new AffineTransform();

    public CanvasPattern(BufferedImage image, String repetition) {
        this.image = image;
        this.repetition = CanvasPattern.normalizeRepetition(repetition);
    }

    Paint toPaint() {
        if (this.image == null || this.image.getWidth() <= 0 || this.image.getHeight() <= 0) {
            return null;
        }
        return new TexturePaint(this.image, new Rectangle2D.Double(0.0, 0.0, this.image.getWidth(), this.image.getHeight()));
    }

    public String getRepetition() {
        return this.repetition;
    }

    public void setTransform(double a, double b, double c, double d, double e, double f) {
        this.transform = new AffineTransform(a, b, c, d, e, f);
    }

    public void setTransform(Object matrix) {
        this.transform = DOMMatrix.from(matrix);
    }

    public void resetTransform() {
        this.transform = new AffineTransform();
    }

    AffineTransform getTransform() {
        return new AffineTransform(this.transform);
    }

    BufferedImage getImage() {
        return this.image;
    }

    private static String normalizeRepetition(String value) {
        String normalized;
        if (value == null || value.isBlank()) {
            return "repeat";
        }
        return switch (normalized = value.trim().toLowerCase(Locale.ROOT)) {
            case "repeat-x", "repeat-y", "no-repeat" -> normalized;
            default -> "repeat";
        };
    }
}

