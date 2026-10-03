/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.canvas;

import com.sighs.apricityui.canvas.CanvasStyleUtil;
import com.sighs.apricityui.canvas.DOMMatrix;
import java.awt.Color;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class CanvasRadialGradient {
    private final float x0;
    private final float y0;
    private final float r0;
    private final float x1;
    private final float y1;
    private final float r1;
    private final List<GradientStop> stops = new ArrayList<GradientStop>();
    private AffineTransform transform = new AffineTransform();

    public CanvasRadialGradient(float x0, float y0, float r0, float x1, float y1, float r1) {
        this.x0 = x0;
        this.y0 = y0;
        this.r0 = Math.max(0.0f, r0);
        this.x1 = x1;
        this.y1 = y1;
        this.r1 = Math.max(0.0f, r1);
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
        float endRadius = Math.max(this.r1, 0.001f);
        float focusDistance = (float)Point2D.distance(this.x0, this.y0, this.x1, this.y1);
        if (focusDistance > endRadius) {
            float scale = endRadius / focusDistance * 0.999f;
            float dx = this.x0 - this.x1;
            float dy = this.y0 - this.y1;
            AffineTransform paintTransform = new AffineTransform(this.transform);
            return new RadialGradientPaint(new Point2D.Float(this.x1, this.y1), endRadius, new Point2D.Float(this.x1 + dx * scale, this.y1 + dy * scale), fractions, colors, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, paintTransform);
        }
        if (this.r0 > 0.0f && this.r1 > this.r0) {
            float ratio = this.r0 / this.r1;
            AffineTransform paintTransform = new AffineTransform(this.transform);
            paintTransform.translate(this.x0 - this.x1 * ratio, this.y0 - this.y1 * ratio);
            paintTransform.scale(ratio, ratio);
            return new RadialGradientPaint(new Point2D.Float(this.x1, this.y1), endRadius, new Point2D.Float(this.x0, this.y0), fractions, colors, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, paintTransform);
        }
        return new RadialGradientPaint(new Point2D.Float(this.x1, this.y1), endRadius, new Point2D.Float(this.x0, this.y0), fractions, colors, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform(this.transform));
    }

    private record GradientStop(float offset, Color color) {
    }
}

