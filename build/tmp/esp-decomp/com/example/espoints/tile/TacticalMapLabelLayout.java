/*
 * Decompiled with CFR 0.152.
 */
package com.example.espoints.tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class TacticalMapLabelLayout {
    private final Map<String, Placement> placements = new HashMap<String, Placement>();
    private final List<Occupied> occupied = new ArrayList<Occupied>();
    private Object frameKey;
    private Bounds bounds = new Bounds(0, 0, 1, 1);
    private long rebuildCount;

    public synchronized void begin(Object key, Bounds newBounds) {
        Objects.requireNonNull(newBounds, "newBounds");
        if (!Objects.equals(this.frameKey, key) || !this.bounds.equals(newBounds)) {
            this.frameKey = key;
            this.bounds = newBounds;
            this.placements.clear();
            this.occupied.clear();
            ++this.rebuildCount;
        }
    }

    public synchronized Placement place(String id, int priority, int anchorX, int anchorY, int preferredOffsetX, int preferredOffsetY, int fullWidth, int abbreviatedWidth, int height) {
        Placement cached = this.placements.get(id);
        if (cached != null) {
            return cached;
        }
        int safeHeight = Math.max(1, height);
        int[][] offsets = new int[][]{{preferredOffsetX, preferredOffsetY}, {preferredOffsetX, preferredOffsetY + safeHeight + 3}, {-Math.max(1, preferredOffsetX) - fullWidth, preferredOffsetY}, {-fullWidth / 2, preferredOffsetY - safeHeight - 3}, {-fullWidth / 2, preferredOffsetY + safeHeight + 3}};
        Placement result = this.tryWidths(priority, anchorX, anchorY, offsets, Math.max(1, fullWidth), safeHeight, Mode.FULL);
        if (result == null && abbreviatedWidth > 0 && abbreviatedWidth < fullWidth) {
            result = this.tryWidths(priority, anchorX, anchorY, offsets, abbreviatedWidth, safeHeight, Mode.ABBREVIATED);
        }
        if (result == null) {
            result = new Placement(anchorX, anchorY, Mode.ICON_ONLY);
        }
        this.placements.put(id, result);
        return result;
    }

    public synchronized long rebuildCount() {
        return this.rebuildCount;
    }

    public synchronized void reset() {
        this.frameKey = null;
        this.placements.clear();
        this.occupied.clear();
    }

    private Placement tryWidths(int priority, int anchorX, int anchorY, int[][] offsets, int width, int height, Mode mode) {
        for (int[] offset : offsets) {
            int x = TacticalMapLabelLayout.clamp(anchorX + offset[0], this.bounds.left(), this.bounds.right() - width);
            int y = TacticalMapLabelLayout.clamp(anchorY + offset[1], this.bounds.top(), this.bounds.bottom() - height);
            Occupied candidate = new Occupied(priority, x, y, x + width, y + height);
            if (!this.occupied.stream().noneMatch(candidate::overlaps)) continue;
            this.occupied.add(candidate);
            return new Placement(x, y, mode);
        }
        return null;
    }

    private static int clamp(int value, int minimum, int maximum) {
        if (maximum < minimum) {
            return minimum;
        }
        return Math.max(minimum, Math.min(maximum, value));
    }

    public record Bounds(int left, int top, int right, int bottom) {
        public Bounds {
            if (right <= left) {
                right = left + 1;
            }
            if (bottom <= top) {
                bottom = top + 1;
            }
        }
    }

    public record Placement(int x, int y, Mode mode) {
    }

    public static enum Mode {
        FULL,
        ABBREVIATED,
        ICON_ONLY;

    }

    private record Occupied(int priority, int left, int top, int right, int bottom) {
        private boolean overlaps(Occupied other) {
            return this.left < other.right && this.right > other.left && this.top < other.bottom && this.bottom > other.top;
        }
    }
}

