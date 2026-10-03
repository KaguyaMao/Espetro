/*
 * Decompiled with CFR 0.152.
 */
package com.example.espoints.tile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class TacticalMapStaticProjection {
    private final Map<String, ScreenPoint> points = new HashMap<String, ScreenPoint>();
    private Object frameKey;
    private long rebuildCount;

    public synchronized void begin(Object key) {
        if (!Objects.equals(this.frameKey, key)) {
            this.frameKey = key;
            this.points.clear();
            ++this.rebuildCount;
        }
    }

    public synchronized ScreenPoint point(String id, double screenX, double screenY) {
        ScreenPoint cached = this.points.get(id);
        if (cached != null) {
            return cached;
        }
        ScreenPoint created = new ScreenPoint(screenX, screenY);
        this.points.put(id, created);
        return created;
    }

    public synchronized long rebuildCount() {
        return this.rebuildCount;
    }

    public synchronized void reset() {
        this.frameKey = null;
        this.points.clear();
    }

    public record ScreenPoint(double x, double y) {
    }
}

