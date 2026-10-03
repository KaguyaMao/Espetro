/*
 * Decompiled with CFR 0.152.
 */
package com.example.espoints.tile;

import com.example.espoints.tile.TacticalMapPyramidLayout;

public final class TacticalMapTileScreenMath {
    private TacticalMapTileScreenMath() {
    }

    public static PixelRect tilePixels(TacticalMapPyramidLayout layout, int level, int tileX, int tileY) {
        int left = tileX * 512;
        int top = tileY * 512;
        return new PixelRect(left, top, left + layout.tileWidth(level, tileX), top + layout.tileHeight(level, tileY));
    }

    public static IntRect project(PixelRect pixels, int levelWidth, int levelHeight, double boundsMinX, double boundsMinZ, double boundsWidth, double boundsHeight, double viewMinX, double viewMinZ, double scaleX, double scaleZ, int screenLeft, int screenTop) {
        if (pixels == null || pixels.isEmpty() || levelWidth <= 0 || levelHeight <= 0 || !Double.isFinite(boundsWidth) || !Double.isFinite(boundsHeight) || boundsWidth <= 0.0 || boundsHeight <= 0.0 || !Double.isFinite(scaleX) || !Double.isFinite(scaleZ)) {
            return new IntRect(0, 0, 0, 0);
        }
        double worldLeft = TacticalMapTileScreenMath.world(boundsMinX, pixels.left(), levelWidth, boundsWidth);
        double worldRight = TacticalMapTileScreenMath.world(boundsMinX, pixels.right(), levelWidth, boundsWidth);
        double worldTop = TacticalMapTileScreenMath.world(boundsMinZ, pixels.top(), levelHeight, boundsHeight);
        double worldBottom = TacticalMapTileScreenMath.world(boundsMinZ, pixels.bottom(), levelHeight, boundsHeight);
        int destLeft = (int)Math.round((double)screenLeft + (worldLeft - viewMinX) * scaleX);
        int destRight = (int)Math.round((double)screenLeft + (worldRight - viewMinX) * scaleX);
        int destTop = (int)Math.round((double)screenTop + (worldTop - viewMinZ) * scaleZ);
        int destBottom = (int)Math.round((double)screenTop + (worldBottom - viewMinZ) * scaleZ);
        return new IntRect(destLeft, destTop, destRight, destBottom);
    }

    public static BlitUv insetUv(int textureWidth, int textureHeight) {
        int width = Math.max(1, textureWidth);
        int height = Math.max(1, textureHeight);
        if (width <= 1 || height <= 1) {
            return new BlitUv(0.0f, 0.0f, width, height, width, height);
        }
        return new BlitUv(0.5f, 0.5f, width - 1, height - 1, width, height);
    }

    public static double maxU(BlitUv uv) {
        return (double)(uv.uOffset() + (float)uv.uWidth()) / (double)uv.textureWidth();
    }

    public static double maxV(BlitUv uv) {
        return (double)(uv.vOffset() + (float)uv.vHeight()) / (double)uv.textureHeight();
    }

    private static double world(double origin, int pixel, int levelSize, double span) {
        return origin + (double)pixel / (double)levelSize * span;
    }

    public record PixelRect(int left, int top, int right, int bottom) {
        public int width() {
            return Math.max(0, this.right - this.left);
        }

        public int height() {
            return Math.max(0, this.bottom - this.top);
        }

        public boolean isEmpty() {
            return this.width() <= 0 || this.height() <= 0;
        }
    }

    public record IntRect(int left, int top, int right, int bottom) {
        public int width() {
            return Math.max(0, this.right - this.left);
        }

        public int height() {
            return Math.max(0, this.bottom - this.top);
        }

        public boolean isEmpty() {
            return this.width() <= 0 || this.height() <= 0;
        }
    }

    public record BlitUv(float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight) {
    }
}

