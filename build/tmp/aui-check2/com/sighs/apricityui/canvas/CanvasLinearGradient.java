/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.canvas;

import com.sighs.apricityui.canvas.CanvasStyleUtil;
import com.sighs.apricityui.canvas.DOMMatrix;
import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class CanvasLinearGradient {
    private final float x0;
    private final float y0;
    private final float x1;
    private final float y1;
    private final List<GradientStop> stops = new ArrayList<GradientStop>();
    private AffineTransform transform = new AffineTransform();

    public CanvasLinearGradient(float x0, float y0, float x1, float y1) {
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
    }

    public void addColorStop(double offset, String color) {
        float safeOffset = (float)Math.max(0.0, Math.min(1.0, offset));
        this.stops.add(new GradientStop(safeOffset, CanvasStyleUtil.parseAwtColor(color)));
        this.stops.sort((a, b) -> Float.compare(a.offset, b.offset));
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

    Paint toPaint() {
        if (this.stops.isEmpty()) {
            return new Color(0, 0, 0, 255);
        }
        if (this.stops.size() == 1) {
            return this.stops.get((int)0).color;
        }
        float[] fractions = new float[this.stops.size()];
        Color[] colors = new Color[this.stops.size()];
        for (int i = 0; i < this.stops.size(); ++i) {
            fractions[i] = this.stops.get((int)i).offset;
            colors[i] = this.stops.get((int)i).color;
        }
        if (fractions[0] > 0.0f) {
            fractions[0] = 0.0f;
        }
        if (fractions[fractions.length - 1] < 1.0f) {
            fractions[fractions.length - 1] = 1.0f;
        }
        return new LinearGradientPaint(new Point2D.Float(this.x0, this.y0), new Point2D.Float(this.x1, this.y1), fractions, colors, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(this.transform));
    }

    private record GradientStop(float offset, Color color) {
    }
}

