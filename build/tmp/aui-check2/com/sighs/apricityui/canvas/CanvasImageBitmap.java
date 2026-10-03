/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.canvas;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class CanvasImageBitmap
implements AutoCloseable {
    private BufferedImage image;

    public CanvasImageBitmap(BufferedImage image) {
        this.image = image;
    }

    BufferedImage image() {
        return this.image;
    }

    public int getWidth() {
        return this.image == null ? 0 : this.image.getWidth();
    }

    public int getHeight() {
        return this.image == null ? 0 : this.image.getHeight();
    }

    public boolean isClosed() {
        return this.image == null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CanvasImageBitmap crop(int sx, int sy, int sw, int sh) {
        if (this.image == null || sw <= 0 || sh <= 0) {
            return new CanvasImageBitmap(null);
        }
        BufferedImage cropped = new BufferedImage(Math.max(1, sw), Math.max(1, sh), 2);
        Graphics2D g = cropped.createGraphics();
        try {
            g.drawImage(this.image, 0, 0, sw, sh, sx, sy, sx + sw, sy + sh, null);
        }
        finally {
            g.dispose();
        }
        return new CanvasImageBitmap(cropped);
    }

    @Override
    public void close() {
        this.image = null;
    }
}

