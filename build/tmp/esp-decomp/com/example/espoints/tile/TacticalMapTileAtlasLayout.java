/*
 * Decompiled with CFR 0.152.
 */
package com.example.espoints.tile;

import com.example.espoints.tile.TacticalMapPyramidLayout;
import com.example.espoints.tile.TacticalMapTileScreenMath;
import java.util.List;

public final class TacticalMapTileAtlasLayout {
    static final int MAX_ATLAS_EDGE = 4096;
    static final long MAX_ATLAS_PIXELS = 8000000L;

    private TacticalMapTileAtlasLayout() {
    }

    public static Spec spec(TacticalMapPyramidLayout layout, int level, List<TacticalMapPyramidLayout.TileCoordinate> tiles) {
        if (layout == null || tiles == null || tiles.isEmpty() || !layout.isValid(level, 0, 0)) {
            return null;
        }
        int minTileX = Integer.MAX_VALUE;
        int maxTileX = Integer.MIN_VALUE;
        int minTileY = Integer.MAX_VALUE;
        int maxTileY = Integer.MIN_VALUE;
        for (TacticalMapPyramidLayout.TileCoordinate tile : tiles) {
            if (tile == null || tile.level() != level || !layout.isValid(level, tile.x(), tile.y())) {
                return null;
            }
            minTileX = Math.min(minTileX, tile.x());
            maxTileX = Math.max(maxTileX, tile.x());
            minTileY = Math.min(minTileY, tile.y());
            maxTileY = Math.max(maxTileY, tile.y());
        }
        int expected = (maxTileX - minTileX + 1) * (maxTileY - minTileY + 1);
        if (tiles.size() != expected) {
            return null;
        }
        int width = (maxTileX - minTileX) * 512 + layout.tileWidth(level, maxTileX);
        int height = (maxTileY - minTileY) * 512 + layout.tileHeight(level, maxTileY);
        if (width <= 0 || height <= 0 || width > 4096 || height > 4096 || (long)width * (long)height > 8000000L) {
            return null;
        }
        return new Spec(level, minTileX, minTileY, maxTileX, maxTileY, width, height);
    }

    public static int atlasX(Spec spec, int tileX) {
        return (tileX - spec.minTileX()) * 512;
    }

    public static int atlasY(Spec spec, int tileY) {
        return (tileY - spec.minTileY()) * 512;
    }

    public static void stampRgba(int[] dest, int destWidth, int destHeight, int[] source, int sourceWidth, int sourceHeight, int destX, int destY) {
        if (dest == null || source == null || destWidth <= 0 || destHeight <= 0 || sourceWidth <= 0 || sourceHeight <= 0 || dest.length < destWidth * destHeight || source.length < sourceWidth * sourceHeight) {
            throw new IllegalArgumentException("Invalid atlas stamp buffers");
        }
        for (int row = 0; row < sourceHeight; ++row) {
            int copyRight;
            int targetY = destY + row;
            if (targetY < 0 || targetY >= destHeight) continue;
            int sourceOffset = row * sourceWidth;
            int copyLeft = Math.max(0, -destX);
            if (copyLeft >= (copyRight = Math.min(sourceWidth, destWidth - destX))) continue;
            System.arraycopy(source, sourceOffset + copyLeft, dest, targetY * destWidth + destX + copyLeft, copyRight - copyLeft);
        }
    }

    public record Spec(int level, int minTileX, int minTileY, int maxTileX, int maxTileY, int width, int height) {
        public TacticalMapTileScreenMath.PixelRect pixels() {
            return new TacticalMapTileScreenMath.PixelRect(this.minTileX * 512, this.minTileY * 512, this.minTileX * 512 + this.width, this.minTileY * 512 + this.height);
        }
    }
}

