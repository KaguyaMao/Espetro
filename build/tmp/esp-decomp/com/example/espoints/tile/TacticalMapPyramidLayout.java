/*
 * Decompiled with CFR 0.152.
 */
package com.example.espoints.tile;

import java.util.ArrayList;
import java.util.List;

public final class TacticalMapPyramidLayout {
    public static final int TILE_SIZE = 512;
    public static final int MAX_LEVELS = 16;
    public static final int MAX_DIMENSION = 32768;
    private final int width;
    private final int height;
    private final int maxLevel;

    public TacticalMapPyramidLayout(int width, int height) {
        int level;
        if (width <= 0 || height <= 0 || width > 32768 || height > 32768 || (long)width * (long)height > 0x4000000L) {
            throw new IllegalArgumentException("Invalid tactical map dimensions");
        }
        this.width = width;
        this.height = height;
        for (level = 0; (TacticalMapPyramidLayout.ceilDivPow2(width, level) > 512 || TacticalMapPyramidLayout.ceilDivPow2(height, level) > 512) && level < 15; ++level) {
        }
        this.maxLevel = level;
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }

    public int maxLevel() {
        return this.maxLevel;
    }

    public int levelWidth(int level) {
        this.checkedLevel(level);
        return TacticalMapPyramidLayout.ceilDivPow2(this.width, level);
    }

    public int levelHeight(int level) {
        this.checkedLevel(level);
        return TacticalMapPyramidLayout.ceilDivPow2(this.height, level);
    }

    public int columns(int level) {
        return TacticalMapPyramidLayout.ceilDiv(this.levelWidth(level), 512);
    }

    public int rows(int level) {
        return TacticalMapPyramidLayout.ceilDiv(this.levelHeight(level), 512);
    }

    public int tileWidth(int level, int tileX) {
        this.checkedLevel(level);
        if (tileX < 0 || tileX >= this.columns(level)) {
            throw new IllegalArgumentException("Invalid tile x");
        }
        return Math.min(512, this.levelWidth(level) - tileX * 512);
    }

    public int tileHeight(int level, int tileY) {
        this.checkedLevel(level);
        if (tileY < 0 || tileY >= this.rows(level)) {
            throw new IllegalArgumentException("Invalid tile y");
        }
        return Math.min(512, this.levelHeight(level) - tileY * 512);
    }

    public boolean isValid(int level, int tileX, int tileY) {
        return level >= 0 && level <= this.maxLevel && tileX >= 0 && tileX < this.columns(level) && tileY >= 0 && tileY < this.rows(level);
    }

    public int chooseLevel(double visibleFractionX, double visibleFractionY, int screenWidth, int screenHeight) {
        if (screenWidth <= 0 || screenHeight <= 0) {
            return this.maxLevel;
        }
        double sourcePixelsPerScreenPixel = Math.max((double)this.width * Math.max(0.0, Math.min(1.0, visibleFractionX)) / (double)screenWidth, (double)this.height * Math.max(0.0, Math.min(1.0, visibleFractionY)) / (double)screenHeight);
        if (sourcePixelsPerScreenPixel <= 1.0) {
            return 0;
        }
        int level = (int)Math.floor(Math.log(sourcePixelsPerScreenPixel) / Math.log(2.0));
        return Math.max(0, Math.min(this.maxLevel, level));
    }

    public List<TileCoordinate> visibleTiles(int level, double minX, double minY, double maxX, double maxY, int ring) {
        this.checkedLevel(level);
        if (ring < 0 || ring > 2) {
            throw new IllegalArgumentException("Invalid tile prefetch ring");
        }
        int width = this.levelWidth(level);
        int height = this.levelHeight(level);
        double epsilon = 1.0 / (double)Math.max(width, height);
        double clampedMinX = TacticalMapPyramidLayout.clampFraction(Math.min(minX, maxX) - epsilon);
        double clampedMaxX = TacticalMapPyramidLayout.clampFraction(Math.max(minX, maxX) + epsilon);
        double clampedMinY = TacticalMapPyramidLayout.clampFraction(Math.min(minY, maxY) - epsilon);
        double clampedMaxY = TacticalMapPyramidLayout.clampFraction(Math.max(minY, maxY) + epsilon);
        int minTileX = Math.max(0, (int)Math.floor(clampedMinX * (double)width / 512.0) - ring);
        int maxTileX = Math.min(this.columns(level) - 1, (int)Math.ceil(clampedMaxX * (double)width / 512.0) - 1 + ring);
        int minTileY = Math.max(0, (int)Math.floor(clampedMinY * (double)height / 512.0) - ring);
        int maxTileY = Math.min(this.rows(level) - 1, (int)Math.ceil(clampedMaxY * (double)height / 512.0) - 1 + ring);
        if (maxTileX < minTileX) {
            maxTileX = minTileX;
        }
        if (maxTileY < minTileY) {
            maxTileY = minTileY;
        }
        ArrayList<TileCoordinate> result = new ArrayList<TileCoordinate>();
        for (int y = minTileY; y <= maxTileY; ++y) {
            for (int x = minTileX; x <= maxTileX; ++x) {
                result.add(new TileCoordinate(level, x, y));
            }
        }
        return result;
    }

    public boolean tilesCover(int level, List<TileCoordinate> tiles, double minX, double minY, double maxX, double maxY) {
        if (tiles == null || tiles.isEmpty() || !this.isValid(level, 0, 0)) {
            return false;
        }
        int minTileX = Integer.MAX_VALUE;
        int maxTileX = Integer.MIN_VALUE;
        int minTileY = Integer.MAX_VALUE;
        int maxTileY = Integer.MIN_VALUE;
        for (TileCoordinate tile : tiles) {
            if (tile == null || tile.level() != level) {
                return false;
            }
            minTileX = Math.min(minTileX, tile.x());
            maxTileX = Math.max(maxTileX, tile.x());
            minTileY = Math.min(minTileY, tile.y());
            maxTileY = Math.max(maxTileY, tile.y());
        }
        int expected = (maxTileX - minTileX + 1) * (maxTileY - minTileY + 1);
        if (tiles.size() != expected) {
            return false;
        }
        double left = (double)minTileX * 512.0 / (double)this.levelWidth(level);
        double top = (double)minTileY * 512.0 / (double)this.levelHeight(level);
        double right = ((double)maxTileX * 512.0 + (double)this.tileWidth(level, maxTileX)) / (double)this.levelWidth(level);
        double bottom = ((double)maxTileY * 512.0 + (double)this.tileHeight(level, maxTileY)) / (double)this.levelHeight(level);
        double epsilon = 1.0 / (double)Math.max(this.levelWidth(level), this.levelHeight(level));
        return left <= minX + epsilon && top <= minY + epsilon && right >= maxX - epsilon && bottom >= maxY - epsilon;
    }

    private void checkedLevel(int level) {
        if (level < 0 || level > this.maxLevel) {
            throw new IllegalArgumentException("Invalid tile level: " + level);
        }
    }

    private static int maximumTile(double fraction, int levelSize) {
        double inclusive = fraction <= 0.0 ? 0.0 : Math.nextDown(fraction);
        return (int)Math.floor(inclusive * (double)levelSize / 512.0);
    }

    private static int ceilDivPow2(int value, int level) {
        return (int)((long)value + (1L << level) - 1L >> level);
    }

    private static int ceilDiv(int value, int divisor) {
        return (value + divisor - 1) / divisor;
    }

    private static double clampFraction(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    public record TileCoordinate(int level, int x, int y) {
    }
}

