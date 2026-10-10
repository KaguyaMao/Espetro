/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.canvas;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

final class CanvasState {
    static final String DEFAULT_FONT = "16px SansSerif";
    Object fillStyle = "#000000";
    Object strokeStyle = "#000000";
    double lineWidth = 1.0;
    String lineCap = "butt";
    String lineJoin = "miter";
    double miterLimit = 10.0;
    double[] lineDash = new double[0];
    double lineDashOffset = 0.0;
    double globalAlpha = 1.0;
    String globalCompositeOperation = "source-over";
    String font = "16px SansSerif";
    String textAlign = "start";
    String textBaseline = "alphabetic";
    String shadowColor = "transparent";
    double shadowBlur = 0.0;
    double shadowOffsetX = 0.0;
    double shadowOffsetY = 0.0;
    String filter = "none";
    boolean imageSmoothingEnabled = true;
    String imageSmoothingQuality = "medium";
    AffineTransform transform = new AffineTransform();
    Shape clip = null;

    CanvasState() {
    }

    CanvasState copy() {
        CanvasState copy = new CanvasState();
        copy.fillStyle = this.fillStyle;
        copy.strokeStyle = this.strokeStyle;
        copy.lineWidth = this.lineWidth;
        copy.lineCap = this.lineCap;
        copy.lineJoin = this.lineJoin;
        copy.miterLimit = this.miterLimit;
        copy.lineDash = (double[])this.lineDash.clone();
        copy.lineDashOffset = this.lineDashOffset;
        copy.globalAlpha = this.globalAlpha;
        copy.globalCompositeOperation = this.globalCompositeOperation;
        copy.font = this.font;
        copy.textAlign = this.textAlign;
        copy.textBaseline = this.textBaseline;
        copy.shadowColor = this.shadowColor;
        copy.shadowBlur = this.shadowBlur;
        copy.shadowOffsetX = this.shadowOffsetX;
        copy.shadowOffsetY = this.shadowOffsetY;
        copy.filter = this.filter;
        copy.imageSmoothingEnabled = this.imageSmoothingEnabled;
        copy.imageSmoothingQuality = this.imageSmoothingQuality;
        copy.transform = new AffineTransform(this.transform);
        copy.clip = this.clip == null ? null : new Area(this.clip);
        return copy;
    }
}

