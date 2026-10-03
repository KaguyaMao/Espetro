/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 */
package com.sighs.apricityui.canvas;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sighs.apricityui.canvas.CanvasImageBitmap;
import com.sighs.apricityui.element.Canvas;
import com.sighs.apricityui.render.Base;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class OffscreenCanvas
extends Canvas {
    public OffscreenCanvas(int width, int height) {
        super(null);
        this.setWidth(width);
        this.setHeight(height);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CanvasImageBitmap transferToImageBitmap() {
        BufferedImage surface = this.getSurface();
        BufferedImage snapshot = new BufferedImage(this.getWidth(), this.getHeight(), 2);
        Graphics2D g = snapshot.createGraphics();
        try {
            Canvas.applyGraphicsDefaults(g);
            g.drawImage((Image)surface, 0, 0, null);
        }
        finally {
            g.dispose();
        }
        this.getContext("2d").clear();
        return new CanvasImageBitmap(snapshot);
    }

    @Override
    public void drawPhase(PoseStack poseStack, Base.RenderPhase phase) {
    }
}

